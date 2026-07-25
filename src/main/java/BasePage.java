import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    protected List<WebElement> getVisibleElements(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception ignored) {}

        return driver.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .toList();
    }

    protected void click(By locator) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            el.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    protected void writeText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected String readText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected double parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return -1;
        String cleaned = raw.replaceAll("[^0-9.,]", "").trim();
        if (cleaned.isEmpty()) return -1;
        // Normalize "1,299.00" vs "1299,00" style inputs
        if (cleaned.contains(",") && cleaned.contains(".")) {
            cleaned = cleaned.replace(",", "");
        } else if (cleaned.contains(",") && !cleaned.contains(".")) {
            cleaned = cleaned.replace(",", ".");
        }
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected boolean waitForUrlContains(String fragment, int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.urlContains(fragment));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean waitForTextToChange(By locator, String previousText, int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(d -> {
                        try {
                            String current = d.findElement(locator).getText();
                            return current != null && !current.equals(previousText);
                        } catch (Exception e) {
                            return false;
                        }
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}