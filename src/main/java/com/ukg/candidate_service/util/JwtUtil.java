package com.ukg.candidate_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("classpath:public_key.pem")
    private Resource publicKeyResource;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(publicKeyResource.getInputStream()))) {
            String publicKeyPEM = reader.lines()
                .filter(line -> !line.startsWith("-----BEGIN") && !line.startsWith("-----END"))
                .collect(Collectors.joining());

            byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            this.publicKey = keyFactory.generatePublic(keySpec);

            System.out.println("Public Key successfully loaded");
        } catch (Exception e) {
            System.err.println("Error loading public key: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

    public List<String> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        List<?> authoritiesList = claims.get("authorities", List.class);
        if (authoritiesList == null) {
            return List.of();
        }
        return authoritiesList.stream()
                .filter(auth -> auth instanceof String)
                .map(auth -> (String) auth)
                .collect(Collectors.toList());
    }
}