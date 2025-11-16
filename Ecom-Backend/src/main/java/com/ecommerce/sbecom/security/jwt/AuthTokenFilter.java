package com.ecommerce.sbecom.security.jwt;

import com.ecommerce.sbecom.security.services.UserDetailsServicesImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private UserDetailsServicesImpl userDetailsService;

    private static final Logger logger= LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());
        try {
            String jwt = parseJWT(request);
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.getUsernameFromJWTToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

            filterChain.doFilter(request, response);
    }

      //  private String parseJWT(HttpServletRequest request) {
      //  String jwt= jwtUtils.getJWTFromCookies(request);
      //  logger.debug("jwt from header: {}", jwt);
      //  return jwt;
      //  }
    private String parseJWT(HttpServletRequest request) {
        String jwtFromCookie= jwtUtils.getJWTFromCookies(request);
        if(jwtFromCookie!=null) {
            return jwtFromCookie;
        }
        String jwtFromHeader= jwtUtils.getJWTFromHeader(request);
        if(jwtFromHeader!=null) {
            return jwtFromHeader;
        }
        return null;
    }
}
