/**
 * Componente cabecera (Header) de la aplicación.
 * Presenta el branding superior de la tienda.
 */
function Header() {
    return (
        <header id="inicio" className="text-center py-4 text-white" style={{ backgroundColor: 'var(--primary-dark)', borderBottom: '5px solid var(--secondary-dark)' }}>
            <div className="container px-3">
                <h1 className="display-5 fw-bold">Pixel Games Store</h1>
                <p className="lead mb-0">Bienvenido a nuestra tienda. Encuentra los mejores videojuegos para todas tus consolas y PC.</p>
            </div>
        </header>
    )
}

export default Header