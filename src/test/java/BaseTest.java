import com.aventstack.extentreports.MediaEntityBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {
    protected WebDriver driver;

    @BeforeSuite
    public void beforeSuite() {
        ReportManager.getInstance();
    }

    @BeforeMethod
    public void setUp(java.lang.reflect.Method method) {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));

        org.testng.annotations.Test testAnnotation = method.getAnnotation(org.testng.annotations.Test.class);
        String description = testAnnotation != null ? testAnnotation.description() : method.getName();
        ReportManager.startTest(method.getName(), description);

        driver.get("https://www.decathlon.com/");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (ITestResult.FAILURE == result.getStatus()) {
                String screenshotPath = takeScreenshot(result.getName());
                if (screenshotPath != null) {
                    attachScreenshotToReport(screenshotPath);
                }
                ReportManager.logFail("Test failed: " + result.getThrowable());
            } else if (ITestResult.SUCCESS == result.getStatus()) {
                ReportManager.logPass("Test completed successfully.");
            } else if (ITestResult.SKIP == result.getStatus()) {
                ReportManager.logInfo("Test skipped.");
            }
            driver.quit();
        }
    }

    @AfterSuite
    public void afterSuite() {
        ReportManager.flush();
    }

    private String takeScreenshot(String testName) {
        File screenshotDir = new File("failed-tests-screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotDir, testName + ".png");
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved at: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    private void attachScreenshotToReport(String screenshotPath) {
        try {
            byte[] bytes = Files.readAllBytes(new File(screenshotPath).toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            if (ReportManager.getTest() != null) {
                ReportManager.getTest().fail("Screenshot on failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            }
        } catch (Exception e) {
            System.out.println("Failed to attach screenshot to report: " + e.getMessage());
        }
    }
}