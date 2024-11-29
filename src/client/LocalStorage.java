package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xml.XMLHandler;
import xml.XMLNode;

/**
 * Per-user chat history, stored locally as one XML file per signed-in
 * user (chathistory/&lt;userId&gt;.xml), one &lt;contact&gt; node per
 * conversation. Deliberately local-only, not server-side: the server
 * never sees plaintext (see ClientHandler.processChatMessage), so
 * history has to live wherever the plaintext already exists - on the
 * device that decrypted it.
 */
public class LocalStorage {

    private static final String STORAGE_DIRECTORY = "chathistory";

    public static class StoredMessage {
        public final String text;
        public final String timestamp;
        public final boolean sentByMe;

        public StoredMessage(String text, String timestamp, boolean sentByMe) {
            this.text = text;
            this.timestamp = timestamp;
            this.sentByMe = sentByMe;
        }
    }

    public static void append(int ownerUserId, int contactUserId, String text, String timestamp, boolean sentByMe) {
        XMLNode root = loadRoot(ownerUserId);
        XMLNode contact = findOrCreateContact(root, contactUserId);

        XMLNode message = new XMLNode("message");
        message.setAttribute("time", timestamp);
        message.setAttribute("sent", String.valueOf(sentByMe));
        message.setText(escape(text));
        contact.addChild(message);

        save(ownerUserId, root);
    }

    public static List<StoredMessage> load(int ownerUserId, int contactUserId) {
        List<StoredMessage> messages = new ArrayList<StoredMessage>();
        XMLNode root = loadRoot(ownerUserId);
        String contactId = String.valueOf(contactUserId);

        for (XMLNode contact : root.getChildren()) {
            if (contactId.equals(contact.getAttributes().get("id"))) {
                for (XMLNode message : contact.getChildren()) {
                    boolean sentByMe = Boolean.parseBoolean(message.getAttributes().get("sent"));
                    String timestamp = message.getAttributes().get("time");
                    String text = unescape(message.getText());
                    messages.add(new StoredMessage(text, timestamp, sentByMe));
                }
                break;
            }
        }
        return messages;
    }

    private static XMLNode findOrCreateContact(XMLNode root, int contactUserId) {
        String contactId = String.valueOf(contactUserId);
        for (XMLNode contact : root.getChildren()) {
            if (contactId.equals(contact.getAttributes().get("id"))) {
                return contact;
            }
        }
        XMLNode contact = new XMLNode("contact");
        contact.setAttribute("id", contactId);
        root.addChild(contact);
        return contact;
    }

    private static XMLNode loadRoot(int ownerUserId) {
        File file = fileFor(ownerUserId);
        if (!file.exists()) {
            return new XMLNode("history");
        }
        try {
            String xml = readFile(file);
            if (xml.trim().length() == 0) {
                return new XMLNode("history");
            }
            return new XMLHandler().parse(xml);
        } catch (IOException ex) {
            System.out.println("[LocalStorage] failed to read history: " + ex.getMessage());
            return new XMLNode("history");
        }
    }

    private static void save(int ownerUserId, XMLNode root) {
        File file = fileFor(ownerUserId);
        file.getParentFile().mkdirs();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(new XMLHandler().stringify(root));
            writer.close();
        } catch (IOException ex) {
            System.out.println("[LocalStorage] failed to save history: " + ex.getMessage());
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

    private static File fileFor(int ownerUserId) {
        return new File(STORAGE_DIRECTORY, ownerUserId + ".xml");
    }

    // XMLHandler.stringify()/XMLParser don't escape text content - has to
    // happen here or a message containing < > & would corrupt the tree.
    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String unescape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }
}
