function Footer() {
    return (
        <footer className="bg-dark text-white pt-5 pb-4 mt-5">
            <div className="container text-center text-md-start">
                <div className="row text-center text-md-start">
                    <div className="col-md-3 col-lg-3 col-xl-3 mx-auto mt-3">
                        <h3 className="text-uppercase mb-4 font-weight-bold text-warning">Pixel Games Store</h3>
                        <p>&copy; 2026 Pixel Games Store. Todos los derechos reservados.</p>
                    </div>

                    <div className="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
                        <h3 className="text-uppercase mb-4 font-weight-bold text-warning">Enlaces</h3>
                        <p><a href="/" className="text-white text-decoration-none">Inicio</a></p>
                        <p><a href="#productos" className="text-white text-decoration-none">Productos</a></p>
                        <p><a href="mailto:contacto@tienda.com" className="text-white text-decoration-none">Contacto</a></p>
                    </div>

                    <div className="col-md-4 col-lg-3 col-xl-3 mx-auto mt-3">
                        <h3 className="text-uppercase mb-4 font-weight-bold text-warning">Contacto</h3>
                        <p>Calle Falsa 123, Ciudad, País</p>
                        <p><a href="mailto:contacto@tienda.com" className="text-white text-decoration-none">contacto@tienda.com</a></p>
                        <p><a href="tel:+1234567890" className="text-white text-decoration-none">+1 234 567 890</a></p>
                    </div>

                    <div className="col-md-2 col-lg-2 col-xl-2 mx-auto mt-3">
                        <h3 className="text-uppercase mb-4 font-weight-bold text-warning">Síguenos</h3>
                        <p>
                            <a href="https://www.facebook.com/tienda" target="_blank" rel="noreferrer" className="text-white text-decoration-none d-block">Facebook</a>
                            <a href="https://www.twitter.com/tienda" target="_blank" rel="noreferrer" className="text-white text-decoration-none d-block">Twitter</a>
                            <a href="https://www.instagram.com/tienda" target="_blank" rel="noreferrer" className="text-white text-decoration-none d-block">Instagram</a>
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    )
}

export default Footer