function Navbar({ cantidadCarrito, onAbrirCarrito, onBuscar, onFiltrarCategoria }) {

    const handleBuscar = (e) => {
        const texto = e.target.value;
        onBuscar(texto);
    }

    const preventSubmit = (e) => e.preventDefault();

    return (
        <nav className="navbar navbar-expand-lg navbar-dark sticky-top shadow-sm" style={{ backgroundColor: '#0f3460' }}>
            <div className="container">
                <a className="navbar-brand fw-bold text-warning" href="#">Pixel Games</a>

                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#menuNavegacion"
                    aria-controls="menuNavegacion" aria-expanded="false" aria-label="Alternar navegación">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="menuNavegacion">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <a className="nav-link" href="#productos">Productos</a>
                        </li>
                        <li className="nav-item dropdown">
                            <a className="nav-link dropdown-toggle" href="#" id="navbarCategorias" role="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                Categorías
                            </a>
                            <ul className="dropdown-menu" aria-labelledby="navbarCategorias">
                                <li><button className="dropdown-item categoria-filtro select-none" onClick={() => onFiltrarCategoria('RPG')}>RPG (Rol)</button></li>
                                <li><button className="dropdown-item categoria-filtro select-none" onClick={() => onFiltrarCategoria('FPS')}>FPS (Disparos)</button></li>
                                <li><hr className="dropdown-divider" /></li>
                                <li><button className="dropdown-item categoria-filtro select-none fw-bold" onClick={() => onFiltrarCategoria('Todos')}>Ver Todos</button></li>
                            </ul>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#contacto">Contacto</a>
                        </li>
                    </ul>

                    {/* Formulario de Búsqueda */}
                    <form className="d-flex" id="formulario-busqueda" onSubmit={preventSubmit}>
                        <input className="form-control me-2" type="search" name="busqueda" id="input-busqueda" placeholder="Buscar un juego..." aria-label="Buscar" onChange={handleBuscar} />

                        {/* Botón del Carrito en Nav */}
                        <button className="btn btn-warning ms-3 position-relative" type="button" onClick={onAbrirCarrito}>
                            🛒
                            {cantidadCarrito > 0 && (
                                <span id="badge-carrito" className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                    {cantidadCarrito}
                                </span>
                            )}
                        </button>
                    </form>
                </div>
            </div>
        </nav>
    )
}

export default Navbar