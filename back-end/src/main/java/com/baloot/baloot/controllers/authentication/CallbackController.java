package com.baloot.baloot.controllers.authentication;

import com.baloot.baloot.Exceptions.EmailAlreadyExistsException;
import com.baloot.baloot.Exceptions.UsernameAlreadyExistsException;
import com.baloot.baloot.Utils.JWTUtils;
import com.baloot.baloot.services.BalootService;
import com.baloot.baloot.services.authentication.RegisterService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static javax.xml.bind.DatatypeConverter.parseDateTime;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.*;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.text.*;
import java.util.Map;

@RestController
public class CallbackController {

    @Autowired
    private BalootService balootService;

    @Autowired
    private RegisterService registerService;

    private final String defaultOAuthPass = "github";

    private final String defaultOAuthAddress = "github-address";

    @GetMapping("/callback")
    public ResponseEntity callbackLogin(@RequestParam(value = "code", required = true) String code) throws IOException, InterruptedException, ParseException {
        if(balootService.userIsLoggedIn()) { //might be better to delete this section !
            String jwtToken = JWTUtils.createJWTToken(balootService.getLoggedInUser().getEmail());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);
            headers.add("userEmail", balootService.getLoggedInUser().getEmail());
            headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "userEmail"));
            return ResponseEntity.ok().headers(headers).build();
        }
        String clientId = "069d734c6c26ac7aaddd";
        String clientSecret = "3995e2a785c19af8c2c34d6df7805b01352167f0";
        String accessTokenUrl = String.format(
                "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code
        );
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(accessTokenUrl))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();
        if(responseBody==null) {
            System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
        }
        // Parse the access token from the response body
        String accessToken = null;
        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
        if (jsonObject.has("access_token")) {
            accessToken = jsonObject.get("access_token").getAsString();
        }

        // Use the access token to get user info from GitHub API
        if (accessToken != null) {
            System.out.println("having access token");
            String userInfoUrl = "https://api.github.com/user";
            HttpRequest userInfoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(userInfoUrl))
                    .GET()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> userInfoResponse = client.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());
            String userInfoResponseBody = userInfoResponse.body();

            // Parse the user info and do something with it
            JsonObject userInfoObject = JsonParser.parseString(userInfoResponseBody).getAsJsonObject();
//            String email = userInfoObject.get("email").getAsString();
            String username = userInfoObject.get("login").getAsString();
            String name = userInfoObject.get("name").getAsString(); // not needed here
            String bday = userInfoObject.get("created_at").getAsString();
            Calendar cal = parseDateTime(bday);
            cal.add(Calendar.YEAR, -18);
            Date date = cal.getTime();
            String birthdate = new SimpleDateFormat("yyyy-MM-dd").format(date).toString();

            String email = username + "@gmail.com";//comment it if issue with email is fixed
            System.out.println(username);
            System.out.println(name);
            System.out.println(birthdate);
            System.out.println(bday);
            try {
                registerService.registerUser(username, defaultOAuthPass, email, birthdate, defaultOAuthAddress);
            }
            catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
                try {
                    balootService.updateUser(username, defaultOAuthPass, birthdate, email, defaultOAuthAddress);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(balootService.getUserByEmail(email).getAddress());
            balootService.loginWithJwtToken(email);
            // Return a response to the client
            String jwtToken = JWTUtils.createJWTToken(email);
            System.out.println("token is : " + jwtToken);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtToken);
            headers.add("userEmail", email);
            headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "userEmail"));
            return ResponseEntity.ok().headers(headers).build();
        }
        return ResponseEntity.badRequest().build();
    }

}
