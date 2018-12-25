package org.iota.ixi;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public final class Keys {

    static String publicKeyToString(PublicKey publicKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = fact.getKeySpec(publicKey, X509EncodedKeySpec.class);
            return Base64.getEncoder().encodeToString(spec.getEncoded());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static String privateKeyToString(PrivateKey privateKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = fact.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);
            byte[] packed = spec.getEncoded();
            String key64 = Base64.getEncoder().encodeToString(packed);
            Arrays.fill(packed, (byte) 0);
            return key64;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static PrivateKey privateKeyFromString(String s) {
        try {
            byte[] clear = Base64.getDecoder().decode(s);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey priv = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);
            return priv;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static PublicKey publicKeyFromString(String s) {
        try {
            byte[] data = Base64.getDecoder().decode(s);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(String data, String filename) {
        try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
            out.print(data);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static PublicKey readPublicKeyFromFile() throws IOException {

        if(!new File("public.key").exists())
            return null;

        BufferedReader br = new BufferedReader(new FileReader("public.key"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return publicKeyFromString(everything);

        } finally {
            br.close();
        }
    }

    static PrivateKey readPrivateKeyFromFile() throws IOException {

        if(!new File("private.key").exists())
            return null;

        BufferedReader br = new BufferedReader(new FileReader("private.key"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return privateKeyFromString(everything);

        } finally {
            br.close();
        }
    }

}