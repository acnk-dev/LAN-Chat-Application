package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rsa.RSA;
import rsa.RSAKeyPair;
import rsa.RSAPrivateKey;
import rsa.RSAPublicKey;

/**
 * Per-process crypto state for the signed-in user: this client's RSA
 * identity (persisted locally via KeyStorage so it's stable across
 * logins - a fresh keypair every login would strand anything the server
 * had queued for this user, encrypted against whatever public key was
 * on file before) and any contacts' public keys already fetched via
 * GET_PUBLIC_KEY, plus messages waiting on a key that hasn't arrived yet.
 *
 * RSA.encrypt()/decrypt() don't touch the instance's own keypair at all
 * - they operate purely on whatever RSAPublicKey/RSAPrivateKey is passed
 * in - so `operator` below is just a reusable handle for calling those
 * methods, never a source of key material itself.
 *
 * Every method here is synchronized: initForUser() is called from the
 * ClientInputStream background thread (see MessageProcessor) as soon as
 * our own userID is known, specifically so it completes before that same
 * thread can go on to process any undelivered messages flushed right
 * after login - but synchronizing rules out any other ordering surprise.
 */
public class CryptoSession {

    private static final RSA operator = new RSA();

    private static RSAKeyPair ownKeyPair;
    private static int ownUserId = -1;
    private static final Map<Integer, RSAPublicKey> publicKeyCache = new HashMap<Integer, RSAPublicKey>();
    private static final Map<Integer, List<PendingMessage>> pending = new HashMap<Integer, List<PendingMessage>>();

    public static class PendingMessage {
        public final int senderUserId;
        public final String plainText;

        public PendingMessage(int senderUserId, String plainText) {
            this.senderUserId = senderUserId;
            this.plainText = plainText;
        }
    }

    /** Loads this user's persisted keypair, or generates and saves a new one the first time they ever log in. */
    public static synchronized void initForUser(int userId) {
        if (ownKeyPair != null && ownUserId == userId) {
            return;
        }
        RSAKeyPair loaded = KeyStorage.load(userId);
        if (loaded != null) {
            ownKeyPair = loaded;
        } else {
            RSA generator = new RSA();
            ownKeyPair = new RSAKeyPair(generator.getPublicKey(), generator.getPrivateKey());
            KeyStorage.save(userId, ownKeyPair);
        }
        ownUserId = userId;
    }

    public static synchronized RSAPublicKey getOwnPublicKey() {
        return ownKeyPair.publicKey;
    }

    public static synchronized RSAPrivateKey getOwnPrivateKey() {
        return ownKeyPair.privateKey;
    }

    /** Stateless RSA operations (encrypt against someone else's public key, decrypt with our own private key). */
    public static RSA getRSA() {
        return operator;
    }

    public static synchronized void cachePublicKey(int contactUserId, RSAPublicKey key) {
        publicKeyCache.put(contactUserId, key);
    }

    public static synchronized RSAPublicKey getCachedPublicKey(int contactUserId) {
        return publicKeyCache.get(contactUserId);
    }

    /**
     * A contact's RSA keypair used to be regenerated every login, so a
     * cached public key would go stale the moment they logged back in.
     * Now that keypairs are persisted (see class doc), this is mostly
     * unnecessary - kept as a safety net in case a contact's local key
     * file is ever lost and they end up with a genuinely new identity.
     */
    public static synchronized void invalidatePublicKey(int contactUserId) {
        publicKeyCache.remove(contactUserId);
    }

    public static synchronized void queuePendingMessage(int contactUserId, int senderUserId, String plainText) {
        List<PendingMessage> list = pending.get(contactUserId);
        if (list == null) {
            list = new ArrayList<PendingMessage>();
            pending.put(contactUserId, list);
        }
        list.add(new PendingMessage(senderUserId, plainText));
    }

    public static synchronized List<PendingMessage> drainPendingMessages(int contactUserId) {
        List<PendingMessage> list = pending.remove(contactUserId);
        return list == null ? new ArrayList<PendingMessage>() : list;
    }

    /** Drops in-memory session state on logout - the persisted keypair file itself is untouched, so the next login reloads the same identity. */
    public static synchronized void reset() {
        ownKeyPair = null;
        ownUserId = -1;
        publicKeyCache.clear();
        pending.clear();
    }
}
