import { useState } from 'react'

/**
 * Barra de navegación principal (componente Navbar de Bootstrap 5).
 * - Enlaces a las secciones (Inicio, Productos, Contacto).
 * - Menú desplegable de categorías, generado a partir del catálogo.
 * - Buscador por título y botón del carrito (siempre visible, también en móvil).
 *
 * @param {Object} props
 * @param {number} props.cantidadCarrito - Cantidad de juegos en el carrito (para el badge)
 * @param {Function} props.onAbrirCarrito - Abre el panel del carrito
 * @param {Function} props.onBuscar - Actualiza el término de búsqueda en App
 * @param {string[]} props.categorias - Categorías existentes en el catálogo
 * @param {string} props.filtroActivo - Categoría seleccionada actualmente
 * @param {Function} props.onFiltrarCategoria - Cambia la categoría seleccionada en App
 */
function Navbar({ cantidadCarrito, onAbrirCarrito, onBuscar, categorias, filtroActivo, onFiltrarCategoria }) {
    // Estado del menú hamburguesa en móvil (abierto / cerrado)
    const [menuAbierto, setMenuAbierto] = useState(false)

    // Al elegir una opción se cierra el menú, sin bloquear el salto del enlace (#seccion)
    const cerrarMenuMovil = { onClick: () => setMenuAbierto(false) }

    const opciones = ['Todos', ...categorias]

    return (
        <nav className="navbar navbar-expand-lg navbar-dark sticky-top shadow-sm" style={{ backgroundColor: 'var(--secondary-dark)' }}
            aria-label="Navegación principal">
            <div className="container">
                <a className="navbar-brand fw-bold text-warning" href="#inicio">
                    <i className="bi bi-controller me-2" aria-hidden="true"></i>Pixel Games
                </a>

                {/* Carrito + botón hamburguesa: fuera del collapse para que se vean en móvil */}
                <div className="d-flex align-items-center order-lg-last ms-auto">
                    <button className="btn btn-warning position-relative" type="button" onClick={onAbrirCarrito}
                        aria-label={`Abrir carrito, ${cantidadCarrito} productos`}>
                        <i className="bi bi-cart3" aria-hidden="true"></i>
                        {cantidadCarrito > 0 && (
                            <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                {cantidadCarrito}
                            </span>
                        )}
                    </button>

                    <button className="navbar-toggler ms-3" type="button" onClick={() => setMenuAbierto(prev => !prev)}
                        aria-controls="menuNavegacion" aria-expanded={menuAbierto}
                        aria-label={menuAbierto ? 'Cerrar menú de navegación' : 'Abrir menú de navegación'}>
                        <span className="navbar-toggler-icon"></span>
                    </button>
                </div>

                <div className={`collapse navbar-collapse ${menuAbierto ? 'show' : ''}`} id="menuNavegacion">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item">
                            <a className="nav-link" href="#inicio" {...cerrarMenuMovil}>Inicio</a>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#productos" {...cerrarMenuMovil}>Productos</a>
                        </li>
                        <li className="nav-item dropdown">
                            <a className="nav-link dropdown-toggle" href="#" id="navbarCategorias" role="button"
                                data-bs-toggle="dropdown" aria-expanded="false">
                                Categorías{filtroActivo !== 'Todos' && <span className="badge bg-warning text-dark ms-1">{filtroActivo}</span>}
                            </a>
                            {/* Opciones generadas dinámicamente con .map() */}
                            <ul className="dropdown-menu" aria-labelledby="navbarCategorias">
                                {opciones.map((cat) => (
                                    <li key={cat}>
                                        <button type="button"
                                            className={`dropdown-item ${filtroActivo === cat ? 'active' : ''}`}
                                            aria-current={filtroActivo === cat ? 'true' : undefined}
                                            onClick={() => { onFiltrarCategoria(cat); setMenuAbierto(false) }}>
                                            {cat === 'Todos' ? 'Ver todos' : cat}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </li>
                        <li className="nav-item">
                            <a className="nav-link" href="#contacto" {...cerrarMenuMovil}>Contacto</a>
                        </li>
                    </ul>

                    {/* Buscador: actualiza el estado en App a medida que se escribe */}
                    <form className="d-flex my-2 my-lg-0 me-lg-3" role="search" onSubmit={(e) => e.preventDefault()}>
                        <input className="form-control" type="search" placeholder="Buscar un juego..."
                            aria-label="Buscar un juego por título" onChange={(e) => onBuscar(e.target.value)} />
                    </form>
                </div>
            </div>
        </nav>
    )
}

export default Navbar
