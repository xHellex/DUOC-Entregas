package cl.duoc.ejemplo.ms.administracion.archivos.controller;

import cl.duoc.ejemplo.ms.administracion.archivos.service.S3Service;
import io.awspring.cloud.s3.S3Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/guias")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Endpoint 1: Crear guía en EFS temporal
    @PostMapping("/generar")
    public ResponseEntity<String> generarGuiaTemporal(
            @RequestParam String transportista,
            @RequestParam String idGuia,
            @RequestParam String contenido) {
        try {
            String path = s3Service.crearGuiaEnEFS(transportista, idGuia, contenido);
            return ResponseEntity.ok("Guia generada en EFS: " + path);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error en EFS: " + e.getMessage());
        }
    }

    // Endpoint 2: Subir a S3 (Mueve de EFS a S3)
    @PostMapping("/subirS3")
    public ResponseEntity<String> subirGuiaAS3(
            @RequestParam String bucketName,
            @RequestParam String transportista,
            @RequestParam String idGuia) {
        try {
            String s3Key = s3Service.subirGuiaAS3(bucketName, transportista, idGuia);
            return ResponseEntity.ok("Guia subida exitosamente a S3. Ruta: " + s3Key);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error subiendo a S3: " + e.getMessage());
        }
    }

    // Endpoint 3: Descargar de S3
    @GetMapping("/descargar")
    public ResponseEntity<Resource> descargarGuia(
            @RequestParam String bucketName,
            @RequestParam String s3Key) {
        try {
            S3Resource s3Resource = s3Service.descargarDeS3(bucketName, s3Key);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Resource.getFilename() + "\"")
                    .body(new InputStreamResource(s3Resource.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint 4: Eliminar de S3
    @DeleteMapping("/eliminar")
    public ResponseEntity<String> eliminarGuia(
            @RequestParam String bucketName,
            @RequestParam String s3Key) {
        s3Service.eliminarDeS3(bucketName, s3Key);
        return ResponseEntity.ok("Guia eliminada de S3: " + s3Key);
    }
}