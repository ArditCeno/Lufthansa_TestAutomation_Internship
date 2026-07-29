import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
    private final By productVariants = By.cssSelector(".variant-option__button-label");
    private final By unavailableSizeOption = By.cssSelector("label.disabled, button[disabled]");
    private final By addToCartButton = By.cssSelector("[data-testid='standalone-add-to-cart']");
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
            if (!variants.isEmpty()) {
                variants.get(0).click();
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
        click(addToCartButton);
    }

    public void goToCart() {
        navigateTo("https://www.decathlon.com/cart");
    }
}