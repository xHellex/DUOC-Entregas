/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.ui.panels;

import DAO.ClienteDAO;
import Modelo.Cliente;
import App.DialogUtils;

import javax.swing.*;
import java.awt.*;

public class ClientePanel extends JPanel {
    private final JTextField txtRut = new JTextField(12);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(30);
    private final JTextField txtComuna = new JTextField(15);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(12);
    private final ClienteDAO dao = new ClienteDAO();

    public ClientePanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(7,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("RUT:")); form.add(txtRut);
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Dirección:")); form.add(txtDireccion);
        form.add(new JLabel("Comuna:")); form.add(txtComuna);
        form.add(new JLabel("Email:")); form.add(txtEmail);
        form.add(new JLabel("Teléfono:")); form.add(txtTelefono);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> onAgregar());
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnLimpiar); acciones.add(btnAgregar);

        add(form, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    private void onAgregar() {
        try {
            Cliente c = new Cliente(
                txtRut.getText().trim(),
                txtNombre.getText().trim(),
                txtDireccion.getText().trim(),
                txtComuna.getText().trim(),
                txtEmail.getText().trim(),
                txtTelefono.getText().trim()
            );
            boolean ok = dao.agregar(c);
            if (ok) {
                DialogUtils.showInfo(this, "Cliente agregado correctamente.");
                limpiar();
            } else DialogUtils.showError(this, "No se pudo agregar cliente.");
        } catch (Exception ex) {
            DialogUtils.showError(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtRut.setText(""); txtNombre.setText(""); txtDireccion.setText(""); txtComuna.setText(""); txtEmail.setText(""); txtTelefono.setText("");
    }
}
