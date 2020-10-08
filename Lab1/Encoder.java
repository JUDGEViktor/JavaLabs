import java.util.List;
import java.util.logging.Level;

public class Encoder {

    private int currentByte;

    private final int EOF_SYMBOL = 256;

    // Number of bits already stored in byte
    private int numBitsFilled;

    public Encoder() {
        currentByte = 0;
        numBitsFilled = 0;
    }

    private int Encode(int b){
        if (b != 0 && b != 1){
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_ENCODING.name());
            return -1;
        }
        currentByte = (currentByte << 1) | b;
        numBitsFilled++;
        if (numBitsFilled == 8) {
            int res = currentByte;
            currentByte = 0;
            numBitsFilled = 0;
            return res;
        }
        return -1;
    }

    public int Encode(CodeTree codeTree, int byteToEncode, ByteWriter byteWriter){
        List<Integer> bits;
        int byteEncoded;
        bits = codeTree.getCode(byteToEncode);
        for(int bit : bits) {
            byteEncoded = Encode(bit);
            if (byteEncoded != -1)
                byteWriter.Write(byteEncoded);
        }
        return -1;
    }


    public void EncodeEnd(CodeTree codeTree, ByteWriter byteWriter){
        List<Integer> bits;
        int byteEncoded;
        bits = codeTree.getCode(EOF_SYMBOL);
        for(int bit : bits) {
            byteEncoded = Encode(bit);
            if (byteEncoded != -1)
                byteWriter.Write(byteEncoded);
        }
        while (numBitsFilled != 0) {
            byteEncoded = Encode(0);
            if (byteEncoded != - 1)
                byteWriter.Write(byteEncoded);
        }
        return;
    }

}
