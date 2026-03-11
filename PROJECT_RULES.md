# Convenciones del proyecto - SauceDemo Selenium E2E

Este documento define las convenciones para que cualquier desarrollador o IA las siga al extender el proyecto.

## Estructura de carpetas

- **`src/test/java/com/saucedemo/pages/`** — Page Objects (una clase por pantalla o flujo relevante). No incluir aserciones ni lógica de negocio fuera de la interacción con la UI.
- **`src/test/java/com/saucedemo/tests/`** — Casos de prueba. Solo usan Page Objects y aserciones; no usan selectores ni locators directamente.
- **`src/test/java/com/saucedemo/data/`** — Datos de prueba (usuarios, checkout) reutilizables.
- **`src/test/java/com/saucedemo/utils/`** — Utilidades (redondeo de precios, helpers).
- **`src/test/java/com/saucedemo/config/`** — Configuración (base URL, credenciales, timeouts).
- **`src/test/resources/`** — `config.properties`, `allure.properties`.

## Page Object Model (POM)

- Cada Page Object encapsula selectores y métodos públicos; **no debe contener aserciones**.
- Los tests obtienen datos y realizan acciones mediante los Page Objects y **afirman en el test**.
- Usar `BasePage` para lógica común (waits, navegación). Los Page Objects extienden `BasePage`.

## Nomenclatura y descripciones

- **Variables y nombres de código:** siempre en **inglés** (clases, métodos, variables, constantes).
- **Pasos de test (Allure / descripciones):** en **español**, con verbos en **infinitivo** (Validar, Navegar, Verificar).
- Ejemplo de paso: *"Verificar que el carrito muestra 2 productos"*, *"Navegar a la página e iniciar sesión"*.

## Datos de prueba

- Usuarios y credenciales: usar clases en `data/` (p. ej. `UserCredentials`, `CheckoutData`) o config.
- No hardcodear credenciales ni datos de checkout en los tests; usar `Config` o clases de datos.

## Utilidades

- Cálculos de precios y redondeo: usar `PriceUtils` (redondeo a 2 decimales para subtotales y totales).

## Tests con fallo conocido

- Tests que se sabe que pueden fallar en el entorno (p. ej. `problem_user`, `performance_glitch_user`) deben marcarse con **`@Tag("knownFailure")`**.
- En la ejecución por defecto (`mvn test`) se excluyen con `excludedGroups=knownFailure` para que el build no falle.
- En CI se pueden ejecutar por separado con `continue-on-error` para que se ejecuten pero no rompan el job.

## Reportes y CI

- Allure: reporte generado en `target/site/allure-maven-plugin/` con `mvn allure:report`.
- Los pasos en español se exponen en Allure mediante `Allure.step("...", () -> { ... })`.

## Configuración

- Base URL y credenciales: `config.properties` o variables de entorno (`BASE_URL`, `DEFAULT_USERNAME`, `DEFAULT_PASSWORD`).
- Timeouts: configurables en `config.properties` y en `Config.java`.

## Añadir nuevos tests o páginas

1. Nuevas pantallas: crear Page Object en `pages/` con selectores encapsulados y métodos sin aserciones.
2. Nuevos casos: crear clase de test en `tests/`, usar solo Page Objects y aserciones; describir pasos en español en infinitivo.
3. Nuevos datos: extender `data/` o `config.properties` según corresponda.
