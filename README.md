# Lufthansa_TestAutomation_Internship
# Decathlon Test Automation Mini-Project

![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.23.0-green)
![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue)
![Maven](https://img.shields.io/badge/Maven-Build-brightgreen)

This repository contains an automated end-to-end (E2E) testing framework designed for the Decathlon E-Commerce website ([decathlon.com](https://www.decathlon.com/)). The project is built using **Java**, **Selenium WebDriver**, **TestNG**, and follows the **Page Object Model (POM)** design pattern.

---

## 📌 Project Overview

The main objective of this project is to automate six core user journey scenarios on the Decathlon platform, ensuring high test accuracy, code reusability, robust waiting mechanisms, and failure handling via automated screenshot capture.

### Test Scenarios Automated

1. **Test 1: Search and Open a Product**
    * Search for a product keyword (e.g., `"backpack"`).
    * Verify search results heading and product count ($> 0$).
    * Validate Product Detail Page (PDP) elements (Title, Price currency format, Add to Cart button).
    * Verify behavior for unavailable sizes (*Notify Me* and disabled *Sold Out* button).

2. **Test 2: Category Filters**
    * Navigate to a subcategory via menu navigation (e.g., *Men $\rightarrow$ Jackets*).
    * Apply a **Color filter** and verify product count changes.
    * Apply a **Price range filter** ($20 - $50) and verify all displayed products fall within the range.

3. **Test 3: Sort Results**
    * Sort products by **"Price: Low to High"** and assert non-decreasing order.
    * Sort products by **"Price: High to Low"** and assert non-increasing order.

4. **Test 4: Add to Cart and Verify Totals**
    * Search and add multiple products to the cart.
    * Assert the cart badge counter increments after each addition.
    * Open the Cart page and verify that the sum of individual item prices equals the **Order Total**.

5. **Test 5: Update Cart Quantities**
    * Increase item quantity to 2 and verify subtotal updates (`unit price × 2`).
    * Verify Order Total increases accordingly.
    * Decrease quantity back to 1 and verify original price restoration.

6. **Test 6: Empty the Cart**
    * Sequentially delete items from the cart.
    * Assert line items decrease progressively until empty.
    * Validate the empty cart message (e.g., *"Your cart is empty"*).

---

## 🛠 Tech Stack & Tools

* **Programming Language:** Java 17
* **Automation Tool:** Selenium WebDriver (v4.23.0)
* **Testing Framework:** TestNG (v7.10.2)
* **Build Management:** Apache Maven
* **Driver Management:** WebDriverManager
* **Design Pattern:** Page Object Model (POM)

---

## 📁 Project Structure

```text
LufthansaProject/
├── failed-tests-screenshots/   # Automatically saved screenshots on test failure
├── bug_reports/                # PDF bug reports for issues found during testing
├── src/
│   ├── main/java/
│   │   └── com/decathlon/pages/
│   │       ├── BasePage.java            # Generic wrapper for Selenium driver methods
│   │       ├── HomePage.java            # Home Page locators & actions
│   │       ├── ProductListingPage.java  # Search/Category listing locators & actions
│   │       ├── ProductDetailPage.java   # PDP locators & actions
│   │       └── CartPage.java            # Cart locators & actions
│   └── test/java/
│       └── com/decathlon/tests/
│           ├── BaseTest.java            # Driver lifecycle setup (@BeforeMethod/@AfterMethod)
│           └── DecathlonAutomationTest.java # TestNG test suite executing Scenarios 1-6
├── pom.xml                     # Maven project configuration & dependencies
├── testng.xml                  # TestNG suite runner XML
└── README.md                   # Project documentation
```
🚀 Getting Started
Prerequisites
Ensure you have the following installed on your machine:

**Java Development Kit (JDK 17+)**

**Apache Maven 3.8+**

**Google Chrome browser installed**

Installation & Execution
1. Clone the Repository:
```
Bash
git clone [https://github.com/ArditCeno/Lufthansa_TestAutomation_Internship.git](https://github.com/ArditCeno/Lufthansa_TestAutomation_Internship.git)
cd Lufthansa_TestAutomation_Internship
```

2. Install Dependencies:
```
Bash
mvn clean install -DskipTests
```

3.Run All Automation Tests:
Using Maven CLI:
```
Bash
mvn clean test
```

## 📸 Failure Handling & Screenshots

The framework incorporates an automatic screenshot capture mechanism inside BaseTest.java.
If any test fails during execution:

A screenshot is automatically saved in the ./failed-tests-screenshots/ directory.

Files are named dynamically using the test method name for easy debugging (e.g., testScenario1_SearchAndProductDetails.png).

## 🐞 Bug Reporting
Any bugs or functional anomalies discovered during manual/automation runs have been documented into formal Bug Tickets and included in PDF format inside the bug_reports/ folder.

## ✉️ Contact & Submission
**Author: Ardit Ceno**

**Submitted to: Lufthansa Industry Solutions** 
