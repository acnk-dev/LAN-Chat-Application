package xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XMLNode {
    private String tagName;
    private Map<String, String> attributes;
    private ArrayList<XMLNode> children;
    private String text;

    public XMLNode(String tagName) {
        this.tagName = tagName;
        attributes = new HashMap<String, String>();
        children = new ArrayList<XMLNode>();
    }

    public void addChild(XMLNode childNode) {
        this.children.add(childNode);
    }

    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<XMLNode> getChildren() {
        return this.children;
    }

    public int getNumberOfChildren() {
        return children.size();
    }

    @Override
    public String toString() {
        return "[TAGNAME: " + this.tagName + " ATTRIBUTES: " + this.attributes + " TEXT: " + this.text + " CHILDREN: " + this.children + "]";
    }
}
