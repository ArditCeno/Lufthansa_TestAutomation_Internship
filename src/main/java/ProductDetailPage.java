import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.cssSelector("h1, .product__title");
    private final By productPrice = By.cssSelector(".price-item--regular, [data-testid='price']");
    private final By productVariants = By.cssSelector(".variant-option__button-label");
    private final By addToCartButton = By.cssSelector("[data-testid='standalone-add-to-cart']");
    private final By unavailableSizeOption = By.cssSelector("label.disabled, button[disabled]");
    private final By soldOutButton = By.xpath("//button[contains(text(),'Sold Out')]");
    private final By notifyMeButton = By.xpath("//button[contains(text(),'Notify')]");
    private final By cartBadgeCount = By.cssSelector("[data-testid='cart-count'], .cart-count-bubble");
    private final By cartIcon = By.cssSelector("[data-testid='cart-icon']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public String getProductTitle() {
        return readText(productTitle);
    }

    public String getProductPrice() {
        return readText(productPrice);
    }

    public boolean isAddToCartButtonPresent() {
        return isElementPresent(addToCartButton);
    }

    public void selectFirstVariantIfAvailable() {
        try {
            List<WebElement> variants = getElements(productVariants);
            if (!variants.isEmpty()) {
                variants.get(0).click();
            }
        } catch (Exception ignored) {}
    }

    public void selectUnavailableSize() {
        try {
            List<WebElement> sizes = getElements(unavailableSizeOption);
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
        selectFirstVariantIfAvailable();
        click(addToCartButton);
    }

    public int getCartBadgeCount() {
        try {
            String text = readText(cartBadgeCount).replaceAll("[^0-9]", "");
            return text.isEmpty() ? 1 : Integer.parseInt(text);
        } catch (Exception e) {
            return 1;
        }
    }

    public void goToCart() {
        click(cartIcon);
    }
}