# Pixel Games Store - Migración a React (Entrega Semana 7)

Este proyecto corresponde a la evolución de la tienda de videojuegos "Pixel Games Store", migrada de HTML/Vanilla JS tradicional a una **Single Page Application (SPA) construida con React y Vite**. 

Esta actividad consolida el aprendizaje de interfaces adaptativas con Bootstrap 5, pero ahora bajo un entorno de trabajo basado en componentes.

## 📁 Arquitectura del Proyecto

- **Archivo de Entrada Principal:** `/index.html` (orquestado a través de `/src/main.jsx`).
- **Ubicación del JSON de Datos:** `/public/juegos.json`. (Se mantiene en la carpeta pública nativa de Vite para permitir que el componente `ProductList` realice el método `fetch()` simulando una petición a una API externa).
- **Componente Contenedor Global:** `/src/App.jsx` concentra el árbol de componentes (Navbar, Carousel, ProductList, Cart y Footer).

## 🚀 Funcionalidades Principales implementadas

1. **Catálogo Reactivo Dinámico:** Los datos se obtienen vía asíncrona de `juegos.json`.
2. **Filtrado Bidireccional y Búsqueda en Tiempo Real:** Las funciones de búsqueda y filtrado de categorías operan en tiempo real usando el estado (`useState`), lo cual soluciona problemas de limpieza de DOM detectados en las tecnologías anteriores (Vanilla JS).
3. **Paginación 'Ver Más':** Se desarrolló un limitador de estado automatizado para mostrar de forma predeterminada 8 productos interactivos.
4. **Carrito de Compras Aislado (Offcanvas):** Gestionado como un arreglo de React (`carrito`), inyectable en `<Cart />`. Recalcula instantáneamente totales (identificando dinámicamente si el producto usa el `precioOferta` o precio regular) y se renderiza en un sistema deslizable de Bootstrap Offcanvas.

## 🛠️ Instrucciones de Ejecución mediante Servidor Local (Para Revisor)

Como el proyecto ahora utiliza React, no puede abrirse simplemente ejecutando el archivo `.html` en un navegador (levantaría un error de CORS para el módulo). Para revisarlo, sigue estos 3 sencillos pasos:

1. Accede a la ruta de la carpeta (Ej. `cd Frontend/Exp3_S7`).
2. Instala las dependencias del proyecto:
   ```bash
   npm install
   ```
3. Ejecuta el servidor de desarrollo local de Vite:
   ```bash
   npm run dev
   ```
4. Abre la URL en tu navegador que entregará la consola (típicamente `http://localhost:5173`).

## 🌐 Configuración de Despliegue Público

El proyecto está preparado para ejecutarse y publicarse automáticamente a Github Pages, para esto el archivo `package.json` incluye los scripts empaquetadores integrados nativamente gracias al paquete `gh-pages` instalado en desarrollo.
Comando rápido de Build y Despliegue (Rama `gh-pages`):
```bash
npm run deploy
```

> **Nota de Mejora (Refactorización del Estado):** De acuerdo a las recomendaciones anteriores acerca de *"extraer funciones comunes para aplicar la visibilidad y restablecer el catálogo (...) para que cada interacción no deje un estado diferente"* — la migración a la filosofía React de estado único resuelve este problema desde su núcleo; cualquier alteración sobre `terminoBusqueda` o `filtroCategoria` ocasiona la re-renderización automática de la grilla de juegos previniendo datos desincronizados del DOM o colisiones de búsquedas repetitivas.
