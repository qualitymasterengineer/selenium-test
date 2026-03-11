package com.saucedemo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for checkout step one (info). No assertions inside.
 */
public class CheckoutPage extends BasePage {

    @FindBy(css = "#first-name")
    private WebElement firstNameInput;

    @FindBy(css = "#last-name")
    private WebElement lastNameInput;

    @FindBy(css = "#postal-code")
    private WebElement postalCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public void fillFirstName(String firstName) {
        waitForVisible(firstNameInput);
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
    }

    public void fillLastName(String lastName) {
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
    }

    public void fillPostalCode(String postalCode) {
        postalCodeInput.clear();
        postalCodeInput.sendKeys(postalCode);
    }

    /** Rellenar nombre, apellido, código postal y continuar. */
    public void fillAndContinue(String firstName, String lastName, String postalCode) {
        fillFirstName(firstName);
        fillLastName(lastName);
        fillPostalCode(postalCode);
        waitForClickable(continueButton);
        continueButton.click();
    }
}
