package cl.paris.marketplace.ms.usuarios.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.PerfilRequest;
import cl.paris.marketplace.ms.usuarios.dto.PerfilResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioCompletoResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioRequest;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioResponse;
import cl.paris.marketplace.ms.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Usuarios",
    description = "Operaciones relacionadas con usuarios, perfiles y consultas consolidadas"
)
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ==========================================
    // ENDPOINTS: USUARIOS
    // ==========================================

    @Operation(
        summary = "Registrar usuario",
        description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Usuario registrado correctamente"
    )
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrarUsuario(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos necesarios para registrar un usuario",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "UsuarioEjemplo",
                            value = """
                            {
                              "email": "cliente@paris.cl",
                              "password": "12345678",
                              "rolId": 1
                            }
                            """
                        )
                    }
                )
            )
            @Valid @RequestBody UsuarioRequest request) {

        UsuarioResponse response = usuarioService.registrarUsuario(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @Operation(
        summary = "Buscar usuarios",
        description = "Obtiene todos los usuarios o filtra por correo electrónico"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada correctamente"
    )
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(
            @RequestParam(required = false) String email) {

        return ResponseEntity.ok(
                usuarioService.buscarUsuarios(email));
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene la información de un usuario específico"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Usuario encontrado"
    )
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(
            @PathVariable UUID id) {

        UsuarioResponse response =
                usuarioService.obtenerUsuarioPorId(id);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINT ADMINISTRATIVO (Llamado vía Feign)
    // ==========================================

    @Operation(
        summary = "Actualizar estado de baneo",
        description = "Permite banear o desbanear un usuario"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Estado actualizado correctamente"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado-baneo")
    public ResponseEntity<Void> actualizarEstadoBaneo(
            @PathVariable UUID id,
            @RequestParam Boolean baneo) {

        usuarioService.actualizarEstadoBaneo(id, baneo);

        return ResponseEntity.ok().build();
    }

    // ==========================================
    // ENDPOINTS: PERFILES
    // ==========================================

    @Operation(
        summary = "Crear perfil",
        description = "Crea el perfil asociado al usuario autenticado"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Perfil creado correctamente"
    )
    @PostMapping("/perfiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilResponse> crearPerfil(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos necesarios para crear un perfil",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "PerfilEjemplo",
                            value = """
                            {
                              "primerNombre": "Juan",
                              "segundoNombre": "Carlos",
                              "primerApellido": "Perez",
                              "segundoApellido": "Gonzalez",
                              "telefono": "+56912345678"
                            }
                            """
                        )
                    }
                )
            )
            @Valid @RequestBody PerfilRequest request,

            Authentication authentication) {

        String emailDelToken = authentication.getName();

        PerfilResponse response =
                usuarioService.crearPerfil(
                        request,
                        emailDelToken);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener perfil",
        description = "Obtiene el perfil asociado a un usuario"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Perfil encontrado"
    )
    @GetMapping("/{usuarioId}/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(
            @PathVariable UUID usuarioId) {

        PerfilResponse response =
                usuarioService.obtenerPerfilPorUsuarioId(usuarioId);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: VISTA CONSOLIDADA (Usa Feign)
    // ==========================================

    @Operation(
        summary = "Obtener usuario completo",
        description = "Obtiene usuario, rol, perfil y métodos de pago asociados"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Información consolidada obtenida correctamente"
    )
    @GetMapping("/{id}/completo")
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                usuarioService.obtenerUsuarioCompleto(id));
    }
}