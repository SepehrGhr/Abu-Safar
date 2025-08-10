package ir.ac.kntu.abusafar.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecretString;

    @Value("${jwt.access_token.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey jwtSecretKey;

    @PostConstruct
    protected void init() {
        if (jwtSecretString.getBytes(StandardCharsets.UTF_8).length < 32) {
            LOGGER.warn("JWT Secret is too short! It should be at least 256 bits (32 bytes) long. Application will use a default secure key for now in dev, but this MUST be fixed for production.");
        }
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
