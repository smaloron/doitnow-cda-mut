package com.example.doitnow.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    // La clé secrète utilisée pour signer les tokens.
    // IMPORTANT : En production, cette clé doit être dans un fichier
    // de configuration externe (application.yml) ou un gestionnaire de secrets,
    // JAMAIS en dur dans le code source !
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Durée de validité du token : 24 heures (en millisecondes)
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24;

    /**
     * Extrait le username (email) du token JWT.
     * C'est le "subject" (sub) du payload.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Méthode générique pour extraire n'importe quel claim du token.
     * Utilise un Function<Claims, T> pour être flexible.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     * Le token contient :
     * - subject : l'email de l'utilisateur
     * - issuedAt : la date de création
     * - expiration : la date d'expiration
     * - signature : la signature avec notre clé secrète
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Surcharge permettant d'ajouter des claims supplémentaires
     * (par ex: rôles, permissions, etc.)
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)                           // Claims personnalisés
                .setSubject(userDetails.getUsername())             // L'identité (email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // Expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Signature HMAC-SHA256
                .compact();                                        // Construit la chaîne finale
    }

    /**
     * Vérifie qu'un token est valide :
     * 1. Le username dans le token correspond à l'utilisateur
     * 2. Le token n'est pas expiré
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Vérifie si le token est expiré en comparant la date d'expiration
     * avec la date actuelle.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Décode et vérifie le token, puis retourne tous les claims.
     * Si la signature est invalide ou le token malformé,
     * une exception sera levée automatiquement par JJWT.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())  // La même clé que pour la signature
                .build()
                .parseClaimsJws(token)           // Parse et vérifie la signature
                .getBody();                      // Retourne le payload (claims)
    }

    /**
     * Convertit notre clé secrète (String hexadécimale) en objet Key
     * utilisable par l'algorithme HMAC-SHA.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
