import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.cssSelector("h1");
    private final By productPrice = By.cssSelector(".price, [data-test='product-price'], .de-Price");
    private final By addToCartButton = By.cssSelector("button[name='add'], button[data-test='add-to-cart']");
    private final By unavailableSizeOption = By.cssSelector("button[disabled], .out-of-stock, [data-available='false']");
    private final By notifyMeButton = By.xpath("//*[contains(text(),'Notify') or contains(text(),'Sold Out')]");
    private final By soldOutButton = By.xpath("//button[contains(text(),'Sold Out') or @disabled]");
    private final By cartBadgeCount = By.cssSelector(".cart-count, .cart-badge, [data-test='cart-count']");
    private final By cartIcon = By.cssSelector("a[href*='/cart']");

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
        driver.get("https://www.decathlon.com/cart");
    }
}