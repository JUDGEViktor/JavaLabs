public class Decoder {
    private int currentByte;

    //Remaining bits in current byte
    private int numBitsRemaining;

    public Decoder() {
        currentByte = 0;
        numBitsRemaining = 0;
    }

    public int ReadBit(ByteReader byteReader) {
        if (currentByte == -1)
            return -1;
        if (numBitsRemaining == 0) {
            currentByte = byteReader.Read();
            if (currentByte == -1)
                return -1;
            numBitsRemaining = 8;
        }
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }

}
