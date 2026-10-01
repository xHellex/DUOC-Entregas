function Carousel() {
    return (
        <div id="carruselDestacados" className="carousel slide my-4" data-bs-ride="carousel" data-bs-interval="3000">

            {/* Indicadores */}
            <div className="carousel-indicators">
                <button type="button" data-bs-target="#carruselDestacados" data-bs-slide-to="0" className="active"
                    aria-current="true" aria-label="Slide 1"></button>
                <button type="button" data-bs-target="#carruselDestacados" data-bs-slide-to="1"
                    aria-label="Slide 2"></button>
                <button type="button" data-bs-target="#carruselDestacados" data-bs-slide-to="2"
                    aria-label="Slide 3"></button>
            </div>

            {/* Contenido del Carrusel */}
            <div className="carousel-inner shadow">
                {/* Primer slide */}
                <div className="carousel-item active">
                    <img src="https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1100&h=400&q=80"
                        className="d-block w-100" style={{ objectFit: 'cover', height: '50vh', minHeight: '350px' }}
                        alt="Lanzamiento de nuevo juego RPG" />
                    <div className="carousel-caption d-none d-md-block"
                        style={{ backgroundColor: 'rgba(0,0,0,0.6)', borderRadius: '10px' }}>
                        <h3>Nuevos Lanzamientos RPG</h3>
                        <p>Explora mundos abiertos increíbles y vive aventuras épicas.</p>
                    </div>
                </div>

                {/* Segundo slide */}
                <div className="carousel-item">
                    <img src="https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=1100&h=400&q=80"
                        className="d-block w-100" style={{ objectFit: 'cover', height: '50vh', minHeight: '350px' }}
                        alt="Consolas de última generación" />
                    <div className="carousel-caption d-none d-md-block"
                        style={{ backgroundColor: 'rgba(0,0,0,0.6)', borderRadius: '10px' }}>
                        <h3>Hardware de Última Generación</h3>
                        <p>Equípate con las mejores consolas y accesorios del mercado.</p>
                    </div>
                </div>

                {/* Tercer slide */}
                <div className="carousel-item">
                    <img src="https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=1100&h=400&q=80"
                        className="d-block w-100" style={{ objectFit: 'cover', height: '50vh', minHeight: '350px' }}
                        alt="Juegos multijugador y eSports" />
                    <div className="carousel-caption d-none d-md-block"
                        style={{ backgroundColor: 'rgba(0,0,0,0.6)', borderRadius: '10px' }}>
                        <h3>Compite al Máximo Nivel</h3>
                        <p>Los mejores juegos competitivos y shooters en primera persona.</p>
                    </div>
                </div>
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
