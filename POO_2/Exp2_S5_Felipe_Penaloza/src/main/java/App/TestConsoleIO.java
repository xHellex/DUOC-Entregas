/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementación simple de ConsoleIO para pruebas; provee entradas predefinidas.
 */
public class TestConsoleIO implements ConsoleIO {
    private final Queue<String> inputs;
    private final StringBuilder out = new StringBuilder();

    public TestConsoleIO(String... inputs) {
        this.inputs = new LinkedList<>();
        for (String s : inputs) this.inputs.add(s);
    }

    @Override
    public String readLine(String prompt) {
        out.append(prompt);
        String next = inputs.poll();
        if (next == null) return "";
        out.append(next).append("\n");
        return next;
    }

    @Override
    public void println(String text) {
        out.append(text).append("\n");
    }

    public String getOutput() {
        return out.toString();
    }
}