const fs = require('fs');
const path = require('path');

const wwwDir = path.join(__dirname, 'www');
if (!fs.existsSync(wwwDir)) fs.mkdirSync(wwwDir, { recursive: true });

fs.copyFileSync(
  path.join(__dirname, 'src', 'index.html'),
  path.join(wwwDir, 'index.html')
);

console.log('✔ Built www/index.html');
