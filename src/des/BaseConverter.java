package des;

import java.math.BigInteger;

public class BaseConverter {

    public static String hexadecimalToBinary(String hexadecimal) {
        BigInteger bigInt = new BigInteger(hexadecimal, 16);
        return MiscellaneousUtils.padding(bigInt.toString(2), 64, true, false);
    }

    public static String binaryToHexadecimal(String binary) {
        BigInteger bin = new BigInteger(binary, 2);
        return bin.toString(16);
    }

    public static String textToBinary(String text) {
        byte[] textBytes = text.getBytes();
        StringBuffer textByteString = new StringBuffer();
        for (byte textByte : textBytes) {
            String ascii = MiscellaneousUtils.padding(Long.toBinaryString(Long.valueOf(textByte + "", 10)), 7, true, false);
            textByteString.append(ascii);
        }
        return textByteString.toString();
    }

    public static String binaryToText(String binary) {
        StringBuffer result = new StringBuffer();

        int padLength = Integer.parseInt(binary.substring(0, 6), 2);
        binary = binary.substring(6).substring(padLength);
        for (int i = 0; i < binary.length(); i = i + 7) {
            int j = (i + 7) < binary.length() ? (i + 7) : binary.length();
            String asciiBytes = binary.substring(i, j);
            int asciiValue = Integer.parseInt(asciiBytes, 2);
            if (asciiValue != 0) {
                String text = (char) asciiValue + "";
                result.append(text);
            }
        }
        return result.toString();
    }
}
