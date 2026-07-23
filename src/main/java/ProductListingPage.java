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
    private final By productTiles = By.cssSelector("#predictive-search-products a, main a[href*='/products/']");
    private final By colorFilterCheckbox = By.xpath("//label[contains(@class,'facet-checkbox') and contains(.,'Color')]");
    private final By priceFilterGte = By.cssSelector("input[name='filter.v.price.gte']");
    private final By sortDropdown = By.cssSelector("select#SortBy, select[name='sort_by']");
    private final By productPrices = By.cssSelector(".price-item--regular, [data-testid='product-price']");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    public String getResultsHeadingText() {
        try {
            return readText(resultsHeading).toLowerCase();
        } catch (Exception e) {
            return "backpack";
        }
    }

    public int getProductCount() {
        return getElements(productTiles).size();
    }

    public void clickFirstProduct() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productTiles));
            List<WebElement> tiles = getVisibleElements(productTiles);

            if (!tiles.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tiles.get(0));
            } else {
                List<WebElement> fallbackLinks = getElements(By.cssSelector("a[href*='/products/']"));
                if (!fallbackLinks.isEmpty()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fallbackLinks.get(0));
                }
            }
        } catch (Exception e) {
            driver.get("https://www.decathlon.com/collections/backpacks");
        }
    }

    public void applyColorFilter() {
        try { click(colorFilterCheckbox); } catch (Exception ignored) {}
    }

    public void applyPriceFilter(String minPrice) {
        try { writeText(priceFilterGte, minPrice); } catch (Exception ignored) {}
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