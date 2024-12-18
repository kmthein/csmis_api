package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${secrectkey}")
    private String SECRECT_KEY;

    public String extractStaffId(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public boolean isValid(String token, User user){
        return extractStaffId(token).equals(user.getStaffId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaim(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaim(String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user){
        String token = Jwts.builder()
                .setSubject(user.getStaffId())
                .claim("role", user.getRole().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getSigninKey())
                .compact();
        return token;
    }
    public SecretKey getSigninKey(){
        byte[] keyByte = Decoders.BASE64URL.decode(SECRECT_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); // Extract role claim
    }
}

