package com.saucedemo.tests;

import com.saucedemo.config.Config;
import com.saucedemo.data.UserCredentials;
import com.saucedemo.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Known failure: SauceDemo performance_glitch_user simulates delays, login may exceed 2 seconds.
 * This test runs in CI with continue-on-error so it does not fail the build.
 */
@Epic("Login")
@Feature("Rendimiento (performance_glitch_user)")
@Tag("knownFailure")
@DisplayName("Validar tiempo de inicio de sesión - performance_glitch_user en menos de 2 segundos [FALLO CONOCIDO]")
class LoginPerformanceGlitchUserTest extends BaseTest {

    private static final long MAX_LOGIN_TIME_MS = 2000;

    @Test
    @Story("Login de performance_glitch_user en menos de 2 s - expected to fail (glitch delays)")
    void performanceGlitchUserLoginUnderTwoSeconds() {
        LoginPage loginPage = new LoginPage(driver);
        UserCredentials user = UserCredentials.performanceGlitchUser(Config.getDefaultPassword());

        loginPage.navigateToBaseUrl();
        loginPage.fillUsername(user.getUsername());
        loginPage.fillPassword(user.getPassword());

        long start = System.currentTimeMillis();
        io.qameta.allure.Allure.step("Medir tiempo desde el clic de login hasta que se carga la página de inventario (URL con inventory.html)", () -> {
            loginPage.clickLogin();
            long deadline = start + 30_000;
            while (System.currentTimeMillis() < deadline && !driver.getCurrentUrl().contains("inventory.html")) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    fail("Interrupted");
                }
            }
        });
        long elapsed = System.currentTimeMillis() - start;

        io.qameta.allure.Allure.step("Verificar que el tiempo es menor a 2000 ms", () -> {
            assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Debe cargar inventario");
            assertTrue(elapsed < MAX_LOGIN_TIME_MS, "Tiempo de login debe ser < 2000 ms, fue: " + elapsed + " ms");
        });
    }
}
