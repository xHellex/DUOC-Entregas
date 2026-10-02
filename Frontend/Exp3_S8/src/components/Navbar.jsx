/**
 * Barra de navegación principal interactiva.
 * Incluye categorización, búsqueda dinámica y acceso directo al carrito de compras.
 */
function Navbar({ cantidadCarrito, onAbrirCarrito, onBuscar, onFiltrarCategoria }) {

    const handleBuscar = (e) => {
        const texto = e.target.value;
        onBuscar(texto);
    }

    const preventSubmit = (e) => e.preventDefault();

    return (
        <nav className="navbar navbar-expand-lg navbar-dark sticky-top shadow-sm" style={{ backgroundColor: 'var(--secondary-dark)' }}>
            <div className="container">
                <a className="navbar-brand fw-bold text-warning" href="#">Pixel Games</a>

                <div className="d-flex align-items-center order-lg-last ms-auto me-2 me-lg-0">
                    {/* Botón del Carrito siempre visible en móviles */}
                    <button className="btn btn-warning position-relative" type="button" onClick={onAbrirCarrito} aria-label={`Abrir carrito con ${cantidadCarrito} productos`}>
                        🛒
                        {cantidadCarrito > 0 && (
                            <span id="badge-carrito" className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                {cantidadCarrito}
                            </span>
                        )}
                    </button>

                    <button className="navbar-toggler ms-2" type="button" data-bs-toggle="collapse" data-bs-target="#menuNavegacion"
                        aria-controls="menuNavegacion" aria-expanded="false" aria-label="Alternar navegación">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                </div>

                <div className="collapse navbar-collapse" id="menuNavegacion">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <a className="nav-link" href="#productos" data-bs-toggle="collapse" data-bs-target=".navbar-collapse.show">Productos</a>
                        </li>
                        <li className="nav-item dropdown">
                            <a className="nav-link dropdown-toggle" href="#" id="navbarCategorias" role="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                Categorías
                            </a>
                            <ul className="dropdown-menu" aria-labelledby="navbarCategorias">
                                <li><button className="dropdown-item categoria-filtro select-none" onClick={() => onFiltrarCategoria('RPG')} data-bs-toggle="collapse" data-bs-target=".navbar-collapse.show">RPG (Rol)</button></li>
                                <li><button className="dropdown-item categoria-filtro select-none" onClick={() => onFiltrarCategoria('FPS')} data-bs-toggle="collapse" data-bs-target=".navbar-collapse.show">FPS (Disparos)</button></li>
                                <li><hr className="dropdown-divider" /></li>
                                <li><button className="dropdown-item categoria-filtro select-none fw-bold" onClick={() => onFiltrarCategoria('Todos')} data-bs-toggle="collapse" data-bs-target=".navbar-collapse.show">Ver Todos</button></li>
                            </ul>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#contacto" data-bs-toggle="collapse" data-bs-target=".navbar-collapse.show">Contacto</a>
                        </li>
                    </ul>

                    {/* Formulario de Búsqueda */}
                    <form className="d-flex my-2 my-lg-0 me-lg-3" id="formulario-busqueda" onSubmit={preventSubmit}>
                        <input className="form-control" type="search" name="busqueda" id="input-busqueda" placeholder="Buscar un juego..." aria-label="Buscar" onChange={handleBuscar} />
                    </form>
                </div>
            </div>
        </nav>
    )
}

export default Navbar