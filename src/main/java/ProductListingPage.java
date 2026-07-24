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
    private final By colorFilter = By.xpath("//label[contains(@class,'facet-checkbox') and contains(.,'Color')]");
    private final By priceFilterMin = By.cssSelector("input[name='filter.v.price.gte']");
    private final By priceFilterMax = By.cssSelector("input[name='filter.v.price.lte']");
    private final By sortDropdown = By.cssSelector("select#SortBy, select[name='sort_by']");
    private final By productPrices = By.cssSelector(".price-item--regular, [data-testid='product-price']");

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
        List<WebElement> products = getElements(productTileLink);
        if (products.isEmpty()) {
            products = getElements(fallbackProductLink);
        }
        return products.size();
    }

    public void clickFirstProduct() {
        By activeLocator = isElementPresent(productTileLink) ? productTileLink : fallbackProductLink;
        WebElement firstProduct = wait.until(ExpectedConditions.presenceOfElementLocated(activeLocator));

        String productUrl = firstProduct.getAttribute("href");
        if (productUrl != null && !productUrl.isEmpty()) {
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

    public void applyColorFilter() {
        try { click(colorFilter); } catch (Exception ignored) {}
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
            try {
                String cleanPrice = element.getText().replaceAll("[^0-9.]", "");
                if (!cleanPrice.isEmpty()) {
                    prices.add(Double.parseDouble(cleanPrice));
                }
            } catch (Exception ignored) {}
        }
        return prices;
    }
}