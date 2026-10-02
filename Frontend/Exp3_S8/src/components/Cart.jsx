function Cart({ visible, onClose, carrito, onEliminarItem, onVaciarCarrito }) {

    // Calcula el total sumando el precio (normal o de oferta) de cada item
    const totalAcumulado = carrito.reduce((suma, item) => {
        const precioReal = item.precioOferta ? item.precioOferta : item.precio
        return suma + precioReal
    }, 0)

    // Calcula monetariamente cuánto descuento obtuvo el usuario
    const totalAhorro = carrito.reduce((suma, item) => {
        return item.precioOferta ? suma + (item.precio - item.precioOferta) : suma
    }, 0)

    return (
        <div className={`offcanvas offcanvas-end ${visible ? 'show' : ''}`} tabIndex="-1" id="offcanvasCart"
            style={{ visibility: visible ? 'visible' : 'hidden', backgroundColor: '#f8f9fa' }}>
            <div className="offcanvas-header text-white" style={{ backgroundColor: '#1a1a2e' }}>
                <h5 className="offcanvas-title fw-bold" id="offcanvasCartLabel">Carrito de Compras</h5>
                <button type="button" className="btn-close btn-close-white" onClick={onClose} aria-label="Close"></button>
            </div>

            <div className="offcanvas-body d-flex flex-column">

                {carrito.length === 0 ? (
                    <div className="text-center text-muted my-auto">
                        <p className="fs-5">Tu carrito está vacío</p>
                        <p className="small">¡Agrega algunos increíbles juegos de la tienda!</p>
                    </div>
                ) : (
                    <>
                        <ul className="list-group list-group-flush mb-4 shadow-sm rounded">
                            <li className="list-group-item bg-dark text-white fw-bold d-flex justify-content-between">
                                <span className="w-50 text-center">Producto</span>
                                <span className="w-25 text-center">Precio</span>
                                <span className="w-25 text-center">Acción</span>
                            </li>
                            {carrito.map(item => (
                                <li key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
                                    <span className="text-truncate w-50" title={item.titulo}>{item.titulo}</span>
                                    <strong className="text-success w-25 text-center">
                                        ${(item.precioOferta || item.precio).toLocaleString('es-CL')}
                                    </strong>
                                    <div className="w-25 text-center">
                                        <button className="btn btn-outline-danger btn-sm px-2 py-0" onClick={() => onEliminarItem(item.id)}>
                                            Borrar
                                        </button>
                                    </div>
                                </li>
                            ))}
                            <li className="list-group-item d-flex justify-content-between align-items-center py-3 fs-5">
                                <strong>Total a Pagar:</strong>
                                <strong className="text-success">${totalAcumulado.toLocaleString('es-CL')}</strong>
                            </li>

                            {/* RENDERIZADO CONDICIONAL DE AHORRO */}
                            {totalAhorro > 0 && (
                                <li className="list-group-item text-center border-top-0 mb-0 py-3" style={{ backgroundColor: '#d1e7dd', color: '#0f5132' }}>
                                    🎉 ¡Estás ahorrando <strong className="fs-5">${totalAhorro.toLocaleString('es-CL')}</strong>!
                                </li>
                            )}
                        </ul>

                        {/* Botones de acción final */}
                        <div className="mt-auto pt-3 border-top">
                            <button className="btn btn-outline-danger w-100 mb-2 fw-bold" onClick={onVaciarCarrito}>
                                Vaciar Carrito
                            </button>
                            <button className="btn w-100 fw-bold text-white shadow-sm" style={{ backgroundColor: '#e94560' }} onClick={onClose}>
                                Ir a Pagar
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    )
}

export default Cart
