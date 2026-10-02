/**
 * Componente cabecera (Header) de la aplicación.
 * Presenta el branding superior de la tienda.
 */
function Header() {
    return (
        <header className="text-center py-4 text-white" style={{ backgroundColor: 'var(--primary-dark)', borderBottom: '5px solid var(--secondary-dark)' }}>
            <h1>Pixel Games Store</h1>
            <p>Bienvenido a nuestra tienda. Encuentra los mejores videojuegos para todas tus consolas y PC.</p>
        </header>
    )
}

export default Header