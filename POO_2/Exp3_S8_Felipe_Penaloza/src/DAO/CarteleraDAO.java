package DAO;

import Conexion.DatabaseConnection;
import Modelo.Pelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarteleraDAO {

    // LISTAR con filtros: genero (null o "Todos" = sin filtro), anoDesde/anoHasta (null = sin límite)
    public List<Pelicula> listarPeliculas(String generoFiltro, Integer anoDesde, Integer anoHasta) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id, titulo, director, ano, duracion, genero, creado_at FROM Cartelera");

        List<Object> params = new ArrayList<>();
        boolean whereAdded = false;

        if (generoFiltro != null && !generoFiltro.isBlank() && !generoFiltro.equalsIgnoreCase("Todos")) {
            sb.append(whereAdded ? " AND" : " WHERE").append(" genero = ?");
            params.add(generoFiltro);
            whereAdded = true;
        }
        if (anoDesde != null) {
            sb.append(whereAdded ? " AND" : " WHERE").append(" ano >= ?");
            params.add(anoDesde);
            whereAdded = true;
        }
        if (anoHasta != null) {
            sb.append(whereAdded ? " AND" : " WHERE").append(" ano <= ?");
            params.add(anoHasta);
            whereAdded = true;
        }
        sb.append(" ORDER BY id DESC");

        List<Pelicula> lista = new ArrayList<>();
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {

            // set params
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // BUSCAR POR ID
    public Pelicula buscarPorId(int id) throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE id = ?";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // BUSCAR POR TITULO (uno)
    public Pelicula buscarPorTitulo(String titulo) throws Exception {
        String sql = "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE LOWER(titulo) = LOWER(?) LIMIT 1";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean existePorTitulo(String titulo) throws Exception {
        String sql = "SELECT 1 FROM Cartelera WHERE LOWER(titulo) = LOWER(?) LIMIT 1";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titulo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // AGREGAR -> retorna la entidad con id asignado
    public Pelicula agregarPelicula(Pelicula p) throws Exception {
        if (p == null) throw new IllegalArgumentException("Pelicula nula");
        // validaciones mínimas en app
        p.setTitulo(p.getTitulo());
        p.setDirector(p.getDirector());
        p.setAno(p.getAno());
        p.setDuracion(p.getDuracion());
        p.setGenero(p.getGenero());

        // prevenir duplicados por título (aquí y DB tiene UNIQUE)
        if (existePorTitulo(p.getTitulo())) {
            throw new IllegalStateException("Ya existe una película con el mismo título.");
        }

        String sql = "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAno());
            ps.setInt(4, p.getDuracion());
            ps.setString(5, p.getGenero());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("No se insertó la película.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
            return p;
        } catch (SQLIntegrityConstraintViolationException ex) {
            // posible condición de carrera o UNIQUE violado
            throw new IllegalStateException("Duplicado detectado por la base de datos.", ex);
        }
    }

    // MODIFICAR (mantengo nombre 'modificar' para compatibilidad con tu UI)
    public boolean modificar(Pelicula p) throws Exception {
        if (p == null || p.getId() == null) throw new IllegalArgumentException("ID requerido");
        String sql = "UPDATE Cartelera SET titulo = ?, director = ?, ano = ?, duracion = ?, genero = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getTitulo());
            ps.setString(2, p.getDirector());
            ps.setInt(3, p.getAno());
            ps.setInt(4, p.getDuracion());
            ps.setString(5, p.getGenero());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR (mantengo nombre eliminarPorId)
    public boolean eliminarPorId(int id) throws Exception {
        String sql = "DELETE FROM Cartelera WHERE id = ?";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Insertar varias en transacción
    public void insertarMultipleTransactional(List<Pelicula> lista) throws Exception {
        if (lista == null || lista.isEmpty()) return;
        String sql = "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            c.setAutoCommit(false);
            try {
                for (Pelicula p : lista) {
                    // si quieres validar existencia por titulo dentro de la transacción, puedes hacerlo
                    ps.setString(1, p.getTitulo());
                    ps.setString(2, p.getDirector());
                    ps.setInt(3, p.getAno());
                    ps.setInt(4, p.getDuracion());
                    ps.setString(5, p.getGenero());
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    // helper mapper
    private Pelicula mapRow(ResultSet rs) throws SQLException {
        return new Pelicula(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("director"),
                rs.getInt("ano"),
                rs.getInt("duracion"),
                rs.getString("genero")
        );
    }
}
