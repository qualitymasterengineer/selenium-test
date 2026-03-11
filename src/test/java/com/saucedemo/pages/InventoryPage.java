package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for product inventory/list. No assertions inside.
 */
public class InventoryPage extends BasePage {

    @FindBy(css = ".title")
    private WebElement pageTitle;

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;

    public InventoryPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getPageTitleText() {
        waitForVisible(pageTitle);
        return pageTitle.getText();
    }

    /** Obtener precio del producto por índice (0-based). */
    public String getPriceByIndex(int index) {
        WebElement item = inventoryItems.get(index);
        WebElement priceEl = item.findElement(org.openqa.selenium.By.cssSelector(".inventory_item_price"));
        return priceEl.getText();
    }

    /** Agregar producto al carrito por índice (0-based). Selector alineado con Playwright data-test^="add-to-cart". */
    public void addProductToCartByIndex(int index) {
        WebElement item = inventoryItems.get(index);
        WebElement addBtn = item.findElement(org.openqa.selenium.By.cssSelector("button[data-test^='add-to-cart']"));
        waitForClickable(addBtn);
        addBtn.click();
    }

    /**
     * Número del badge del carrito. Si el badge no está visible (carrito vacío), devuelve 0.
     * Alineado con Playwright: if (!badge.isVisible()) return 0.
     */
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty() || !badges.get(0).isDisplayed()) {
            return 0;
        }
        String text = badges.get(0).getText();
        if (text == null || text.isBlank()) return 0;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void goToCart() {
        waitForClickable(cartLink);
        cartLink.click();
    }

    /** Obtener nombre del producto por índice. */
    public String getProductNameByIndex(int index) {
        WebElement item = inventoryItems.get(index);
        WebElement nameEl = item.findElement(org.openqa.selenium.By.cssSelector(".inventory_item_name"));
        return nameEl.getText();
    }

    /** Abrir detalle del producto por índice. */
    public void openProductDetailByIndex(int index) {
        WebElement item = inventoryItems.get(index);
        WebElement nameLink = item.findElement(org.openqa.selenium.By.cssSelector(".inventory_item_name"));
        waitForClickable(nameLink);
        nameLink.click();
    }
}
