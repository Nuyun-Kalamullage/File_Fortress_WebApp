package edu.sltc.vaadin.models;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

/**
 * @author Nuyun-Kalamullage
 * @IDE IntelliJ IDEA
 * @date 02/01/2024
 * @package edu.sltc.vaadin.data
 * @project_Name File_Fortress_WebApp
 */

public class PublicKeyHolder extends HashMap<String, String> {

    // Eagerly initialized singleton instance
    private static final PublicKeyHolder instance = new PublicKeyHolder();

    // Private constructor to prevent instantiation
    private PublicKeyHolder() {
        // Initialize if needed
    }

    // Method to get the singleton instance
    public static PublicKeyHolder getInstance() {
        return instance;
    }

    @Override
    public String get(Object key) {
        return super.get(key);
    }
    public PublicKey get(String key){
        byte[] publicKeyBytes = Base64.getDecoder().decode(super.get(key));
        // Create an X509EncodedKeySpec from the decoded bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        // Get the public key from the key specification
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
