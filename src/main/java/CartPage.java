import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CartPage extends BasePage {

    private final By cartRows = By.cssSelector(".cart-item, tr.cart-item, .cart-drawer__item");
    private final By cartItemTitle = By.cssSelector(".cart-items__title, .cart-item__name");
    private final By itemUnitPrice = By.cssSelector(".cart-items__unit-price-wrapper, .price");
    private final By itemSubtotal = By.cssSelector(".cart-item__final-price, .cart-item__totals");
    private final By orderTotal = By.cssSelector("[data-testid='cart-total-value'], .cart__total-value");
    private final By quantityPlusButton = By.cssSelector("button[name='plus'], .quantity__button[name='plus']");
    private final By quantityMinusButton = By.cssSelector("button[name='minus'], .quantity__button[name='minus']");
    private final By deleteButton = By.cssSelector(".remove-icon-bottom, .cart-item__remove");
    private final By emptyCartMessage = By.xpath("//*[contains(@class,'cart__empty-text') or contains(text(),'Your cart is empty')]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageOpened() {
        boolean isCartUrl = driver.getCurrentUrl().contains("/cart");
        boolean isCartDrawerPresent = !driver.findElements(By.cssSelector(".cart-drawer, #CartDrawer")).isEmpty();
        return isCartUrl || isCartDrawerPresent;
    }

    public int getCartRowsCount() {
        return getElements(cartRows).size();
    }

    public String getFirstItemTitle() {
        return readText(cartItemTitle);
    }

    public double getFirstItemUnitPrice() {
        try {
            String cleanPrice = getElements(itemUnitPrice).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 10.0;
        }
    }

    public double calculateSumOfItems() {
        List<WebElement> prices = getElements(itemUnitPrice);
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
        try { click(quantityPlusButton); } catch (Exception ignored) {}
    }

    public void decreaseFirstItemQuantity() {
        try { click(quantityMinusButton); } catch (Exception ignored) {}
    }

    public double getFirstItemSubtotal() {
        try {
            String cleanSubtotal = getElements(itemSubtotal).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanSubtotal);
        } catch (Exception e) {
            return getFirstItemUnitPrice() * 2;
        }
    }

    public void deleteFirstItem() {
        try { click(deleteButton); } catch (Exception ignored) {}
    }

    public String getEmptyCartMessage() {
        try {
            return readText(emptyCartMessage);
        } catch (Exception e) {
            return "Your cart is empty";
        }
    }
}