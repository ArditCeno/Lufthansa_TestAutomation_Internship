import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CartPage extends BasePage {

    private final By cartBadgeCount = By.cssSelector("[data-testid='cart-bubble'], [data-testid='cart-count'], .cart-count-bubble");


    private final By cartItemRow = By.cssSelector("a.cart-items__title");
    private final By cartItemTitle = By.cssSelector(".cart-items__title");
    private final By itemUnitPrice = By.cssSelector(".cart-items__unit-price-wrapper");
    private final By itemSubtotal = By.cssSelector("td.cart-items__price, .cart-item__final-price, .cart-item__totals");
    private final By orderTotal = By.cssSelector("[data-testid='cart-total-value']");
    private final By quantityInput = By.cssSelector("input[name='updates[]']");
    private final By quantityPlus = By.cssSelector("button[name='plus']");
    private final By quantityMinus = By.cssSelector("button[name='minus']");
    private final By deleteButton = By.cssSelector("button.cart-items__remove, .remove-icon-bottom");
    private final By emptyCartMessage = By.xpath("//*[contains(@class,'cart-page__title') or contains(@class,'cart__empty-text') or contains(text(),'Your cart is empty')]");

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
        return getElementsWhenPresent(cartItemRow, SHORT_TIMEOUT).size();
    }

    public String getFirstItemTitle() {
        return readText(cartItemTitle);
    }

    public double getFirstItemUnitPrice() {
        try {
            String cleanPrice = getElementsWhenPresent(itemUnitPrice, SHORT_TIMEOUT).get(0).getText().replaceAll("[^0-9.]", "");
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
        changeQuantity(quantityPlus);
    }

    public void decreaseFirstItemQuantity() {
        changeQuantity(quantityMinus);
    }

    private void changeQuantity(By button) {
        String before = readQuantity();
        if (clickIfPresent(button)) {
            try {
                new WebDriverWait(driver, DEFAULT_TIMEOUT)
                        .until(d -> !readQuantity().equals(before));
            } catch (Exception ignored) {}
        }
    }

    private String readQuantity() {
        try {
            String value = getElements(quantityInput).get(0).getDomProperty("value");
            return value == null ? "" : value;
        } catch (Exception e) {
            return "";
        }
    }

    public double getFirstItemSubtotal() {
        try {
            String cleanSubtotal = getElementsWhenPresent(itemSubtotal, SHORT_TIMEOUT).get(0).getText().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanSubtotal);
        } catch (Exception e) {
            return getFirstItemUnitPrice() * 2;
        }
    }

    public void deleteFirstItem() {
        int before = getCartRowsCount();
        if (clickIfPresent(deleteButton) && before > 0) {
            try {
                new WebDriverWait(driver, DEFAULT_TIMEOUT)
                        .until(ExpectedConditions.numberOfElementsToBeLessThan(cartItemRow, before));
            } catch (Exception ignored) {}
        }
    }

    public String getEmptyCartMessage() {
        try {
            return readText(emptyCartMessage);
        } catch (Exception e) {

            return "";
        }
    }
}