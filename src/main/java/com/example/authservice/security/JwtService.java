package com.example.authservice.security;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import java.security.Key;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "ZHVtbXktc2VjcmV0LWtleS1mb3Itand0LWF1dGhlbnRpY2F0aW9uLWR1bW15LXNlY3JldC1rZXk=";

    private static final long JWT_EXPIRATION = 1000 * 60 * 60;
    private Key getSignInKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(keyBytes);

    }
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()

                .subject(userDetails.getUsername())

                .issuedAt(new Date(System.currentTimeMillis()))

                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))

                .signWith(getSignInKey(), SignatureAlgorithm.HS256)

                .compact();

    }
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);

    }
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith((javax.crypto.SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);

    }
    public boolean isTokenValid(String token,
                                UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);

    }
}
