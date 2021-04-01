package com.Viktor.main;

import java.util.ArrayList;

class Encoder {

    public Encoder() {
        currentByte = 0;
        numBitsFilled = 0;
    }

    int Encode(int bit){
        if (bit != 0 && bit != 1){
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

    byte[] EncodeInt(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) (value >>> 0)};
    }


    ArrayList<Byte> Encode(CodeTree codeTree, byte[] bytesToCompress){
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
