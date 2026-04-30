/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;
import DAO.CarteleraDAO;
import Modelo.Pelicula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author joaqu
 */
public class FormularioModificarEliminar extends JFrame{
    private final JTextField txtId = new JTextField(5);
    private final JTextField txtTitulo = new JTextField(20);
    private final JTextField txtDirector = new JTextField(20);
    private final JTextField txtAno = new JTextField(6);
    private final JTextField txtDuracion = new JTextField(6);
    private final JTextField txtGenero = new JTextField(12);

    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");

    private final CarteleraDAO dao = new CarteleraDAO();

    public FormularioModificarEliminar() {
        setTitle("Cine Magenta - Modificar / Eliminar");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 260);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(7, 2, 8, 6));
        p.add(new JLabel("ID:"));
        p.add(txtId);

        p.add(new JLabel("Título:"));
        p.add(txtTitulo);

        p.add(new JLabel("Director:"));
        p.add(txtDirector);

        p.add(new JLabel("Año:"));
        p.add(txtAno);

        p.add(new JLabel("Duración (min):"));
        p.add(txtDuracion);

        p.add(new JLabel("Género:"));
        p.add(txtGenero);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnBuscar);
        acciones.add(btnModificar);
        acciones.add(btnEliminar);
        acciones.add(btnLimpiar);

        add(p, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        // Eventos
        btnBuscar.addActionListener(this::onBuscar);
        btnModificar.addActionListener(this::onModificar);
        btnEliminar.addActionListener(this::onEliminar);
        btnLimpiar.addActionListener(e -> limpiar());

        setVisible(true);
    }

    private void onBuscar(ActionEvent e) {
        try {
            int id = parseInt(txtId.getText(), "ID");
            Pelicula p = dao.buscarPorId(id);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "No existe película con ID " + id, "Atención", JOptionPane.WARNING_MESSAGE);
                limpiarDatos();
                return;
            }
            // Precarga
            txtTitulo.setText(p.getTitulo());
            txtDirector.setText(p.getDirector());
            txtAno.setText(String.valueOf(p.getAno()));
            txtDuracion.setText(String.valueOf(p.getDuracion()));
            txtGenero.setText(p.getGenero());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onModificar(ActionEvent e) {
        try {
            validarObligatorios();
            int id = parseInt(txtId.getText(), "ID");
            int ano = parseInt(txtAno.getText(), "Año");
            int dur = parseInt(txtDuracion.getText(), "Duración");

            if (dur <= 0) throw new IllegalArgumentException("Duración debe ser > 0");

            Pelicula p = new Pelicula(id,
                    txtTitulo.getText().trim(),
                    txtDirector.getText().trim(),
                    ano,
                    dur,
                    txtGenero.getText().trim());

            dao.modificar(p);
            JOptionPane.showMessageDialog(this, "Película modificada correctamente");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar(ActionEvent e) {
        try {
            int id = parseInt(txtId.getText(), "ID");
            int resp = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas eliminar la película ID " + id + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) return;

            dao.eliminarPorId(id);
            JOptionPane.showMessageDialog(this, "Película eliminada correctamente");
            limpiar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtId.setText("");
        limpiarDatos();
        txtId.requestFocus();
    }

    private void limpiarDatos() {
        txtTitulo.setText("");
        txtDirector.setText("");
        txtAno.setText("");
        txtDuracion.setText("");
        txtGenero.setText("");
    }

    private void validarObligatorios() {
        if (txtId.getText().isBlank() || txtTitulo.getText().isBlank()
                || txtDirector.getText().isBlank() || txtAno.getText().isBlank()
                || txtDuracion.getText().isBlank() || txtGenero.getText().isBlank()) {
            throw new IllegalArgumentException("Debes completar todos los campos.");
        }
    }

    private int parseInt(String s, String campo) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser numérico.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormularioModificarEliminar::new);
    }
}
