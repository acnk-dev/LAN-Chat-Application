package des;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import json.JSONHandler;
import parser.Parser;

import static parser.MiscellaneousParser.convertIntToInteger;
import static parser.MiscellaneousParser.parseInt1DArray;
import static parser.MiscellaneousParser.parseInt2DArray;

public class Metadata implements Serializable {
    private String key;
    private int[][] IP;
    private int[][] PC1;
    private int[][] PC2;
    private int[][] eBitSelectionTable;
    private Map<Integer, int[][]> S = new HashMap<Integer, int[][]>();
    private int[][] IPInverse = new int[8][8];
    private int[][] P;
    private int[] numberOfLeftShifts;
    private static Parser<Map<String, Object>> parser = JSONHandler.getParser();

    public Metadata() {
        generateKey();
        generateIPMatrix();
        generatePC1Matrix();
        generatePC2Matrix();
        generateEBitSelectionMatrix();
        generateSBoxes();
        generateIPInverseMatrix();
        generatePMatrix();
        generateNumberOfLeftShifts();
    }

    public Metadata(String key, int[][] IP, int[][] PC1, int[][] PC2, int[][] eBitSelectionTable,
                     Map<Integer, int[][]> S, int[][] IPInverse, int[][] P, int[] numberOfLeftShifts) {
        this.key = key;
        this.IP = IP;
        this.PC1 = PC1;
        this.PC2 = PC2;
        this.eBitSelectionTable = eBitSelectionTable;
        this.S = S;
        this.IPInverse = IPInverse;
        this.P = P;
        this.numberOfLeftShifts = numberOfLeftShifts;
    }

    public void generateKey() {
        key = Arrays.toString(MiscellaneousUtils.generateMatrix(0, 1, 1, 64, 0, true)[0]).replaceAll("[\\[\\]\\s,]", "");
        this.key = key;
    }

    private void generateIPMatrix() {
        IP = MiscellaneousUtils.generateMatrix(1, 64, 8, 8, 0, false);
    }

    private void generatePC1Matrix() {
        PC1 = MiscellaneousUtils.generateMatrix(1, 64, 8, 7, 8, false);
    }

    private void generatePC2Matrix() {
        PC2 = MiscellaneousUtils.generateMatrix(1, 56, 8, 6, 0, false);
    }

    private void generateEBitSelectionMatrix() {
        eBitSelectionTable = MiscellaneousUtils.generateMatrix(1, 32, 8, 6, 0, true);
    }

    private void generateSBoxes() {
        int[][] temp = new int[4][16];
        for (int i = 1; i <= 8; i++) {
            for (int j = 0; j < 4; j++) {
                temp[j] = MiscellaneousUtils.generateMatrix(0, 15, 1, 16, 0, false)[0];
            }
            S.put(i, temp);
        }
    }

    private void generateIPInverseMatrix() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int position = IP[i][j] - 1;
                IPInverse[position / 8][position % 8] = i * 8 + j + 1;
            }
        }
    }

    private void generatePMatrix() {
        P = MiscellaneousUtils.generateMatrix(1, 32, 8, 4, 0, false);
    }

    private void generateNumberOfLeftShifts() {
        numberOfLeftShifts = MiscellaneousUtils.generateMatrix(1, 27, 1, 27, 0, true)[0];
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public int[][] getIP() {
        return IP;
    }

    public int[][] getPC1() {
        return PC1;
    }

    public int[][] getPC2() {
        return PC2;
    }

    public int[][] geteBitSelectionTable() {
        return eBitSelectionTable;
    }

    public Map<Integer, int[][]> getS() {
        return S;
    }

    public int[][] getIPInverse() {
        return IPInverse;
    }

    public int[][] getP() {
        return P;
    }

    public int[] getNumberOfLeftShifts() {
        return numberOfLeftShifts;
    }

    public static String serializeMetadata(Metadata metadata) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", metadata.getKey());
        map.put("initialPermutation", convertIntToInteger(metadata.getIP()));
        map.put("PC1", convertIntToInteger(metadata.getPC1()));
        map.put("PC2", convertIntToInteger(metadata.getPC2()));
        map.put("eBitSelectionTable", convertIntToInteger(metadata.geteBitSelectionTable()));
        for (int i = 1; i <= 8; i++) {
            map.put("S" + i, convertIntToInteger(metadata.getS().get(i)));
        }
        map.put("inverseInitialPermutation", convertIntToInteger(metadata.getIPInverse()));
        map.put("P", convertIntToInteger(metadata.getP()));
        map.put("numberOfLeftShifts", convertIntToInteger(metadata.getNumberOfLeftShifts()));
        return parser.stringify(map);
    }

    @SuppressWarnings("unchecked")
    public static Metadata deserializeMetadata(String metadataString) {
        Map<String, Object> metadata = parser.parse(metadataString);
        String key = (String) metadata.get("key");
        int[][] initialPermutation = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("initialPermutation"));
        int[][] PC1 = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("PC1"));
        int[][] PC2 = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("PC2"));
        int[][] eBitSelectionTable = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("eBitSelectionTable"));
        Map<Integer, int[][]> S = new HashMap<Integer, int[][]>();
        for (int i = 1; i <= 8; i++) {
            S.put(i, parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("S" + i)));
        }
        int[][] inverseInitialPermutation = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("inverseInitialPermutation"));
        int[][] P = parseInt2DArray((List<List<Integer>>) (List<?>) metadata.get("P"));
        int[] numberOfLeftShifts = parseInt1DArray((List<Integer>) metadata.get("numberOfLeftShifts"));

        return new Metadata(key, initialPermutation, PC1, PC2, eBitSelectionTable, S, inverseInitialPermutation, P, numberOfLeftShifts);
    }
}
