/**
 * Componente Carousel (Banner Hero).
 * Presenta imágenes promocionales grandes iterando sobre un arreglo.
 */
function Carousel() {
    const slides = [
        {
            id: 0,
            img: "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1920&q=80",
            title: "Nuevos Lanzamientos RPG",
            desc: "Explora mundos abiertos increíbles y vive aventuras épicas."
        },
        {
            id: 1,
            img: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=1920&q=80",
            title: "Hardware de Última Generación",
            desc: "Equípate con las mejores consolas y accesorios del mercado."
        },
        {
            id: 2,
            img: "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=1920&q=80",
            title: "Compite al Máximo Nivel",
            desc: "Los mejores juegos competitivos y shooters en primera persona."
        }
    ]

    return (
        <div id="carruselDestacados" className="carousel slide my-4" data-bs-ride="carousel" data-bs-interval="3000">

            {/* Indicadores Dinámicos */}
            <div className="carousel-indicators">
                {slides.map((slide, index) => (
                    <button
                        key={`indicator-${slide.id}`}
                        type="button"
                        data-bs-target="#carruselDestacados"
                        data-bs-slide-to={index}
                        className={index === 0 ? "active" : ""}
                        aria-current={index === 0 ? "true" : "false"}
                        aria-label={`Slide ${index + 1}`}>
                    </button>
                ))}
            </div>

            {/* Contenido Dinámico del Carrusel */}
            <div className="carousel-inner shadow" style={{ borderRadius: '10px' }}>
                {slides.map((slide, index) => (
                    <div key={slide.id} className={`carousel-item ${index === 0 ? "active" : ""}`}>
                        <img
                            src={slide.img}
                            className="d-block w-100 object-fit-cover"
                            style={{ height: '50vh', minHeight: '300px' }}
                            loading={index === 0 ? "eager" : "lazy"}
                            alt={slide.title}
                        />
                        <div className="carousel-caption d-none d-sm-block p-2"
                            style={{ backgroundColor: 'rgba(0,0,0,0.6)', borderRadius: '10px' }}>
                            <h3 className="fs-5 fs-md-3">{slide.title}</h3>
                            <p className="d-none d-md-block text-white-50">{slide.desc}</p>
                        </div>
                    </div>
                ))}
            </div>

            {/* Controles */}
            <button className="carousel-control-prev" type="button" data-bs-target="#carruselDestacados" data-bs-slide="prev">
                <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Anterior</span>
            </button>
            <button className="carousel-control-next" type="button" data-bs-target="#carruselDestacados" data-bs-slide="next">
                <span className="carousel-control-next-icon" aria-hidden="true"></span>
                <span className="visually-hidden">Siguiente</span>
            </button>
        </div>
    )
}

export default Carousel
