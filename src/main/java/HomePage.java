import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class HomePage extends BasePage {

    private final By cookieAcceptBtn = By.cssSelector("#onetrust-accept-btn-handler, button[id*='accept'], #didomi-notice-agree-button");
    private final By searchInputLocators = By.cssSelector("input[type='search'], input[name='q'], input[placeholder*='Search']");

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
            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInputLocators));
            input.clear();
            input.sendKeys(keyword + Keys.ENTER);
        } catch (Exception e) {

            driver.get("https://www.decathlon.com/search?q=" + keyword.replace(" ", "+"));
        }
    }

    public void navigateToSubcategory() {
        acceptCookiesIfPresent();
        try {
            driver.get("https://www.decathlon.com/collections/mens-jackets-coats");
        } catch (Exception e) {
            System.out.println("Could not navigate to category");
        }
    }
}