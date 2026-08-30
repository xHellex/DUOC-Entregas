package com.bancoxyz.batch.advanced;

/**
 * Excepcion de dominio que se lanza cuando un registro contiene datos
 * invalidos segun las reglas de negocio (montos negativos, tipos
 * desconocidos, etc.). Se usa junto con la SkipPolicy para que estas
 * filas se omitan de forma controlada sin abortar el Job.
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String mensaje) {
        super(mensaje);
    }
}
