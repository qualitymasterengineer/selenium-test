package com.saucedemo.tests;

import com.saucedemo.config.Config;
import com.saucedemo.data.UserCredentials;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Carrito")
@Feature("Estado vacío")
@DisplayName("Validar estado del carrito vacío - No agregar productos")
class CartEmptyStateTest extends BaseTest {

    @Test
    @Story("Carrito vacío tras login")
    void cartEmptyAfterLogin() {
        LoginPage loginPage = new LoginPage(driver);
        UserCredentials user = UserCredentials.standardUser(Config.getDefaultPassword());

        io.qameta.allure.Allure.step("Iniciar sesión en la aplicación", () -> {
            loginPage.navigateToBaseUrl();
            loginPage.login(user.getUsername(), user.getPassword());
        });

        InventoryPage inventoryPage = new InventoryPage(driver);
        io.qameta.allure.Allure.step("Verificar que el carrito está vacío (badge en 0)", () -> {
            loginPage.waitForUrlContains("inventory");
            assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Debe estar en inventario");
            int badgeCount = inventoryPage.getCartBadgeCount();
            assertEquals(0, badgeCount, "Badge del carrito debe ser 0");
        });
    }
}
