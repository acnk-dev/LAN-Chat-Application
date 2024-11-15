package parser;

import java.util.Arrays;

public class ByteArrayParser implements Parser<byte[]> {

    private static ByteArrayParser instance = null;

    private ByteArrayParser() {
    }

    @Override
    public String stringify(byte[] byteArray) {
        return Arrays.toString(byteArray == null ? new byte[0] : byteArray);
    }

    @Override
    public byte[] parse(String byteString) {
        String inner = byteString.substring(byteString.indexOf('[') + 1, byteString.indexOf(']'));
        if (inner.length() == 0) {
            return new byte[0];
        }

        String[] byteValues = inner.split(", ");
        byte[] byteArray = new byte[byteValues.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = Byte.parseByte(byteValues[i]);
        }
        return byteArray;
    }

    public static ByteArrayParser getParser() {
        if (instance == null) {
            instance = new ByteArrayParser();
        }
        return instance;
    }
}
