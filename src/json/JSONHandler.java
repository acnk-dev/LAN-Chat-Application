package json;

import json.elements.JSONArray;
import json.elements.JSONBoolean;
import json.elements.JSONElement;
import json.elements.JSONNumber;
import json.elements.JSONObject;
import json.elements.JSONString;
import parser.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONHandler implements Parser<Map<String, Object>> {
    private static JSONHandler instance;
    private static int index;
    private static String jsonString;

    private static final char OPEN_CURLY_BRACE = '{';
    private static final char CLOSE_CURLY_BRACE = '}';
    private static final char OPEN_SQUARE_BRACKET = '[';
    private static final char CLOSE_SQUARE_BRACKET = ']';
    private static final char DOUBLE_QUOTE = '"';
    private static final char COLON = ':';
    private static final char COMMA = ',';

    private JSONHandler() {
    }

    public static JSONHandler getParser() {
        if (instance == null) {
            instance = new JSONHandler();
        }
        return instance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(String jsonString) {
        return (Map<String, Object>) parseElement(jsonString).getValue();
    }

    @Override
    public String stringify(Map<String, Object> map) {
        StringBuilder stringifiedJSON = new StringBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            stringifiedJSON.append(wrapWithQuotes(entry.getKey())).append(": ");
            Object value = entry.getValue();

            if (value == null) {
                stringifiedJSON.append("null");
            } else if (value.getClass().isArray()) {
                if (value.getClass().getCanonicalName().equals("java.lang.String[]")) {
                    value = handleStringArray((String[]) value);
                } else {
                    value = Arrays.deepToString((Object[]) value);
                }
                stringifiedJSON.append(value);
            } else if (value instanceof Number || value instanceof Boolean) {
                stringifiedJSON.append(value);
            } else {
                stringifiedJSON.append(wrapWithQuotes(escapeString(value.toString())));
            }
            stringifiedJSON.append(", ");
        }

        if (stringifiedJSON.length() > 0) {
            stringifiedJSON.setLength(stringifiedJSON.length() - 2);
        }

        return "{" + stringifiedJSON.toString() + "}";
    }

    public static JSONElement parseElement(String jsonString) {
        index = 0;
        JSONHandler.jsonString = jsonString;
        skipWhitespace();
        return parseValue();
    }

    private static JSONElement parseValue() {
        char currentChar = jsonString.charAt(index);

        if (currentChar == OPEN_CURLY_BRACE) {
            return parseObject();
        } else if (currentChar == OPEN_SQUARE_BRACKET) {
            return parseArray();
        } else if (currentChar == DOUBLE_QUOTE) {
            return parseString();
        } else if (Character.isDigit(currentChar) || currentChar == '-') {
            return parseNumber();
        } else if (currentChar == 't' || currentChar == 'f') {
            return parseBoolean();
        } else if (currentChar == 'n') {
            return parseNull();
        }

        throw new RuntimeException("Invalid JSON");
    }

    private static JSONObject parseObject() {
        Map<String, JSONElement> properties = new HashMap<String, JSONElement>();

        consume(OPEN_CURLY_BRACE);
        skipWhitespace();
        while (jsonString.charAt(index) != CLOSE_CURLY_BRACE) {
            String propertyName = parseString().getValue().toString();
            skipWhitespace();

            consume(COLON);
            skipWhitespace();

            JSONElement propertyValue = parseValue();
            properties.put(propertyName, propertyValue);

            skipWhitespace();

            if (jsonString.charAt(index) == COMMA) {
                consume(COMMA);
                skipWhitespace();
            }
        }

        consume(CLOSE_CURLY_BRACE);
        return new JSONObject(properties);
    }

    private static JSONArray parseArray() {
        List<JSONElement> elements = new ArrayList<JSONElement>();

        consume(OPEN_SQUARE_BRACKET);
        skipWhitespace();

        while (jsonString.charAt(index) != CLOSE_SQUARE_BRACKET) {
            JSONElement element = parseValue();
            elements.add(element);

            skipWhitespace();

            if (jsonString.charAt(index) == COMMA) {
                consume(COMMA);
                skipWhitespace();
            }
        }

        consume(CLOSE_SQUARE_BRACKET);
        return new JSONArray(elements);
    }

    private static JSONString parseString() {
        consume(DOUBLE_QUOTE);

        // stringify() escapes ", \, \n and \t - this has to reverse that
        // or any string containing a quote or backslash breaks parsing.
        StringBuilder sb = new StringBuilder();
        while (jsonString.charAt(index) != DOUBLE_QUOTE) {
            char c = jsonString.charAt(index);
            if (c == '\\') {
                index++;
                char escaped = jsonString.charAt(index);
                switch (escaped) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    default:
                        sb.append(escaped);
                }
            } else {
                sb.append(c);
            }
            index++;
        }

        consume(DOUBLE_QUOTE);
        return new JSONString(sb.toString());
    }

    private static JSONNumber parseNumber() {
        int startIndex = index;

        if (jsonString.charAt(index) == '-') {
            index++;
        }
        while (Character.isDigit(jsonString.charAt(index)) || jsonString.charAt(index) == '.') {
            index++;
        }

        String numberStr = jsonString.substring(startIndex, index);
        if (numberStr.contains(".")) {
            return new JSONNumber(Double.parseDouble(numberStr));
        } else {
            return new JSONNumber(Integer.parseInt(numberStr));
        }
    }

    private static JSONBoolean parseBoolean() {
        String boolStr = consumeWord();
        if (boolStr.equals("true")) {
            return new JSONBoolean(true);
        } else if (boolStr.equals("false")) {
            return new JSONBoolean(false);
        }

        throw new RuntimeException("Invalid Boolean Value");
    }

    private static JSONElement parseNull() {
        consumeWord();
        return null;
    }

    private static String consumeWord() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(jsonString.charAt(index))) {
            sb.append(jsonString.charAt(index));
            index++;
        }
        return sb.toString();
    }

    private static void consume(char expected) {
        if (jsonString.charAt(index) == expected) {
            index++;
        } else {
            throw new RuntimeException("Expected: " + expected);
        }
    }

    private static void skipWhitespace() {
        while (Character.isWhitespace(jsonString.charAt(index))) {
            index++;
        }
    }

    private static String escapeString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String handleStringArray(String[] array) {
        String input = Arrays.deepToString(array);
        input = input.replace("[", "").replace("]", "");
        String[] words = input.split(", ");

        String[] quotedWords = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            quotedWords[i] = wrapWithQuotes(words[i]);
        }

        return "[" + String.join(", ", quotedWords) + "]";
    }

    private static String wrapWithQuotes(String string) {
        return "\"" + string + "\"";
    }
}
