import React, { useState, useEffect, useMemo } from 'react'
import ProductCard from './ProductCard'

/**
 * Componente que muestra el listado completo de productos
 * Carga datos desde juegos.json y permite filtrar
 */
/**
 * Componente que muestra el listado completo de productos interactivamente
 * Gestiona peticiones a la API simulada, filtros y paginación
 */
function ProductList({ onAgregarAlCarrito, filtroCategoria, terminoBusqueda, carrito }) {
    const [productos, setProductos] = useState([])
    const [error, setError] = useState(null)
    const [mostrarTodos, setMostrarTodos] = useState(false)
    const [cargando, setCargando] = useState(true)

    // Cargar productos de forma asíncrona (Simulación Fetch ComponentMount)
    useEffect(() => {
        fetch('./juegos.json')
            .then(res => {
                if (!res.ok) throw new Error('Error de red')
                return res.json()
            })
            .then(data => setProductos(data))
            .catch(err => setError(err.message))
            .finally(() => setCargando(false))
    }, [])

    // Resetea el paginado (volver a limitar a 8) cada vez que el criterio cambia
    useEffect(() => {
        setMostrarTodos(false);
    }, [filtroCategoria, terminoBusqueda]);

    // OPTIMIZACIÓN (useMemo): Solo re-calcular filtros si cambia la lista o las vars de búsqueda
    const productosFiltrados = React.useMemo(() => {
        return productos.filter(p => {
            const coincideCategoria = !filtroCategoria || filtroCategoria === 'Todos' || p.categoria === filtroCategoria
            const coincideBusqueda = !terminoBusqueda || p.titulo.toLowerCase().includes(terminoBusqueda.toLowerCase())
            return coincideCategoria && coincideBusqueda
        })
    }, [productos, filtroCategoria, terminoBusqueda])

    const limiteMostrar = 8
    const productosPaginados = mostrarTodos ? productosFiltrados : productosFiltrados.slice(0, limiteMostrar)

    // RENDERIZADO CONDICIONAL 1: Cargando datos
    if (cargando) return (
        <div className="text-center my-5 py-5">
            <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
                <span className="visually-hidden">Cargando catálogo...</span>
            </div>
            <p className="mt-3 fs-5 text-muted">Desplegando la biblioteca...</p>
        </div>
    )

    // RENDERIZADO CONDICIONAL 2: Errores (Try Catch en el Fetch / Status code fail)
    if (error) {
        return <div className="alert alert-danger text-center shadow-sm">Error al cargar catálogo: {error}</div>
    }

    // Identificador dinámico de categoría
    const tituloSeccion = filtroCategoria && filtroCategoria !== 'Todos' ? `Juegos de ${filtroCategoria}` : "Juegos Destacados"

    return (
        <section id="productos" className="mt-4 mb-5">
            <h2 className="text-center mb-4 text-dark fw-bold">{tituloSeccion}</h2>

            {/* RENDERIZADO CONDICIONAL: sin resultados */}
            {productosFiltrados.length === 0 ? (
                <div className="alert alert-warning text-center">
                    No se encontraron juegos con ese criterio. Intenta con otras palabras.
                </div>
            ) : (
                <>
                    <div className="row g-4">
                        {productosPaginados.map(producto => {
                            const estaEnCarrito = carrito.some(item => item.id === producto.id);

                            return (
                                <ProductCard
                                    key={producto.id}
                                    producto={producto}
                                    onAgregar={onAgregarAlCarrito}
                                    enCarrito={estaEnCarrito}
                                />
                            )
                        })}
                    </div>

                    {/* Botón Ver Más */}
                    {productosFiltrados.length > limiteMostrar && (
                        <div className="text-center mt-5">
                            <button
                                className="btn btn-lg btn-outline-primary px-5"
                                onClick={() => setMostrarTodos(!mostrarTodos)}
                            >
                                {mostrarTodos ? 'Ocultar Juegos ▲' : 'Ver Más Juegos ▼'}
                            </button>
                        </div>
                    )}
                </>
            )}
        </section>
    )
}

export default ProductList
