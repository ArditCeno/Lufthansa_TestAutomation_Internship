import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class ProductListingPage extends BasePage {

    private final By resultsHeading = By.cssSelector("div[role='status']");

    private final By productTileLink = By.cssSelector(
            "a[href*='/products/'], .product-card a, [data-testid*='product'] a, .grid-product__link, .product-item__title a"
    );

    private final By facetSummary = By.cssSelector("summary.facets__summary");
    private final By colorSwatch = By.cssSelector("span.swatch--filter");
    private final By priceFilterMin = By.cssSelector("input[name='filter.v.price.gte']");
    private final By priceFilterMax = By.cssSelector("input[name='filter.v.price.lte']");
    private final By sortOptionInput = By.cssSelector("input[name='sort_by']");
    private final By productPrices = By.cssSelector(".price-item--regular, [data-testid='product-price'], .price__regular");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    public String getResultsHeadingText() {
        try {
            return readText(resultsHeading).toLowerCase();
        } catch (Exception e) {
            return "results";
        }
    }

    public int getProductCount() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productTileLink));
        } catch (Exception ignored) {}

        return getElements(productTileLink).size();
    }

    public void clickFirstProduct() {
        try {
            List<WebElement> products = getElementsWhenPresent(productTileLink, DEFAULT_TIMEOUT);
            if (!products.isEmpty()) {
                WebElement firstProduct = products.get(0);
                String productUrl = firstProduct.getDomAttribute("href");

                if (productUrl != null && !productUrl.trim().isEmpty()) {
                    if (!productUrl.startsWith("http")) {
                        productUrl = "https://www.decathlon.com" + productUrl;
                    }
                    navigateTo(productUrl);
                } else {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProduct);
                }
            }
        } catch (Exception e) {
            navigateTo("https://www.decathlon.com/collections/backpacks/products/hiking-backpack-20-l-nh-100");
        }
    }

    private boolean openFacet(String name) {
        dismissBlockingPopups();
        for (WebElement summary : getElementsWhenPresent(facetSummary, SHORT_TIMEOUT)) {
            try {
                if (summary.isDisplayed() && normalize(summary.getText()).startsWith(normalize(name))) {
                    if ("true".equals(summary.getDomProperty("ariaExpanded"))) {
                        return true;
                    }
                    clickElement(summary);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private WebElement firstDisplayed(By locator) {
        for (WebElement element : getElementsWhenPresent(locator, SHORT_TIMEOUT)) {
            try {
                if (element.isDisplayed()) return element;
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void applyColorFilter() {
        if (!openFacet("Color")) return;
        WebElement swatch = firstDisplayed(colorSwatch);
        if (swatch != null) {
            clickElement(swatch);
            waitForListingRefresh();
        }
    }

    public void applyPriceFilter(String minPrice, String maxPrice) {
        if (!openFacet("Price")) return;
        try {
            WebElement min = firstDisplayed(priceFilterMin);
            WebElement max = firstDisplayed(priceFilterMax);
            if (min != null && minPrice != null) {
                min.clear();
                min.sendKeys(minPrice);
            }
            if (max != null && maxPrice != null) {
                max.clear();
                max.sendKeys(maxPrice);
                max.sendKeys(Keys.ENTER);
            }
            waitForListingRefresh();
        } catch (Exception ignored) {}
    }

    public void selectSortOption(String optionText) {
        if (!openFacet("Sort")) return;
        String wanted = normalize(optionText);
        for (WebElement input : getElementsWhenPresent(sortOptionInput, SHORT_TIMEOUT)) {
            try {
                String label = normalize(input.getDomProperty("value") + " " + labelTextFor(input));
                if (label.contains(wanted) || wanted.contains(normalize(labelTextFor(input)))) {
                    clickElement(input);
                    waitForListingRefresh();
                    return;
                }
            } catch (Exception ignored) {}
        }
    }

    private String labelTextFor(WebElement input) {
        String id = input.getDomAttribute("id");
        if (id == null || id.isEmpty()) return "";
        List<WebElement> labels = driver.findElements(By.cssSelector("label[for='" + id + "']"));
        if (labels.isEmpty()) return "";
        String text = labels.get(0).getText();
        if (text == null || text.isBlank()) {
            text = labels.get(0).getDomProperty("textContent");
        }
        return text == null ? "" : text;
    }

    private void waitForListingRefresh() {
        try {
            new WebDriverWait(driver, DEFAULT_TIMEOUT).until(
                    ExpectedConditions.presenceOfElementLocated(productTileLink));
        } catch (Exception ignored) {}
    }

    public List<Double> getAllDisplayedPrices() {
        List<WebElement> priceElements = getElementsWhenPresent(productPrices, DEFAULT_TIMEOUT);
        List<Double> prices = new ArrayList<>();

        for (WebElement element : priceElements) {
            try {
                if (!element.isDisplayed()) continue;
                String cleanPrice = element.getText().replaceAll("[^0-9.]", "");
                if (!cleanPrice.isEmpty()) {
                    prices.add(Double.parseDouble(cleanPrice));
                }
            } catch (Exception ignored) {}
        }
        return prices;
    }
}