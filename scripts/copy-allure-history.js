/**
 * Copia allure-report/history → allure-results/history para Trend y Graphs.
 * Origen: target/site/allure-maven-plugin/history (salida Maven).
 * Destino: target/allure-results/history (entrada para siguiente generación).
 */
const fs = require('fs');
const path = require('path');

const projectRoot = path.resolve(__dirname, '..');
const reportHistory = path.join(projectRoot, 'target', 'site', 'allure-maven-plugin', 'history');
const resultsHistory = path.join(projectRoot, 'target', 'allure-results', 'history');

if (!fs.existsSync(reportHistory)) {
  console.log('copy-allure-history: no previous report history found, skipping');
  process.exit(0);
}

if (!fs.existsSync(resultsHistory)) {
  fs.mkdirSync(resultsHistory, { recursive: true });
}
const files = fs.readdirSync(reportHistory);
for (const file of files) {
  fs.copyFileSync(path.join(reportHistory, file), path.join(resultsHistory, file));
}
console.log('copy-allure-history: copied', files.length, 'history file(s)');
