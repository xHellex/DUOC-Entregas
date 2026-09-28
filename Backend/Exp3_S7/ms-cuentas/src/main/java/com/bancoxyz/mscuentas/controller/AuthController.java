package com.bancoxyz.mscuentas.controller;

import com.bancoxyz.mscuentas.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticacion. Emite un token JWT para credenciales validas.
 * Simplificado para fines academicos: valida un usuario/clave fijo.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String clave = credenciales.get("clave");
        if ("admin".equals(usuario) && "admin123".equals(clave)) {
            String token = jwtUtil.generarToken(usuario);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
    }
}
