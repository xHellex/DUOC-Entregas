import { useState, useEffect, useMemo } from 'react'
import Header from './components/Header'
import Navbar from './components/Navbar'
import Carousel from './components/Carousel'
import ProductList from './components/ProductList'
import Cart from './components/Cart'
import ContactForm from './components/ContactForm'
import Footer from './components/Footer'
import AdminForm from './components/AdminForm'
import './App.css'

// Categorías que el administrador puede asignar aunque aún no exista un juego de ese tipo
const CATEGORIAS_BASE = ['RPG', 'FPS', 'Aventura', 'Estrategia', 'Deportes']

/**
 * Componente raíz. Aquí vive el estado compartido (State Lifting):
 * catálogo, carrito, filtros, modo admin y notificaciones.
 * Los hijos reciben datos y funciones por props, así un cambio en uno
 * (ej. filtrar en Navbar) se refleja en otro (ProductList).
 */
function App() {
  // ---------- Estado del catálogo ----------
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)
  const [modoAdmin, setModoAdmin] = useState(false)

  // Carga inicial del catálogo desde el JSON (simula una API)
  useEffect(() => {
    fetch('./juegos.json')
      .then(res => {
        if (!res.ok) throw new Error('No se pudo obtener el catálogo')
        return res.json()
      })
      .then(data => setProductos(data))
      .catch(err => setError(err.message))
      .finally(() => setCargando(false))
  }, [])

  // ---------- Estado del carrito (persistido en localStorage) ----------
  const [carrito, setCarrito] = useState(() => {
    try {
      const carritoGuardado = localStorage.getItem('carritoStore')
      return carritoGuardado ? JSON.parse(carritoGuardado) : []
    } catch (err) {
      console.error('localStorage corrupto, se reinicia el carrito:', err)
      return []
    }
  })

  useEffect(() => {
    localStorage.setItem('carritoStore', JSON.stringify(carrito))
  }, [carrito])

  // ---------- Estado de la interfaz ----------
  const [carritoVisible, setCarritoVisible] = useState(false)
  const [filtroCategoria, setFiltroCategoria] = useState('Todos')
  const [terminoBusqueda, setTerminoBusqueda] = useState('')
  const [toast, setToast] = useState({ visible: false, mensaje: '', tipo: 'success' })

  // Oculta el toast a los 3 s; la limpieza evita que un timer viejo cierre uno nuevo
  useEffect(() => {
    if (!toast.visible) return
    const t = setTimeout(() => setToast(prev => ({ ...prev, visible: false })), 3000)
    return () => clearTimeout(t)
  }, [toast])

  const mostrarToast = (mensaje, tipo = 'success') => setToast({ visible: true, mensaje, tipo })

  // Categorías presentes en el catálogo (se recalculan solo si cambia la lista)
  const categoriasCatalogo = useMemo(
    () => [...new Set(productos.map(p => p.categoria))].sort(),
    [productos]
  )
  const categoriasAdmin = useMemo(
    () => [...new Set([...CATEGORIAS_BASE, ...categoriasCatalogo])],
    [categoriasCatalogo]
  )

  // ---------- Acciones del carrito ----------
  const agregarAlCarrito = (producto) => {
    setCarrito(prev => (prev.some(item => item.id === producto.id) ? prev : [...prev, producto]))
    mostrarToast(`¡${producto.titulo} agregado al carrito!`)
  }

  const eliminarDelCarrito = (id) => setCarrito(prev => prev.filter(item => item.id !== id))
  const vaciarCarrito = () => setCarrito([])

  // Compra simulada: no hay pasarela de pago, solo vaciamos el carrito y avisamos
  const simularCompra = () => {
    setCarrito([])
    setCarritoVisible(false)
    mostrarToast('¡Compra simulada con éxito! Gracias por preferirnos.')
  }

  // ---------- Acciones del administrador (agregar / eliminar del catálogo) ----------
  const agregarJuegoAlCatalogo = (nuevoJuego) => {
    setProductos(prev => [nuevoJuego, ...prev])
    setFiltroCategoria('Todos') // para que el juego nuevo quede visible
    mostrarToast(`«${nuevoJuego.titulo}» se agregó al catálogo.`)
  }

  const eliminarJuegoDelCatalogo = (producto) => {
    if (!window.confirm(`¿Eliminar «${producto.titulo}» del catálogo?`)) return
    setProductos(prev => prev.filter(item => item.id !== producto.id))
    // Si estaba en el carrito, también se quita (ya no existe en la tienda)
    setCarrito(prev => prev.filter(item => item.id !== producto.id))
    mostrarToast(`«${producto.titulo}» se eliminó del catálogo.`, 'danger')
  }

  return (
    <>
      <Header />

      {/* Barra superior con el interruptor del modo administrador */}
      <div className="bg-dark py-1 px-3 d-flex justify-content-end align-items-center">
        <div className="form-check form-switch text-white mb-0">
          <input className="form-check-input" type="checkbox" role="switch" id="adminSwitch"
            checked={modoAdmin} onChange={() => setModoAdmin(prev => !prev)} />
          <label className="form-check-label small" htmlFor="adminSwitch">Modo administrador</label>
        </div>
      </div>

      <Navbar
        cantidadCarrito={carrito.length}
        onAbrirCarrito={() => {
          setCarritoVisible(true)
          setToast(prev => ({ ...prev, visible: false })) // el toast no debe tapar los botones del carrito
        }}
        onBuscar={setTerminoBusqueda}
        categorias={categoriasCatalogo}
        filtroActivo={filtroCategoria}
        onFiltrarCategoria={setFiltroCategoria}
      />
      <Carousel />

      <main className="container">
        {/* Renderizado condicional del panel de administración */}
        {modoAdmin && (
          <AdminForm onAgregarJuego={agregarJuegoAlCatalogo} categorias={categoriasAdmin} />
        )}

        {/* La key reinicia la paginación "Ver más" cada vez que cambia el filtro o la búsqueda */}
        <ProductList
          key={`${filtroCategoria}|${terminoBusqueda}`}
          productos={productos}
          cargando={cargando}
          error={error}
          modoAdmin={modoAdmin}
          onAgregarAlCarrito={agregarAlCarrito}
          onEliminarJuego={eliminarJuegoDelCatalogo}
          filtroCategoria={filtroCategoria}
          onLimpiarFiltro={() => setFiltroCategoria('Todos')}
          terminoBusqueda={terminoBusqueda}
          carrito={carrito}
        />
        <ContactForm />
      </main>

      <Footer />

      <Cart
        visible={carritoVisible}
        onClose={() => setCarritoVisible(false)}
        carrito={carrito}
        onEliminarItem={eliminarDelCarrito}
        onVaciarCarrito={vaciarCarrito}
        onComprar={simularCompra}
      />

      {/* Notificación tipo toast (verde = éxito, rojo = eliminación) */}
      <div className="toast-container position-fixed bottom-0 start-50 translate-middle-x p-3">
        <div className={`toast align-items-center text-bg-${toast.tipo} border-0 ${toast.visible ? 'show' : 'hide'}`}
          role="status" aria-live="polite" aria-atomic="true">
          <div className="d-flex">
            <div className="toast-body fw-bold">{toast.mensaje}</div>
            <button type="button" className="btn-close btn-close-white me-2 m-auto" aria-label="Cerrar notificación"
              onClick={() => setToast(prev => ({ ...prev, visible: false }))}></button>
          </div>
        </div>
      </div>
    </>
  )
}

export default App
