import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePage extends BasePage {

    private final By cartIcon = By.cssSelector("[data-testid='cart-icon']");
    private final By searchButton = By.cssSelector("button[aria-label='Search']");
    private final By searchInput = By.id("cmdk-input");
    private final By topLevelCategory = By.xpath("//a[contains(text(),'Men')]");
    private final By subcategoryMenu = By.cssSelector(".menu-drawer__menu--childlist a");

    private final By cookieAcceptBtn = By.cssSelector("#onetrust-accept-btn-handler, button[id*='accept'], #didomi-notice-agree-button");
    private final By stayOnUsSiteBtn = By.xpath("//button[contains(text(),'Stay on U.S. Site')] | //a[contains(text(),'Stay on U.S. Site')] | //*[contains(text(),'Stay on U.S. Site')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void dismissPopups() {
        clickFirstIfDisplayed(stayOnUsSiteBtn);
        dismissBlockingPopups();
    }

    public void searchProduct(String keyword) {
        dismissPopups();
        navigateTo("https://www.decathlon.com/search?q=" + keyword.replace(" ", "+"));
    }

    public void navigateToSubcategory() {
        dismissPopups();
        if (!clickIfPresent(topLevelCategory) || !clickIfPresent(subcategoryMenu)) {


            navigateTo("https://www.decathlon.com/collections/mens-jackets");
        }
    }

    public void clickCartIcon() {
        dismissPopups();
        click(cartIcon);
    }
}