package rsa;

import java.math.BigInteger;

public class RSAPrivateKey {
    private BigInteger n, d;

    public RSAPrivateKey(BigInteger n, BigInteger d) {
        this.n = n;
        this.d = d;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getD() {
        return this.d;
    }

    @Override
    public String toString() {
        return "<d>" + this.d + "</d>" + "<n>" + this.n + "</n>";
    }
}
