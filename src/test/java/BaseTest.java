import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        // Konfigurime shtesë për Chrome 150+ që të mos bllokojë elementët DOM
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://www.decathlon.com/");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (ITestResult.FAILURE == result.getStatus()) {
                takeScreenshot(result.getName());
            }
            driver.quit();
        }
    }

    private void takeScreenshot(String testName) {
        File screenshotDir = new File("failed-tests-screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotDir, testName + ".png");
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot u ruajt në: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("S'u realizua dot screenshot: " + e.getMessage());
        }
    }
}