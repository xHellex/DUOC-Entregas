// Elementos del DOM
const contenedorJuegos = document.getElementById('contenedor-juegos');

/**
 * Carga el catálogo de juegos desde la API
 */
function cargarJuegos() {
    fetch('juegos.json')
        .then(respuesta => {
            if (!respuesta.ok) throw new Error('Error de red');
            return respuesta.json();
        })
        .then(datos => renderizarJuegos(datos))
        .catch(error => {
            console.error('Error al cargar juegos:', error);

            // Alerta de error en UI
            const contenedorJuegos = document.getElementById('contenedor-juegos');
            contenedorJuegos.innerHTML = `
                <div class="col-12 text-center mt-4">
                    <div class="alert alert-danger shadow-sm" role="alert">
                        <h4 class="alert-heading">¡Ups! Algo salió mal.</h4>
                        <p>Lo sentimos, no pudimos cargar el catálogo de juegos en este momento.</p>
                        <hr>
                        <p class="mb-0">Por favor, verifica tu conexión o intenta actualizar la página más tarde.</p>
                    </div>
                </div>
            `;
        });
}

// Variables del carrito
let carrito = [];
const cuerpoCarrito = document.getElementById('cuerpo-carrito');
const totalCarrito = document.getElementById('total-carrito');

/**
 * Renderiza dinámicamente las tarjetas de los juegos en el DOM
 */
function renderizarJuegos(juegos) {
    const contenedorJuegos = document.getElementById('contenedor-juegos');
    contenedorJuegos.innerHTML = '';

    juegos.forEach(juego => {
        const columna = document.createElement('div');
        columna.classList.add('col-12', 'col-md-6', 'col-lg-3');

        columna.innerHTML = `
            <div class="card h-100 shadow-sm interactivo">
                <img src="${juego.imagen}" class="card-img-top" alt="${juego.titulo}">
                <div class="card-body d-flex flex-column text-center">
                    <h3 class="card-title h5" style="color: #0f3460;">${juego.titulo}</h3>
                    <p class="card-text text-muted flex-grow-1">${juego.descripcion}</p>
                    <p class="fw-bold fs-5 text-success">$${juego.precio.toLocaleString('es-CL')}</p>
                    <button class="btn w-100 text-white btn-agregar" style="background-color: #e94560;">Agregar al Carrito</button>
                </div>
            </div>
        `;

        // Efectos hover
        const tarjeta = columna.querySelector('.interactivo');
        tarjeta.addEventListener('mouseover', () => {
            tarjeta.style.transform = 'scale(1.05)';
            tarjeta.style.transition = 'transform 0.3s ease';
        });
        tarjeta.addEventListener('mouseout', () => {
            tarjeta.style.transform = 'scale(1)';
        });

        // Evento para agregar al carrito
        const botonAgregar = columna.querySelector('.btn-agregar');
        botonAgregar.addEventListener('click', () => {
            agregarAlCarrito(juego);
        });

        contenedorJuegos.appendChild(columna);
    });
}

/**
 * Agrega un producto al carrito y actualiza la vista
 */
function agregarAlCarrito(juego) {
    carrito.push(juego);
    actualizarDOMCarrito();
}

/**
 * Actualiza la tabla del carrito en el DOM
 */
function actualizarDOMCarrito() {
    cuerpoCarrito.innerHTML = '';

    // Estado vacío
    if (carrito.length === 0) {
        cuerpoCarrito.innerHTML = '<tr><td colspan="3" class="text-muted">El carrito está vacío.</td></tr>';
        totalCarrito.textContent = '$0';
        return;
    }

    let total = 0;

    // Agregar filas a la tabla
    carrito.forEach((producto, index) => {
        total += producto.precio;

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${producto.titulo}</td>
            <td class="text-success fw-bold">$${producto.precio.toLocaleString('es-CL')}</td>
            <td>
                <button class="btn btn-sm btn-outline-danger" onclick="eliminarDelCarrito(${index})">
                    Eliminar
                </button>
            </td>
        `;
        cuerpoCarrito.appendChild(fila);
    });

    totalCarrito.textContent = `$${total.toLocaleString('es-CL')}`;
}

/**
 * Elimina un producto específico del carrito
 */
function eliminarDelCarrito(indice) {
    carrito.splice(indice, 1);
    actualizarDOMCarrito();
}

// Inicialización
cargarJuegos();

// Formulario de contacto
const formularioContacto = document.getElementById('formulario-contacto');
const mensajeAlerta = document.getElementById('mensaje-alerta');

/**
 * Maneja el evento de envío del formulario de contacto
 */
function manejarEnvioFormulario(evento) {
    evento.preventDefault();

    const nombre = document.getElementById('nombre').value.trim();
    const email = document.getElementById('email').value.trim();
    const mensaje = document.getElementById('mensaje').value.trim();

    // Validación
    if (nombre === '' || email === '' || mensaje === '') {
        mostrarAlerta('Por favor, completa todos los campos antes de enviar.', 'danger');
        return;
    }

    // Confirmación de envío
    mostrarAlerta(`¡Gracias por contactarnos, ${nombre}! Hemos recibido tu mensaje.`, 'success');
    formularioContacto.reset();
}

/**
 * Muestra alertas en el DOM
 */
function mostrarAlerta(texto, tipo) {
    mensajeAlerta.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${texto}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
        </div>
    `;
}

if (formularioContacto) {
    formularioContacto.addEventListener('submit', manejarEnvioFormulario);
}

// Búsqueda de productos
const formularioBusqueda = document.getElementById('formulario-busqueda');
const inputBusqueda = document.getElementById('input-busqueda');

/**
 * Filtra los productos basados en el término de búsqueda
 */
formularioBusqueda.addEventListener('submit', (evento) => {
    evento.preventDefault();

    const terminoBusqueda = inputBusqueda.value.toLowerCase().trim();

    if (terminoBusqueda === '') {
        alert('Por favor, ingresa el nombre de un juego para buscar.');
        return;
    }

    const tarjetasJuegos = document.querySelectorAll('#contenedor-juegos .col-12');
    let juegosEncontrados = 0;

    tarjetasJuegos.forEach(tarjeta => {
        const tituloJuego = tarjeta.querySelector('.card-title').textContent.toLowerCase();

        if (tituloJuego.includes(terminoBusqueda)) {
            tarjeta.style.display = 'block';
            juegosEncontrados++;
        } else {
            tarjeta.style.display = 'none';
        }
    });

    if (juegosEncontrados === 0) {
        alert(`No se encontraron juegos que coincidan con "${terminoBusqueda}".`);
    }

    formularioBusqueda.reset();
});
