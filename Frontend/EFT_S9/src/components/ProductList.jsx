import { useState, useMemo } from 'react'
import ProductCard from './ProductCard'

const LIMITE_INICIAL = 8 // juegos visibles antes de presionar "Ver más"

/**
 * Lista de videojuegos. Recibe el catálogo desde App.jsx por props
 * y genera una ProductCard por cada juego con .map().
 * Aplica el filtro por categoría y la búsqueda por título.
 *
 * @param {Object} props
 * @param {Object[]} props.productos - Catálogo completo (estado de App)
 * @param {boolean} props.cargando - true mientras se lee juegos.json
 * @param {string|null} props.error - Mensaje de error de la carga
 * @param {boolean} props.modoAdmin - Muestra los botones de eliminar
 * @param {Function} props.onAgregarAlCarrito
 * @param {Function} props.onEliminarJuego
 * @param {string} props.filtroCategoria
 * @param {Function} props.onLimpiarFiltro - Vuelve a mostrar todas las categorías
 * @param {string} props.terminoBusqueda
 * @param {Object[]} props.carrito - Para saber qué juegos ya están agregados
 */
function ProductList({ productos, cargando, error, modoAdmin, onAgregarAlCarrito, onEliminarJuego,
    filtroCategoria, onLimpiarFiltro, terminoBusqueda, carrito }) {
    const [mostrarTodos, setMostrarTodos] = useState(false)

    // Optimización: el filtrado solo se recalcula si cambian sus dependencias
    const productosFiltrados = useMemo(() => {
        const termino = terminoBusqueda.trim().toLowerCase()
        return productos.filter(p => {
            const coincideCategoria = filtroCategoria === 'Todos' || p.categoria === filtroCategoria
            const coincideBusqueda = termino === '' || p.titulo.toLowerCase().includes(termino)
            return coincideCategoria && coincideBusqueda
        })
    }, [productos, filtroCategoria, terminoBusqueda])

    // Set con los ids del carrito: consulta rápida para cada tarjeta
    const idsEnCarrito = useMemo(() => new Set(carrito.map(item => item.id)), [carrito])

    const productosVisibles = mostrarTodos ? productosFiltrados : productosFiltrados.slice(0, LIMITE_INICIAL)

    // Renderizado condicional 1: cargando datos
    if (cargando) return (
        <div className="text-center my-5 py-5" role="status">
            <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }} aria-hidden="true"></div>
            <p className="mt-3 fs-5 text-muted">Cargando catálogo...</p>
        </div>
    )

    // Renderizado condicional 2: error al cargar
    if (error) {
        return <div className="alert alert-danger text-center shadow-sm my-5" role="alert">Error al cargar el catálogo: {error}</div>
    }

    const tituloSeccion = filtroCategoria !== 'Todos' ? `Juegos de ${filtroCategoria}` : 'Juegos Destacados'

    return (
        <section id="productos" className="mt-4 mb-5" aria-labelledby="titulo-productos">
            <h2 id="titulo-productos" className="text-center mb-2 fw-bold" style={{ color: 'var(--primary-dark)' }}>
                {modoAdmin && <span className="badge bg-danger me-2 align-middle fs-6">ADMIN</span>}
                {tituloSeccion}
            </h2>
            <p className="text-center text-muted mb-4">
                {productosFiltrados.length} {productosFiltrados.length === 1 ? 'juego encontrado' : 'juegos encontrados'}
                {filtroCategoria !== 'Todos' && (
                    <button type="button" className="btn btn-link btn-sm align-baseline" onClick={onLimpiarFiltro}>
                        Quitar filtro
                    </button>
                )}
            </p>

            {/* Renderizado condicional 3: sin resultados */}
            {productosFiltrados.length === 0 ? (
                <div className="alert alert-warning text-center">
                    No se encontraron juegos con ese criterio. Prueba con otra búsqueda o categoría.
                </div>
            ) : (
                <>
                    <div className="row g-4">
                        {productosVisibles.map(producto => (
                            <ProductCard
                                key={producto.id}
                                producto={producto}
                                onAgregar={onAgregarAlCarrito}
                                enCarrito={idsEnCarrito.has(producto.id)}
                                modoAdmin={modoAdmin}
                                onEliminar={onEliminarJuego}
                            />
                        ))}
                    </div>

                    {/* Botón que alterna su texto según el estado */}
                    {productosFiltrados.length > LIMITE_INICIAL && (
                        <div className="text-center mt-5">
                            <button className="btn btn-lg btn-outline-primary px-5" aria-expanded={mostrarTodos}
                                onClick={() => setMostrarTodos(prev => !prev)}>
                                {mostrarTodos ? 'Ocultar juegos ▲' : `Ver más juegos (${productosFiltrados.length - LIMITE_INICIAL}) ▼`}
                            </button>
                        </div>
                    )}
                </>
            )}
        </section>
    )
}

export default ProductList
