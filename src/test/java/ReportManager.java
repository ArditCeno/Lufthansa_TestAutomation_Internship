import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
            spark.config().setDocumentTitle("Decathlon Automation Report");
            spark.config().setReportName("Decathlon.com - Selenium/TestNG Regression Suite");

            extentReports = new ExtentReports();
            extentReports.attachReporter(spark);
            extentReports.setSystemInfo("Site", "https://www.decathlon.com/");
            extentReports.setSystemInfo("Framework", "Selenium 4 + TestNG + Page Object Model");
        }
        return extentReports;
    }

    public static void startTest(String name, String description) {
        currentTest.set(getInstance().createTest(name, description));
    }

    public static ExtentTest getTest() {
        return currentTest.get();
    }

    public static void logInfo(String message) {
        if (getTest() != null) getTest().info(message);
    }

    public static void logPass(String message) {
        if (getTest() != null) getTest().pass(message);
    }

    public static void logFail(String message) {
        if (getTest() != null) getTest().fail(message);
    }

    public static void flush() {
        getInstance().flush();
    }
}