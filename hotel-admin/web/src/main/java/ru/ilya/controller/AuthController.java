package ru.ilya.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.ilya.dto.AuthResponse;
import ru.ilya.dto.LoginRequest;
import ru.ilya.exception.ApiError;
import ru.ilya.security.JwtTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService){
        this.authenticationManager=authenticationManager;
        this.jwtTokenService=jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest request){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();  //Возвращаем юзера созданного в HotelUserDetailsService
            List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            String role;
            if (authorities.isEmpty()){
                role="USER";
            } else{
                role = authorities.get(0).replace("ROLE_", "");
            }
            String token = jwtTokenService.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
            return ResponseEntity.ok(new AuthResponse(token, "Bearer", userDetails.getUsername(), role, authorities));
        } catch (AuthenticationException ex){
            ApiError body = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Неверный логин или пароль");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
}