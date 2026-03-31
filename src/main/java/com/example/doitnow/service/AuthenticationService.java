package com.example.doitnow.service;

import com.example.doitnow.dto.auth.AuthenticationRequest;
import com.example.doitnow.dto.auth.AuthenticationResponse;
import com.example.doitnow.dto.auth.RegisterRequest;
import com.example.doitnow.model.User;
import com.example.doitnow.repository.InMemoryUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Dans service/AuthenticationService.java
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final InMemoryUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur.
     * 1. Crée l'utilisateur avec le mot de passe hashé
     * 2. Sauvegarde l'utilisateur
     * 3. Génère un token JWT
     * 4. Retourne le token
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Vérifier si l'email est déjà utilisé
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un compte avec cet email existe déjà");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // HACHAGE !
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Connexion d'un utilisateur existant.
     * 1. Authentifie via AuthenticationManager (vérifie email + mot de passe)
     * 2. Si succès, récupère l'utilisateur
     * 3. Génère un nouveau token JWT
     * 4. Retourne le token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // AuthenticationManager vérifie les identifiants.
        // Si invalides, il lève une AuthenticationException → 401 automatique
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si on arrive ici, l'authentification a réussi
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // Ne devrait jamais arriver car on vient de s'authentifier

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
