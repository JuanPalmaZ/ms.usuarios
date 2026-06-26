package cl.paris.marketplace.ms.usuarios.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.AuthRequest;
import cl.paris.marketplace.ms.usuarios.dto.AuthResponse;
import cl.paris.marketplace.ms.usuarios.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Autenticación",
    description = "Operaciones relacionadas con autenticación y generación de tokens JWT"
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(
        summary = "Iniciar sesión",
        description = "Valida las credenciales del usuario y genera un token JWT"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Login realizado correctamente"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Credenciales de acceso",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "LoginEjemplo",
                            value = """
                            {
                              "email": "cliente@paris.cl",
                              "password": "12345678"
                            }
                            """
                        )
                    }
                )
            )
            @RequestBody AuthRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generarToken(userDetails);

        return ResponseEntity.ok(
                new AuthResponse(token));
    }
}