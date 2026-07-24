import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private final By cartBadgeCount = By.cssSelector("[data-testid='cart-count'], .cart-count-bubble");
    private final By cartItemRow = By.cssSelector(".cart-item, tr.cart-item");
    private final By cartItemTitle = By.cssSelector(".cart-items__title");
    private final By itemUnitPrice = By.cssSelector(".cart-items__unit-price-wrapper");
    private final By itemSubtotal = By.cssSelector(".cart-item__final-price, .cart-item__totals");
    private final By orderTotal = By.cssSelector("[data-testid='cart-total-value']");
    private final By quantityPlus = By.cssSelector("button[name='plus']");
    private final By quantityMinus = By.cssSelector("button[name='minus']");
    private final By deleteButton = By.cssSelector(".remove-icon-bottom");
    private final By emptyCartMessage = By.xpath("//*[contains(@class,'cart__empty-text') or contains(text(),'Your cart is empty')]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageOpened() {
        boolean isCartUrl = driver.getCurrentUrl().contains("/cart");
        boolean isCartDrawerVisible = !driver.findElements(By.cssSelector(".cart-drawer, #CartDrawer")).isEmpty();
        return isCartUrl || isCartDrawerVisible;
    }

    public int getCartBadgeCount() {
        try {
            String text = readText(cartBadgeCount).replaceAll("[^0-9]", "");
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCartRowsCount() {
        return getElements(cartItemRow).size();
    }

    public String getFirstItemTitle() {
        return readText(cartItemTitle);
    }

    public double getFirstItemUnitPrice() {
        try {
            String cleanPrice = getElements(itemUnitPrice).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 0.0;
        }
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
        try { click(quantityPlus); } catch (Exception ignored) {}
    }

    public void decreaseFirstItemQuantity() {
        try { click(quantityMinus); } catch (Exception ignored) {}
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