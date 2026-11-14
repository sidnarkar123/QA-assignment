
package tests;

import base.BaseTest;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {

        String email = System.getenv("TEST_EMAIL");
        String password = System.getenv("TEST_PASSWORD");

        if (email == null || password == null) {
            throw new RuntimeException("Login credentials are missing");
        }

        return new Object[][] {
                {email, password, true},
                {email, "WrongPassword123", false},
                {"invalid@example.com", "WrongPassword123", false}
        };
    }

    @Test(dataProvider = "loginData")
    public void verifyLogin(String email, String password, boolean validCredentials) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username"))
        );

        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("password"))
        );

        passwordField.clear();
        passwordField.sendKeys(password);

        WebElement loginButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[@type='submit']")
                )
        );

        if (!loginButton.isEnabled()) {

            Assert.assertFalse(
                    validCredentials,
                    "Login button should be enabled for valid credentials"
            );

            System.out.println("Login button disabled for: " + email);
            return;
        }

        Assert.assertTrue(
                validCredentials || loginButton.isEnabled(),
                "Login button state is incorrect"
        );

        loginButton.click();

        if (validCredentials) {

            wait.until(ExpectedConditions.urlContains("dashboard"));

            Assert.assertTrue(
                    driver.getCurrentUrl().contains("dashboard"),
                    "Dashboard was not opened"
            );

            System.out.println("Login successful");

        } else {

            WebElement errorMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(
                                    "//*[contains(text(),'Invalid Email Id / Mobile No or Password.')]"
                            )
                    )
            );

            Assert.assertTrue(errorMessage.isDisplayed());

            System.out.println("Invalid login rejected");
        }
    }
}
