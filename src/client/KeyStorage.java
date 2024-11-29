package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import rsa.RSAKeyPair;
import xml.XMLHandler;

/**
 * Persists this user's RSA keypair locally, one file per user
 * (keys/&lt;userId&gt;.xml) - survives logout/login so their identity
 * stays stable. Without this, a fresh keypair every login means the
 * public key the server has on file for an offline user goes stale the
 * instant they log back in, permanently breaking anything encrypted
 * against the old one (see CryptoSession).
 */
public class KeyStorage {

    private static final String STORAGE_DIRECTORY = "keys";

    public static RSAKeyPair load(int userId) {
        File file = fileFor(userId);
        if (!file.exists()) {
            return null;
        }
        try {
            String xml = readFile(file);
            if (xml.trim().length() == 0) {
                return null;
            }
            return XMLHandler.parseRSAKeyPair(xml);
        } catch (IOException ex) {
            System.out.println("[KeyStorage] failed to read keypair: " + ex.getMessage());
            return null;
        }
    }

    public static void save(int userId, RSAKeyPair keyPair) {
        File file = fileFor(userId);
        file.getParentFile().mkdirs();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(XMLHandler.stringifyRSAKeyPair(keyPair));
            writer.close();
        } catch (IOException ex) {
            System.out.println("[KeyStorage] failed to save keypair: " + ex.getMessage());
        }
    }

    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    private static File fileFor(int userId) {
        return new File(STORAGE_DIRECTORY, userId + ".xml");
    }
}
