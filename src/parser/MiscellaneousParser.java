package parser;

import java.util.List;

public class MiscellaneousParser {

    public static int[][] parseInt2DArray(List<List<Integer>> list) {
        int[][] parsedArray = new int[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            List<Integer> innerList = list.get(i);
            parsedArray[i] = new int[innerList.size()];

            for (int j = 0; j < innerList.size(); j++) {
                parsedArray[i][j] = innerList.get(j);
            }
        }

        return parsedArray;
    }

    public static int[] parseInt1DArray(List<Integer> list) {
        int[] parsedArray = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            parsedArray[i] = list.get(i);
        }

        return parsedArray;
    }

    public static Integer[][] convertIntToInteger(int[][] primitiveArray) {
        Integer[][] objectArray = new Integer[primitiveArray.length][];

        for (int i = 0; i < primitiveArray.length; i++) {
            objectArray[i] = new Integer[primitiveArray[i].length];
            for (int j = 0; j < primitiveArray[i].length; j++) {
                objectArray[i][j] = primitiveArray[i][j];
            }
        }

        return objectArray;
    }

    public static Integer[] convertIntToInteger(int[] primitiveArray) {
        Integer[] objectArray = new Integer[primitiveArray.length];

        for (int i = 0; i < primitiveArray.length; i++) {
            objectArray[i] = primitiveArray[i];
        }

        return objectArray;
    }
}
