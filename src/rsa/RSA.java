package rsa;

import security.AsymmetricCipher;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA implements AsymmetricCipher<RSAPublicKey, RSAPrivateKey> {
    private BigInteger p, q, n, phiN, e, d;
    private SecureRandom secureRandom;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    public RSA() {
        secureRandom = new SecureRandom();
        this.p = BigInteger.probablePrime(512, secureRandom);
        this.q = BigInteger.probablePrime(512, secureRandom);
        this.n = p.multiply(q);
        this.phiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = getRelativelyPrimeBigInteger(secureRandom, phiN);
        this.publicKey = new RSAPublicKey(this.n, this.e);
        d = this.e.modInverse(this.phiN);
        this.privateKey = new RSAPrivateKey(this.n, this.d);
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public BigInteger encrypt(BigInteger message, RSAPublicKey publicKey) {
        if (message.compareTo(publicKey.getN()) == -1) {
            return message.modPow(publicKey.getE(), publicKey.getN());
        }
        return null;
    }

    @Override
    public BigInteger decrypt(BigInteger cipher, RSAPrivateKey privateKey) {
        return cipher.modPow(privateKey.getD(), privateKey.getN());
    }

    private BigInteger getRelativelyPrimeBigInteger(SecureRandom secureRandom, BigInteger x) {
        boolean found = false;
        BigInteger relativelyBigInteger = new BigInteger("65537", 10);
        while (!found) {
            relativelyBigInteger = new BigInteger(512, secureRandom);
            if (isRelativelyPrime(x, relativelyBigInteger)) {
                found = true;
            }
        }
        return relativelyBigInteger;
    }

    private boolean isRelativelyPrime(BigInteger x, BigInteger y) {
        return x.gcd(y).equals(BigInteger.ONE);
    }
}
