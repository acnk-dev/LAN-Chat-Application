package xml;

import java.math.BigInteger;
import java.util.Map;

import parser.Parser;
import rsa.RSAKeyPair;
import rsa.RSAPrivateKey;
import rsa.RSAPublicKey;

/**
 * Wraps XMLParser/XMLNode behind the same Parser<T> shape JSONHandler
 * already uses. parse() just delegates to XMLParser's existing tree
 * builder; stringify() is new - no XMLNode-to-string serializer existed
 * before this.
 */
public class XMLHandler implements Parser<XMLNode> {

    @Override
    public XMLNode parse(String xml) {
        return new XMLParser(xml).parse();
    }

    @Override
    public String stringify(XMLNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(node.getTagName());
        for (Map.Entry<String, String> attribute : node.getAttributes().entrySet()) {
            sb.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
        }
        sb.append(">");

        if (node.getNumberOfChildren() == 0) {
            if (node.getText() != null) {
                sb.append(node.getText());
            }
        } else {
            for (XMLNode child : node.getChildren()) {
                sb.append(stringify(child));
            }
        }

        sb.append("</").append(node.getTagName()).append(">");
        return sb.toString();
    }

    /** RSA public keys need to cross the wire (SET_PUBLIC_KEY / GET_PUBLIC_KEY) - wrap n/e as a small XML tree. */
    public static String stringifyRSAPublicKey(RSAPublicKey publicKey) {
        XMLNode root = new XMLNode("RSAPublicKey");
        XMLNode n = new XMLNode("n");
        n.setText(publicKey.getN().toString());
        XMLNode e = new XMLNode("e");
        e.setText(publicKey.getE().toString());
        root.addChild(n);
        root.addChild(e);
        return new XMLHandler().stringify(root);
    }

    public static RSAPublicKey parseRSAPublicKey(String xml) {
        XMLNode root = new XMLHandler().parse(xml);
        BigInteger n = null;
        BigInteger e = null;
        for (XMLNode child : root.getChildren()) {
            if ("n".equals(child.getTagName())) {
                n = new BigInteger(child.getText());
            } else if ("e".equals(child.getTagName())) {
                e = new BigInteger(child.getText());
            }
        }
        return new RSAPublicKey(n, e);
    }

    /**
     * This user's own keypair, persisted locally (client.KeyStorage) so
     * their identity survives across logins - without this, an offline
     * user's public key on the server goes stale the moment a fresh
     * keypair gets generated at their next login, permanently breaking
     * anything encrypted against the old one.
     */
    public static String stringifyRSAKeyPair(RSAKeyPair keyPair) {
        XMLNode root = new XMLNode("RSAKeyPair");
        XMLNode n = new XMLNode("n");
        n.setText(keyPair.publicKey.getN().toString());
        XMLNode e = new XMLNode("e");
        e.setText(keyPair.publicKey.getE().toString());
        XMLNode d = new XMLNode("d");
        d.setText(keyPair.privateKey.getD().toString());
        root.addChild(n);
        root.addChild(e);
        root.addChild(d);
        return new XMLHandler().stringify(root);
    }

    public static RSAKeyPair parseRSAKeyPair(String xml) {
        XMLNode root = new XMLHandler().parse(xml);
        BigInteger n = null;
        BigInteger e = null;
        BigInteger d = null;
        for (XMLNode child : root.getChildren()) {
            if ("n".equals(child.getTagName())) {
                n = new BigInteger(child.getText());
            } else if ("e".equals(child.getTagName())) {
                e = new BigInteger(child.getText());
            } else if ("d".equals(child.getTagName())) {
                d = new BigInteger(child.getText());
            }
        }
        return new RSAKeyPair(new RSAPublicKey(n, e), new RSAPrivateKey(n, d));
    }
}
