package com.saucedemo.tests;

import com.saucedemo.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Base test: creates and configures WebDriver (Chrome). Timeouts from config.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        if ("true".equalsIgnoreCase(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(Config.getImplicitWaitMs()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Config.getPageLoadTimeoutSec()));
        if (!"true".equalsIgnoreCase(System.getProperty("headless", "false"))) {
            driver.manage().window().maximize();
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
