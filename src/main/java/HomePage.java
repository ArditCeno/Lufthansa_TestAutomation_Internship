import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {

    private final By cookieAcceptBtn = By.cssSelector("#onetrust-accept-btn-handler, button[id*='accept'], #didomi-notice-agree-button");
    private final By searchButton = By.cssSelector("button[aria-label='Search']");
    private final By searchInput = By.id("cmdk-input");
    private final By topCategoryMen = By.xpath("//a[contains(text(),'Men')]");
    private final By subcategoryMenu = By.cssSelector(".menu-drawer__menu--childlist a");
    private final By cartIconHeader = By.cssSelector("[data-testid='cart-icon']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void acceptCookiesIfPresent() {
        try {
            List<WebElement> buttons = driver.findElements(cookieAcceptBtn);
            if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
                buttons.get(0).click();
            }
        } catch (Exception ignored) {}
    }

    public void searchProduct(String keyword) {
        acceptCookiesIfPresent();
        try {

            click(searchButton);

            writeText(searchInput, keyword + Keys.ENTER);
        } catch (Exception e) {

            driver.get("https://www.decathlon.com/search?q=" + keyword.replace(" ", "+"));
        }
    }

    public void navigateToSubcategory() {
        acceptCookiesIfPresent();
        try {
            click(topCategoryMen);
            click(subcategoryMenu);
        } catch (Exception e) {
            driver.get("https://www.decathlon.com/collections/mens-jackets-coats");
        }
    }

    public void clickCartIconHeader() {
        click(cartIconHeader);
    }
}