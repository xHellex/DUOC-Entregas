// 1. Selección del contenedor principal en el DOM
const contenedorJuegos = document.getElementById('contenedor-juegos');

/**
 * 2. Función para cargar los juegos usando Fetch API
 * Se conecta al archivo JSON externo y maneja las promesas (then/catch)
 */
function cargarJuegos() {
    fetch('juegos.json')
        .then(respuesta => {
            if (!respuesta.ok) throw new Error('Error al conectar con la fuente de datos');
            return respuesta.json();
        })
        .then(datos => renderizarJuegos(datos))
        .catch(error => console.error('Error en Fetch API:', error));
}

/**
 * 3. Función para renderizar dinámicamente el HTML en el DOM
 * Utiliza createElement, innerHTML y appendChild 
 */
function renderizarJuegos(juegos) {
    juegos.forEach(juego => {
        // Crear el elemento contenedor de la columna
        const columna = document.createElement('div');
        columna.classList.add('col-12', 'col-md-6', 'col-lg-3');

        // Construir la estructura interna de la tarjeta
        columna.innerHTML = `
            <div class="card h-100 shadow-sm interactivo">
                <img src="${juego.imagen}" class="card-img-top" alt="${juego.titulo}">
                <div class="card-body d-flex flex-column">
                    <h3 class="card-title h5" style="color: #0f3460;">${juego.titulo}</h3>
                    <p class="card-text text-muted flex-grow-1">${juego.descripcion}</p>
                    <a href="${juego.url}" class="btn w-100 text-white btn-ver" style="background-color: #e94560;">Ver más</a>
                </div>
            </div>
        `;

        // 4. Implementación de Eventos de Interacción

        // Evento mouseover: Cambia un estilo al pasar el cursor
        const tarjeta = columna.querySelector('.interactivo');
        tarjeta.addEventListener('mouseover', () => {
            tarjeta.style.transform = 'scale(1.05)';
            tarjeta.style.transition = 'transform 0.3s ease';
        });
        tarjeta.addEventListener('mouseout', () => {
            tarjeta.style.transform = 'scale(1)';
        });

        // Evento click: Acción al presionar el botón
        const botonVer = columna.querySelector('.btn-ver');
        botonVer.addEventListener('click', () => {
            alert(`¡Has seleccionado el juego: ${juego.titulo}!`);
        });

        // 5. Inyectar el nodo final en el DOM
        contenedorJuegos.appendChild(columna);
    });
}

// 6. Iniciar la carga de datos cuando el script se ejecuta
cargarJuegos();

// 1. Selección de los elementos del formulario
const formularioContacto = document.getElementById('formulario-contacto');
const mensajeAlerta = document.getElementById('mensaje-alerta');

/**
 * 2. Función para manejar el evento submit del formulario
 * Evita la recarga de la página y valida los campos ingresados.
 */
function manejarEnvioFormulario(evento) {
    // Evita el comportamiento predeterminado del evento (que el formulario recargue la página)
    evento.preventDefault();

    // Obtener los valores de los campos
    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const mensaje = document.getElementById('mensaje').value.trim();

    // 3. Validación básica
    if (nombre === '' || email === '' || mensaje === '') {
        // Manipulación del DOM para mostrar error
        mostrarAlerta('Por favor, completa todos los campos antes de enviar.', 'danger');
        return; // Detiene la ejecución si hay error
    }

    // 4. Si todo está correcto, simular envío y limpiar formulario
    mostrarAlerta(`¡Gracias por contactarnos, ${nombre}! Hemos recibido tu mensaje.`, 'success');
    formularioContacto.reset(); // Limpia los campos del formulario
}

/**
 * 5. Función reutilizable para mostrar alertas en el DOM
 * Crea y estructura alertas usando clases de Bootstrap
 */
function mostrarAlerta(texto, tipo) {
    // Inyecta dinámicamente un div con las clases de alerta de Bootstrap
    mensajeAlerta.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${texto}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    `;
}

// 6. Asociar el manejador de eventos al formulario
if (formularioContacto) {
    formularioContacto.addEventListener('submit', manejarEnvioFormulario);
}
