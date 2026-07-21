import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

public class ProductListingPage extends BasePage {
    private By resultsHeading = By.cssSelector(".search-results-heading");
    private By productTiles = By.cssSelector(".product-block");
    private By colorFilterCheckbox = By.xpath("//label[contains(text(),'Blue')]");
    private By priceFilterRange = By.xpath("//label[contains(text(),'$20 - $50')]");
    private By productPrices = By.cssSelector(".product-price-amount");
    private By sortDropdown = By.id("sort-by-select");

    public ProductListingPage(WebDriver driver){
        super(driver);
    }
    public String getResultHeadingText(){
        return readText(resultsHeading);
    }
    public int getProductCount(){
        return getElements(productTiles).get(0).size();
    }
    public void clickFirstProduct(){
        getElements(productTiles).get(0).click();
    }
    public void applyColorFilter(){
        click(colorFilterCheckbox);
    }
    public void applyPriceFilter(){
        click(priceFilterRange);
    }
    public void selectSortOption(String optionText){
        click(sortDropdown);
        click(By.xpath("//option[contains(text(),'" + optionText + "')]"));
    }
    public List<Double> getAllDisplayedPrices() {
        List<WebElement> priceElements = getElements(productPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement element : priceElements) {
            String cleanPrice = element.getText().replaceAll("[^0-9.]", "");
            if (!cleanPrice.isEmpty()) {
                prices.add(Double.parseDouble(cleanPrice));
            }
        }
        return prices;
    }
}
