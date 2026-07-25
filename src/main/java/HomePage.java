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
        try {
            List<WebElement> usModalBtns = driver.findElements(stayOnUsSiteBtn);
            if (!usModalBtns.isEmpty() && usModalBtns.get(0).isDisplayed()) {
                usModalBtns.get(0).click();
            }
        } catch (Exception ignored) {}

        try {
            List<WebElement> cookieBtns = driver.findElements(cookieAcceptBtn);
            if (!cookieBtns.isEmpty() && cookieBtns.get(0).isDisplayed()) {
                cookieBtns.get(0).click();
            }
        } catch (Exception ignored) {}
    }

    public void searchProduct(String keyword) {
        dismissPopups();

        driver.get("https://www.decathlon.com/search?q=" + keyword.replace(" ", "+"));
        dismissPopups();
    }

    public void navigateToSubcategory() {
        dismissPopups();
        try {
            click(topLevelCategory);
            click(subcategoryMenu);
        } catch (Exception e) {
            driver.get("https://www.decathlon.com/collections/mens-jackets-coats");
        }
    }

    public void clickCartIcon() {
        dismissPopups();
        click(cartIcon);
    }
}