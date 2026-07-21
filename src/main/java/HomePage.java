import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private By searchBox = By.id("search-input");
    private By searchButton = By.cssSelector("button[type='submit']");
    private By topCategoryMenu = By.xpath("//a[contains(text(),'Men')]");
    private By subCategoryMenu = By.xpath("//a[contains(text(),'Jackets')]");

    public HomePage (WebDriver driver){
        super(driver);
    }

    public void searchProduct(String productName){
        writeText(searchBox, productName);
        click (searchButton);
    }
    public void navigateToSubcategory(){
        click (topCategoryMenu);
        click (subCategoryMenu);
    }

}
