import { useState } from 'react'
import { validarRequerido, validarLargoMinimo, validarPrecio, validarUrl, sinErrores } from '../utils/validaciones'

const FORM_INICIAL = {
    titulo: '',
    descripcion: '',
    precio: '',
    precioOferta: '',
    categoria: 'RPG',
    imagen: '',
}

/**
 * Panel de administración: agrega un videojuego nuevo al catálogo.
 * No guarda nada por sí mismo: arma el objeto y se lo entrega a App.jsx
 * mediante la prop onAgregarJuego (el estado del catálogo vive en App).
 *
 * @param {Object} props
 * @param {Function} props.onAgregarJuego - Recibe el juego nuevo y lo suma al estado global
 * @param {string[]} props.categorias - Categorías disponibles para el select
 */
function AdminForm({ onAgregarJuego, categorias }) {
    const [formData, setFormData] = useState(FORM_INICIAL)
    const [errores, setErrores] = useState({})
    const [intentoEnvio, setIntentoEnvio] = useState(false)

    // Reglas de validación del formulario de administración
    const validar = (valores) => {
        const errorOferta = valores.precioOferta === ''
            ? ''
            : validarPrecio(valores.precioOferta, 'precio de oferta') ||
              (Number(valores.precioOferta) >= Number(valores.precio) ? 'La oferta debe ser menor al precio normal.' : '')
        return {
            titulo: validarRequerido(valores.titulo, 'título') || validarLargoMinimo(valores.titulo, 2, 'El título'),
            precio: validarPrecio(valores.precio, 'precio normal'),
            precioOferta: errorOferta,
            imagen: validarUrl(valores.imagen),
        }
    }

    const handleChange = (e) => {
        const { name, value } = e.target
        const nuevos = { ...formData, [name]: value }
        setFormData(nuevos)
        if (intentoEnvio) setErrores(validar(nuevos))
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        setIntentoEnvio(true)
        const nuevosErrores = validar(formData)
        setErrores(nuevosErrores)
        if (!sinErrores(nuevosErrores)) return

        const nuevoJuego = {
            id: Date.now(), // ID único basado en la hora actual
            titulo: formData.titulo.trim(),
            descripcion: formData.descripcion.trim() || 'Sin descripción.',
            precio: Number(formData.precio),
            precioOferta: formData.precioOferta ? Number(formData.precioOferta) : null,
            categoria: formData.categoria,
            imagen: formData.imagen.trim(),
        }

        // Sube el dato al componente padre (App.jsx), que actualiza el estado
        onAgregarJuego(nuevoJuego)

        setFormData(FORM_INICIAL)
        setErrores({})
        setIntentoEnvio(false)
    }

    const claseCampo = (campo) =>
        `form-control ${intentoEnvio ? (errores[campo] ? 'is-invalid' : 'is-valid') : ''}`

    return (
        <section className="card shadow border-danger border-2 my-4" aria-labelledby="titulo-admin">
            <div className="card-header bg-danger text-white fw-bold" id="titulo-admin">
                <i className="bi bi-shield-lock-fill me-2" aria-hidden="true"></i>
                Panel de Administración: agregar un juego al catálogo
            </div>
            <div className="card-body bg-light">
                <form onSubmit={handleSubmit} noValidate>
                    <div className="row g-3">
                        <div className="col-md-6">
                            <label htmlFor="admin-titulo" className="form-label fw-bold">Título del juego *</label>
                            <input type="text" className={claseCampo('titulo')} id="admin-titulo" name="titulo"
                                value={formData.titulo} onChange={handleChange} placeholder="Ej: Hollow Knight" />
                            <div className="invalid-feedback">{errores.titulo}</div>
                        </div>
                        <div className="col-md-6">
                            <label htmlFor="admin-categoria" className="form-label fw-bold">Categoría *</label>
                            <select className="form-select" id="admin-categoria" name="categoria"
                                value={formData.categoria} onChange={handleChange}>
                                {categorias.map((cat) => (
                                    <option key={cat} value={cat}>{cat}</option>
                                ))}
                            </select>
                        </div>

                        <div className="col-sm-6 col-md-4">
                            <label htmlFor="admin-precio" className="form-label fw-bold">Precio normal ($) *</label>
                            <input type="number" className={claseCampo('precio')} id="admin-precio" name="precio"
                                value={formData.precio} onChange={handleChange} min="1" step="1" placeholder="Ej: 35000" />
                            <div className="invalid-feedback">{errores.precio}</div>
                        </div>
                        <div className="col-sm-6 col-md-4">
                            <label htmlFor="admin-precioOferta" className="form-label fw-bold">Precio oferta ($)</label>
                            <input type="number" className={claseCampo('precioOferta')} id="admin-precioOferta" name="precioOferta"
                                value={formData.precioOferta} onChange={handleChange} min="1" step="1" placeholder="Opcional" />
                            <div className="invalid-feedback">{errores.precioOferta}</div>
                        </div>
                        <div className="col-md-4">
                            <label htmlFor="admin-imagen" className="form-label fw-bold">URL de la imagen *</label>
                            <input type="url" className={claseCampo('imagen')} id="admin-imagen" name="imagen"
                                value={formData.imagen} onChange={handleChange} placeholder="https://..." />
                            <div className="invalid-feedback">{errores.imagen}</div>
                        </div>

                        <div className="col-12">
                            <label htmlFor="admin-descripcion" className="form-label fw-bold">Descripción</label>
                            <textarea className="form-control" id="admin-descripcion" name="descripcion" rows="2"
                                value={formData.descripcion} onChange={handleChange} placeholder="Breve reseña del juego (opcional)"></textarea>
                        </div>

                        <div className="col-12 text-end">
                            <button type="submit" className="btn btn-danger fw-bold shadow-sm px-4">
                                <i className="bi bi-plus-circle me-2" aria-hidden="true"></i>Agregar al catálogo
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    )
}

export default AdminForm
