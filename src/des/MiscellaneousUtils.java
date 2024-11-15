package des;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MiscellaneousUtils {

    public static String padding(String input, int numberOfBits, boolean padAtStart, boolean appendLengthAtStart) {
        int padLength = numberOfBits - input.length();
        if (appendLengthAtStart) {
            padLength = padLength - 6;
        }
        if (padLength > 0) {
            StringBuffer temp = new StringBuffer();
            for (int i = 0; i < padLength; i++) {
                temp.append("0");
            }

            input = padAtStart ? temp + input : input + temp;
        }

        if (appendLengthAtStart) {
            String padLengthStr = Integer.toBinaryString(padLength);
            if (padLengthStr.length() < 6) {
                padLengthStr = padding(padLengthStr, 6, true, false);
            }

            return padLengthStr + input;
        }
        return input;
    }

    public static int[][] generateMatrix(int start, int end, int rows, int columns, int bitSkipInterval, boolean isDuplicateAllowed) {
        int[][] matrix = new int[rows][columns];
        int size = rows * columns;
        ArrayList<Integer> numbers = new ArrayList<Integer>(size);

        if (isDuplicateAllowed) {
            Random random = new Random();
            int number;
            while (numbers.size() != size) {
                number = random.nextInt(end + 1);
                if (number >= start && number <= end) {
                    numbers.add(number);
                }
            }
        } else {
            for (int i = start; i <= end; i++) {
                if (bitSkipInterval != 0 && i % bitSkipInterval == 0) {
                    continue;
                }
                numbers.add(i);
            }
            Collections.shuffle(numbers);
        }

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = numbers.get(index++);
            }
        }

        return matrix;
    }
}
