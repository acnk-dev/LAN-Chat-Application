package xml;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLParser {
    private String xml;
    private Matcher tagsMatcher;
    private Stack<XMLNode> nodesStack;
    private int currentIndex;

    public XMLParser(String xml) {
        this.xml = xml;
        this.nodesStack = new Stack<XMLNode>();
        this.tagsMatcher = getMatcher(RegexLibrary.FETCH_TAGS, xml);
        currentIndex = 0;
    }

    public XMLNode parse() {
        String rootTag = getNextTag();

        XMLNode rootNode = new XMLNode(rootTag.substring(1, rootTag.length() - 1));
        nodesStack.push(rootNode);

        XMLNode lastNode = null;
        String nextNodeTag;
        int tagBodyStartIndex = -1;
        while (!nodesStack.empty()) {
            if ((nextNodeTag = getNextTag()) != null) {

                if (nextNodeTag.matches(RegexLibrary.CLOSING_TAG)) {
                    XMLNode childNode = nodesStack.pop();
                    XMLNode stackTop = !nodesStack.empty() ? nodesStack.peek() : childNode;
                    parseAttributes(childNode);
                    try {
                        childNode.setTagName(childNode.getTagName().substring(childNode.getTagName().indexOf("<") + 1, childNode.getTagName().indexOf(" ")));
                    } catch (StringIndexOutOfBoundsException e) {
                    }

                    if (childNode.getNumberOfChildren() == 0) {
                        childNode.setText(xml.substring(tagBodyStartIndex, tagsMatcher.start()));
                    }

                    if (!nodesStack.empty()) {
                        stackTop.addChild(childNode);
                    }
                    lastNode = stackTop;
                } else {
                    tagBodyStartIndex = tagsMatcher.end();
                    nodesStack.push(new XMLNode(nextNodeTag.substring(1, nextNodeTag.length() - 1)));
                }
            }
        }

        return lastNode;
    }

    private void parseAttributes(XMLNode childNode) {
        Matcher matcher = getMatcher(RegexLibrary.FETCH_ATTRIBUTES, childNode.getTagName());
        while (matcher.find()) {
            childNode.setAttribute(matcher.group(1), matcher.group(2));
        }
    }

    private String getNextTag() {
        if (tagsMatcher.find()) {
            return tagsMatcher.group(1);
        }
        return null;
    }

    private Matcher getMatcher(String regex, String target) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(target);
    }
}
