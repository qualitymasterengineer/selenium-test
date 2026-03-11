package com.saucedemo.tests;

import com.saucedemo.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Login")
@Feature("Credenciales inválidas")
@DisplayName("Validar login inválido - Usuario incorrecto")
class InvalidLoginTest extends BaseTest {

    @Test
    @Story("Credenciales incorrectas muestran error y no redirigen")
    void invalidCredentialsShowError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.navigateToBaseUrl();
        io.qameta.allure.Allure.step("Intentar login con credenciales incorrectas (invalid_user / wrong_password)", () -> {
            loginPage.login("invalid_user", "wrong_password");
        });

        io.qameta.allure.Allure.step("Verificar que se muestra mensaje de error (visible y no vacío) y que se permanece en la página de login", () -> {
            assertTrue(loginPage.isErrorMessageVisible(), "Mensaje de error debe ser visible");
            assertFalse(loginPage.getErrorMessageText().isBlank(), "Mensaje de error no debe estar vacío");
            assertFalse(driver.getCurrentUrl().contains("inventory.html"), "Debe permanecer en login");
        });
    }
}
