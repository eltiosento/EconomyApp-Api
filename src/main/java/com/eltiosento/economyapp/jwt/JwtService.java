package com.eltiosento.economyapp.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {

        Date actualDate = new Date();

        return Jwts
                .builder()
                .claim("role", user.getRole().getName())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .subject(user.getUsername())
                .issuedAt(actualDate)
                .expiration(new Date(actualDate.getTime() + jwtExpiration))
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);

    }

    public String getUsernameFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = getUsernameFromToken(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaimsFromToken(String token) {

        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);

    }

    private Date getExpirationToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {

        Date actualDate = new Date();

        return getExpirationToken(token).before(actualDate);
    }
}
