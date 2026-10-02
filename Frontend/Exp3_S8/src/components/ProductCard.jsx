/**
 * Componente que muestra una tarjeta individual de producto
 * @param {Object} props - Propiedades del componente  
 * @param {Object} props.producto - Datos del producto
 * @param {Function} props.onAgregar - Callback para agregar al carrito
 * @param {boolean} props.enCarrito - Indica si el producto ya está en el carrito
 */
function ProductCard({ producto, onAgregar, enCarrito }) {
    return (
        <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
            <div className="card h-100 shadow-sm interactivo">

                {/* Badge de Categoría encima de la imagen */}
                <span className="badge bg-secondary position-absolute top-0 start-0 m-2">
                    {producto.categoria}
                </span>

                <img
                    src={producto.imagen}
                    className="card-img-top object-fit-cover"
                    alt={`Portada de ${producto.titulo}`}
                    style={{ height: '200px' }}
                    loading="lazy"
                    onError={(e) => { e.currentTarget.src = 'https://via.placeholder.com/400x200?text=Cargando...' }}
                />

                <div className="card-body d-flex flex-column">
                    <h5 className="card-title fw-bold text-dark">{producto.titulo}</h5>
                    <p className="card-text text-muted small flex-grow-1">{producto.descripcion}</p>

                    <div className="mt-2 mb-3">
                        {producto.precioOferta ? (
                            <>
                                <del className="text-muted d-block small">${producto.precio.toLocaleString('es-CL')}</del>
                                <span className="fs-5 fw-bold text-success">${producto.precioOferta.toLocaleString('es-CL')}</span>
                            </>
                        ) : (
                            <span className="fs-5 fw-bold text-dark">${producto.precio.toLocaleString('es-CL')}</span>
                        )}
                    </div>

                    {/* Botón dinámico según el requerimiento de la Semana 8 */}
                    {enCarrito ? (
                        <button className="btn w-100 btn-secondary mt-auto" disabled>
                            En el carrito
                        </button>
                    ) : (
                        <button className="btn w-100 text-white mt-auto" style={{ backgroundColor: '#e94560' }} onClick={() => onAgregar(producto)}>
                            Agregar al Carrito
                        </button>
                    )}
                </div>
            </div>
        </div>
    )
}

export default ProductCard
