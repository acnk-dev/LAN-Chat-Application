package rsa;

import java.math.BigInteger;

public class RSAPublicKey {
    private BigInteger n, e;

    public RSAPublicKey(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getE() {
        return this.e;
    }

    @Override
    public String toString() {
        return "<e>" + this.e + "</e>" + "<n>" + this.n + "</n>";
    }
}
