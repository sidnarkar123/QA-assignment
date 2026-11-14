
package tests;

import base.BaseTest;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PunchInTest extends BaseTest {

    @Test
    public void verifyPunchInPopup() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String email = System.getenv("TEST_EMAIL");
        String password = System.getenv("TEST_PASSWORD");

        if (email == null || password == null) {
            throw new RuntimeException("Login credentials are missing");
        }

        // Login
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("username"))
        );

        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.sendKeys(password);

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("dashboard"));

        System.out.println("Login successful");

        // Open Dashboard
        WebElement dashboard = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/dashboard']")
                )
        );

        dashboard.click();

        // Click Punched In card
        WebElement punchedIn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h6[normalize-space()='Punched In']")
                )
        );

        punchedIn.click();

        // Verify popup
        WebElement popup = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//*[contains(text(),'No data available for this section.')]"
                        )
                )
        );

        String message = popup.getText();

        Assert.assertEquals(
                message,
                "No data available for this section."
        );

        System.out.println("Popup verified: " + message);
    }
}
