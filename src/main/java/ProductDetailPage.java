import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.cssSelector("h1.product-title");
    private final By productPrice = By.cssSelector(".current-price");
    private final By addToCartButton = By.cssSelector("button.add-to-cart");
    private final By unavailableSizeOption = By.xpath("//button[contains(@class,'out-of-stock')]");
    private final By notifyMeButton = By.cssSelector("button.notify-me");
    private final By soldOutButton = By.xpath("//button[contains(text(),'Sold Out')]");
    private final By cartBadgeCount = By.cssSelector(".cart-badge-number");
    private final By cartIcon = By.cssSelector("a.cart-icon");

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
        click(unavailableSizeOption);
    }

    public boolean isNotifyMeDisplayed() {
        return isElementPresent(notifyMeButton);
    }

    public boolean isSoldOutButtonDisabled() {
        try {
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(soldOutButton));
            return !button.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddToCart() {
        click(addToCartButton);
    }

    public int getCartBadgeCount() {
        String text = readText(cartBadgeCount).replaceAll("[^0-9]", "");
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    public void goToCart() {
        click(cartIcon);
    }
}