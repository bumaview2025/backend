package bumaview.bumaview.global.security.jwt;

import bumaview.bumaview.global.properties.JwtProperties;
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
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.accessExpiration = jwtProperties.getAccessExpiration();
    }

    public String createAccessToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .claim("madeBy", "nuri")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getAccessToken(String authorizationHeader) {
        return jwtVerifyAccessToken(authorizationHeader.substring(7));
    }

    private String jwtVerifyAccessToken(String token) {
        try {
            String madeBy = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("madeBy", String.class);
            if(!madeBy.equals("nuri")) throw new InvalidJsonWebTokenException();
            return token;
        } catch (JwtException e) {
            throw new InvalidJsonWebTokenException();
        }
    }
}