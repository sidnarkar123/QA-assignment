
package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        options.setExperimentalOption("prefs",
                java.util.Map.of(
                        "profile.default_content_setting_values.geolocation", 1
                ));

        driver = new ChromeDriver(options);

        driver.get("https://test.fieldforceconnect.com/");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
