package com.saucedemo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for product detail view. No assertions inside.
 */
public class ProductDetailPage extends BasePage {

    @FindBy(css = ".inventory_details_name.large_size")
    private WebElement productTitle;

    @FindBy(css = ".inventory_details_desc")
    private WebElement productDescription;

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public String getProductTitle() {
        waitForVisible(productTitle);
        return productTitle.getText();
    }

    public String getProductDescription() {
        waitForVisible(productDescription);
        return productDescription.getText();
    }
}
