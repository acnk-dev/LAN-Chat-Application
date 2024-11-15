package security;

import java.math.BigInteger;

public interface AsymmetricCipher<PublicKey, PrivateKey> {
    BigInteger encrypt(BigInteger message, PublicKey publicKey);

    BigInteger decrypt(BigInteger cipherText, PrivateKey privateKey);

    PublicKey getPublicKey();

    PrivateKey getPrivateKey();
}
