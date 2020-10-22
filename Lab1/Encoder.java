import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Encoder {

    public Encoder() {
        currentByte = 0;
        numBitsFilled = 0;
    }

    private int Encode(int bit){
        if (bit != 0 && bit != 1){
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_ENCODING.name());
            return -1;
        }
        currentByte = (currentByte << 1) | bit;
        numBitsFilled++;
        if (numBitsFilled == 8) {
            int res = currentByte;
            currentByte = 0;
            numBitsFilled = 0;
            return res;
        }
        return -1;
    }

    public byte[] EncodeInt(int length) {
        return new byte[]{
            (byte) (length >>> 24),
            (byte) (length >>> 16),
            (byte) (length >>> 8),
            (byte) (length >>> 0)};
    }


    public ArrayList<Byte> Encode(CodeTree codeTree, byte[] bytesToCompress){
        ArrayList<Byte> compressedBytes = new ArrayList<Byte>();
        ArrayList<Integer> bits;
        int byteEncoded;
        for(byte byteToEncode : bytesToCompress){
            bits = codeTree.getCode(byteToEncode);
            for(int bit : bits) {
                byteEncoded = Encode(bit);
                if (byteEncoded != -1)
                    compressedBytes.add((byte)byteEncoded);
            }
        }
        while (numBitsFilled != 0) {
            byteEncoded = Encode(0);
            if (byteEncoded != - 1)
                compressedBytes.add((byte)byteEncoded);
        }
        return compressedBytes;
    }


    private int currentByte;

    private int numBitsFilled;

}
