package com.food.backend.service;

import com.food.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Slf4j
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    final private Logger logger = Logger.getLogger(JwtService.class.getName());

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean hasTokenExpired(String token) {

        try{
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        }
        catch (ExpiredJwtException e){
            return true;
        }

    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        try{
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }
        catch (ExpiredJwtException e){
           throw new ExpiredJwtException(null, null, "Token has expired");
        }
        catch (Exception e) {
            logger.info("Error while extracting claims from token");
            return null;
        }
    }

    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        extraClaims.put("enabled", userDetails.isEnabled());
        extraClaims.put("id", userDetails.getId());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getJwtExpirationTime() {
        return jwtExpiration;
    }
    private String buildToken(
            Map<String, Object> extraClaims,
            User userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token) {
        logger.info("extracting all claims from token");
        return  Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSignInKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        } catch (Exception e) {
            throw new RuntimeException("Error while getting secret key");
        }


    }

    public boolean isTokenValid(String token, User userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }





}
