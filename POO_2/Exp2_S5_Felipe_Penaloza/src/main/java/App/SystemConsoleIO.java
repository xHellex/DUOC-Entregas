/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App;

import java.util.Scanner;

/**
 * Implementación real de ConsoleIO usando System.in/out.
 */
public class SystemConsoleIO implements ConsoleIO {
    private final Scanner scanner;

    public SystemConsoleIO() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }
}