package cl.duoc.ejemplo.ms.administracion.archivos.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;

@Service
public class S3Service {

    @Autowired
    private S3Template s3Template;

    // Ruta donde el contenedor Docker verá el disco EFS
    private final String EFS_PATH = "/app/efs";

    /**
     * REQUISITO 1: Crear guía de despacho en EFS (Almacenamiento Temporal)
     */
    public String crearGuiaEnEFS(String transportista, String idGuia, String contenido) throws IOException {
        // Asegurar que la carpeta EFS exista en el contenedor
        File efsDir = new File(EFS_PATH);
        if (!efsDir.exists()) {
            efsDir.mkdirs();
        }

        String fileName = "guia_" + idGuia + ".txt";
        File guiaFile = new File(efsDir, fileName);

        // Crear y escribir el archivo localmente en el EFS
        try (FileWriter writer = new FileWriter(guiaFile)) {
            writer.write("--- GUIA DE DESPACHO ---\n");
            writer.write("Transportista: " + transportista + "\n");
            writer.write("ID Guia: " + idGuia + "\n");
            writer.write("Contenido: " + contenido + "\n");
        }
        return guiaFile.getAbsolutePath();
    }

    /**
     * REQUISITO 2: Subida automática a AWS S3 desde EFS
     * Formato requerido: /fecha/transportista/guia123.pdf
     */
    public String subirGuiaAS3(String bucketName, String transportista, String idGuia) throws IOException {
        String fileName = "guia_" + idGuia + ".txt";
        Path efsFilePath = Paths.get(EFS_PATH, fileName);

        if (!Files.exists(efsFilePath)) {
            throw new IOException("La guia no se encuentra en el EFS temporal: " + efsFilePath.toString());
        }

        // Obtener fecha actual para la estructura de carpetas (ej. /20260606/)
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String s3Key = fecha + "/" + transportista + "/" + fileName;

        // Subir a S3 usando el archivo desde EFS
        s3Template.upload(bucketName, s3Key, Files.newInputStream(efsFilePath));

        // (Opcional) Borrar archivo temporal del EFS tras subirlo a S3
        Files.delete(efsFilePath);

        return s3Key;
    }

    /**
     * REQUISITOS RESTANTES: Descargar y Eliminar desde S3
     */
    public S3Resource descargarDeS3(String bucketName, String key) {
        return s3Template.download(bucketName, key);
    }

    public void eliminarDeS3(String bucketName, String key) {
        s3Template.deleteObject(bucketName, key);
    }
}