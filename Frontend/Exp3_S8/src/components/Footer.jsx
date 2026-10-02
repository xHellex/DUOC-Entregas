/**
 * Componente pie de página (Footer).
 * Contiene enlaces rápidos, redes sociales y copyright.
 */
function Footer() {
    return (
        <footer className="text-white py-5 mt-auto" style={{ backgroundColor: 'var(--primary-dark)' }}>
            <div className="container">
                <div className="row">
                    <div className="col-md-4 mb-4 mb-md-0">
                        <h2 className="h4 fw-bold text-warning mb-3">Pixel Games Store</h2>
                        <p className="text-muted">Tu destino número uno para los mejores videojuegos. Explora, compra y juega con nosotros.</p>
                    </div>

                    <div className="col-md-4 mb-4 mb-md-0">
                        <h3 className="h4 fw-bold mb-3">Enlaces Rápidos</h3>
                        <ul className="list-unstyled">
                            <li className="mb-2"><a href="#" className="text-decoration-none text-muted interactivo d-inline-block">🏠 Inicio</a></li>
                            <li className="mb-2"><a href="#productos" className="text-decoration-none text-muted interactivo d-inline-block">🎮 Productos</a></li>
                            <li className="mb-2"><a href="#contacto" className="text-decoration-none text-muted interactivo d-inline-block">✉️ Contacto</a></li>
                        </ul>
                    </div>

                    <div className="col-md-4">
                        <h3 className="h4 fw-bold mb-3">Síguenos</h3>
                        <div className="d-flex gap-3">
                            <a href="#" className="text-muted fs-4 interactivo"><i className="bi bi-facebook"></i></a>
                            <a href="#" className="text-muted fs-4 interactivo"><i className="bi bi-twitter-x"></i></a>
                            <a href="#" className="text-muted fs-4 interactivo"><i className="bi bi-instagram"></i></a>
                            <a href="#" className="text-muted fs-4 interactivo"><i className="bi bi-discord"></i></a>
                        </div>
                    </div>
                </div>

                <hr className="my-4 border-secondary" />

                <div className="text-center text-muted">
                    <p className="mb-0">&copy; 2026 Pixel Games Store. Todos los derechos reservados.</p>
                </div>
            </div>
        </footer>
    )
}

export default Footer