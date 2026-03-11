package com.saucedemo.pages;

import com.saucedemo.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for login screen. No assertions inside.
 */
public class LoginPage extends BasePage {

    @FindBy(css = "#user-name")
    private WebElement usernameInput;

    @FindBy(css = "#password")
    private WebElement passwordInput;

    @FindBy(css = "#login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /** Navegar a la URL base (página de login). */
    public void navigateToBaseUrl() {
        driver.get(baseUrl);
    }

    public void fillUsername(String username) {
        waitForVisible(usernameInput);
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    public void fillPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        waitForClickable(loginButton);
        loginButton.click();
    }

    /** Rellenar usuario/contraseña y enviar login. */
    public void login(String username, String password) {
        fillUsername(username);
        fillPassword(password);
        clickLogin();
    }

    /** Login con credenciales por defecto desde config. */
    public void loginWithDefaultCredentials() {
        login(Config.getDefaultUsername(), Config.getDefaultPassword());
    }

    public String getErrorMessageText() {
        if (!isErrorMessageVisible()) {
            return "";
        }
        return errorMessage.getText();
    }

    public boolean isErrorMessageVisible() {
        try {
            waitForVisible(errorMessage);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
