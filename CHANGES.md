# Review Notes — Decathlon Selenium Suite

Review of the automation framework, with the fixes applied. Two separate problems were found:
the suite was **slow**, and it was **passing without testing anything**.

The headline issue: before these changes, five of the six scenarios passed because every
interaction silently failed. Wrong selectors and swallowed exceptions meant clicks never landed,
and the assertions were weak enough not to notice.

---

## 1. Speed: mixing implicit and explicit waits

`BaseTest` set a 10s implicit wait while `BasePage` used a 15s `WebDriverWait`. Selenium's own
documentation warns against combining the two — each poll of the explicit wait blocks for the
implicit timeout, so a "15 second" wait costs far more than 15 seconds.

On top of that, nearly every page-object method wrapped its wait in `catch (Exception ignored)`.
A missing locator therefore burned the full timeout *and* let the test carry on as if it had worked.

| Where | Cost when the locator was missing |
|---|---|
| `HomePage.dismissPopups` | 2 lookups ≈ 20s, called twice per search → ~40s |
| `ProductListingPage.applyPriceFilter` | 2 × `writeText` ≈ 30s |
| `ProductListingPage.applyColorFilter` / `selectSortOption` | 15s each |
| `CartPage` quantity +/−, delete, empty message | 15s each |

**Fixed**

- Implicit wait set to `Duration.ZERO`; all waiting is now explicit.
- Two tiers: `DEFAULT_TIMEOUT` (15s) for elements a scenario depends on, `SHORT_TIMEOUT` (3s) for
  optional things like banners and facets.
- Added `clickIfPresent()`, `getElementsWhenPresent(locator, timeout)`, `navigateTo()`.
- Anything that counts elements now waits explicitly, since removing the implicit wait would
  otherwise turn it into a race.
- Added a 30s `pageLoadTimeout`.

Scenario 1 went from timing out after ~40s to passing in ~10s.

---

## 2. The geo-redirect modal — the reason most tests were meaningless

Decathlon shows a country-redirect modal (*"Looks like you're outside the U.S."*,
`#sg_country_redirect_mod`) shortly **after** each navigation. It covers the page and swallows every
click, producing `ElementClickInterceptedException`.

`dismissPopups()` was only called *before* `driver.get(...)`, so it ran against the old page and the
modal was never actually dismissed.

Consequences, all of which were passing green:

- **Scenario 2** — filter clicks intercepted, so no filter was ever applied.
- **Scenario 3** — sort never applied.
- **Scenarios 4, 5, 6** — *Add to cart* was intercepted, so the cart stayed **empty**. Scenario 6
  then "verified" an empty cart that had never had anything in it.

**Fixed**

- Popup dismissal moved into `BasePage` (`dismissBlockingPopups()`), keyed on the modal's real
  control (`#spicegems_cr_btn_no`).
- Added `navigateTo(url)` = navigate **then** dismiss. All navigation now goes through it.
- `clickAddToCart()` dismisses blockers before clicking.

---

## 3. Selectors that matched nothing

Every one of these was verified against the live site.

### Product detail page — no `<h1>` exists

`getProductTitle()` looked for `h1, .product__title, .product-title, [data-testid='product-title']`.
The current PDP has **none** of these, so scenario 1 failed on a 15s timeout.

The visible title is an `<h3>` inside `[data-testid='product-information-details']`.
`getProductTitle()` now tries an ordered list of candidates (old selectors kept last as a fallback)
and falls back to `textContent` for the hidden copies that return `""` from `getText()`.

### Dead category URL

`https://www.decathlon.com/collections/mens-jackets-coats` returns **404**. It still renders
recommended products, so nothing threw — the test just landed on a page with no filter UI at all.
Correct URL is `/collections/mens-jackets`. (`ProductListingPage` also had a dead fallback PDP URL.)

### Facets are hidden until opened

Filter inputs are in the DOM but zero-sized or `opacity: 0` until their `<summary>` is expanded, so
Selenium reports every one as not displayed and refuses to click.

| Control | Was | Now |
|---|---|---|
| Color | `//label[contains(@class,'facet-checkbox')...]` (no match) | open the *Color* facet, click `span.swatch--filter` |
| Price | typed into `input[name='filter.v.price.gte']` (not displayed) | open the *Price* facet, then type + Enter |
| Sort | `Select` on `select[name='sort_by']` | native select is 0×0 and unusable; now clicks the radio input in the *Sort* facet |

Sort option text also differs: the site says `Price, low to high`, the test asks for
`Price: Low to High`. Matching is now punctuation-insensitive via a `normalize()` helper, so the
existing test code keeps working.

### Cart selectors

Verified against a cart with a real item in it:

| Element | Was | Now |
|---|---|---|
| Line item row | `.cart-item, tr.cart-item` (no match) | `a.cart-items__title` (one per item, avoids the header row) |
| Line total | `.cart-item__final-price, .cart-item__totals` (no match) | `td.cart-items__price` |
| Remove | `.remove-icon-bottom` | `button.cart-items__remove` (old kept as fallback) |
| Badge | `[data-testid='cart-count'], .cart-count-bubble` (no match) | `[data-testid='cart-bubble']` |

`.cart-items__title`, `.cart-items__unit-price-wrapper`, `[data-testid='cart-total-value']`,
`button[name='plus']` and `button[name='minus']` were all correct.

The cart re-renders asynchronously, so quantity changes and deletions now wait for the DOM to
actually update before the test asserts on it.

### A fallback that faked a pass

```java
public String getEmptyCartMessage() {
    try { return readText(emptyCartMessage); }
    catch (Exception e) { return "Your cart is empty"; }   // <-- always passes
}
```

Returning the expected string on failure made scenario 6's assertion pass whether or not the cart
emptied. It now returns `""`.

---

## 4. Headless support + GitHub Actions

`BaseTest` honours a `HEADLESS` environment variable (or `-Dheadless=true`), adding
`--headless=new`, `--window-size=1920,1080`, `--no-sandbox`, `--disable-dev-shm-usage`.
Locally the browser still opens visibly by default.

Added `.github/workflows/selenium-tests.yml` — runs on every push and PR to `main`, daily at
02:00 UTC, and on manual dispatch. It sets up JDK 17 + Chrome, runs `mvn -B test`, publishes the
JUnit results, and uploads failure screenshots and surefire reports as artifacts.

---

## Result

All six scenarios pass headless, and now actually exercise the site:

```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
Total time: 02:56 min
```

Most of that remaining time is the six separate browser launches (see point 3 below) — the waits
themselves are no longer the bottleneck.

---

## Still worth fixing (not changed here)

These are test-design issues rather than bugs, so the assertions were left as they are:

1. **The assertions are weaker than the README claims.** Scenario 2 asserts
   `getProductCount() >= 0`, which is true no matter what — a product count can't be negative. It
   would pass on a blank page. Scenario 3 asserts only `assertNotNull` on the price list rather than
   checking the order is non-decreasing. Scenarios 4 and 5 assert only that the URL contains
   `/cart`, never that a price total is correct. The README describes real checks (price ranges,
   sort order, subtotal = unit × 2, badge increments) that the code does not perform.

2. **`catch (Exception ignored)` hides breakage.** It is what let a completely dead selector look
   like a passing test for so long. Failing loudly — or at minimum logging — would have surfaced all
   of this immediately.

3. **A fresh Chrome per test method.** `@BeforeMethod` launches and quits a browser six times.
   Sharing a driver where scenarios allow it would cut the runtime meaningfully.

4. **Selector fragility.** Several of these are Shopify theme classes that will move again. The
   `data-testid` attributes on the site are the more stable choice where they exist.
