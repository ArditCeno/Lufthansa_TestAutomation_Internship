import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ProductListingPage extends BasePage {

    private final By resultsHeading = By.cssSelector("div[role='status']");
    private final By productTileLink = By.cssSelector("#predictive-search-products a");
    private final By fallbackProductLink = By.cssSelector("a[href*='/products/']");

    private final By colorFacetGroup = By.xpath("//details[.//legend[contains(.,'Color')] or .//summary[contains(.,'Color')]]");
    private final By colorFacetOptions = By.xpath("//details[.//legend[contains(.,'Color')] or .//summary[contains(.,'Color')]]//label[contains(@class,'facet-checkbox')]");

    private final By priceFilterMin = By.cssSelector("input[name='filter.v.price.gte']");
    private final By priceFilterMax = By.cssSelector("input[name='filter.v.price.lte']");
    private final By sortDropdown = By.cssSelector("select#SortBy, select[name='sort_by']");
    private final By productPrices = By.cssSelector(".price-item--regular, [data-testid='product-price']");

    private final By productTileSwatchLabel = By.cssSelector(".product-item [aria-label], .card__color-swatch[title]");

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

    public boolean headingReflectsTerm(String term) {
        return getResultsHeadingText().contains(term.toLowerCase());
    }

    public int getProductCount() {
        List<WebElement> products = getElements(productTileLink);
        if (products.isEmpty()) {
            products = getElements(fallbackProductLink);
        }
        return products.size();
    }

    public void clickFirstProduct() {
        By locatorToUse = isElementPresent(productTileLink) ? productTileLink : fallbackProductLink;
        WebElement firstProduct = wait.until(ExpectedConditions.presenceOfElementLocated(locatorToUse));

        String productUrl = firstProduct.getAttribute("href");

        if (productUrl != null && !productUrl.trim().isEmpty()) {
            driver.get(productUrl);
        } else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstProduct);
            try {
                firstProduct.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProduct);
            }
        }
    }

    public void clickNthProduct(int index) {
        By locatorToUse = isElementPresent(productTileLink) ? productTileLink : fallbackProductLink;
        List<WebElement> products = getElements(locatorToUse);
        if (index >= products.size()) throw new IndexOutOfBoundsException("Only " + products.size() + " products available");
        String productUrl = products.get(index).getAttribute("href");
        if (productUrl != null && !productUrl.isBlank()) {
            driver.get(productUrl);
        } else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", products.get(index));
        }
    }

    public String applyColorFilterAndGetSelection() {
        try {
            List<WebElement> group = getElements(colorFacetGroup);
            if (!group.isEmpty() && group.get(0).getAttribute("open") == null) {
                click(colorFacetGroup); // open the group if it's a collapsed <details>
            }
        } catch (Exception ignored) {}

        try {
            List<WebElement> options = getElements(colorFacetOptions);
            if (options.isEmpty()) return null;
            String colorName = options.get(0).getText().trim();
            options.get(0).click();
            return colorName.isEmpty() ? null : colorName;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verifyTileColors(String expectedColor) {
        List<WebElement> swatches = getElements(productTileSwatchLabel);
        if (swatches.isEmpty()) {
            throw new IllegalStateException("No per-tile color indicator found in current markup - not verifiable");
        }
        for (WebElement swatch : swatches) {
            String label = swatch.getAttribute("aria-label");
            if (label == null || label.isBlank()) label = swatch.getAttribute("title");
            if (label != null && !label.toLowerCase().contains(expectedColor.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public void applyPriceFilter(String minPrice, String maxPrice) {
        try {
            if (minPrice != null) writeText(priceFilterMin, minPrice);
            if (maxPrice != null) writeText(priceFilterMax, maxPrice);
        } catch (Exception ignored) {}
    }

    public void selectSortOption(String optionText) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(sortDropdown));
            Select select = new Select(dropdown);
            select.selectByVisibleText(optionText);
        } catch (Exception ignored) {}
    }

    public List<Double> getAllDisplayedPrices() {
        List<WebElement> priceElements = getElements(productPrices);
        List<Double> prices = new ArrayList<>();

        for (WebElement element : priceElements) {
            double price = parsePrice(element.getText());
            if (price >= 0) {
                prices.add(price);
            }
        }
        return prices;
    }

    public boolean isNonDecreasing(List<Double> prices) {
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) > prices.get(i + 1)) return false;
        }
        return true;
    }

    public boolean isNonIncreasing(List<Double> prices) {
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) < prices.get(i + 1)) return false;
        }
        return true;
    }

    public boolean allWithinRange(List<Double> prices, double min, double max) {
        return prices.stream().allMatch(p -> p >= min && p <= max);
    }
}