package com.example.doitnow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Dans model/User.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    private String password;

    // --- Implémentation de UserDetails ---
    // Spring Security utilise cette interface pour connaître
    // l'identité et les droits de l'utilisateur.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Pour l'instant, pas de rôles. On retourne une liste vide.
        return List.of();
    }

    @Override
    public String getUsername() {
        // Spring Security utilise "username". Dans notre cas, c'est l'email.
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Le compte n'expire pas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Le compte n'est pas verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Les identifiants n'expirent pas
    }

    @Override
    public boolean isEnabled() {
        return true; // Le compte est activé
    }
}
