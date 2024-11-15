package xml;

public interface RegexLibrary {
    String FETCH_TAGS = "(<[\\w\\W]*?>)";
    String FETCH_ATTRIBUTES = "(?:(\\w*)=\\\"([^\\\"]*)\\\")";
    String OPENING_TAG = "<\\w*>";
    String CLOSING_TAG = "</\\w*>";
}
