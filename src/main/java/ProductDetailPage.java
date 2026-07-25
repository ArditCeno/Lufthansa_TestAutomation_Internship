import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Pattern;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.cssSelector("h1, .product__title");
    private final By productPrice = By.cssSelector(".price-item--regular, [data-testid='price']");
    private final By productVariants = By.cssSelector(".variant-option__button-label");
    private final By unavailableSizeOption = By.cssSelector("label.disabled, button[disabled]");
    private final By addToCartButton = By.cssSelector("[data-testid='standalone-add-to-cart']");
    private final By soldOutButton = By.xpath("//button[contains(text(),'Sold Out')]");
    private final By notifyMeButton = By.xpath("//button[contains(text(),'Notify')]");

    private static final Pattern CURRENCY_PATTERN =
            Pattern.compile("[\\$€£]\\s?\\d{1,3}(,\\d{3})*(\\.\\d{2})?|\\d{1,3}(,\\d{3})*(\\.\\d{2})?\\s?[\\$€£]");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public String getProductTitle() {
        return readText(productTitle);
    }

    public String getProductPrice() {
        return readText(productPrice);
    }

    public boolean priceMatchesCurrencyPattern() {
        String price = getProductPrice();
        return price != null && CURRENCY_PATTERN.matcher(price.trim()).find();
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

    public boolean hasUnavailableSizeOption() {
        return !getElements(unavailableSizeOption).isEmpty();
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

    public boolean isSoldOutButtonDisplayed() {
        return isElementPresent(soldOutButton);
    }

    public boolean isSoldOutButtonDisabled() {
        try {
            WebElement button = driver.findElement(soldOutButton);
            return !button.isEnabled();
        } catch (Exception e) {
            return true;
        }
    }

    public double getUnitPrice() {
        return parsePrice(getProductPrice());
    }

    public void clickAddToCart() {
        selectFirstVariantIfAvailable();
        click(addToCartButton);
    }

    public void goToCart() {
        driver.get("https://www.decathlon.com/cart");
    }
}