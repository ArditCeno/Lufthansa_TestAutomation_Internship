import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class ProductDetailPage extends BasePage {

    private final By[] productTitleCandidates = {
            By.cssSelector("[data-testid='product-information-details'] h3"),
            By.cssSelector(".view-product-title"),
            By.cssSelector("[data-testid='sticky-product-title']"),
            By.cssSelector("h1, .product__title, .product-title, [data-testid='product-title']")
    };

    private final By productPrice = By.cssSelector(".price-item--regular, [data-testid='price']");

    // Lokatore te zgjeruar per opsionet e masave/varianteve
    private final By productVariants = By.cssSelector(
            ".variant-option__button-label, [data-testid*='variant'], fieldset label:not(.disabled), .product-form__input input + label"
    );
    private final By unavailableSizeOption = By.cssSelector("label.disabled, button[disabled]");

    // Lokatore fleksibel per butonin Add to Cart
    private final By addToCartButton = By.cssSelector(
            "[data-testid='standalone-add-to-cart'], button[name='add'], button.product-form__submit, .add-to-cart-button"
    );
    private final By soldOutButton = By.xpath("//button[contains(text(),'Sold Out')]");
    private final By notifyMeButton = By.xpath("//button[contains(text(),'Notify')]");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public String getProductTitle() {
        for (int i = 0; i < productTitleCandidates.length; i++) {
            Duration timeout = (i == 0) ? DEFAULT_TIMEOUT : Duration.ZERO;
            for (WebElement element : getElementsWhenPresent(productTitleCandidates[i], timeout)) {
                String text = element.getText().trim();
                if (text.isEmpty()) {
                    String content = element.getDomProperty("textContent");
                    text = (content == null) ? "" : content.trim();
                }
                if (!text.isEmpty()) {
                    return text;
                }
            }
        }
        return "";
    }

    public String getProductPrice() {
        return readText(productPrice);
    }

    public boolean isAddToCartButtonPresent() {
        return isElementPresent(addToCartButton);
    }

    public void selectFirstVariantIfAvailable() {
        try {
            List<WebElement> variants = getElementsWhenPresent(productVariants, SHORT_TIMEOUT);
            for (WebElement variant : variants) {
                if (variant.isDisplayed() && variant.isEnabled()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", variant);
                    Thread.sleep(500);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    public void selectUnavailableSize() {
        try {
            List<WebElement> sizes = getElementsWhenPresent(unavailableSizeOption, SHORT_TIMEOUT);
            if (!sizes.isEmpty()) sizes.get(0).click();
        } catch (Exception ignored) {}
    }

    public boolean isNotifyMeDisplayed() {
        return isElementPresent(notifyMeButton);
    }

    public boolean isSoldOutButtonDisabled() {
        try {
            WebElement button = driver.findElement(soldOutButton);
            return !button.isEnabled();
        } catch (Exception e) {
            return true;
        }
    }

    public void clickAddToCart() {
        dismissBlockingPopups();

        selectFirstVariantIfAvailable();

        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
            btn.click();
        } catch (Exception e) {

            WebElement btn = driver.findElement(addToCartButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public void goToCart() {
        navigateTo("https://www.decathlon.com/cart");
    }
}