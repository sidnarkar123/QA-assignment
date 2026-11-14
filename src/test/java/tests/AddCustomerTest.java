
package tests;

import base.BaseTest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddCustomerTest extends BaseTest {

    private WebDriverWait wait;

    @DataProvider(name = "customerData")
    public Object[][] customerData() {

        String id = String.valueOf(Instant.now().toEpochMilli());

        return new Object[][] {
                {
                        "QA Automation Customer",
                        "QA-" + id,
                        "Test Person",
                        "9876543210",
                        "5551234",
                        "qa" + id + "@example.com",
                        "QA Automation Tester",
                        "Mumbai",
                        "400014"
                }
        };
    }

    public WebElement findElement(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    public void enterText(By locator, String value) {

        WebElement element = findElement(locator);
        element.clear();
        element.sendKeys(value);
    }

    public void clickElement(By locator) {

        try {
            new WebDriverWait(driver, Duration.ofSeconds(12))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            By.cssSelector("div.go2072408551")
                    ));
        } catch (TimeoutException e) {
            // Continue if notification is not present.
        }

        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element
        );

        try {
            wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            ).click();

        } catch (ElementClickInterceptedException e) {

            wait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.cssSelector("div.go2072408551")
                    )
            );

            wait.until(
                    ExpectedConditions.elementToBeClickable(locator)
            ).click();
        }
    }

    public By dropdown(String label) {
        return By.xpath(
                "//label[normalize-space()='" + label +
                        "']/following::div[@role='combobox'][1]"
        );
    }

    public void selectOption(By dropdown, String optionText) {

        clickElement(dropdown);

        By optionLocator = By.xpath(
                "//*[normalize-space(.)='" + optionText + "']"
        );

        WebElement option = wait.until(
                ExpectedConditions.visibilityOfElementLocated(optionLocator)
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", option
        );

        try {
            wait.until(
                    ExpectedConditions.elementToBeClickable(optionLocator)
            ).click();

        } catch (ElementClickInterceptedException e) {

            option = driver.findElement(optionLocator);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", option
            );
        }

        System.out.println("Selected: " + optionText);
    }

    public void login() {

        String email = System.getenv("TEST_EMAIL");
        String password = System.getenv("TEST_PASSWORD");

        Assert.assertNotNull(email, "TEST_EMAIL is missing");
        Assert.assertNotNull(password, "TEST_PASSWORD is missing");

        enterText(By.name("username"), email);
        enterText(By.name("password"), password);

        clickElement(By.xpath("//button[@type='submit']"));

        wait.until(ExpectedConditions.urlContains("dashboard"));

        System.out.println("Login successful");
    }

    public void navigateToAddCustomer() {

        clickElement(
                By.xpath("//*[normalize-space(.)='My Customers']")
        );

        clickElement(
                By.xpath("//*[normalize-space(.)='My Customer']")
        );

        By manage = By.xpath("//*[normalize-space(.)='Manage']");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(manage)
        );

        clickElement(manage);

        clickElement(
                By.xpath("//*[normalize-space(.)='New Customer']")
        );

        findElement(By.name("LeadName"));

        System.out.println("Add Customer form opened");
    }

    public void selectAddress() {

        clickElement(By.name("Address"));

        By searchBox = By.cssSelector(
                "input[placeholder='Search location']"
        );

        WebElement search = findElement(searchBox);

        search.clear();
        search.sendKeys("Dadar, Mumbai, Maharashtra, India");

        By suggestions = By.cssSelector(".pac-item");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(suggestions)
        );

        List<WebElement> results = driver.findElements(suggestions);

        Assert.assertTrue(
                results.size() > 0,
                "No address suggestions found"
        );

        results.get(0).click();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        clickElement(
                By.xpath("//button[normalize-space()='Done']")
        );

        wait.until(driver -> {

            String address = driver.findElement(
                    By.name("Address")
            ).getAttribute("value");

            return address != null
                    && !address.equals("-")
                    && address.toLowerCase().contains("dadar");
        });

        String address = driver.findElement(
                By.name("Address")
        ).getAttribute("value");

        System.out.println("Address selected: " + address);
    }

    @Test(dataProvider = "customerData")
    public void addCustomerTest(
            String customerName,
            String refNo,
            String contactName,
            String mobile,
            String telephone,
            String email,
            String designation,
            String location,
            String pincode
    ) {

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        login();
        navigateToAddCustomer();

        enterText(By.name("LeadName"), customerName);
        enterText(By.name("RefNo"), refNo);
        enterText(By.name("PersonName"), contactName);
        enterText(By.name("MobileNo"), mobile);
        enterText(By.name("ContactNo"), telephone);
        enterText(By.name("Email"), email);

        enterText(
                By.cssSelector(
                        "input[placeholder='Enter Contact Person Designation']"
                ),
                designation
        );

        enterText(By.name("PersonLocation"), location);

        selectAddress();

        selectOption(dropdown("Country"), "India");
        selectOption(dropdown("State"), "Maharashtra");
        selectOption(dropdown("City"), "Mumbai");
        selectOption(dropdown("Locality"), "Dadar");

        enterText(By.name("PinCode"), pincode);

        selectOption(dropdown("Customer Source"), "Facebook");
        selectOption(dropdown("Customer Stage"), "Enquiry");
        selectOption(dropdown("Customer Type"), "Retailer / Sub Dealer");

        clickElement(
                By.xpath(
                        "//button[normalize-space()='Save' " +
                                "or .//*[normalize-space()='Save']]"
                )
        );

        By successMessage = By.xpath(
                "//*[contains(normalize-space(.)," +
                        "'Customer Added Successfully')]"
        );

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(successMessage)
        );

        Assert.assertTrue(
                message.isDisplayed(),
                "Customer was not added successfully"
        );

        System.out.println(message.getText());
        System.out.println("Customer: " + customerName);
        System.out.println("Reference: " + refNo);
    }
}
