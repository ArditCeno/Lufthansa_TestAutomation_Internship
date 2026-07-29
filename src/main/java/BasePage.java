import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    protected static final Duration SHORT_TIMEOUT = Duration.ofSeconds(3);

    protected static final By GEO_REDIRECT_DISMISS =
            By.cssSelector("#spicegems_cr_btn_no, #sg_country_redirect_mod a.spicegems_cr_main-btn");
    protected static final By COOKIE_ACCEPT =
            By.cssSelector("#onetrust-accept-btn-handler, button[id*='accept'], #didomi-notice-agree-button");

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WebDriverWait shortWait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        this.shortWait = new WebDriverWait(driver, SHORT_TIMEOUT);
    }

    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    protected List<WebElement> getElementsWhenPresent(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception ignored) {}

        return driver.findElements(locator);
    }

    protected List<WebElement> getVisibleElements(By locator) {
        return getElementsWhenPresent(locator, DEFAULT_TIMEOUT).stream()
                .filter(WebElement::isDisplayed)
                .toList();
    }

    protected void click(By locator) {
        click(locator, DEFAULT_TIMEOUT);
    }

    protected void click(By locator, Duration timeout) {
        WebElement el = new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    protected boolean clickIfPresent(By locator) {
        try {
            click(locator, SHORT_TIMEOUT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void writeText(By locator, String text) {
        writeText(locator, text, DEFAULT_TIMEOUT);
    }

    protected void writeText(By locator, String text, Duration timeout) {
        WebElement element = new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected String readText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected boolean isElementPresent(By locator) {
        return !getElementsWhenPresent(locator, SHORT_TIMEOUT).isEmpty();
    }

    protected void navigateTo(String url) {
        driver.get(url);
        dismissBlockingPopups();
    }

    protected void dismissBlockingPopups() {
        clickIfPresent(GEO_REDIRECT_DISMISS);
        clickFirstIfDisplayed(COOKIE_ACCEPT);
    }

    protected void clickFirstIfDisplayed(By locator) {
        try {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                found.get(0).click();
            }
        } catch (Exception ignored) {}
    }

    protected static String normalize(String text) {
        return text == null ? "" : text.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }
}