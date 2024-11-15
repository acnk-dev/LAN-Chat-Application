package rsa;

public class RSAKeyPair {
    public final RSAPublicKey publicKey;
    public final RSAPrivateKey privateKey;

    public RSAKeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
