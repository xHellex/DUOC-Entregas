import { useState } from 'react'
import Header from './components/Header'
import Navbar from './components/Navbar'
import Carousel from './components/Carousel'
import ProductList from './components/ProductList'
import Cart from './components/Cart'
import ContactForm from './components/ContactForm'
import Footer from './components/Footer'
import './App.css'

function App() {
  const [carrito, setCarrito] = useState([])
  const [carritoVisible, setCarritoVisible] = useState(false)
  const [filtroCategoria, setFiltroCategoria] = useState('Todos')
  const [terminoBusqueda, setTerminoBusqueda] = useState('')

  const agregarAlCarrito = (producto) => {
    setCarrito(prev => [...prev, producto])
  }

  const eliminarDelCarrito = (indice) => {
    setCarrito(prev => prev.filter((_, i) => i !== indice))
  }

  const totalCarrito = carrito.reduce(
    (sum, item) => sum + (item.precioOferta || item.precio), 0
  )

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
      <main className="container">
        <ProductList
          onAgregarAlCarrito={agregarAlCarrito}
          filtroCategoria={filtroCategoria}
          terminoBusqueda={terminoBusqueda}
        />
        <ContactForm />
      </main>
      <Cart
        carrito={carrito}
        onEliminar={eliminarDelCarrito}
        total={totalCarrito}
        visible={carritoVisible}
        onCerrar={() => setCarritoVisible(false)}
      />
      <Footer />
    </>
  )
}

export default App
