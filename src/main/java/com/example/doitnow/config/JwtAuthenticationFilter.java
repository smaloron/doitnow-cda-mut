package com.example.doitnow.config;

import com.example.doitnow.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si pas de header ou pas de Bearer → passer au filtre suivant
        //    (la requête sera rejetée plus tard par Spring Security si l'endpoint est protégé)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (tout ce qui vient après "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Extraire le username (email) du token
        final String userEmail = jwtService.extractUsername(jwt);

        // 5. Si on a un email ET que l'utilisateur n'est pas déjà authentifié
        //    dans le contexte de sécurité (évite de refaire le travail)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Charger les détails de l'utilisateur depuis notre repository
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Vérifier que le token est valide
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Créer un objet Authentication avec les détails de l'utilisateur
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,     // Principal (l'identité)
                                null,            // Credentials (null car on n'a plus besoin du mdp)
                                userDetails.getAuthorities() // Les rôles/permissions
                        );

                // 9. Attacher les détails de la requête web
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. Mettre l'authentification dans le SecurityContextHolder
                //     → Spring Security sait maintenant QUI fait la requête
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Passer au filtre suivant dans la chaîne
        filterChain.doFilter(request, response);
    }
}
