# Pixel Games Store - Integración Final (Entrega Semana 8)

Este proyecto corresponde a la fase final de la tienda de videojuegos "Pixel Games Store", migrada a una **Single Page Application (SPA) construida con React y Vite**. 

Esta actividad consolida el desarrollo con React (implementando Hooks, Eventos, Renderizado Condicional y Props), demostrando la creación de componentes dinámicos e interactivos que cumplen íntegramente con los requisitos de la Semana 8.

## 📁 Arquitectura del Proyecto

- **Archivo de Entrada Principal:** `/index.html` (orquestado a través de `/src/main.jsx`).
- **Ubicación del JSON de Datos:** `/public/juegos.json` permite que el componente `ProductList` realice el método `fetch()` simulando una petición asíncrona a un backend.
- **Componente Contenedor Global:** `/src/App.jsx` concentra el árbol de componentes (Navbar, Carousel, ProductList, Cart y Footer) y distribuye el estado a través de sus Props.

## 🔗 Enlaces y Rutas de Despliegue Oficiales (Criterio 5)

- 🌐 **Aplicación de Producción PWA (GitHub Pages):** [https://xHellex.github.io/DUOC-Entregas/](https://xHellex.github.io/DUOC-Entregas/)
- 💻 **Código Fuente (Repositorio Exp3_S8):** [Repositorio en GitHub](https://github.com/xHellex/DUOC-Entregas/tree/main/Frontend/Exp3_S8)

## 📸 Capturas de Evidencia (Criterio Rúbrica)
Las evidencias solicitadas por la rúbrica (Carga de datos, agregar/eliminar carrito, y renderizado condicional de botones) se ubican físicamente dentro del directorio `capturas/` en la raíz de esta entrega (`Exp3_S8`).

## 🚀 Funcionalidades Principales implementadas

1. **Catálogo Reactivo Dinámico (`useEffect` & `fetch`):** Los datos se obtienen de manera asíncrona validando errores de red en el proceso.
2. **Filtrado Bidireccional y Búsqueda en Tiempo Real (`useState`):** Las funciones de búsqueda y filtrado de categorías operan en tiempo real solucionando cualquier desincronización del DOM.
3. **Renderizado Condicional (Criterio Semana 8):** El botón de compra evalúa reactivamente si el artículo ya está en el carrito, bloqueando duplicados de compra y cambiando su aspecto visual de "Agregar al Carrito" a un botón gris inhabilitado "En el carrito".
4. **Persistencia mediante LocalStorage (Extra de Calidad):** El carrito asegura su estado en el disco global; los artículos seleccionados no se perderán al hacer refresh de la pestaña.
5. **Cálculos Estructurales y Feedback Visual (Extra de Calidad):**
    - Despliegue de **"Notificaciones Toast"** informando el agregado exitoso.
    - Botón para **Vaciar Carrito** implementado en la vista global.
    - Utilización de array functions avanzadas (como `.reduce()`) en `<Cart />` para sumar automáticamente el total e identificar el **descuento o ahorro neto** logrado validando ofertas.

## 🛠️ Instrucciones de Ejecución mediante Servidor Local (Para Revisor)

1. Accede a la ruta de la carpeta de la entrega: `cd Frontend/Exp3_S8`.
2. Instala las dependencias críticas de React del proyecto:
   ```bash
   npm install
   ```
3. Ejecuta el servidor de desarrollo en caliente Vite:
   ```bash
   npm run dev
   ```
4. Abre la URL en tu navegador que entregará la consola (típicamente `http://localhost:5173`).

## 🌐 Configuración de Despliegue Público (CI/CD)

El proyecto cuenta con un flujo CI/CD configurado de extremo a extremo mediante **GitHub Actions** (`.github/workflows/deploy.yml`). Al ejecutar un `git push` hacia la rama principal, el servidor de GitHub compila automáticamente esta aplicación en Vite y publica su carpeta `dist` estática montándola nativamente en **GitHub Pages**.
