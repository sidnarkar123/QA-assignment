
package utils;

import org.testng.annotations.DataProvider;

public class TestData {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {

        return new Object[][] {

                // Valid email and password
                {
                        System.getenv("TEST_EMAIL"),
                        System.getenv("TEST_PASSWORD"),
                        true
                },

                // Valid email and incorrect password
                {
                        System.getenv("TEST_EMAIL"),
                        "WrongPassword123",
                        false
                },

                // Invalid email and password
                {
                        "invalid@example.com",
                        "WrongPassword123",
                        false
                }
        };
    }
}

