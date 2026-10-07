import { useState } from 'react'
import { validarRequerido, validarLargoMinimo, validarEmail, sinErrores } from '../utils/validaciones'

const FORM_INICIAL = { nombre: '', email: '', mensaje: '' }

/**
 * Formulario de contacto (nombre, email y mensaje).
 * Valida con JavaScript antes de "enviar" y muestra el error bajo cada campo.
 * Se usa noValidate para que el navegador no bloquee el envío con sus mensajes
 * nativos y así mostrar nuestros propios mensajes con estilos de Bootstrap.
 */
function ContactForm() {
    const [datos, setDatos] = useState(FORM_INICIAL)
    const [errores, setErrores] = useState({})
    const [intentoEnvio, setIntentoEnvio] = useState(false)
    const [enviado, setEnviado] = useState(null)

    // Reglas de validación de cada campo
    const validar = (valores) => ({
        nombre: validarRequerido(valores.nombre, 'nombre') || validarLargoMinimo(valores.nombre, 3, 'El nombre'),
        email: validarEmail(valores.email),
        mensaje: validarRequerido(valores.mensaje, 'mensaje') || validarLargoMinimo(valores.mensaje, 10, 'El mensaje'),
    })

    // Actualiza el campo y, si ya se intentó enviar, re-valida en vivo
    const manejarCambio = (e) => {
        const { name, value } = e.target
        const nuevosDatos = { ...datos, [name]: value }
        setDatos(nuevosDatos)
        if (intentoEnvio) setErrores(validar(nuevosDatos))
    }

    const manejarEnvio = (e) => {
        e.preventDefault()
        setIntentoEnvio(true)
        const nuevosErrores = validar(datos)
        setErrores(nuevosErrores)

        if (!sinErrores(nuevosErrores)) {
            setEnviado(null)
            return
        }

        // Envío simulado: no hay backend, solo confirmamos al usuario
        setEnviado(datos.nombre.trim())
        setDatos(FORM_INICIAL)
        setErrores({})
        setIntentoEnvio(false)
    }

    // Clase de Bootstrap según el estado de validación del campo
    const claseCampo = (campo) =>
        `form-control ${intentoEnvio ? (errores[campo] ? 'is-invalid' : 'is-valid') : ''}`

    return (
        <section id="contacto" className="my-5 p-4 bg-white rounded shadow-sm">
            <h2 className="text-center mb-4 fw-bold" style={{ color: 'var(--primary-dark)' }}>Contáctanos</h2>

            {/* Renderizado condicional: confirmación de envío */}
            {enviado && (
                <div className="alert alert-success alert-dismissible" role="status">
                    ¡Gracias por contactarnos, {enviado}! Hemos recibido tu mensaje.
                    <button type="button" className="btn-close" onClick={() => setEnviado(null)} aria-label="Cerrar"></button>
                </div>
            )}

            {/* Renderizado condicional: resumen de errores */}
            {intentoEnvio && !sinErrores(errores) && (
                <div className="alert alert-danger" role="alert">
                    Revisa los campos marcados en rojo antes de enviar.
                </div>
            )}

            <form id="formulario-contacto" onSubmit={manejarEnvio} noValidate>
                <div className="mb-3">
                    <label htmlFor="contacto-nombre" className="form-label fw-bold">Nombre completo</label>
                    <input type="text" className={claseCampo('nombre')} id="contacto-nombre" name="nombre"
                        placeholder="Ej. Juan Pérez" value={datos.nombre} onChange={manejarCambio}
                        aria-describedby="error-nombre" />
                    <div id="error-nombre" className="invalid-feedback">{errores.nombre}</div>
                </div>
                <div className="mb-3">
                    <label htmlFor="contacto-email" className="form-label fw-bold">Correo electrónico</label>
                    <input type="email" className={claseCampo('email')} id="contacto-email" name="email"
                        placeholder="juan.perez@correo.cl" value={datos.email} onChange={manejarCambio}
                        aria-describedby="error-email" />
                    <div id="error-email" className="invalid-feedback">{errores.email}</div>
                </div>
                <div className="mb-3">
                    <label htmlFor="contacto-mensaje" className="form-label fw-bold">Mensaje</label>
                    <textarea className={claseCampo('mensaje')} id="contacto-mensaje" name="mensaje" rows="4"
                        placeholder="Escribe tu mensaje aquí (mínimo 10 caracteres)..." value={datos.mensaje}
                        onChange={manejarCambio} aria-describedby="error-mensaje"></textarea>
                    <div id="error-mensaje" className="invalid-feedback">{errores.mensaje}</div>
                </div>

                <button type="submit" className="btn btn-brand w-100 fw-bold">Enviar Mensaje</button>
            </form>
        </section>
    )
}

export default ContactForm
