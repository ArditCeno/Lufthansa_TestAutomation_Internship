import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecathlonAutomationTest extends BaseTest {

    @Test(priority = 1)
    public void testScenario1_SearchAndProductDetails() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        homePage.searchProduct("backpack");

        Assert.assertTrue(plp.getResultsHeadingText().contains("backpack"), "Mospërputhje në kërkim!");
        Assert.assertTrue(plp.getProductCount() > 0, "Asnjë produkt nuk u gjet!");

        plp.clickFirstProduct();

        Assert.assertFalse(pdp.getProductTitle().isEmpty(), "Titulli produktit bosh!");
        Assert.assertTrue(pdp.getProductPrice().matches("^\\$[0-9]+\\.[0-9]{2}$"), "Format monedhe gabim!");
        Assert.assertTrue(pdp.isAddToCartButtonPresent(), "Butoni shto në kartë mungon!");
    }

    @Test(priority = 2)
    public void testScenario2_CategoryFilters() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.navigateToSubcategory();
        int initialCount = plp.getProductCount();

        plp.applyColorFilter();
        int countAfterColor = plp.getProductCount();
        Assert.assertNotEquals(initialCount, countAfterColor, "Filtri nuk funksionon!");

        plp.applyPriceFilter();
        List<Double> prices = plp.getAllDisplayedPrices();
        for (double price : prices) {
            Assert.assertTrue(price >= 20.0 && price <= 50.0, "Çmimi jashtë filtrit!");
        }
    }

    @Test(priority = 3)
    public void testScenario3_SortResults() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.searchProduct("shoes");

        plp.selectSortOption("Price: Low to High");
        List<Double> lowToHighPrices = plp.getAllDisplayedPrices();
        List<Double> sortedCopy = new ArrayList<>(lowToHighPrices);
        Collections.sort(sortedCopy);
        Assert.assertEquals(lowToHighPrices, sortedCopy, "Gabim Low to High!");

        plp.selectSortOption("Price: High to Low");
        List<Double> highToLowPrices = plp.getAllDisplayedPrices();
        Collections.sort(sortedCopy, Collections.reverseOrder());
        Assert.assertEquals(highToLowPrices, sortedCopy, "Gabim High to Low!");
    }

    @Test(priority = 4)
    public void testScenario4_AddToCartAndTotals() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        String[] itemsToSearch = {"socks", "shorts", "cap"};
        int expectedBadgeCount = 0;

        for (String item : itemsToSearch) {
            homePage.searchProduct(item);
            plp.clickFirstProduct();
            pdp.clickAddToCart();
            expectedBadgeCount++;
            Assert.assertEquals(pdp.getCartBadgeCount(), expectedBadgeCount, "Badge nuk u rrit!");
        }

        pdp.goToCart();
        Assert.assertTrue(cartPage.isCartPageOpened(), "Nuk jemi te karta!");
        Assert.assertEquals(cartPage.getOrderTotal(), cartPage.calculateSumOfItems(), "Totali nuk përputhet!");
    }

    @Test(priority = 5)
    public void testScenario5_UpdateCartQuantities() {
        testScenario4_AddToCartAndTotals();
        CartPage cartPage = new CartPage(driver);

        double unitPrice = cartPage.getFirstItemUnitPrice();
        double initialOrderTotal = cartPage.getOrderTotal();

        cartPage.increaseFirstItemQuantity();

        Assert.assertEquals(cartPage.getFirstItemSubtotal(), unitPrice * 2, "Nën-totali gabim!");
        Assert.assertEquals(cartPage.getOrderTotal(), initialOrderTotal + unitPrice, "Total i papërditësuar!");

        cartPage.decreaseFirstItemQuantity();
        Assert.assertEquals(cartPage.getOrderTotal(), initialOrderTotal, "Çmimi nuk u rikthye!");
    }

    @Test(priority = 6)
    public void testScenario6_EmptyTheCart() {
        testScenario4_AddToCartAndTotals();
        CartPage cartPage = new CartPage(driver);

        int rows = cartPage.getCartRowsCount();
        while (rows > 0) {
            cartPage.deleteFirstItem();
            int newRows = cartPage.getCartRowsCount();
            Assert.assertEquals(newRows, rows - 1, "Rreshti nuk u fshi!");
            rows = newRows;
        }

        Assert.assertEquals(cartPage.getEmptyCartMessage(), "Your cart is empty", "Mesazhi mungon!");
    }
}