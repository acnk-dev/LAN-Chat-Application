package security;

public interface SymmetricCipher {
    String encrypt(String plainText, String metadata);

    String encrypt(String plainText, String metadata, String key);

    String decrypt(String cipherText, String metadata);

    String decrypt(String cipherText, String metadata, String key);

    String generateMetadata();

    String generateAndSetNewKey();

    void setKey(String key);
}
