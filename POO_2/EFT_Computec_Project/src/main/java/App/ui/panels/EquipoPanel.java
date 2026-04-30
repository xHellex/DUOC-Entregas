/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.ui.panels;

import DAO.EquipoDAO;
import Modelo.Desktop;
import Modelo.Equipo;
import Modelo.Laptop;
import App.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class EquipoPanel extends JPanel {
    private final JTextField txtModelo = new JTextField(20);
    private final JTextField txtCpu = new JTextField(20);
    private final JTextField txtDisco = new JTextField(8);
    private final JTextField txtRam = new JTextField(6);
    private final JTextField txtPrecio = new JTextField(10);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"LAPTOP","DESKTOP"});
    private final JTextField txtPantalla = new JTextField(6);
    private final JCheckBox chkTouch = new JCheckBox("Touch");
    private final JTextField txtPuertos = new JTextField(4);
    private final JTextField txtPotencia = new JTextField(6);
    private final JTextField txtFactor = new JTextField(8);

    private final EquipoDAO dao = new EquipoDAO();

    public EquipoPanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(11,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Modelo:")); form.add(txtModelo);
        form.add(new JLabel("CPU:")); form.add(txtCpu);
        form.add(new JLabel("Disco (MB):")); form.add(txtDisco);
        form.add(new JLabel("RAM (GB):")); form.add(txtRam);
        form.add(new JLabel("Precio:")); form.add(txtPrecio);
        form.add(new JLabel("Tipo:")); form.add(cbTipo);

        form.add(new JLabel("Pantalla (pulg):")); form.add(txtPantalla);
        form.add(new JLabel("Touch:")); form.add(chkTouch);
        form.add(new JLabel("Puertos USB:")); form.add(txtPuertos);

        form.add(new JLabel("Potencia Fuente (W):")); form.add(txtPotencia);
        form.add(new JLabel("Factor forma:")); form.add(txtFactor);

        JButton btnAgregar = new JButton("Agregar Equipo");
        btnAgregar.addActionListener(e -> onAgregar());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnAgregar);

        add(form, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
        cbTipo.addActionListener(e -> actualizarVisibilidad());
        actualizarVisibilidad();
    }

    private void actualizarVisibilidad() {
        String tipo = (String) cbTipo.getSelectedItem();
        boolean laptop = "LAPTOP".equals(tipo);
        txtPantalla.setEnabled(laptop); chkTouch.setEnabled(laptop); txtPuertos.setEnabled(laptop);
        txtPotencia.setEnabled(!laptop); txtFactor.setEnabled(!laptop);
    }

    private void onAgregar() {
        try {
            String tipo = (String) cbTipo.getSelectedItem();
            Equipo equipo;
            if ("LAPTOP".equals(tipo)) {
                equipo = new Laptop(
                    txtModelo.getText().trim(),
                    txtCpu.getText().trim(),
                    Integer.parseInt(txtDisco.getText().trim()),
                    Integer.parseInt(txtRam.getText().trim()),
                    new BigDecimal(txtPrecio.getText().trim()),
                    Double.parseDouble(txtPantalla.getText().trim()),
                    chkTouch.isSelected(),
                    Integer.parseInt(txtPuertos.getText().trim())
                );
            } else {
                equipo = new Desktop(
                    txtModelo.getText().trim(),
                    txtCpu.getText().trim(),
                    Integer.parseInt(txtDisco.getText().trim()),
                    Integer.parseInt(txtRam.getText().trim()),
                    new BigDecimal(txtPrecio.getText().trim()),
                    Integer.parseInt(txtPotencia.getText().trim()),
                    txtFactor.getText().trim()
                );
            }
            dao.insertar(equipo);
            DialogUtils.showInfo(this, "Equipo agregado correctamente (ID: " + equipo.getId() + ")");
            limpiar();
        } catch (NumberFormatException nf) {
            DialogUtils.showError(this, "Campos numéricos inválidos.");
        } catch (Exception ex) {
            DialogUtils.showError(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtModelo.setText(""); txtCpu.setText(""); txtDisco.setText(""); txtRam.setText(""); txtPrecio.setText("");
        txtPantalla.setText(""); chkTouch.setSelected(false); txtPuertos.setText("");
        txtPotencia.setText(""); txtFactor.setText("");
    }
}
