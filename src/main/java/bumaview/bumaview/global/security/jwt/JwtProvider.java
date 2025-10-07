package bumaview.bumaview.global.security.jwt;

import bumaview.bumaview.global.properties.JwtProperties;
import bumaview.bumaview.global.security.jwt.exception.InvalidJsonWebTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey secretKey;
    private final Long accessExpiration;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.accessExpiration = jwtProperties.getAccessExpiration();
    }

    public String createAccessToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("madeBy", "bumaview")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }

    public String getAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidJsonWebTokenException();
        }
        return jwtVerifyAccessToken(authorizationHeader.substring(7));
    }

    private String jwtVerifyAccessToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new InvalidJsonWebTokenException();
            }
            
            String madeBy = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("madeBy", String.class);
            if (madeBy == null || !madeBy.equals("bumaview")) {
                throw new InvalidJsonWebTokenException();
            }
            return token;
        } catch (JwtException e) {
            throw new InvalidJsonWebTokenException();
        }
    }
}