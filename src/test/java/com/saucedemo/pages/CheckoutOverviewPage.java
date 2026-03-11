package com.saucedemo.pages;

import com.saucedemo.utils.PriceUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for checkout overview (step two). No assertions inside.
 */
public class CheckoutOverviewPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".summary_subtotal_label")
    private WebElement subtotalLabel;

    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    @FindBy(id = "finish")
    private WebElement finishButton;

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getPageTitleText() {
        waitForVisible(pageTitle);
        return pageTitle.getText();
    }

    /** Subtotal from summary (e.g. "Item total: $49.98"). */
    public double getSubtotal() {
        waitForVisible(subtotalLabel);
        String text = subtotalLabel.getText();
        return PriceUtils.parseAndRound(extractPrice(text));
    }

    /** Tax from summary (e.g. "Tax: $4.00"). */
    public double getTax() {
        waitForVisible(taxLabel);
        String text = taxLabel.getText();
        return PriceUtils.parseAndRound(extractPrice(text));
    }

    /** Total from summary (e.g. "Total: $53.98"). */
    public double getTotal() {
        waitForVisible(totalLabel);
        String text = totalLabel.getText();
        return PriceUtils.parseAndRound(extractPrice(text));
    }

    private static String extractPrice(String text) {
        if (text == null) return "0";
        int idx = text.indexOf('$');
        return idx >= 0 ? text.substring(idx + 1).trim() : text.trim();
    }

    public void clickFinish() {
        waitForClickable(finishButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", finishButton);
        finishButton.click();
    }
}
