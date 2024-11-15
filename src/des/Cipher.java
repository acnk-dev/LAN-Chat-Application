package des;

import security.SymmetricCipher;

public class Cipher implements SymmetricCipher {
    private Encryption encryption;
    private Decryption decryption;
    private Metadata metadata;

    public Cipher() {
        encryption = new Encryption();
    }

    @Override
    public String encrypt(String plainText, String metadata) {
        return encryption.encrypt(plainText, metadata);
    }

    @Override
    public String encrypt(String plainText, String metadata, String key) {
        Metadata metadataEncrypt = Metadata.deserializeMetadata(metadata);
        metadataEncrypt.setKey(key);
        return encryption.encrypt(plainText, Metadata.serializeMetadata(metadataEncrypt));
    }

    @Override
    public String decrypt(String cipherText, String metadata) {
        decryption = new Decryption(metadata);
        return decryption.decrypt(cipherText);
    }

    @Override
    public String decrypt(String cipherText, String metadata, String key) {
        this.metadata = Metadata.deserializeMetadata(metadata);
        setKey(key);
        decryption = new Decryption(Metadata.serializeMetadata(this.metadata));
        return decryption.decrypt(cipherText);
    }

    @Override
    public String generateMetadata() {
        this.metadata = new Metadata();
        return Metadata.serializeMetadata(metadata);
    }

    @Override
    public String generateAndSetNewKey() {
        metadata.generateKey();
        return metadata.getKey();
    }

    @Override
    public void setKey(String key) {
        metadata.setKey(key);
    }
}
