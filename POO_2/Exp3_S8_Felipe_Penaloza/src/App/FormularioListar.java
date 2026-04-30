/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import DAO.CarteleraDAO;
import Modelo.Pelicula;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FormularioListar extends JFrame {
    private final CarteleraDAO dao = new CarteleraDAO();
    private final JComboBox<String> cbGenero;
    private final JTextField txtAnoDesde = new JTextField(6);
    private final JTextField txtAnoHasta = new JTextField(6);
    private final JButton btnFiltrar = new JButton("Filtrar");
    private final JButton btnRefrescar = new JButton("Refrescar");
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable tabla = new JTable(model);

    private static final String[] GENEROS = {"Todos","Accion","Comedia","Drama","Terror","CienciaFiccion","Romance","Documental"};

    public FormularioListar() {
        setTitle("Cine Magenta - Listado");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 450);
        setLocationRelativeTo(null);

        cbGenero = new JComboBox<>(GENEROS);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Género:"));
        filtros.add(cbGenero);
        filtros.add(new JLabel("Año desde:"));
        filtros.add(txtAnoDesde);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtAnoHasta);
        filtros.add(btnFiltrar);
        filtros.add(btnRefrescar);

        model.setColumnIdentifiers(new Object[]{"ID","Título","Director","Año","Duración","Género"});
        tabla.setAutoCreateRowSorter(true);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> cargarConFiltros());
        btnRefrescar.addActionListener(e -> cargarTodos());

        cargarTodos();
        setVisible(true);
    }

    private void cargarTodos() {
        try {
            List<Pelicula> lista = dao.listarPeliculas(null, null, null);
            poblarTabla(lista);
        } catch (Exception ex) {
            DialogUtils.showError(this, "Error al cargar listado: " + ex.getMessage());
        }
    }

    private void cargarConFiltros() {
        try {
            String gen = (String) cbGenero.getSelectedItem();
            if ("Todos".equals(gen)) gen = null;
            Integer desde = null, hasta = null;
            if (!txtAnoDesde.getText().isBlank()) desde = Integer.valueOf(txtAnoDesde.getText().trim());
            if (!txtAnoHasta.getText().isBlank()) hasta = Integer.valueOf(txtAnoHasta.getText().trim());
            List<Pelicula> lista = dao.listarPeliculas(gen, desde, hasta);
            poblarTabla(lista);
        } catch (NumberFormatException nfe) {
            DialogUtils.showError(this, "Formato de año inválido.");
        } catch (Exception ex) {
            DialogUtils.showError(this, "Error al filtrar: " + ex.getMessage());
        }
    }

    private void poblarTabla(List<Pelicula> lista) {
        model.setRowCount(0);
        for (Pelicula p : lista) {
            model.addRow(new Object[]{
                p.getId(), p.getTitulo(), p.getDirector(), p.getAno(), p.getDuracion(), p.getGenero()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FormularioListar::new);
    }

    
}
