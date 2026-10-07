// Redes sociales del pie de página (se recorren con .map para no repetir el marcado)
const REDES = [
    { nombre: 'Facebook', icono: 'bi-facebook' },
    { nombre: 'X (Twitter)', icono: 'bi-twitter-x' },
    { nombre: 'Instagram', icono: 'bi-instagram' },
    { nombre: 'Discord', icono: 'bi-discord' },
]

/**
 * Pie de página. Sus tres columnas se ordenan con CSS Grid (.footer-grid en App.css):
 * en móvil quedan apiladas y en pantallas grandes se ubican lado a lado.
 */
function Footer() {
    return (
        <footer className="text-white py-5 mt-5" style={{ backgroundColor: 'var(--primary-dark)' }}>
            <div className="container">
                <div className="footer-grid">
                    <div>
                        <h2 className="h4 fw-bold text-warning mb-3">Pixel Games Store</h2>
                        <p className="text-white-50 mb-0">Tu tienda de videojuegos para PC y consolas. Explora, compra y juega.</p>
                    </div>

                    <nav aria-label="Enlaces del pie de página">
                        <h3 className="h5 fw-bold mb-3">Enlaces rápidos</h3>
                        <ul className="list-unstyled mb-0">
                            <li className="mb-2"><a href="#inicio" className="link-footer">Inicio</a></li>
                            <li className="mb-2"><a href="#productos" className="link-footer">Productos</a></li>
                            <li><a href="#contacto" className="link-footer">Contacto</a></li>
                        </ul>
                    </nav>

                    <div>
                        <h3 className="h5 fw-bold mb-3">Síguenos</h3>
                        <div className="d-flex gap-3">
                            {REDES.map((red) => (
                                <a key={red.nombre} href="#" className="link-footer fs-4" aria-label={red.nombre}
                                    onClick={(e) => e.preventDefault()}>
                                    <i className={`bi ${red.icono}`} aria-hidden="true"></i>
                                </a>
                            ))}
                        </div>
                    </div>
                </div>

                <hr className="my-4 border-secondary" />
                <p className="text-center text-white-50 mb-0">&copy; 2026 Pixel Games Store. Todos los derechos reservados.</p>
            </div>
        </footer>
    )
}

export default Footer
