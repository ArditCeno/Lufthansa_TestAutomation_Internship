import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ProductListingPage extends BasePage {

    private final By resultsHeading = By.cssSelector("h1, .de-Heading, .search-title");
    private final By productTiles = By.cssSelector("a[href*='/products/'], .product-card, .de-ProductTile");
    private final By colorFilterCheckbox = By.xpath("//*[contains(text(),'Blue') or contains(text(),'Black')]");
    private final By priceFilterRange = By.xpath("//*[contains(text(),'$20') or contains(text(),'$50')]");
    private final By productPrices = By.cssSelector(".price, .de-Price, [data-test='product-price']");
    private final By sortDropdown = By.cssSelector("select[id*='sort'], select[name*='sort']");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    public String getResultsHeadingText() {
        return readText(resultsHeading).toLowerCase();
    }

    public int getProductCount() {
        return getElements(productTiles).size();
    }

    public void clickFirstProduct() {
        List<WebElement> tiles = getVisibleElements(productTiles);
        if (!tiles.isEmpty()) {
            tiles.get(0).click();
        }
    }

    public void applyColorFilter() {
        try { click(colorFilterCheckbox); } catch (Exception ignored) {}
    }

    public void applyPriceFilter() {
        try { click(priceFilterRange); } catch (Exception ignored) {}
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