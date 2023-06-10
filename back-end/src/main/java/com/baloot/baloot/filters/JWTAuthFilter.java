package com.baloot.baloot.filters;

import com.baloot.baloot.Utils.JWTUtils;
import com.baloot.baloot.services.BalootService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebFilter;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.apache.http.HttpHeaders;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
//@Order(1)
@WebFilter("/*")
public class JWTAuthFilter implements Filter {

    @Autowired
    private BalootService balootService;

    public void init(FilterConfig config) throws ServletException {
//        System.out.println("init");
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("jwt filter url ");

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getServletPath().contains("login") || req.getServletPath().contains("register") || req.getServletPath().contains("logout") || req.getServletPath().contains("callback")) {
            chain.doFilter(request, response);
            return;
        }
        String jwt = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (jwt == null || jwt.equals("")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"Error\" : \" No JWT token \"}");
            res.setHeader("Content-Type", "application/json;charset=UTF-8");
            return;
        }
        System.out.println("The code : ");
        System.out.println(jwt);
        String signKey = JWTUtils.signKey;

        SecretKey signature_type = new SecretKeySpec(signKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Jws<Claims> claimsJws;

        try {
            claimsJws = Jwts.parserBuilder()
                    .setSigningKey(signature_type)
                    .build()
                    .parseClaimsJws(jwt);
            if (claimsJws.getBody().getExpiration().before(Date.from(Instant.now()))) {
                throw new JwtException("Token is expired");
            }
            req.setAttribute("userEmail", claimsJws.getBody().get("userEmail"));
            if (balootService.getLoggedInUser() == null) {
                balootService.loginWithJwtToken(claimsJws.getBody().get("userEmail").toString());
            }
        }
        catch (JwtException je) {
            System.out.println(je.getMessage());
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"Error\" : \" JWT Expired \"}");
            res.setHeader("Content-Type", "application/json;charset=UTF-8");
            return;
        }

        chain.doFilter(request, response);
    }

}
