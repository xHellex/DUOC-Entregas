/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.ui;

import javax.swing.*;
import java.awt.*;
import App.ui.panels.ClientePanel;
import App.ui.panels.EquipoPanel;
import App.ui.panels.VentaPanel;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Computec - Sistema de Ventas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JMenuBar mb = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem miSalir = new JMenuItem("Salir");
        miSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(miSalir);

        mb.add(menuArchivo);
        setJMenuBar(mb);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", new ClientePanel());
        tabs.addTab("Equipos", new EquipoPanel());
        tabs.addTab("Ventas", new VentaPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
