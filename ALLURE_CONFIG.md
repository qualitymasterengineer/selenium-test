# Configuración detallada de Allure para mostrar los reportes

Este documento describe la configuración del proyecto Selenium para que Allure genere y muestre los reportes igual que en el proyecto Playwright (Environment, Executors, Categories, Trend, fallos conocidos y mensaje en Passed).

---

## 1. Dependencias

**Maven (`pom.xml`):**

| Dependencia       | Uso |
|-------------------|-----|
| `allure-junit5`   | Reporter: escribe en `target/allure-results/` durante la ejecución (resultados, steps). |

**Node (solo para scripts de preparación):** se usan scripts en `scripts/` que requieren Node.js (para escribir env, executor, categories, history y parches). No hay dependencias npm obligatorias; los scripts usan solo `fs` y `path`.

---

## 2. Reporter en Maven / JUnit 5

Allure se integra por el listener de JUnit 5 (`allure-junit5`). Los resultados se escriben en **`target/allure-results/`** durante `mvn test`. Maven **no** genera el HTML; el reporte HTML se genera con `mvn allure:report` (o con la CLI de Allure).

En `src/test/resources/allure.properties`:

- `allure.results.directory=target/allure-results`

---

## 3. Generación del reporte HTML

El reporte se genera con el plugin de Maven **después** de ejecutar los tests y los scripts de preparación.

### Comando base (solo Maven)

```bash
mvn allure:report
```

- **Entrada:** `target/allure-results/` (resultados + archivos que añaden los scripts).
- **Salida:** `target/site/allure-maven-plugin/`.
- **Recomendado:** ejecutar antes `npm run report:allure:prepare` para rellenar Environment, Executors, Categories, history y parches.

### Orden de pasos en `report:allure:prepare` (y en `report:allure:generate`)

En `package.json`, el script **prepare-allure.js** ejecuta **en este orden**:

1. **copy-allure-history.js** — Copia `target/site/allure-maven-plugin/history` → `target/allure-results/history` para que el nuevo reporte incluya tendencias (Trend, Graphs).
2. **write-allure-env.js** — Escribe `target/allure-results/environment.properties` (Environment).
3. **write-allure-executor.js** — Escribe `target/allure-results/executor.json` (Executors).
4. **write-allure-categories.js** — Escribe `target/allure-results/categories.json` (Categories).
5. **patch-allure-known-failures.js** — Modifica los `*-result.json` de tests de fallo conocido (status → `broken`, mensaje con prefijo `Known failure: `).
6. **patch-allure-passed-message.js** — Asigna mensaje por defecto a tests passed para evitar "Empty" en Categories.

Después se ejecuta **`mvn allure:report`** (en `report:allure:generate`), que genera el HTML en `target/site/allure-maven-plugin/`.

---

## 4. Environment (sección "Environment")

**Archivo generado:** `target/allure-results/environment.properties`  
**Script:** `scripts/write-allure-env.js`

Contenido (clave=valor):

| Clave        | Origen |
|-------------|--------|
| `Java.Version` | Salida de `java -version` |
| `OS`        | `process.platform` (win32, linux, darwin) |
| `OS.Arch`   | `process.arch` (x64, arm64) |
| `Base.URL`  | `process.env.BASE_URL` o `https://www.saucedemo.com` |

La pestaña **Environment** del reporte muestra Java, SO y URL base.

---

## 5. Executors (sección "Executors")

**Archivo generado:** `target/allure-results/executor.json`  
**Script:** `scripts/write-allure-executor.js`

Define **quién** ejecutó los tests y el “build”.

- **Si se ejecuta en GitHub Actions** (`GITHUB_ACTIONS === 'true'`):
  - `name`: `"GitHub Actions"`
  - `type`: `"github"`
  - `buildName`: nombre del workflow (ej. `"Selenium E2E"`)
  - `buildOrder`: `GITHUB_RUN_NUMBER`
  - `buildUrl`: URL del run en GitHub Actions
  - `reportName`: `"Run #<número>"`

- **Si se ejecuta en local**:
  - `name`: `"Selenium Local"`
  - `buildName`: fecha/hora local (ej. `"Local 2025-03-10 19:30:00"`)
  - `buildOrder`: timestamp
  - `reportName`: `"Run #<fecha>"`

La pestaña **Executors** del reporte muestra este nombre y enlace al run (cuando hay `buildUrl`).

---

## 6. Categories (sección "Categories")

**Archivo generado:** `target/allure-results/categories.json`  
**Script:** `scripts/write-allure-categories.js` (y plantilla en `src/test/resources/allure/categories.json`)

Define cómo se agrupan los tests en la pestaña **Categories**:

