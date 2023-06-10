package com.baloot.baloot.controllers.authentication;

import com.baloot.baloot.services.BalootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
public class LoginController {
    @Autowired
    private BalootService balootService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, Object> payLoad) {
        try {
            String jwtToken = balootService.loginByEmail(payLoad.get("username").toString(), payLoad.get("password").toString());
            System.out.println(jwtToken);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);
            headers.add("userEmail", payLoad.get("username").toString());
            headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "userEmail"));
            return ResponseEntity.ok().headers(headers).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
