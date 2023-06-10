package com.baloot.baloot.Utils;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class JWTUtils {

    public static String signKey = "--------------baloot2023--------------";

    public static String createJWTToken(String userEmail) {

        if(userEmail == null) {
            System.out.println("NULL");
        }
        System.out.println(userEmail + " is email");
//        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(hmac_secret_key), SignatureAlgorithm.HS256.getJcaName());

        SecretKey signature_type = new SecretKeySpec(signKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        String jwt_token = Jwts.builder()
                .claim("userEmail", userEmail)
                .setId(UUID.randomUUID().toString())
                .setIssuer("BALOOT_SYSTEM")                                                      // iss claim
                .setIssuedAt(Date.from(Instant.now()))                                          // iat claim
                .setExpiration(Date.from(Instant.now().plus(24, ChronoUnit.HOURS))) // exp claim
                .signWith(signature_type)
                .compact();
//        System.out.println("token is : " + jwt_token);
        return jwt_token;
    }

}