| Categoría          | Condición | Uso en el reporte |
|--------------------|-----------|---------------------|
| **Known failures** | `status === 'broken'` y mensaje coincide con `.*Known failure.*` | Tests de fallo conocido (naranja). |
| **Test defects**   | `status === 'broken'` y mensaje **no** empieza por "Known failure:" | Otros broken (naranja). |
| **Product defects**| `status === 'failed'` | Fallos de la aplicación (rojo). |
| **Passed**         | `status === 'passed'` | Tests correctos (verde). |
| **Skipped**        | `status === 'skipped'` | Omitidos. |
| **Unknown**        | `status === 'unknown'` | Estado desconocido. |

El orden en el array influye en cómo se muestran. Los patches dejan los resultados alineados con estas reglas.

---

## 7. Fallos conocidos (Known failures)

**Script:** `scripts/patch-allure-known-failures.js`

- **Entrada:** archivos `*-result.json` en `target/allure-results/`.
- **Criterio:** el nombre del test coincide con alguno de estos patrones:
  - `problem_user` y detalle / `ProductDescriptionProblemUser`
  - `performance_glitch` y 2 segundo / `LoginPerformanceGlitchUser`
- **Cambios:**
  - `status` → `'broken'`.
  - `statusDetails.message` → se antepone `"Known failure: "` si no lo tiene. Así la categoría **Known failures** los agrupa.

Debe ejecutarse **antes** de `patch-allure-passed-message.js` y **antes** de `mvn allure:report`.

---

## 8. Mensaje por defecto en tests Passed

**Script:** `scripts/patch-allure-passed-message.js`

- **Entrada:** archivos `*-result.json` en `target/allure-results/`.
- **Condición:** `status === 'passed'` y mensaje vacío o solo espacios.
- **Cambio:** `statusDetails.message` → `"Expected successful result"`.

En la categoría **Passed** se muestra ese texto en lugar de &lt;Empty&gt;.

---

## 9. Trend e historial (Overview y Graphs)

**Script:** `scripts/copy-allure-history.js`

- **Origen:** `target/site/allure-maven-plugin/history` (del reporte generado en la ejecución anterior).
- **Destino:** `target/allure-results/history`.

Allure, al ejecutar `mvn allure:report`, lee `target/allure-results/history` y lo combina con los resultados nuevos para la gráfica **Trend** en Overview y las gráficas en **Graphs**. Si no existe `history` (primera vez), el script no hace nada y las tendencias empiezan desde cero.

---

## 10. Scripts de npm relacionados con Allure

| Script                   | Descripción |
|--------------------------|-------------|
| `report:allure:prepare`  | Solo preparación: history, env, executor, categories y patches; **no** ejecuta `mvn allure:report`. |
| `report:allure:generate` | Ejecuta prepare y luego `mvn allure:report` (genera el HTML). |
| `report:allure:open`     | Levanta `http-server` en `target/site/allure-maven-plugin` (puerto 8080). Abrir **http://localhost:8080** para ver el reporte (evita 404 al hacer clic en un test). |
| `report:allure`         | Ejecuta generate y luego open (genera y abre el servidor). |
| `allure:clean`          | Borra `target/allure-results/` y `target/site/allure-maven-plugin/`. |

---

## 11. Carpetas implicadas

| Carpeta | Contenido | ¿Versionada? |
|---------|-----------|--------------|
| `target/allure-results/` | Salida de JUnit/Allure + archivos generados por los scripts (env, executor, categories, history). Entrada de `mvn allure:report`. | No (en `.gitignore`) |
| `target/site/allure-maven-plugin/` | Reporte HTML generado por `mvn allure:report`. | No (en `.gitignore`) |
| `src/test/resources/allure/` | Plantilla `categories.json` (copiada por Maven en fase test si se desea). | Sí |

---

## 12. Resumen del flujo completo

1. **Ejecutar tests:** `mvn test` → Allure escribe en `target/allure-results/`.
2. **Preparar y generar reporte (recomendado):**
   ```bash
   npm run report:allure:generate
   ```
   - Copiar history (si existe).
   - Escribir `environment.properties`, `executor.json`, `categories.json`.
   - Parchear known failures y mensaje de passed.
   - `mvn allure:report`.
3. **Ver reporte:** `npm run report:allure:open` y abrir **http://localhost:8080** (o usar `report:allure` para generar y abrir en un solo paso).
4. **CI:** El workflow de GitHub Actions puede ejecutar `report:allure:prepare` antes de generar/subir el reporte si se desea Environment, Executors y Categories en los artefactos.

Con esta configuración, Allure muestra correctamente Environment, Executors, Categories, Trend/Graphs y evita "Empty" en Passed y mezcla de known failures con fallos reales, alineado con el proyecto Playwright.
