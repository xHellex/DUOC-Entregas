import { useState } from 'react'

function ContactForm() {
    const [nombre, setNombre] = useState('')
    const [email, setEmail] = useState('')
    const [mensaje, setMensaje] = useState('')
    const [alerta, setAlerta] = useState(null)

    const manejarEnvio = (e) => {
        e.preventDefault()

        if (nombre.trim() === '' || email.trim() === '' || mensaje.trim() === '') {
            setAlerta({ tipo: 'danger', texto: 'Por favor, completa todos los campos antes de enviar.' })
            return
        }

        setAlerta({ tipo: 'success', texto: `¡Gracias por contactarnos, ${nombre}! Hemos recibido tu mensaje.` })

        // Reset
        setNombre('')
        setEmail('')
        setMensaje('')
    }

    return (
        <section id="contacto" className="mt-5 mb-5 p-4 bg-white rounded shadow-sm">
            <h2 className="text-center mb-4" style={{ color: '#1a1a2e' }}>Contáctanos</h2>

            <form id="formulario-contacto" onSubmit={manejarEnvio}>
                <div className="mb-3">
                    <label htmlFor="nombre" className="form-label">Nombre completo</label>
                    <input
                        type="text"
                        className="form-control"
                        id="nombre"
                        placeholder="Ingresa tu nombre"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Correo electrónico</label>
                    <input
                        type="email"
                        className="form-control"
                        id="email"
                        placeholder="ejemplo@correo.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="mensaje" className="form-label">Mensaje</label>
                    <textarea
                        className="form-control"
                        id="mensaje"
                        rows="3"
                        placeholder="Escribe tu mensaje aquí..."
                        value={mensaje}
                        onChange={(e) => setMensaje(e.target.value)}
                    ></textarea>
                </div>

                {alerta && (
                    <div className="mb-3">
                        <div className={`alert alert-${alerta.tipo} alert-dismissible fade show`} role="alert">
                            {alerta.texto}
                            <button type="button" className="btn-close" onClick={() => setAlerta(null)} aria-label="Cerrar"></button>
                        </div>
                    </div>
                )}

                <button type="submit" className="btn text-white w-100" style={{ backgroundColor: '#0f3460' }}>
                    Enviar Mensaje
                </button>
            </form>
        </section>
    )
}

export default ContactForm