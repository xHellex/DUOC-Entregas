# Pixel Games Store 🎮

Tienda online de videojuegos desarrollada como **Single Page Application con React + Vite + Bootstrap 5**.
Evaluación Final Transversal (EFT) — Desarrollo Frontend I (PFY2201), Duoc UC.

- 🌐 **Sitio publicado (GitHub Pages):** https://xhellex.github.io/DUOC-Entregas/
- 💻 **Código fuente:** https://github.com/xHellex/DUOC-Entregas/tree/main/Frontend/EFT_S9
- 👤 **Autor:** Felipe Peñaloza

---

## Funcionalidades

| Funcionalidad | Cómo se usa |
|---|---|
| Catálogo de videojuegos | Al abrir el sitio se cargan los juegos desde `public/juegos.json` (se muestra un spinner mientras carga). Cada juego es una tarjeta con imagen, nombre, categoría, descripción y precio. |
| Filtro por categoría | Menú **Categorías** de la barra de navegación. Las opciones se generan a partir de los juegos del catálogo. **Quitar filtro** vuelve a mostrar todo. |
| Búsqueda por título | Campo **Buscar un juego...** de la barra de navegación; filtra mientras escribes. |
| Ver más / Ocultar | Se muestran 8 juegos y el botón despliega el resto. |
| Carrito de compras | **Agregar al carrito** suma el juego (el botón cambia a "En el carrito"). El ícono del carrito muestra la cantidad y abre el panel lateral con total, ahorro por ofertas, **Borrar**, **Vaciar carrito** y **Simular compra**. El carrito se guarda en `localStorage`. |
| Formulario de contacto | Valida nombre (mín. 3 caracteres), email (formato válido) y mensaje (mín. 10 caracteres). Muestra el error bajo cada campo y un mensaje de confirmación al enviar. |
| Modo administrador | Activa el interruptor **Modo administrador** (barra oscura bajo el encabezado): aparece un formulario para **agregar un juego** al catálogo y un botón **Eliminar del catálogo** en cada tarjeta. Los cambios son en memoria: al recargar la página vuelve el catálogo original del JSON. |
| Diseño responsivo | Probado en 360, 414, 768, 1024, 1366 y 1920 px de ancho: 1 a 4 columnas de tarjetas según la pantalla; menú hamburguesa en móvil con el carrito siempre visible. |

## Requisitos

- [Node.js](https://nodejs.org/) **20 o superior** (incluye npm)
- Un navegador actualizado (Chrome, Firefox, Edge o Safari)

## Instalación y ejecución local

```bash
# 1. Clonar el repositorio y entrar a la carpeta del proyecto
git clone https://github.com/xHellex/DUOC-Entregas.git
cd DUOC-Entregas/Frontend/EFT_S9
#    (si usas el .zip de la entrega: descomprímelo y entra a la carpeta EFT_S9)

# 2. Instalar dependencias
npm install

# 3. Levantar el servidor de desarrollo
npm run dev
```

Abre la dirección que muestra la terminal (normalmente http://localhost:5173).

### Otros comandos

| Comando | Qué hace |
|---|---|
| `npm run build` | Genera la versión de producción en `dist/` |
| `npm run preview` | Sirve localmente la carpeta `dist/` para revisarla |
| `npm run lint` | Revisa el código con ESLint |

## Estructura del proyecto

```
EFT_S9/
├── index.html                 # HTML base (lang="es", metadatos, Bootstrap Icons)
├── public/
│   ├── juegos.json            # Datos de los videojuegos (simula una API)
│   ├── favicon.svg
│   └── img/sin-imagen.svg     # Imagen de respaldo si una portada no carga
└── src/
    ├── main.jsx               # Punto de entrada: monta <App /> e importa Bootstrap
    ├── App.jsx                # Componente raíz: estado global y funciones
    ├── App.css                # Variables CSS, CSS Grid del footer y estilos propios
    ├── utils/
    │   └── validaciones.js    # Validaciones reutilizadas por ambos formularios
    └── components/
        ├── Header.jsx         # Encabezado <header>
        ├── Navbar.jsx         # Navegación, categorías, búsqueda y botón del carrito
        ├── Carousel.jsx       # Carrusel de Bootstrap generado desde un arreglo
        ├── ProductList.jsx    # Filtra y lista los juegos (useMemo)
        ├── ProductCard.jsx    # Tarjeta de un juego
        ├── AdminForm.jsx      # Formulario para agregar juegos al catálogo
        ├── Cart.jsx           # Panel lateral del carrito
        ├── ContactForm.jsx    # Formulario de contacto con validación
        └── Footer.jsx         # Pie de página <footer>
```

## Cómo se cumplen los requerimientos

**HTML, CSS y Bootstrap 5**
- Etiquetas semánticas: `<header>`, `<nav>`, `<main>`, `<section>`, `<article>` (cada tarjeta) y `<footer>`.
- Flexbox: grilla de Bootstrap (`row`/`col-*`) y utilidades `d-flex` en la navegación, las tarjetas y el carrito.
- CSS Grid: columnas del pie de página (`.footer-grid` en `App.css`).
- Componentes de Bootstrap: Navbar, Dropdown, Carousel, Cards, Offcanvas, Toast, Forms con validación, Alerts, Badges y Spinner.
- CSS propio con variables (`--primary-dark`, `--accent`, etc.) en `App.css`.

**JavaScript**
- Los datos de cada juego (nombre, categoría, precio, descripción, imagen) están en `juegos.json` y se cargan con `fetch`.
- Las tarjetas se generan dinámicamente con `.map()`; el filtrado usa `.filter()` y los totales del carrito `.reduce()`.
- Validación de formularios con funciones propias (`src/utils/validaciones.js`) y expresiones regulares.

**React**
- Proyecto dividido en 9 componentes funcionales.
- **State lifting:** `App.jsx` guarda el catálogo (`productos`), el carrito, la categoría y la búsqueda, y los entrega por **props**. Por ejemplo, `Navbar` cambia la categoría y `ProductList` muestra el resultado.
- **Agregar y eliminar del catálogo:** `setProductos(prev => [nuevo, ...prev])` y `setProductos(prev => prev.filter(...))`. Siempre se actualiza con funciones, sin mutar el arreglo.
- **Hooks:** `useState`, `useEffect` (carga del JSON, guardado en localStorage, cierre del toast, tecla Escape) y `useMemo` (filtrado y categorías).
- **Renderizado condicional:** spinner de carga, error, "sin resultados", carrito vacío, botón "En el carrito", precio de oferta, panel admin y mensajes de los formularios.

## Despliegue

El sitio se publica en GitHub Pages con GitHub Actions (`.github/workflows/deploy.yml` en la raíz del repositorio). Cada `push` a `main` que modifica `Frontend/EFT_S9/` instala dependencias (`npm ci`), compila (`npm run build`) y publica la carpeta `dist/`. `vite.config.js` usa `base: './'` para que las rutas funcionen dentro de `/DUOC-Entregas/`.

## Capturas

Las capturas de pantalla del sitio están en la carpeta [`capturas/`](./capturas).

## Video de presentación

El video (.mp4) con el recorrido del sitio y la explicación del código se entrega junto a este proyecto en la plataforma AVA.

## Tecnologías

React 18.3 · Vite 5 · Bootstrap 5.3 · Bootstrap Icons · JavaScript (ES6+) · HTML5 · CSS3
