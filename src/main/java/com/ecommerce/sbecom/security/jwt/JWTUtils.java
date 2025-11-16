package com.ecommerce.sbecom.security.jwt;

import com.ecommerce.sbecom.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtils {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
    //1 day
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtexpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    //Extracting JWT from Cookies thrgh HttpServletRequest
    public String getJWTFromCookies(HttpServletRequest request) {
           Cookie cookie= WebUtils.getCookie(request, jwtCookie);
              if(cookie!=null) {
                    return cookie.getValue();
                }
                else {
                    return null;}
    }
    public String getJWTFromHeader(HttpServletRequest request) {
        String bearerToken= request.getHeader("Authorization");
       if(bearerToken!=null && bearerToken.startsWith("Bearer ")) {
           return bearerToken.substring(7);
       }
       return null;
    }

    public ResponseCookie generateJWTCookie(UserDetailsImpl UserPrincipal) {
        String jwt= generateTokenFromUsername(UserPrincipal.getUsername());
        ResponseCookie cookie= ResponseCookie.from(jwtCookie, jwt)
                .path("/api")//cookie will be sent for all the endpoints starting with /api
                .maxAge(24*60*60)//1 day
                .httpOnly(false)
                .secure(false)//not accessible from javascript
                .build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie= ResponseCookie.from(jwtCookie, null)
                .path("/api")//cookie will be sent for all the endpoints starting with /api
                .build();
        return cookie;
    }

    //Generating Token from Username
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtexpirationMs)))
                .signWith(key())
                .compact();
    }

    //Getting username from token
    public String getUsernameFromJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //Generate Signing Key
    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }
    //Validate Token
    public boolean validateToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
               return true;
        }   catch (MalformedJwtException e ) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
           catch (ExpiredJwtException e ) {
           logger.error("JWT token is Expired: {}", e.getMessage());
        }
           catch (UnsupportedJwtException e ) {
           logger.error("JWT token is unsupported: {}", e.getMessage());
        }
            catch (IllegalArgumentException e ) {
           logger.error("JWT claims string is Empty: {}", e.getMessage());
        }
        return false;
    }
}
