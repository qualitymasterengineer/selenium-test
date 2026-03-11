# SauceDemo - Automatización E2E con Selenium

Proyecto de automatización E2E para [SauceDemo](https://www.saucedemo.com/) usando **Selenium 4 (WebDriver)**, **Java**, **JUnit 5** y **Page Object Model (POM)**.

## Requisitos

- **JDK 11** o superior (Selenium 4 requiere Java 11+)
- **Maven 3.6+**
- **Chrome** (para ejecutar tests con ChromeDriver; Selenium Manager descarga el driver automáticamente)

**Si `mvn -version` muestra un JDK menor a 11** (por ejemplo Java 8), Maven está usando otro JDK. Configura `JAVA_HOME` apuntando a un JDK 11+ antes de ejecutar Maven. En PowerShell, para usar el mismo `java` que ves con `java -version`:

```powershell
$javaPath = (Get-Command java).Source
$env:JAVA_HOME = (Get-Item $javaPath).Directory.Parent.FullName
mvn -version   # debe mostrar Java 11 o superior
```

## Instalación

1. Clonar el repositorio (o abrir el proyecto).
2. En la raíz del proyecto:

```bash
mvn clean compile test-compile
```

No es necesario instalar ChromeDriver manualmente; Selenium 4 usa Selenium Manager para gestionar drivers.

## Variables de entorno (opcional)

Se pueden sobreescribir base URL y credenciales por defecto:

| Variable           | Descripción                    | Por defecto           |
|--------------------|--------------------------------|------------------------|
| `BASE_URL`         | URL base de la aplicación      | `https://www.saucedemo.com` |
| `DEFAULT_USERNAME` | Usuario por defecto            | `standard_user`        |
| `DEFAULT_PASSWORD` | Contraseña por defecto         | `secret_sauce`         |

Ejemplo en Windows (PowerShell):

```powershell
$env:BASE_URL="https://www.saucedemo.com"
$env:DEFAULT_USERNAME="standard_user"
$env:DEFAULT_PASSWORD="secret_sauce"
mvn test
```

En Linux/macOS:

```bash
export BASE_URL=https://www.saucedemo.com
export DEFAULT_USERNAME=standard_user
export DEFAULT_PASSWORD=secret_sauce
mvn test
```

También se puede editar `src/test/resources/config.properties`.

## Ejecutar tests

- **Todos los tests (excluyendo fallos conocidos):**

```bash
mvn test
```

- **Incluir también los tests marcados como fallo conocido** (pueden fallar):

```bash
mvn test -DexcludedGroups= -Dgroups=knownFailure
```

O ejecutar todo sin excluir grupos:

```bash
mvn test -DexcludedGroups=
```

- **Una clase de test concreta:**

```bash
mvn test -Dtest=CheckoutPricesAndTotalsTest
```

## Reporte Allure

La configuración detallada (Environment, Executors, Categories, Trend, fallos conocidos) está en **[ALLURE_CONFIG.md](ALLURE_CONFIG.md)**.

### Flujo recomendado (con preparación tipo Playwright)

Se usan scripts Node en `scripts/` para rellenar Environment, Executors, Categories, history y parches. Requiere **Node.js** instalado.

```powershell
mvn test -Dheadless=true
npm run report:allure:generate
npm run report:allure:open
```

- **`report:allure:generate`**: ejecuta la preparación (history, env, executor, categories, patches) y luego `mvn allure:report`.
- **`report:allure:open`**: levanta un servidor HTTP en el reporte; abre **http://localhost:8080** en el navegador.

**Ver el reporte sin 404:** No abras `index.html` con doble clic (`file://`). Usa siempre `npm run report:allure:open` (o `npx http-server target/site/allure-maven-plugin -p 8080`) y entra a **http://localhost:8080**.

### Flujo mínimo (solo Maven)

```powershell
mvn test -Dheadless=true
mvn allure:report
npx http-server target/site/allure-maven-plugin -p 8080
```

Luego abre **http://localhost:8080**. Sin los scripts de preparación no tendrás Environment, Executors ni Categories completos ni parches de known failures / passed.

## Estructura del proyecto

```
scripts/              # Preparación Allure (env, executor, categories, history, patches)
src/test/
├── java/com/saucedemo/
│   ├── config/     # Configuración (URL, credenciales, timeouts)
│   ├── data/       # Datos de prueba (usuarios, checkout)
│   ├── pages/      # Page Objects (Login, Inventory, Cart, Checkout, etc.)
│   ├── tests/      # Casos de prueba
│   └── utils/      # Utilidades (PriceUtils, etc.)
└── resources/
    ├── config.properties
    ├── allure.properties
    └── allure/categories.json   # Plantilla Categories
```

## Casos de prueba implementados

1. **Validar precios y totales** — Login, agregar 2 productos, carrito, checkout, verificar subtotal/total/impuesto y confirmación.
2. **Validar estado del carrito vacío** — Login y verificar badge en 0.
3. **Validar usuario bloqueado** — `locked_out_user` no puede iniciar sesión; mensaje de error y sin redirección.
4. **Validar login inválido** — Credenciales incorrectas; mensaje de error visible y permanencia en login.
5. **Validar descripción de producto** — Título en detalle coincide con el de la lista (`standard_user`).
6. **Validar descripción de producto (problem_user)** — Mismo flujo con `problem_user`; **marcado como fallo conocido**.
7. **Validar tiempo de inicio de sesión** — `standard_user` en menos de 2 segundos.
8. **Validar tiempo de inicio de sesión (performance_glitch_user)** — Misma medición con `performance_glitch_user`; **marcado como fallo conocido**.

Los tests 6 y 8 están etiquetados con `@Tag("knownFailure")` y se excluyen por defecto para no fallar el build; en CI se pueden ejecutar con `continue-on-error`.

## Convenciones

Ver [PROJECT_RULES.md](PROJECT_RULES.md) para convenciones de código, POM, pasos en español y manejo de fallos conocidos.

## CI/CD

El workflow `.github/workflows/selenium.yml`:

1. Ejecuta los tests excluyendo `knownFailure`.
2. Ejecuta los tests de fallo conocido con `continue-on-error`.
3. Genera el reporte Allure y sube resultados e informe como artefactos.
