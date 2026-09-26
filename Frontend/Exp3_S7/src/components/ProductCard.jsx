/**
 * Componente que muestra una tarjeta individual de producto
 * @param {Object} props - Propiedades del componente  
 * @param {Object} props.producto - Datos del producto
 * @param {Function} props.onAgregar - Callback para agregar al carrito
 */
function ProductCard({ producto, onAgregar }) {
    return (
        <div className="col-12 col-md-6 col-lg-3">
            <div className="card h-100 shadow-sm interactivo" style={{ transition: 'transform 0.3s ease' }}
                onMouseOver={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                onMouseOut={(e) => e.currentTarget.style.transform = 'scale(1)'}>

                <img src={producto.imagen} className="card-img-top" alt={producto.titulo} />

                <div className="card-body d-flex flex-column text-center">
                    <h3 className="card-title h5" style={{ color: '#0f3460' }}>{producto.titulo}</h3>
                    <span className="badge bg-secondary mb-2 align-self-center py-1 px-3">{producto.categoria}</span>
                    <p className="card-text text-muted flex-grow-1">{producto.descripcion}</p>

                    {/* RENDERIZADO CONDICIONAL: mostrar oferta si existe */}
                    {producto.precioOferta ? (
                        <div className="mt-2 mb-3">
                            <span className="text-muted text-decoration-line-through me-2">
                                ${producto.precio.toLocaleString('es-CL')}
                            </span>
                            <span className="fw-bold fs-5 text-danger">
                                ${producto.precioOferta.toLocaleString('es-CL')}
                            </span>
                        </div>
                    ) : (
                        <p className="fw-bold fs-5 text-success mt-2 mb-3">
                            ${producto.precio.toLocaleString('es-CL')}
                        </p>
                    )}

                    <button className="btn w-100 text-white mt-auto" style={{ backgroundColor: '#e94560' }} onClick={() => onAgregar(producto)}>
                        Agregar al Carrito
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ProductCard
