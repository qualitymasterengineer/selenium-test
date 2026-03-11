package com.saucedemo.tests;

import com.saucedemo.config.Config;
import com.saucedemo.data.UserCredentials;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.pages.ProductDetailPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Producto")
@Feature("Descripción")
@DisplayName("Validar descripción de producto - El título en detalle coincide con el de la lista")
class ProductDescriptionTest extends BaseTest {

    @Test
    @Story("Título en detalle coincide con lista (standard_user)")
    void productDetailTitleMatchesList() {
        LoginPage loginPage = new LoginPage(driver);
        UserCredentials user = UserCredentials.standardUser(Config.getDefaultPassword());

        loginPage.navigateToBaseUrl();
        loginPage.login(user.getUsername(), user.getPassword());

        InventoryPage inventoryPage = new InventoryPage(driver);
        String productNameFromList = inventoryPage.getProductNameByIndex(0);

        io.qameta.allure.Allure.step("Obtener el nombre del primer producto en la lista (índice 0) y abrir su detalle", () -> {
            inventoryPage.openProductDetailByIndex(0);
        });

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        io.qameta.allure.Allure.step("Verificar que la URL contiene inventory-item.html y que el título del producto en la página de detalle coincide exactamente con el nombre obtenido en la lista", () -> {
            assertTrue(driver.getCurrentUrl().contains("inventory-item.html"), "URL debe contener inventory-item.html");
            assertEquals(productNameFromList, detailPage.getProductTitle(), "Título en detalle debe coincidir con el de la lista");
        });
    }
}
