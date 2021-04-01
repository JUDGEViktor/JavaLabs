package com.Viktor.main;

import java.util.ArrayList;

class Decoder {

    final class MyPair{
        final int pos;
        final int bit;
        MyPair(int pos_, int bit_){
            this.pos = pos_;
            this.bit = bit_;
        }
    }

    Decoder() {
        currentByte = 0;
        numBitsRemaining = 0;
    }

    public void SetOffset(int offset){
        pos = offset;
    }

    MyPair ReadBit(byte[] bytes) {
        if (numBitsRemaining == 0 && pos < bytes.length) {
            currentByte = bytes[pos++];
            numBitsRemaining = 8;
        } else if(pos == bytes.length && numBitsRemaining == 0){
            return new MyPair(pos, -1);
        }

        numBitsRemaining--;
        return new MyPair(pos, (currentByte >>> numBitsRemaining) & 1);
    }

    int DecodeInt(ArrayList<Byte> bytes){
        return ((bytes.get(0) & 0xFF) << 24) |
                ((bytes.get(1) & 0xFF) << 16) |
                ((bytes.get(2) & 0xFF) << 8 ) |
                ((bytes.get(3) & 0xFF) << 0 );
    }


    private int numBitsRemaining;

    private int currentByte;

    private int pos;

}
