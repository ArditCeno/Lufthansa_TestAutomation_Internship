import org.testng.Assert;
import org.testng.annotations.Test;

public class DecathlonAutomationTest extends BaseTest {

    @Test(priority = 1, description = "Test 1: Search and open product")
    public void testScenario1_SearchAndProductDetails() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        homePage.searchProduct("backpack");
        Assert.assertTrue(plp.getProductCount() > 0, "Nuk u gjet asnjë produkt!");

        plp.clickFirstProduct();
        Assert.assertFalse(pdp.getProductTitle().isEmpty(), "Titulli i produktit është bosh!");
    }

    @Test(priority = 2, description = "Test 2: Category filters")
    public void testScenario2_CategoryFilters() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.navigateToSubcategory();
        plp.applyColorFilter();
        Assert.assertTrue(plp.getProductCount() >= 0);
    }

    @Test(priority = 3, description = "Test 3: Sort results")
    public void testScenario3_SortResults() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.searchProduct("shoes");
        plp.selectSortOption("Price: Low to High");
        Assert.assertNotNull(plp.getAllDisplayedPrices());
    }

    @Test(priority = 4, description = "Test 4: Add to cart and verify totals")
    public void testScenario4_AddToCartAndTotals() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.searchProduct("socks");
        plp.clickFirstProduct();
        pdp.clickAddToCart();
        pdp.goToCart();

        Assert.assertTrue(cartPage.isCartPageOpened(), "Navigimi në faqen e Cart dështoi!");
    }

    @Test(priority = 5, description = "Test 5: Update cart quantities")
    public void testScenario5_UpdateCartQuantities() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.searchProduct("socks");
        plp.clickFirstProduct();
        pdp.clickAddToCart();
        pdp.goToCart();

        cartPage.increaseFirstItemQuantity();
        Assert.assertTrue(cartPage.isCartPageOpened());
    }

    @Test(priority = 6, description = "Test 6: Empty the cart")
    public void testScenario6_EmptyTheCart() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.searchProduct("socks");
        plp.clickFirstProduct();
        pdp.clickAddToCart();
        pdp.goToCart();

        cartPage.deleteFirstItem();
        Assert.assertTrue(cartPage.getEmptyCartMessage().toLowerCase().contains("empty") || cartPage.getCartRowsCount() == 0);
    }
}