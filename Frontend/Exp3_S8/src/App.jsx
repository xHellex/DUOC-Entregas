import { useState, useEffect } from 'react'
import Header from './components/Header'
import Navbar from './components/Navbar'
import Carousel from './components/Carousel'
import ProductList from './components/ProductList'
import Cart from './components/Cart'
import ContactForm from './components/ContactForm'
import Footer from './components/Footer'
import './App.css'

function App() {
  const [carrito, setCarrito] = useState(() => {
    const carritoGuardado = localStorage.getItem('carritoStore')
    return carritoGuardado ? JSON.parse(carritoGuardado) : []
  })

  // Guardar en localStorage cada vez que el carrito cambie
  useEffect(() => {
    localStorage.setItem('carritoStore', JSON.stringify(carrito))
  }, [carrito])

  const [carritoVisible, setCarritoVisible] = useState(false)
  const [filtroCategoria, setFiltroCategoria] = useState('Todos')
  const [terminoBusqueda, setTerminoBusqueda] = useState('')
  const [toast, setToast] = useState({ visible: false, mensaje: '' })

  const agregarAlCarrito = (producto) => {
    if (!carrito.some((item) => item.id === producto.id)) {
      setCarrito([...carrito, producto])

      // Mostrar Toast interactivo
      setToast({ visible: true, mensaje: `¡${producto.titulo} agregado al carrito!` })
      setTimeout(() => setToast({ visible: false, mensaje: '' }), 3000)
    }
  }

  const eliminarDelCarrito = (id) => {
    setCarrito(carrito.filter(item => item.id !== id))
  }

  const vaciarCarrito = () => {
    setCarrito([])
  }

  return (
    <>
      <Header />
      <Navbar
        cantidadCarrito={carrito.length}
        onAbrirCarrito={() => setCarritoVisible(true)}
        onBuscar={setTerminoBusqueda}
        onFiltrarCategoria={setFiltroCategoria}
      />
      <Carousel />
      <main className="container flex-grow-1">
        <ProductList
          onAgregarAlCarrito={agregarAlCarrito}
          filtroCategoria={filtroCategoria}
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
      />

      {/* Componente Toast de Bootstrap para Notificaciones */}
      <div className="toast-container position-fixed bottom-0 end-0 p-3" style={{ zIndex: 1055 }}>
        <div className={`toast align-items-center text-bg-success border-0 ${toast.visible ? 'show' : 'hide'}`} role="alert" aria-live="assertive" aria-atomic="true">
          <div className="d-flex">
            <div className="toast-body fw-bold">
              🛒 {toast.mensaje}
            </div>
            <button type="button" className="btn-close btn-close-white me-2 m-auto" onClick={() => setToast({ visible: false, mensaje: '' })}></button>
          </div>
        </div>
      </div>
    </>
  )
}

export default App
