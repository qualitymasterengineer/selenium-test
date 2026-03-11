package com.saucedemo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for checkout complete (thank you). No assertions inside.
 */
public class CheckoutCompletePage extends BasePage {

    @FindBy(css = ".title")
    private WebElement confirmationHeader;

    @FindBy(css = ".complete-header")
    private WebElement completeHeader;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getConfirmationHeaderText() {
        waitForVisible(completeHeader);
        return completeHeader.getText();
    }

    /** Indica si la página de pedido completado está visible (alineado con Playwright isOrderComplete). */
    public boolean isOrderComplete() {
        if (!driver.getCurrentUrl().contains("checkout-complete")) return false;
        try {
            waitForVisible(completeHeader);
            return completeHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitleText() {
        waitForVisible(confirmationHeader);
        return confirmationHeader.getText();
    }
}
