/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.ui.panels;

import DAO.ClienteDAO;
import DAO.EquipoDAO;
import DAO.VentaDAO;
import Modelo.Equipo;
import Modelo.Venta;
import App.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class VentaPanel extends JPanel {
    private final JTextField txtRut = new JTextField(12);
    private final JTextField txtEquipoId = new JTextField(6);
    private final JTextField txtPrecio = new JTextField(10);
    private final JTextField txtDescuento = new JTextField(8);
    private final JTextField txtVendedor = new JTextField(12);

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    public VentaPanel() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(6,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("RUT Cliente:")); form.add(txtRut);
        form.add(new JLabel("ID Equipo:")); form.add(txtEquipoId);
        form.add(new JLabel("Precio unitario:")); form.add(txtPrecio);
        form.add(new JLabel("Descuento:")); form.add(txtDescuento);
        form.add(new JLabel("Vendedor:")); form.add(txtVendedor);

        JButton btnRegistrar = new JButton("Registrar Venta");
        btnRegistrar.addActionListener(e -> onRegistrar());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnRegistrar);

        add(form, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);
    }

    private void onRegistrar() {
        try {
            String rut = txtRut.getText().trim();
            int equipoId = Integer.parseInt(txtEquipoId.getText().trim());
            BigDecimal precioUnit = new BigDecimal(txtPrecio.getText().trim());
            BigDecimal descuento = txtDescuento.getText().trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(txtDescuento.getText().trim());
            String vendedor = txtVendedor.getText().trim();

            if (clienteDAO.buscarPorRut(rut) == null) {
                DialogUtils.showError(this, "Cliente no encontrado: " + rut);
                return;
            }
            Equipo equipo = equipoDAO.buscarPorId(equipoId);
            if (equipo == null) {
                DialogUtils.showError(this, "Equipo no encontrado: " + equipoId);
                return;
            }

            Venta v = new Venta(rut, equipoId, precioUnit, descuento, vendedor);
            ventaDAO.registrarVenta(v);
            DialogUtils.showInfo(this, "Venta registrada. ID: " + v.getId());
            limpiar();
        } catch (NumberFormatException nf) {
            DialogUtils.showError(this, "Formato numérico inválido.");
        } catch (Exception ex) {
            DialogUtils.showError(this, "Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        txtRut.setText(""); txtEquipoId.setText(""); txtPrecio.setText(""); txtDescuento.setText(""); txtVendedor.setText("");
    }
}
