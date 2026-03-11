package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object for cart. No assertions inside.
 */
public class CartPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(css = "button[data-test='checkout']")
    private WebElement checkoutButton;

    public CartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getPageTitleText() {
        waitForVisible(pageTitle);
        return pageTitle.getText();
    }

    public int getItemCount() {
        return cartItems.size();
    }

    /** Subtotal del carrito: suma de precios de los ítems (cada .cart_item tiene .inventory_item_price). */
    public double getCartSubtotal() {
        double sum = 0;
        for (WebElement item : cartItems) {
            String priceText = item.findElement(By.cssSelector(".inventory_item_price")).getText();
            sum += com.saucedemo.utils.PriceUtils.parseAndRound(priceText);
        }
        return com.saucedemo.utils.PriceUtils.roundToTwoDecimals(sum);
    }

    public void proceedToCheckout() {
        waitForClickable(checkoutButton);
        checkoutButton.click();
    }
}
