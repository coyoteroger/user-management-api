package com.nisum.app.application.service;

import java.util.Date;
import java.util.function.Function;
import io.jsonwebtoken.Claims;


public interface IJwtService {
    String generateToken(String email);
    String extractEmail(String token);

    Date extractExpiration(String token);


    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Boolean isTokenExpired(String token);

    Boolean validateToken(String token, String email);
}
