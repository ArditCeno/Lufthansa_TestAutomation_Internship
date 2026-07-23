import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    private final By searchBox = By.id("search-input");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By topCategoryMenu = By.xpath("//a[contains(text(),'Men')]");
    private final By subCategoryMenu = By.xpath("//a[contains(text(),'Jackets')]");


    private final By acceptCookiesButton = By.id("onetrust-accept-btn-handler");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void acceptCookiesIfPresent() {
        if (isElementPresent(acceptCookiesButton)) {
            click(acceptCookiesButton);
        }
    }

    public void searchProduct(String productName) {
        acceptCookiesIfPresent();
        writeText(searchBox, productName);

        try {
            click(searchButton);
        } catch (Exception e) {
            driver.findElement(searchBox).sendKeys(Keys.ENTER);
        }
    }

    public void navigateToSubcategory() {
        acceptCookiesIfPresent();

        Actions actions = new Actions(driver);
        WebElement topMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(topCategoryMenu));

        actions.moveToElement(topMenu).perform();

        click(subCategoryMenu);
    }
}