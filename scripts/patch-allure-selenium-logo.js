/**
 * Parche para la sección Executors: convivencia de executor nativo (Maven/Jenkins) y Selenium.
 * 1. Añade un segundo ejecutor "Selenium Framework" (type selenium-custom) en widgets/executors.json.
 * 2. Inyecta CSS (.custom-selenium-logo::before) y JS que añade la clase al ítem de Selenium sin borrar el original.
 * Si el parche falla, el executor nativo sigue viéndose; solo se pierde el icono en la línea de Selenium.
 */
const fs = require('fs');
const path = require('path');

const projectRoot = path.resolve(__dirname, '..');
const reportDir = path.join(projectRoot, 'target', 'site', 'allure-maven-plugin');
const indexPath = path.join(reportDir, 'index.html');
const executorsPath = path.join(reportDir, 'widgets', 'executors.json');
const logoSource = path.join(projectRoot, 'assets', 'selenium-logo.svg');
const logoDest = path.join(reportDir, 'selenium-logo.svg');

if (!fs.existsSync(reportDir)) {
  console.log('patch-allure-selenium-logo: no report dir (run mvn allure:report first), skipping');
  process.exit(0);
}
if (!fs.existsSync(indexPath)) {
  console.log('patch-allure-selenium-logo: index.html not found, skipping');
  process.exit(0);
}
if (!fs.existsSync(logoSource)) {
  console.log('patch-allure-selenium-logo: selenium-logo.svg not found, skipping');
  process.exit(0);
}

function getPomValue(pom, tagName) {
  const escaped = tagName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const m = pom.match(new RegExp('<' + escaped + '>([^<]+)<\\/' + escaped + '>'));
  return m ? m[1].trim() : null;
}

const pomPath = path.join(projectRoot, 'pom.xml');
const pom = fs.existsSync(pomPath) ? fs.readFileSync(pomPath, 'utf8') : '';
const projectName = getPomValue(pom, 'name') || 'SauceDemo Selenium E2E';

const seleniumVersion = getPomValue(pom, 'selenium.version');
const seleniumBuildName = seleniumVersion ? `Selenium ${seleniumVersion}` : 'Selenium 4.x';

fs.copyFileSync(logoSource, logoDest);

const isGitHubActions = process.env.GITHUB_ACTIONS === 'true';
const repoUrl =
  process.env.GITHUB_SERVER_URL && process.env.GITHUB_REPOSITORY
    ? `${process.env.GITHUB_SERVER_URL}/${process.env.GITHUB_REPOSITORY}`
    : 'https://github.com/qualitymasterengineer/selenium-test';
const buildUrl =
  process.env.GITHUB_SERVER_URL &&
  process.env.GITHUB_REPOSITORY &&
  process.env.GITHUB_RUN_ID
    ? `${process.env.GITHUB_SERVER_URL}/${process.env.GITHUB_REPOSITORY}/actions/runs/${process.env.GITHUB_RUN_ID}`
    : repoUrl;

const githubActionsExecutor = {
  reportName: 'Allure Report',
  name: 'GitHub Actions',
  type: 'github',
  buildName: projectName,
  buildOrder: parseInt(process.env.GITHUB_RUN_NUMBER || '0', 10),
  reportUrl: repoUrl,
  buildUrl,
};

// 1. Actualizar widgets/executors.json: en CI prepender GitHub Actions; mantener Maven y añadir Selenium Framework
if (fs.existsSync(executorsPath)) {
  let executors = JSON.parse(fs.readFileSync(executorsPath, 'utf8'));
  if (!Array.isArray(executors)) executors = [executors];
  if (isGitHubActions) {
    const hasGh = executors.some(e => e.type === 'github' && e.name === 'GitHub Actions');
    if (!hasGh) executors.unshift(githubActionsExecutor);
  }
  const hasSelenium = executors.some(e => (e.type === 'selenium-custom') || (e.name && e.name.includes('Selenium Framework')));
  if (!hasSelenium) {
    executors.push({
      name: 'Selenium Framework',
      type: 'selenium-custom',
      buildName: seleniumBuildName,
      reportUrl: 'https://www.selenium.dev'
    });
  }
  fs.writeFileSync(executorsPath, JSON.stringify(executors), 'utf8');
}

const customStyle = `
<style id="allure-custom-selenium-style">
.custom-selenium-logo::before {
  content: '';
  display: inline-block;
  width: 20px;
  height: 20px;
  background-image: url('selenium-logo.svg');
  background-size: contain;
  background-repeat: no-repeat;
  margin-right: 5px;
  vertical-align: middle;
}
</style>
`;

const injectScript = `
<script>
(function(){
  function patchSeleniumExecutor() {
    var items = document.querySelectorAll('.executor__item, [class*="executor"] a, #content [class*="executor"]');
    for (var i = 0; i < items.length; i++) {
      var item = items[i];
      var text = (item.innerText || item.textContent || '').trim();
      if ((text.indexOf('selenium-custom') >= 0 || text.indexOf('Selenium Framework') >= 0) && !item.classList.contains('custom-selenium-logo')) {
        item.classList.add('custom-selenium-logo');
        var nativeIcon = item.querySelector('.executor-icon, [class*="executor-icon"]');
        if (nativeIcon) nativeIcon.style.display = 'none';
        if (item.innerHTML && item.innerHTML.indexOf('selenium-custom') >= 0) {
          item.innerHTML = item.innerHTML.replace(/selenium-custom/g, 'Selenium Automation');
        }
      }
    }
  }
  function runPatch() {
    patchSeleniumExecutor();
  }
  var patchTimer = null;
  function schedulePatch() {
    if (patchTimer) clearTimeout(patchTimer);
    patchTimer = setTimeout(runPatch, 150);
  }
  [500, 1200, 2500, 4000].forEach(function(ms) { setTimeout(runPatch, ms); });
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
      [300, 1000, 2000].forEach(function(ms) { setTimeout(runPatch, ms); });
      var body = document.body;
      if (body) {
        var obs = new MutationObserver(function() { schedulePatch(); });
        obs.observe(body, { childList: true, subtree: true });
      }
    });
  } else {
    var body = document.body;
    if (body) {
      var obs = new MutationObserver(function() { schedulePatch(); });
      obs.observe(body, { childList: true, subtree: true });
    }
  }
})();
</script>
`;

let html = fs.readFileSync(indexPath, 'utf8');
if (html.indexOf('custom-selenium-logo') >= 0) {
  console.log('patch-allure-selenium-logo: already patched');
} else {
  html = html.replace('</head>', customStyle + '\n</head>');
  html = html.replace('</body>', injectScript + '\n</body>');
  fs.writeFileSync(indexPath, html, 'utf8');
  console.log('patch-allure-selenium-logo: executors + logo patched');
}
process.exit(0);
