package des;

public class BitManipulations {

    public static String cyclicLeftShift(String bits, int shiftBy) {
        return bits.substring(shiftBy) + bits.substring(0, shiftBy);
    }

    public static String XOR(String bits1, String bits2) {
        int length = Integer.max(bits1.length(), bits2.length());

        long a = Long.parseLong(bits1, 2);
        long b = Long.parseLong(bits2, 2);

        long c = a ^ b;

        String ans = Long.toBinaryString(c);
        ans = MiscellaneousUtils.padding(ans, length, true, false);

        return ans;
    }
}
