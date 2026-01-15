package com.vkvish19.github;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static byte[] s_garbage = { 1, 85, 7, 105, 7, 9, 47, 9, 67, 9, 6, 73, 6, 3, 5, (byte)164, 98, 6, 13, 0, 2 };
    private static final String s_signature = "ZNWK";
    public static void main(String[] args) throws Exception
    {
        List<Integer> list = new ArrayList<>();
        list.add(111);
        list.add(222);
        list.add(333);
        list.add(444);
        list.add(555);

        list.removeIf(i -> 333 == i);
        System.out.println("list.size() = " + list.size());
        System.out.println("list = " + list);
    }

    private static String deobfuscateWindows(String value)
    {
        String retVal;
        if(value.startsWith(s_signature))
        {
            value = value.substring(s_signature.length());
            byte[] dataBytes = hexStringToBytes(value);
            obfuscate(s_garbage, dataBytes);
            retVal = bytesToHexString(dataBytes);
        }
        else
        {
            retVal = value;
        }
        return retVal;
    }

    public static void obfuscate(byte[] pKey, byte[] dataBuffer) {
        int keyIndex = 0;
        int publicKeyLength = pKey.length;

        // Zip through the data stream encrypting or decrypting as we go
        for (int i = 0; i < dataBuffer.length; i++) {
            dataBuffer[i] = (byte) (dataBuffer[i] ^ pKey[keyIndex]);

            // Reset the key index if needed
            keyIndex++;
            if (keyIndex >= publicKeyLength) {
                keyIndex = 0;
            }
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] dataBytes = new byte[len / 2];

        for (int i = 0; i < dataBytes.length; i++) {
            String byteHex = hexString.substring(i * 2, i * 2 + 2);
            dataBytes[i] = (byte) Integer.parseInt(byteHex, 16);
        }

        return dataBytes;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}
