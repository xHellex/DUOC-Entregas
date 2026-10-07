// Imagen local de respaldo si la URL del juego no carga (incluida en /public/img)
const IMAGEN_RESPALDO = './img/sin-imagen.svg'

/**
 * Tarjeta de un videojuego (componente Card de Bootstrap 5).
 * Muestra imagen, nombre, categoría, descripción y precio (con oferta si existe).
 *
 * @param {Object} props
 * @param {Object} props.producto - Datos del juego
 * @param {Function} props.onAgregar - Agrega el juego al carrito
 * @param {boolean} props.enCarrito - true si el juego ya está en el carrito
 * @param {boolean} props.modoAdmin - Muestra el botón para eliminar del catálogo
 * @param {Function} props.onEliminar - Elimina el juego del catálogo
 */
function ProductCard({ producto, onAgregar, enCarrito, modoAdmin, onEliminar }) {
    const formatoCLP = (valor) => `$${valor.toLocaleString('es-CL')}`

    // Si la imagen falla, usa la de respaldo. La marca data-respaldo evita
    // un bucle infinito si la imagen de respaldo también fallara.
    const manejarErrorImagen = (e) => {
        const img = e.currentTarget
        if (img.dataset.respaldo) return
        img.dataset.respaldo = 'true'
        img.src = IMAGEN_RESPALDO
    }

    return (
        <div className="col-12 col-sm-6 col-lg-4 col-xl-3">
            <article className={`card h-100 shadow-sm tarjeta-juego ${modoAdmin ? 'border-danger border-2' : ''}`}>
                <span className="badge bg-secondary position-absolute top-0 start-0 m-2">{producto.categoria}</span>
                {producto.precioOferta && (
                    <span className="badge bg-success position-absolute top-0 end-0 m-2">
                        -{Math.round((1 - producto.precioOferta / producto.precio) * 100)}%
                    </span>
                )}

                <img
                    src={producto.imagen}
                    className="card-img-top"
                    alt={`Portada de ${producto.titulo}`}
                    width="460"
                    height="215"
                    loading="lazy"
                    onError={manejarErrorImagen}
                />

                <div className="card-body d-flex flex-column">
                    <h3 className="card-title h5 fw-bold">{producto.titulo}</h3>
                    <p className="card-text text-muted small flex-grow-1">{producto.descripcion}</p>

                    {/* Renderizado condicional del precio: normal u oferta */}
                    <div className="mt-2 mb-3">
                        {producto.precioOferta ? (
                            <>
                                <del className="text-muted d-block small">{formatoCLP(producto.precio)}</del>
                                <span className="fs-5 fw-bold text-success">{formatoCLP(producto.precioOferta)}</span>
                            </>
                        ) : (
                            <span className="fs-5 fw-bold">{formatoCLP(producto.precio)}</span>
                        )}
                    </div>

                    {/* Renderizado condicional del botón: "Agregar" o "En el carrito" */}
                    {enCarrito ? (
                        <button className="btn w-100 btn-secondary mt-auto" disabled>
                            <i className="bi bi-check2-circle me-1" aria-hidden="true"></i>En el carrito
                        </button>
                    ) : (
                        <button className="btn btn-brand w-100 mt-auto" onClick={() => onAgregar(producto)}>
                            <i className="bi bi-cart-plus me-1" aria-hidden="true"></i>Agregar al carrito
                        </button>
                    )}

                    {modoAdmin && (
                        <button className="btn w-100 btn-outline-danger mt-2 fw-bold" onClick={() => onEliminar(producto)}>
                            <i className="bi bi-trash3 me-1" aria-hidden="true"></i>Eliminar del catálogo
                        </button>
                    )}
                </div>
            </article>
        </div>
    )
}

export default ProductCard
