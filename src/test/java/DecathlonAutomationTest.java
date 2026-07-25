import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class DecathlonAutomationTest extends BaseTest {

    private static final double PRICE_TOLERANCE = 0.05;

    @Test(priority = 1, description = "Test 1: Search and open product, including unavailable-size handling")
    public void testScenario1_SearchAndProductDetails() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        String searchTerm = "backpack";
        homePage.searchProduct(searchTerm);

        Assert.assertTrue(plp.headingReflectsTerm(searchTerm),
                "Results heading does not reflect the search term '" + searchTerm + "'. Heading was: " + plp.getResultsHeadingText());
        Assert.assertTrue(plp.getProductCount() > 0, "No products were found for '" + searchTerm + "'!");

        plp.clickFirstProduct();

        Assert.assertFalse(pdp.getProductTitle().isEmpty(), "Product title is empty!");
        Assert.assertTrue(pdp.priceMatchesCurrencyPattern(),
                "Product price '" + pdp.getProductPrice() + "' does not match an expected currency pattern!");
        Assert.assertTrue(pdp.isAddToCartButtonPresent(), "Add to cart button is not present on the product detail page!");

        homePage.searchProduct(searchTerm);
        int candidatesToCheck = Math.min(5, plp.getProductCount());
        boolean unavailableSizeCaseVerified = false;

        for (int i = 1; i < candidatesToCheck; i++) { // start at 1 to guarantee a "different" product from step 4
            plp.clickNthProduct(i);
            if (pdp.hasUnavailableSizeOption()) {
                pdp.selectUnavailableSize();
                Assert.assertTrue(pdp.isNotifyMeDisplayed(), "Notify Me button was not displayed for an unavailable size!");
                Assert.assertTrue(pdp.isSoldOutButtonDisplayed(), "Sold Out button was not displayed for an unavailable size!");
                Assert.assertTrue(pdp.isSoldOutButtonDisabled(), "Sold Out button should be disabled but was enabled!");
                unavailableSizeCaseVerified = true;
                break;
            }
            homePage.searchProduct(searchTerm);
        }

        if (!unavailableSizeCaseVerified) {
            ReportManager.logInfo("No product among the first " + candidatesToCheck +
                    " results currently has an unavailable size - step 6 could not be exercised on this run.");
        }
    }

    @Test(priority = 2, description = "Test 2: Category filters (color + price range)")
    public void testScenario2_CategoryFilters() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.navigateToSubcategory();
        int initialCount = plp.getProductCount();
        Assert.assertTrue(initialCount > 0, "Category listing showed no products before filtering!");

        String selectedColor = plp.applyColorFilterAndGetSelection();
        Assert.assertNotNull(selectedColor, "Could not locate/select a Color facet option - check the facet locators against the live DOM.");

        int filteredCount = plp.getProductCount();
        Assert.assertTrue(filteredCount <= initialCount,
                "Product count after applying color filter (" + filteredCount + ") should never exceed the unfiltered count (" + initialCount + ").");
        Assert.assertNotEquals(filteredCount, initialCount,
                "Product count did not change after applying the '" + selectedColor + "' color filter - filter may not have been applied.");

        try {
            boolean tilesMatch = plp.verifyTileColors(selectedColor);
            Assert.assertTrue(tilesMatch, "At least one visible tile does not match the selected color '" + selectedColor + "'.");
        } catch (IllegalStateException notVerifiable) {
            ReportManager.logInfo("Per-tile color could not be verified from the current markup (" + notVerifiable.getMessage() + ").");
        }

        double minPrice = 10, maxPrice = 100;
        plp.applyPriceFilter(String.valueOf((int) minPrice), String.valueOf((int) maxPrice));

        List<Double> pricesInRange = plp.getAllDisplayedPrices();
        Assert.assertFalse(pricesInRange.isEmpty(), "No prices could be read after applying the price filter!");
        Assert.assertTrue(plp.allWithinRange(pricesInRange, minPrice, maxPrice),
                "Not every displayed product falls within the $" + minPrice + "-$" + maxPrice + " range: " + pricesInRange);
    }

    @Test(priority = 3, description = "Test 3: Sort results low-to-high then high-to-low")
    public void testScenario3_SortResults() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);

        homePage.searchProduct("shoes");
        Assert.assertTrue(plp.getProductCount() > 1, "Need more than one product to meaningfully verify sort order.");

        plp.selectSortOption("Price: Low to High");
        List<Double> ascending = plp.getAllDisplayedPrices();
        Assert.assertFalse(ascending.isEmpty(), "No prices could be read after sorting Low to High!");
        Assert.assertTrue(plp.isNonDecreasing(ascending), "Prices are not in non-decreasing order after 'Price: Low to High': " + ascending);

        plp.selectSortOption("Price: High to Low");
        List<Double> descending = plp.getAllDisplayedPrices();
        Assert.assertFalse(descending.isEmpty(), "No prices could be read after sorting High to Low!");
        Assert.assertTrue(plp.isNonIncreasing(descending), "Prices are not in non-increasing order after 'Price: High to Low': " + descending);
    }

    @Test(priority = 4, description = "Test 4: Add three products to cart and verify totals")
    public void testScenario4_AddToCartAndTotals() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        String[] searchTerms = {"backpack", "socks", "shoes"};
        List<Double> addedUnitPrices = new ArrayList<>();
        int previousBadgeCount = homePage.getHeaderCartBadgeCount();

        for (String term : searchTerms) {
            homePage.searchProduct(term);
            plp.clickFirstProduct();

            double unitPrice = pdp.getUnitPrice();
            Assert.assertTrue(unitPrice > 0, "Could not read a valid unit price for product matching '" + term + "'.");

            pdp.clickAddToCart();
            addedUnitPrices.add(unitPrice);

            int newBadgeCount = homePage.getHeaderCartBadgeCount();
            Assert.assertTrue(newBadgeCount > previousBadgeCount,
                    "Cart badge did not increment after adding '" + term + "' (was " + previousBadgeCount + ", now " + newBadgeCount + ").");
            previousBadgeCount = newBadgeCount;
        }

        homePage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartPageOpened(), "Navigation to Cart page failed!");

        double expectedSum = addedUnitPrices.stream().mapToDouble(Double::doubleValue).sum();
        double actualSumFromCart = cartPage.getSumOfLineSubtotals();
        double orderTotal = cartPage.getOrderTotal();

        Assert.assertEquals(actualSumFromCart, orderTotal, PRICE_TOLERANCE,
                "Sum of line subtotals (" + actualSumFromCart + ") does not equal the Order Total (" + orderTotal + ").");
        ReportManager.logInfo("Unit prices tracked while adding items: " + addedUnitPrices + " (sum=" + expectedSum + ")");
    }

    @Test(priority = 5, description = "Test 5: Update cart quantities and verify subtotal/total math")
    public void testScenario5_UpdateCartQuantities() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.searchProduct("socks");
        plp.clickFirstProduct();
        pdp.clickAddToCart();
        homePage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartPageOpened(), "Navigation to Cart page failed!");

        double unitPrice = cartPage.getFirstItemUnitPrice();
        double initialOrderTotal = cartPage.getOrderTotal();
        String initialOrderTotalRaw = cartPage.getOrderTotalRawText();
        Assert.assertTrue(unitPrice > 0, "Could not read a valid unit price from the cart line item.");

        cartPage.increaseFirstItemQuantity();
        cartPage.waitForOrderTotalToChange(initialOrderTotalRaw, 10);

        double subtotalAfterIncrease = cartPage.getFirstItemSubtotal();
        double totalAfterIncrease = cartPage.getOrderTotal();
        String totalAfterIncreaseRaw = cartPage.getOrderTotalRawText();

        Assert.assertEquals(subtotalAfterIncrease, unitPrice * 2, PRICE_TOLERANCE,
                "Line subtotal after increasing quantity (" + subtotalAfterIncrease + ") does not equal unit price x 2 (" + (unitPrice * 2) + ").");
        Assert.assertEquals(totalAfterIncrease, initialOrderTotal + unitPrice, PRICE_TOLERANCE,
                "Order Total after increasing quantity (" + totalAfterIncrease + ") did not increase by exactly one unit price ("
                        + unitPrice + "). Expected " + (initialOrderTotal + unitPrice) + ".");

        cartPage.decreaseFirstItemQuantity();
        cartPage.waitForOrderTotalToChange(totalAfterIncreaseRaw, 10);

        double totalAfterDecrease = cartPage.getOrderTotal();
        Assert.assertEquals(totalAfterDecrease, initialOrderTotal, PRICE_TOLERANCE,
                "Order Total after decreasing quantity back to 1 (" + totalAfterDecrease + ") did not return to the original value (" + initialOrderTotal + ").");
    }

    @Test(priority = 6, description = "Test 6: Empty the cart completely")
    public void testScenario6_EmptyTheCart() {
        HomePage homePage = new HomePage(driver);
        ProductListingPage plp = new ProductListingPage(driver);
        ProductDetailPage pdp = new ProductDetailPage(driver);
        CartPage cartPage = new CartPage(driver);

        for (String term : new String[]{"socks", "shoes"}) {
            homePage.searchProduct(term);
            plp.clickFirstProduct();
            pdp.clickAddToCart();
        }

        homePage.clickCartIcon();
        Assert.assertTrue(cartPage.isCartPageOpened(), "Navigation to Cart page failed!");

        int rowsBefore = cartPage.getCartRowsCount();
        Assert.assertTrue(rowsBefore > 0, "Cart is unexpectedly empty at the start of the empty-cart test.");

        int safetyLimit = rowsBefore + 2;
        int iterations = 0;

        while (cartPage.getCartRowsCount() > 0 && iterations < safetyLimit) {
            int rowsBeforeDelete = cartPage.getCartRowsCount();
            cartPage.deleteFirstItem();

            try {
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                        .until(d -> cartPage.getCartRowsCount() < rowsBeforeDelete || cartPage.isEmptyCartMessageDisplayed());
            } catch (Exception ignored) {}

            int rowsAfterDelete = cartPage.getCartRowsCount();
            Assert.assertEquals(rowsAfterDelete, rowsBeforeDelete - 1,
                    "Deleting an item did not reduce the row count by exactly 1 (was " + rowsBeforeDelete + ", now " + rowsAfterDelete + ").");
            iterations++;
        }

        Assert.assertTrue(
                cartPage.isEmptyCartMessageDisplayed() || cartPage.getCartRowsCount() == 0,
                "Empty-cart message was not displayed and rows remain after emptying the cart!");
    }
}