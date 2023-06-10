package com.baloot.baloot.Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//SHA-256
public class HashString {

    public static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            hexString.append(String.format("%02x", b & 0xff));
        }
        return hexString.toString();
    }

    public static boolean checkPassword(String password, String hashedPassword) throws NoSuchAlgorithmException {
        String newHashedPassword = hashString(password);
        return newHashedPassword.equals(hashedPassword);
    }

}
