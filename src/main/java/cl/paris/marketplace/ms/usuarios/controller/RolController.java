package cl.paris.marketplace.ms.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.RolRequest;
import cl.paris.marketplace.ms.usuarios.dto.RolResponse;
import cl.paris.marketplace.ms.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
    name = "Roles",
    description = "Operaciones relacionadas con la administración de roles"
)
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final UsuarioService usuarioService;

    public RolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
        summary = "Crear rol",
        description = "Registra un nuevo rol en el sistema"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Rol creado correctamente"
    )
    @PostMapping
    public ResponseEntity<RolResponse> crearRol(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos necesarios para crear un rol",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "RolEjemplo",
                            value = """
                            {
                              "nombreRol": "CLIENTE"
                            }
                            """
                        )
                    }
                )
            )
            @Valid @RequestBody RolRequest request) {

        RolResponse response = usuarioService.crearRol(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @Operation(
        summary = "Listar roles",
        description = "Obtiene todos los roles registrados en el sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Listado obtenido correctamente"
    )
    @GetMapping
    public ResponseEntity<List<RolResponse>> listarRoles() {

        return ResponseEntity.ok(
                usuarioService.listarRoles());
    }
}   