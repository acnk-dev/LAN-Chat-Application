package des;

import java.util.Map;

public class Decryption {
    private String key;
    private String kPlus;
    private int[][] initialPermutation;
    private int[][] PC1;
    private int[][] PC2;
    private int[][] eBitSelectionTable;
    private Map<Integer, int[][]> S;
    private int[][] inverseInitialPermutation = new int[8][8];
    private int[][] P;
    private int[] numberOfLeftShifts;
    private String[] C = new String[17];
    private String[] D = new String[17];
    private String[] L = new String[17];
    private String[] R = new String[17];
    private String[] keys = new String[17];

    public Decryption(Metadata metadata) {
        fetchMetaData(metadata);
        this.key = MiscellaneousUtils.padding(this.key, 64, true, false);
        generateSubkeys();
    }

    public Decryption(String metadata) {
        fetchMetaData(Metadata.deserializeMetadata(metadata));
        generateSubkeys();
    }

    private void generateSubkeys() {
        kPlus = permute(key, PC1, 8, 7);

        C[0] = kPlus.substring(0, 28);
        D[0] = kPlus.substring(28);

        for (int i = 1; i <= 16; i++) {
            C[i] = BitManipulations.cyclicLeftShift(C[i - 1], numberOfLeftShifts[i - 1]);
            D[i] = BitManipulations.cyclicLeftShift(D[i - 1], numberOfLeftShifts[i - 1]);
        }

        for (int i = 1; i <= 16; i++) {
            keys[i] = permute(C[i] + D[i], PC2, 8, 6);
        }
    }

    public String decrypt(String cipherText) {
        String[] cipherTextBlocks = cipherText.split(" ");

        for (int i = 0; i < cipherTextBlocks.length; i++) {
            cipherTextBlocks[i] = BaseConverter.hexadecimalToBinary(cipherTextBlocks[i]);
        }

        StringBuffer plainText = new StringBuffer();
        for (String cipherTextBlock : cipherTextBlocks) {
            plainText.append(decrypt64BitChunk(cipherTextBlock));
        }

        return plainText.toString();
    }

    private String decrypt64BitChunk(String message) {
        String permutedMessage = permute(message, initialPermutation, 8, 8);

        message = permutedMessage.toString();

        L[0] = message.substring(0, 32);
        R[0] = message.substring(32);

        for (int i = 1; i <= 16; i++) {
            L[i] = R[i - 1];
            R[i] = BitManipulations.XOR(L[i - 1], manglerFunction(R[i - 1], keys[17 - i].toString()));
        }

        String R16L16 = R[16] + L[16];

        String plainTextInBytes = permute(R16L16, inverseInitialPermutation, 8, 8);

        return BaseConverter.binaryToText(plainTextInBytes.toString());
    }

    private String manglerFunction(String rightPartMessage, String key) {
        String expandedString = permute(rightPartMessage, eBitSelectionTable, 8, 6);
        String output = BitManipulations.XOR(expandedString.toString(), key);
        output = sBox(output);
        return permute(output, P, 8, 4);
    }

    private String sBox(String input) {
        StringBuffer output = new StringBuffer();
        int SCount = 1;
        for (int i = 0; i < 48; i = i + 6) {
            String sixBits = input.substring(i, i + 6);
            int row = Integer.parseInt(String.valueOf(sixBits.charAt(0)) + String.valueOf(sixBits.charAt(5)) + "", 2);
            int column = Integer.parseInt(sixBits.substring(1, 5), 2);

            String ans = Integer.toBinaryString(S.get(SCount++)[row][column]);
            ans = MiscellaneousUtils.padding(ans, 4, true, false);

            output.append(ans);
        }
        return output.toString();
    }

    private String permute(String input, int[][] PCMatrix, int PCMatrixRows, int PCMatrixCols) {
        StringBuffer permutedString = new StringBuffer();
        for (int i = 0; i < PCMatrixRows; i++) {
            for (int j = 0; j < PCMatrixCols; j++) {
                permutedString.append(input.charAt(PCMatrix[i][j] - 1) + "");
            }
        }
        return permutedString.toString();
    }

    private void fetchMetaData(Metadata metaData) {
        this.key = metaData.getKey();
        this.initialPermutation = metaData.getIP();
        this.PC1 = metaData.getPC1();
        this.PC2 = metaData.getPC2();
        this.eBitSelectionTable = metaData.geteBitSelectionTable();
        this.P = metaData.getP();
        this.inverseInitialPermutation = metaData.getIPInverse();
        this.numberOfLeftShifts = metaData.getNumberOfLeftShifts();
        this.S = metaData.getS();
    }
}
