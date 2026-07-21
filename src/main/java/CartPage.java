import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CartPage extends BasePage {
    private By cartRows = By.cssSelector(".cart-item-row");
    private By itemPrices = By.cssSelector(".cart-item-price");
    private By orderTotal = By.cssSelector(".order-total-amount");
    private By quantityPlusButton = By.cssSelector(".quantity-increment");
    private By quantityMinusButton = By.cssSelector(".quantity-decrement");
    private By itemSubtotal = By.cssSelector(".cart-item-subtotal");
    private By deleteButton = By.cssSelector(".button-delete-item");
    private By emptyCartMessage = By.cssSelector(".empty-cart-text");

    public CartPage (WebDriver driver){
        super(driver);
    }
    public boolean isCartPageOpened(){
        return driver.getCurrentUrl().contains("/cart");
    }
    public int getCartRowsCount() {
        return isElementPresent(cartRows) ? getElements(cartRows).size() : 0;
    }
    public double calculateSumOfItems(){
        List<WebElement> prices = getElements(itemPrices);
        double sum = 0;
        for(WebElement price : prices){
            sum += Double.parseDouble(price.getText().replaceAll("[^0-9.]",""));
        }
        return sum;
    }
    public double getOrderTotal() {
        return Double.parseDouble(readText(orderTotal).replaceAll("[^0-9.]", ""));
    }

    public void increaseFirstItemQuantity() {
        getElements(quantityPlusButton).get(0).click();
    }

    public void decreaseFirstItemQuantity() {
        getElements(quantityMinusButton).get(0).click();
    }

    public double getFirstItemSubtotal() {
        return Double.parseDouble(getElements(itemSubtotal).get(0).getText().replaceAll("[^0-9.]", ""));
    }

    public double getFirstItemUnitPrice() {
        return Double.parseDouble(getElements(itemPrices).get(0).getText().replaceAll("[^0-9.]", ""));
    }

    public void deleteFirstItem() {
        click(deleteButton);
    }

    public String getEmptyCartMessage() {
        return readText(emptyCartMessage);
    }
}
