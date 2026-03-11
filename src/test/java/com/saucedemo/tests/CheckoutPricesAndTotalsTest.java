package com.saucedemo.tests;

import com.saucedemo.config.Config;
import com.saucedemo.data.CheckoutData;
import com.saucedemo.data.UserCredentials;
import com.saucedemo.pages.*;
import com.saucedemo.utils.PriceUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Checkout")
@Feature("Precios y totales")
@DisplayName("Validar precios y totales - Agregar productos al carrito y completar checkout")
class CheckoutPricesAndTotalsTest extends BaseTest {

    @Test
    @Story("Flujo completo de compra con validación de precios")
    void checkoutPricesAndTotals() {
        LoginPage loginPage = new LoginPage(driver);
        UserCredentials user = UserCredentials.standardUser(Config.getDefaultPassword());
        CheckoutData checkoutData = CheckoutData.defaultData();

        Allure.step("Navegar a la página e iniciar sesión", () -> {
            loginPage.navigateToBaseUrl();
            loginPage.login(user.getUsername(), user.getPassword());
        });

        InventoryPage inventoryPage = new InventoryPage(driver);
        Allure.step("Verificar que se muestra la página de productos", () -> {
            assertTrue(driver.getCurrentUrl().contains("inventory.html"), "URL debe contener inventory.html");
            assertTrue(inventoryPage.getPageTitleText().contains("Products"), "Título debe contener Products");
        });

        List<Double> prices = new ArrayList<>();
        Allure.step("Agregar dos productos al carrito", () -> {
            String price0 = inventoryPage.getPriceByIndex(0);
            String price1 = inventoryPage.getPriceByIndex(1);
            prices.add(PriceUtils.parseAndRound(price0));
            prices.add(PriceUtils.parseAndRound(price1));
            inventoryPage.addProductToCartByIndex(0);
            inventoryPage.addProductToCartByIndex(1);
            double expectedSubtotal = PriceUtils.subtotal(prices);
            int badge = inventoryPage.getCartBadgeCount();
            assertEquals(2, badge, "Badge del carrito debe mostrar 2");
        });

        double expectedSubtotal = PriceUtils.subtotal(prices);

        Allure.step("Ir al carrito y validar productos y subtotal", () -> {
            inventoryPage.goToCart();
            CartPage cartPage = new CartPage(driver);
            assertTrue(driver.getCurrentUrl().contains("cart.html"), "URL debe contener cart.html");
            assertEquals("Your Cart", cartPage.getPageTitleText(), "Título debe ser Your Cart");
            assertEquals(2, cartPage.getItemCount(), "Debe haber 2 ítems en el carrito");
            assertEquals(expectedSubtotal, cartPage.getCartSubtotal(), 0.01, "Subtotal del carrito debe coincidir con el esperado");
        });

        CartPage cartPage = new CartPage(driver);
        Allure.step("Proceder al checkout e ingresar datos de envío", () -> {
            cartPage.proceedToCheckout();
            assertTrue(driver.getCurrentUrl().contains("checkout-step-one"), "URL debe contener checkout-step-one antes de rellenar");
            CheckoutPage checkoutPage = new CheckoutPage(driver);
            checkoutPage.fillAndContinue(checkoutData.getFirstName(), checkoutData.getLastName(), checkoutData.getPostalCode());
        });

        CheckoutOverviewPage overviewPage = new CheckoutOverviewPage(driver);
        Allure.step("Validar resumen de precios (subtotal, impuesto y total)", () -> {
            overviewPage.waitForUrlContains("checkout-step-two");
            assertTrue(driver.getCurrentUrl().contains("checkout-step-two"), "URL debe contener checkout-step-two");
            assertEquals("Checkout: Overview", overviewPage.getPageTitleText(), "Título debe ser Checkout: Overview");
            double subtotalShown = overviewPage.getSubtotal();
            double taxShown = overviewPage.getTax();
            double totalShown = overviewPage.getTotal();
            assertEquals(expectedSubtotal, subtotalShown, 0.01, "Subtotal mostrado debe coincidir con el esperado");
            assertEquals(PriceUtils.total(subtotalShown, taxShown), totalShown, 0.01, "Total debe ser subtotal + impuesto redondeado");
            assertTrue(totalShown > 0, "Total debe ser mayor que 0");
            assertTrue(taxShown >= 0, "Impuesto debe ser >= 0");
        });

        Allure.step("Finalizar la compra", () -> {
            overviewPage.clickFinish();
        });

        CheckoutCompletePage completePage = new CheckoutCompletePage(driver);
        Allure.step("Verificar confirmación del pedido", () -> {
            assertTrue(driver.getCurrentUrl().contains("checkout-complete"),
                "URL debe contener checkout-complete. Actual: " + driver.getCurrentUrl());
            assertTrue(completePage.isOrderComplete(), "Pedido debe estar completado");
            String header = completePage.getConfirmationHeaderText();
            assertTrue(header.toLowerCase().contains("thank you"), "Mensaje debe contener thank you");
        });
    }
}
