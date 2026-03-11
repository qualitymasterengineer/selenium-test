package com.saucedemo.tests;

import com.saucedemo.config.Config;
import com.saucedemo.data.UserCredentials;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Login")
@Feature("Usuario bloqueado")
@DisplayName("Validar usuario bloqueado - locked_out_user no puede iniciar sesión")
class LockedOutUserTest extends BaseTest {

    private static final String EXPECTED_ERROR = "Epic sadface: Sorry, this user has been locked out.";

    @Test
    @Story("locked_out_user muestra error y no redirige")
    void lockedOutUserCannotLogin() {
        LoginPage loginPage = new LoginPage(driver);
        UserCredentials user = UserCredentials.lockedOutUser(Config.getDefaultPassword());

        loginPage.navigateToBaseUrl();
        io.qameta.allure.Allure.step("Intentar login con locked_out_user / secret_sauce", () -> {
            loginPage.login(user.getUsername(), user.getPassword());
        });

        io.qameta.allure.Allure.step("Verificar que el mensaje de error visible es exactamente: Epic sadface: Sorry, this user has been locked out.", () -> {
            assertTrue(loginPage.isErrorMessageVisible(), "Mensaje de error debe ser visible");
            assertEquals(EXPECTED_ERROR, loginPage.getErrorMessageText(), "Mensaje de error debe coincidir exactamente");
        });

        io.qameta.allure.Allure.step("Verificar que se permanece en la URL de login (sin redirección a inventario)", () -> {
            assertFalse(driver.getCurrentUrl().contains("inventory.html"), "No debe redirigir a inventario");
            assertTrue(driver.getCurrentUrl().contains("saucedemo.com") && !driver.getCurrentUrl().contains("inventory"), "Debe permanecer en login");
        });
    }
}
