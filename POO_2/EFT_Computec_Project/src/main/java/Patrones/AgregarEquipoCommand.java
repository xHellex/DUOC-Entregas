/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patrones;

import DAO.EquipoDAO;
import Modelo.Equipo;

public class AgregarEquipoCommand implements Command {
    private final EquipoDAO dao;
    private final Equipo equipo;

    public AgregarEquipoCommand(EquipoDAO dao, Equipo equipo) {
        this.dao = dao;
        this.equipo = equipo;
    }

    @Override
    public void execute() throws Exception {
        dao.insertar(equipo);
    }
}
