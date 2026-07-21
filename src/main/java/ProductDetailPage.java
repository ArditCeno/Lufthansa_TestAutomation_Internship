import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {
    private By productTitle = By.cssSelector("h1.product-title");
    private By productPrice = By.cssSelector(".current-price");
    private By addToCartButton = By.cssSelector("button.add-to-cart");
    private By unavailableSizeOption = By.xpath("//button[contains(@class,'out-of-stock')]");
    private By notifyMeButton = By.cssSelector("button.notify-me");
    private By soldOutButton = By.xpath("//button[contains(text(),'Sold Out')]");
    private By cartBadgeCount = By.cssSelector(".cart-badge-number");
    private By cartIcon = By.cssSelector("a.cart-icon");

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
        return !wait.until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(soldOutButton)).isEnabled();
    }

    public void clickAddToCart() {
        click(addToCartButton);
    }

    public int getCartBadgeCount() {
        return Integer.parseInt(readText(cartBadgeCount).trim());
    }

    public void goToCart() {
        click(cartIcon);
    }

}
