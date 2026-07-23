import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ProductListingPage extends BasePage {

    private final By resultsHeading = By.cssSelector(".search-results-heading");
    private final By productTiles = By.cssSelector(".product-block");
    private final By colorFilterCheckbox = By.xpath("//label[contains(text(),'Blue')]");
    private final By priceFilterRange = By.xpath("//label[contains(text(),'$20 - $50')]");
    private final By productPrices = By.cssSelector(".product-price-amount");
    private final By sortDropdown = By.id("sort-by-select");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    public String getResultsHeadingText() {
        return readText(resultsHeading);
    }

    public int getProductCount() {
        return getElements(productTiles).size();
    }

    public void clickFirstProduct() {
        getElements(productTiles).get(0).click();
    }

    public void applyColorFilter() {
        click(colorFilterCheckbox);
    }

    public void applyPriceFilter() {
        click(priceFilterRange);
    }

    public void selectSortOption(String optionText) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(sortDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(optionText);
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
            } catch (Exception e) {
                // Ingonrohen elementet e zbrazëta gjatë leximit dinamik
            }
        }
        return prices;
    }
}