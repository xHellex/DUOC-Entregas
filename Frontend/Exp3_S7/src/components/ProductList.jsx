import { useState, useEffect } from 'react'
import ProductCard from './ProductCard'

/**
 * Componente que muestra el listado completo de productos
 * Carga datos desde juegos.json y permite filtrar
 */
function ProductList({ onAgregarAlCarrito, filtroCategoria, terminoBusqueda }) {
    const [productos, setProductos] = useState([])
    const [error, setError] = useState(null)
    const [mostrarTodos, setMostrarTodos] = useState(false)

    // Cargar productos al montar el componente
    useEffect(() => {
        fetch('./juegos.json')
            .then(res => {
                if (!res.ok) throw new Error('Error de red')
                return res.json()
            })
            .then(data => setProductos(data))
            .catch(err => setError(err.message))
    }, [])

    // Resetea el paginado cuando hay filtros nuevos activos
    useEffect(() => {
        setMostrarTodos(false);
    }, [filtroCategoria, terminoBusqueda]);

    // Filtrar productos según categoría y búsqueda
    const productosFiltrados = productos.filter(p => {
        const coincideCategoria = !filtroCategoria || filtroCategoria === 'Todos' || p.categoria === filtroCategoria
        const coincideBusqueda = !terminoBusqueda || p.titulo.toLowerCase().includes(terminoBusqueda.toLowerCase())
        return coincideCategoria && coincideBusqueda
    })

    const limiteMostrar = 8
    const productosPaginados = mostrarTodos ? productosFiltrados : productosFiltrados.slice(0, limiteMostrar)

    // RENDERIZADO CONDICIONAL: error
    if (error) {
        return <div className="alert alert-danger text-center shadow-sm">Error al cargar catálogo: {error}</div>
    }

    return (
        <section id="productos" className="mt-5 mb-5">
            <h2 className="text-center mb-4" style={{ color: '#1a1a2e' }}>Juegos Destacados</h2>

            {/* RENDERIZADO CONDICIONAL: sin resultados */}
            {productosFiltrados.length === 0 ? (
                <div className="alert alert-warning text-center">
                    No se encontraron juegos con ese criterio. Intenta con otras palabras.
                </div>
            ) : (
                <>
                    <div className="row g-4">
                        {productosPaginados.map(producto => (
                            <ProductCard
                                key={producto.id}
                                producto={producto}
                                onAgregar={onAgregarAlCarrito}
                            />
                        ))}
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
