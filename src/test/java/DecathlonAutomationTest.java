import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecathlonAutomationTest extends BaseTest {

    @Test(priority = 1, description = "Test 1: Search and open a product")
    public void testScenario1_SearchAndProductDetails() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        homePage.searchProduct("backpack");

        Assert.assertTrue(plp.getResultsHeadingText().contains("backpack"), "Search heading mismatch!");
        Assert.assertTrue(plp.getProductCount() > 0, "No products found for the search term!");

        plp.clickFirstProduct();

        Assert.assertFalse(pdp.getProductTitle().isEmpty(), "Product title is empty!");
        Assert.assertTrue(pdp.getProductPrice().matches("^\\$[0-9]+\\.[0-9]{2}$"), "Currency format is invalid!");
        Assert.assertTrue(pdp.isAddToCartButtonPresent(), "Add to Cart button is missing!");
    }

    @Test(priority = 2, description = "Test 2: Category filters")
    public void testScenario2_CategoryFilters() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.navigateToSubcategory();
        int initialCount = plp.getProductCount();

        plp.applyColorFilter();
        int countAfterColor = plp.getProductCount();
        Assert.assertNotEquals(initialCount, countAfterColor, "Color filter did not change the product count!");

        plp.applyPriceFilter();
        List<Double> prices = plp.getAllDisplayedPrices();
        for (double price : prices) {
            Assert.assertTrue(price >= 20.0 && price <= 50.0, "Product price " + price + " falls outside the $20-$50 range!");
        }
    }

    @Test(priority = 3, description = "Test 3: Sort results")
    public void testScenario3_SortResults() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.searchProduct("shoes");

        plp.selectSortOption("Price: Low to High");
        List<Double> lowToHighPrices = plp.getAllDisplayedPrices();
        List<Double> sortedCopy = new ArrayList<>(lowToHighPrices);
        Collections.sort(sortedCopy);
        Assert.assertEquals(lowToHighPrices, sortedCopy, "Prices are not sorted from Low to High!");

        plp.selectSortOption("Price: High to Low");
        List<Double> highToLowPrices = plp.getAllDisplayedPrices();
        Collections.sort(sortedCopy, Collections.reverseOrder());
        Assert.assertEquals(highToLowPrices, sortedCopy, "Prices are not sorted from High to Low!");
    }

    @Test(priority = 4, description = "Test 4: Add to cart and verify totals")
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
            Assert.assertEquals(pdp.getCartBadgeCount(), expectedBadgeCount, "Cart badge count did not increment!");
        }

        pdp.goToCart();
        Assert.assertTrue(cartPage.isCartPageOpened(), "Failed to navigate to the Cart page!");
        Assert.assertEquals(cartPage.getOrderTotal(), cartPage.calculateSumOfItems(), "Sum of item prices does not equal Order Total!");
    }

    @Test(priority = 5, description = "Test 5: Update cart quantities")
    public void testScenario5_UpdateCartQuantities() {
        testScenario4_AddToCartAndTotals();
        CartPage cartPage = new CartPage(driver);

        double unitPrice = cartPage.getFirstItemUnitPrice();
        double initialOrderTotal = cartPage.getOrderTotal();

        cartPage.increaseFirstItemQuantity();

        Assert.assertEquals(cartPage.getFirstItemSubtotal(), unitPrice * 2, "Line subtotal did not update to unit price x 2!");
        Assert.assertEquals(cartPage.getOrderTotal(), initialOrderTotal + unitPrice, "Order Total did not increase by exactly one unit price!");

        cartPage.decreaseFirstItemQuantity();
        Assert.assertEquals(cartPage.getOrderTotal(), initialOrderTotal, "Order Total did not return to its original value!");
    }

    @Test(priority = 6, description = "Test 6: Empty the cart")
    public void testScenario6_EmptyTheCart() {
        testScenario4_AddToCartAndTotals();
        CartPage cartPage = new CartPage(driver);

        int rows = cartPage.getCartRowsCount();
        while (rows > 0) {
            cartPage.deleteFirstItem();
            int newRows = cartPage.getCartRowsCount();
            Assert.assertEquals(newRows, rows - 1, "Number of rows in the cart did not decrease by 1!");
            rows = newRows;
        }

        Assert.assertEquals(cartPage.getEmptyCartMessage(), "Your cart is empty", "Empty cart message is not displayed!");
    }
}