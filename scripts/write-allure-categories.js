/**
 * Escribe allure-results/categories.json (sección Categories).
 * Alineado con Playwright: Known failures, Test defects, Product defects, Passed, Skipped, Unknown.
 */
const fs = require('fs');
const path = require('path');

const projectRoot = path.resolve(__dirname, '..');
const resultsDir = path.join(projectRoot, 'target', 'allure-results');
if (!fs.existsSync(resultsDir)) fs.mkdirSync(resultsDir, { recursive: true });

const categories = [
  { name: 'Known failures', matchedStatuses: ['broken'], messageRegex: '.*Known failure.*' },
  { name: 'Test defects', matchedStatuses: ['broken'], messageRegex: '^(?!Known failure:).*' },
  { name: 'Product defects', matchedStatuses: ['failed'] },
  { name: 'Passed', matchedStatuses: ['passed'] },
  { name: 'Skipped', matchedStatuses: ['skipped'] },
  { name: 'Unknown', matchedStatuses: ['unknown'] },
];

fs.writeFileSync(path.join(resultsDir, 'categories.json'), JSON.stringify(categories, null, 2), 'utf8');
console.log('write-allure-categories: categories.json written');
