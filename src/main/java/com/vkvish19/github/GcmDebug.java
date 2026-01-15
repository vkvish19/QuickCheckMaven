package com.vkvish19.github;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class GcmDebug
{

    // This method must be identical to the one you use in production
    private static byte[] getKeyBytes(String key)
    {
        if (key == null || key.length() != 32)
        {
            throw new IllegalArgumentException("Key must be 32 hex chars.");
        }
        int keyLen = key.length() / 2;
        byte[] keyBytes = new byte[keyLen];
        for (int i = 0; i < keyLen; i++)
        {
            String data = key.substring(i * 2, i * 2 + 2);
            keyBytes[i] = (byte) Integer.parseInt(data, 16);
        }
        return keyBytes;
    }

    public static void main(String[] args) throws Exception
    {
        // --- 1. Use a KNOWN key ---
        String hexKey = "9a8d5a01aa3320502837c5f9c2b6033e"; // Example key with high-bit values
        byte[] keyBytes = getKeyBytes(hexKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // --- 2. Use a KNOWN Nonce/IV (16 bytes) ---
        byte[] iv = new byte[16];
        // For a real test, you might use a known pattern, e.g., all zeros.
        // For this run, a random one is fine as long as we print it.
        new SecureRandom().nextBytes(iv);

        // --- 3. Check for Associated Data (AAD) ---
        // **** THIS IS THE MOST IMPORTANT PART TO VERIFY ****
        // If your production code uses AAD, ADD THE CALL TO cipher.updateAAD() HERE.
        // For example:
        // byte[] aad = "my-secret-aad".getBytes(StandardCharsets.UTF_8);
        byte[] aad = null; // Assume no AAD for now. Change if needed.

        // --- 4. Encrypt a KNOWN plaintext ---
        byte[] plaintext = "TEST".getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);

        if (aad != null && aad.length > 0)
        {
            cipher.updateAAD(aad);
        }

        // doFinal automatically appends the tag
        byte[] encryptedDataWithTag = cipher.doFinal(plaintext);

        // --- 5. Print EVERYTHING for comparison ---
        System.out.println("--- Java GCM Debug Output ---");
        System.out.println("Key (Base64):      " + Base64.getEncoder().encodeToString(keyBytes));
        System.out.println("Nonce/IV (Base64): " + Base64.getEncoder().encodeToString(iv));
        System.out.println("AAD (Base64):        " + (aad != null ? Base64.getEncoder().encodeToString(aad) : "null"));
        System.out.println("Plaintext:         " + new String(plaintext, StandardCharsets.UTF_8));
        System.out.println("Final Ciphertext (Base64): " + Base64.getEncoder().encodeToString(encryptedDataWithTag));
    }
}