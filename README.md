# 🎴 SwipeShot

**Limpiador de fotos estilo Tinder para Android**
Desliza para conservar o borrar. Recupera fotos eliminadas hasta 7 días después.

---

## 📲 Descargar APK (sin compilar)

1. Ve a la pestaña **[Releases](../../releases)** de este repositorio
2. Descarga el último `.apk`
3. En Android: **Ajustes → Seguridad → Instalar apps desconocidas** ✓
4. Abre el APK e instala

> El APK se compila automáticamente con GitHub Actions en cada `git push`.

---

## 🚀 Subir a GitHub desde Termux (paso a paso)

### Primera vez — configurar Git

```bash
# Instalar git
pkg install git

# Tu identidad (usa el mismo email que en GitHub)
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

### Subir el proyecto

```bash
cd swipeshot-apk

# Inicializar repo
git init
git config --global --add safe.directory $(pwd)

# Agregar archivos
git add .
git commit -m "🎴 SwipeShot inicial"

# Conectar con GitHub (cambia TU_USUARIO)
git branch -M main
git remote add origin https://github.com/TU_USUARIO/swipeshot.git
git push -u origin main
```

Cuando pida contraseña → usa tu **Personal Access Token** de GitHub
(GitHub → Settings → Developer settings → Tokens classic → Generate new token → marcar `repo`)

### Ver el APK compilándose

1. Ve a tu repo en GitHub
2. Clic en **Actions** (menú superior)
3. Verás el workflow corriendo (~5-8 minutos)
4. Al terminar → clic en **Releases** → descarga el APK

---

## 🎮 Cómo usar la app

| Gesto | Acción |
|-------|--------|
| Deslizar ➡️ | 💚 Conservar foto |
| Deslizar ⬅️ | 🗑 Mover a papelera |
| Deslizar ⬆️ | ⏭ Saltar para después |
| Botón ↩️ | Deshacer última acción |

**Papelera:** Las fotos eliminadas se mueven a una carpeta segura. Puedes recuperarlas desde el botón 🗑️ hasta **7 días** después. Pasado ese tiempo se borran definitivamente.

---

## 🗂️ Estructura del proyecto

```
swipeshot-apk/
├── src/
│   └── index.html          ← Toda la UI (HTML + CSS + JS)
├── .github/
│   └── workflows/
│       └── build.yml       ← GitHub Actions: compila el APK
├── package.json            ← Dependencias Capacitor
├── capacitor.config.json   ← Config de la app Android
├── build.js                ← Copia src/ → www/
└── README.md
```

---

## ⚙️ Cómo funciona la compilación

```
git push → GitHub Actions →
  1. Instala Node.js + Java + Android SDK
  2. npm install (Capacitor)
  3. npm run build (copia index.html → www/)
  4. npx cap sync android (genera proyecto Android)
  5. ./gradlew assembleDebug (compila APK)
  6. Sube APK como Release descargable
```

---

## 🔧 Actualizar la app

```bash
# Editar src/index.html con tus cambios, luego:
git add .
git commit -m "✨ Mejora X"
git push
```
GitHub Actions compilará un nuevo APK automáticamente.

---

## 📝 Licencia

MIT — úsalo, modifícalo y compártelo libremente.
