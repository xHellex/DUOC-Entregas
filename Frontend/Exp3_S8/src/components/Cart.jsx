// src/components/Cart.jsx

/**
 * Componente del carrito de compras (Usando Bootstrap Offcanvas)
 * Muestra los productos agregados, total y permite eliminar
 */
function Cart({ carrito, onEliminar, total, visible, onCerrar }) {
    return (
        <>
            {/* Backdrop: Fondo oscuro cuando el carrito está abierto */}
            {visible && <div className="offcanvas-backdrop fade show" onClick={onCerrar}></div>}

            <div className={`offcanvas offcanvas-end ${visible ? 'show' : ''}`} style={visible ? { visibility: 'visible' } : {}} tabIndex="-1" id="offcanvasCarrito" aria-labelledby="offcanvasCarritoLabel">
                <div className="offcanvas-header bg-dark text-white">
                    <h5 className="offcanvas-title" id="offcanvasCarritoLabel">Carrito de Compras</h5>
                    <button type="button" className="btn-close btn-close-white" onClick={onCerrar} aria-label="Cerrar"></button>
                </div>
                <div className="offcanvas-body">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle text-center">
                            <thead className="table-dark">
                                <tr>
                                    <th>Producto</th>
                                    <th>Precio</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>
                            <tbody>
                                {carrito.length === 0 ? (
                                    <tr>
                                        <td colSpan="3" className="text-muted">El carrito está vacío.</td>
                                    </tr>
                                ) : (
                                    carrito.map((item, index) => (
                                        <tr key={index}>
                                            <td>{item.titulo}</td>
                                            <td className="text-success fw-bold">${(item.precioOferta || item.precio).toLocaleString('es-CL')}</td>
                                            <td>
                                                <button className="btn btn-sm btn-outline-danger" onClick={() => onEliminar(index)}>
                                                    Eliminar
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                            <tfoot>
                                <tr className="fw-bold fs-5">
                                    <td className="text-end">Total:</td>
                                    <td colSpan="2" className="text-start text-success">${total.toLocaleString('es-CL')}</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Cart
