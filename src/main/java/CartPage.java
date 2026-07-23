import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CartPage extends BasePage {

    private final By cartRows = By.cssSelector(".cart-item-row");
    private final By itemPrices = By.cssSelector(".cart-item-price");
    private final By orderTotal = By.cssSelector(".order-total-amount");
    private final By quantityPlusButton = By.cssSelector(".quantity-increment");
    private final By quantityMinusButton = By.cssSelector(".quantity-decrement");
    private final By itemSubtotal = By.cssSelector(".cart-item-subtotal");
    private final By deleteButton = By.cssSelector(".button-delete-item");
    private final By emptyCartMessage = By.cssSelector(".empty-cart-text");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageOpened() {
        return driver.getCurrentUrl().contains("/cart");
    }

    public int getCartRowsCount() {
        return isElementPresent(cartRows) ? getElements(cartRows).size() : 0;
    }

    public double calculateSumOfItems() {
        List<WebElement> prices = getElements(itemPrices);
        double sum = 0;
        for (WebElement price : prices) {
            String cleanPrice = price.getText().replaceAll("[^0-9.]", "");
            if (!cleanPrice.isEmpty()) {
                sum += Double.parseDouble(cleanPrice);
            }
        }
        return sum;
    }

    public double getOrderTotal() {
        String cleanTotal = readText(orderTotal).replaceAll("[^0-9.]", "");
        return cleanTotal.isEmpty() ? 0.0 : Double.parseDouble(cleanTotal);
    }

    public void increaseFirstItemQuantity() {
        getElements(quantityPlusButton).get(0).click();
    }

    public void decreaseFirstItemQuantity() {
        getElements(quantityMinusButton).get(0).click();
    }

    public double getFirstItemSubtotal() {
        String cleanSubtotal = getElements(itemSubtotal).get(0).getText().replaceAll("[^0-9.]", "");
        return cleanSubtotal.isEmpty() ? 0.0 : Double.parseDouble(cleanSubtotal);
    }

    public double getFirstItemUnitPrice() {
        String cleanPrice = getElements(itemPrices).get(0).getText().replaceAll("[^0-9.]", "");
        return cleanPrice.isEmpty() ? 0.0 : Double.parseDouble(cleanPrice);
    }

    public void deleteFirstItem() {
        click(deleteButton);
    }

    public String getEmptyCartMessage() {
        return readText(emptyCartMessage);
    }
}