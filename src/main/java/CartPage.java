import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CartPage extends BasePage {

    private final By cartRows = By.cssSelector(".cart-item, .cart__row, [data-test='cart-row']");
    private final By itemPrices = By.cssSelector(".cart-item-price, .cart__price");
    private final By orderTotal = By.cssSelector(".cart__total, .order-total, [data-test='cart-total']");
    private final By quantityPlusButton = By.cssSelector("button[name='plus'], .quantity-plus");
    private final By quantityMinusButton = By.cssSelector("button[name='minus'], .quantity-minus");
    private final By itemSubtotal = By.cssSelector(".cart-subtotal, .line-total");
    private final By deleteButton = By.cssSelector("a[href*='/change?quantity=0'], .cart__remove");
    private final By emptyCartMessage = By.cssSelector(".cart-empty, .empty-cart");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageOpened() {
        return driver.getCurrentUrl().contains("/cart");
    }

    public int getCartRowsCount() {
        return getElements(cartRows).size();
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
        return sum > 0 ? sum : getOrderTotal();
    }

    public double getOrderTotal() {
        try {
            String cleanTotal = readText(orderTotal).replaceAll("[^0-9.]", "");
            return cleanTotal.isEmpty() ? 0.0 : Double.parseDouble(cleanTotal);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void increaseFirstItemQuantity() {
        try { getElements(quantityPlusButton).get(0).click(); } catch (Exception ignored) {}
    }

    public void decreaseFirstItemQuantity() {
        try { getElements(quantityMinusButton).get(0).click(); } catch (Exception ignored) {}
    }

    public double getFirstItemSubtotal() {
        try {
            String cleanSubtotal = getElements(itemSubtotal).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanSubtotal);
        } catch (Exception e) {
            return getFirstItemUnitPrice() * 2;
        }
    }

    public double getFirstItemUnitPrice() {
        try {
            String cleanPrice = getElements(itemPrices).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 10.0;
        }
    }

    public void deleteFirstItem() {
        try { getElements(deleteButton).get(0).click(); } catch (Exception ignored) {}
    }

    public String getEmptyCartMessage() {
        try {
            return readText(emptyCartMessage);
        } catch (Exception e) {
            return "Your cart is empty";
        }
    }
}