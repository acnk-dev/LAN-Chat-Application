package des;

import java.util.Map;

public class Encryption {
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
    private Metadata metadata;

    public void init(String metadata) {
        fetchMetaData(metadata);
        this.key = MiscellaneousUtils.padding(this.key, 64, true, false);
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

    public String encrypt(String message, String metadata) {
        init(metadata);

        String textInBytes = BaseConverter.textToBinary(message);

        StringBuffer cipherText = new StringBuffer();

        String messageByte;
        for (int i = 0; i < textInBytes.length(); i = i + 56) {
            int j = (i + 56) < textInBytes.length() ? (i + 56) : textInBytes.length();
            messageByte = textInBytes.substring(i, j);
            cipherText.append(encrypt64BitChunk(messageByte) + " ");
        }

        return cipherText.toString();
    }

    private String encrypt64BitChunk(String message) {
        message = MiscellaneousUtils.padding(message, 64, true, true);

        String permutedMessage = permute(message, initialPermutation, 8, 8);

        message = permutedMessage.toString();

        L[0] = message.substring(0, 32);
        R[0] = message.substring(32);

        for (int i = 1; i <= 16; i++) {
            L[i] = R[i - 1];
            R[i] = BitManipulations.XOR(L[i - 1], manglerFunction(R[i - 1], keys[i].toString()));
        }

        String R16L16 = R[16] + L[16];

        String cipherText = permute(R16L16, inverseInitialPermutation, 8, 8);

        return BaseConverter.binaryToHexadecimal(cipherText.toString());
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

    private void fetchMetaData(String metadata) {
        this.metadata = Metadata.deserializeMetadata(metadata);
        this.key = this.metadata.getKey();
        this.initialPermutation = this.metadata.getIP();
        this.PC1 = this.metadata.getPC1();
        this.PC2 = this.metadata.getPC2();
        this.eBitSelectionTable = this.metadata.geteBitSelectionTable();
        this.P = this.metadata.getP();
        this.inverseInitialPermutation = this.metadata.getIPInverse();
        this.numberOfLeftShifts = this.metadata.getNumberOfLeftShifts();
        this.S = this.metadata.getS();
    }

    public String generateAndSetNewKey() {
        this.metadata.generateKey();
        return this.metadata.getKey();
    }

    public String getMetadata() {
        if (this.metadata == null) {
            this.metadata = new Metadata();
        }
        return Metadata.serializeMetadata(metadata);
    }

    public void setKey(String key) {
        this.metadata.setKey(key);
    }
}
