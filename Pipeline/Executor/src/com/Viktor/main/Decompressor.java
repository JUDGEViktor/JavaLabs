package com.Viktor.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Decompressor implements Method {

	enum Modes{
		DECOMPRESS_LENGTH_OF_METHA_INFO,
		DECOMPRESS_METHA_INFO,
		DECOMPRESS_LENGTH_OF_DATA,
		DECOMPRESS_EVEN_FLAG,
		DECOMPRESS_DATA;
	}

    public Decompressor(Logger logger){
        mode = Modes.DECOMPRESS_LENGTH_OF_METHA_INFO;
		accumulatedBytes = new ArrayList<>();
		decompressedFrequencies = new HashMap<>();
		decompressedData = new ArrayList<>();
		resultDecodedData = new ArrayList<>();
		decoder = new Decoder();
		byte_ = null;
		this.logger = logger;
    }

	@Override
	public byte[] ApplyMethod(byte[] bytesToDecompress){
		curPosOfBytesToDecompress = 0;
		while(curPosOfBytesToDecompress < bytesToDecompress.length) {
			if (mode == Modes.DECOMPRESS_LENGTH_OF_METHA_INFO) {
				DecompressLengthOfMethaInfo(bytesToDecompress);
			}
			if (mode == Modes.DECOMPRESS_METHA_INFO) {
				DecompressMethaInfo(bytesToDecompress);
			}
			if (mode == Modes.DECOMPRESS_LENGTH_OF_DATA) {
				DecompressLengthOfData(bytesToDecompress);
			}
			if (mode == Modes.DECOMPRESS_EVEN_FLAG){
				DecompressEvenFlag(bytesToDecompress);
			}
			if (mode == Modes.DECOMPRESS_DATA) {
				DecompressData(bytesToDecompress);
			}
		}
		if(resultDecodedData.size() != 0) {
			byte[] res = new byte[resultDecodedData.size()];
			for (int i = 0; i < resultDecodedData.size(); i++)
				res[i] = resultDecodedData.get(i);
			resultDecodedData.clear();
			return res;
		} else
			return null;
	}

	private void DecompressLengthOfMethaInfo(byte[] bytesToDecompress){
		//accumulate four necessary bytes required for length
		while(curPosOfBytesToDecompress < bytesToDecompress.length && accumulatedBytes.size() < 4){
			accumulatedBytes.add(bytesToDecompress[curPosOfBytesToDecompress++]);
		}
		if(accumulatedBytes.size() == 4){
			lengthOfMethaInfo = decoder.DecodeInt(accumulatedBytes);
			accumulatedBytes.clear();
			mode = Modes.DECOMPRESS_METHA_INFO;
		}
	}


	private void DecompressMethaInfo(byte[] bytesToDecompress){
		//first byte is code of symbol
		//next four bytes if frequency of this symbol
		while(curPosOfBytesToDecompress < bytesToDecompress.length){
			if(byte_ == null){
				byte_ = bytesToDecompress[curPosOfBytesToDecompress++];
			} else {
				accumulatedBytes.add(bytesToDecompress[curPosOfBytesToDecompress++]);
				if(accumulatedBytes.size() == 4){
					decompressedFrequencies.put(byte_, decoder.DecodeInt(accumulatedBytes));
					byte_ = null;
					accumulatedBytes.clear();
					if(decompressedFrequencies.size() == lengthOfMethaInfo)
						break;
				}
			}
		}
		if(decompressedFrequencies.size() == lengthOfMethaInfo){
			frequencies = new FrequencyTable(decompressedFrequencies);
			codeTree = new FrequencyTree(frequencies.GetFrequencies());
			currentNode = codeTree.root;
			mode = Modes.DECOMPRESS_LENGTH_OF_DATA;
			decompressedFrequencies.clear();
		}
	}


	private void DecompressLengthOfData(byte[] bytesToDecompress){
		//accumulate four necessary bytes required for length
		while(curPosOfBytesToDecompress < bytesToDecompress.length && accumulatedBytes.size() < 4){
			accumulatedBytes.add(bytesToDecompress[curPosOfBytesToDecompress++]);
		}
		if(accumulatedBytes.size() == 4){
			lengthOfData = decoder.DecodeInt(accumulatedBytes);
			accumulatedBytes.clear();
			mode = Modes.DECOMPRESS_EVEN_FLAG;
		}
	}

	private void DecompressEvenFlag(byte[] bytesToDecompress){
		//accumulate one necessary byte required for evenOrOdd flag
		while(curPosOfBytesToDecompress < bytesToDecompress.length && accumulatedBytes.size() < 1){
			accumulatedBytes.add(bytesToDecompress[curPosOfBytesToDecompress++]);
		}
		if(accumulatedBytes.size() == 1){
			isEvenFlag = accumulatedBytes.get(0);
			accumulatedBytes.clear();
			mode = Modes.DECOMPRESS_DATA;
		}
	}

	private void DecompressData(byte[] bytesToDecompress){
		decoder.SetOffset(curPosOfBytesToDecompress);
		Decoder.MyPair myPair;
		while (true) {
			myPair = decoder.ReadBit(bytesToDecompress);
			curPosOfBytesToDecompress = myPair.pos;
			Node nextNode = null;
			if (myPair.bit == -1) {
				break;
			} else if (myPair.bit == 0) {
				nextNode = currentNode.leftChild;
			} else if (myPair.bit == 1) {
				nextNode = currentNode.rightChild;
			}
			if (nextNode instanceof Leaf) {
				decompressedData.add(((Leaf) nextNode).symbol);
				currentNode = codeTree.root;
				if (decompressedData.size() == lengthOfData) {
					break;
				}
			} else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode) nextNode;
		}
		if (decompressedData.size() == lengthOfData) {
			resultDecodedData.addAll(decompressedData);
			decompressedData.clear();
			decoder = new Decoder();
			mode = Modes.DECOMPRESS_LENGTH_OF_METHA_INFO;
			// check if even flag is 1 this means that we add in compression
			// one extra unnecessary byte and now we must skip it
			if(isEvenFlag == 1)
				curPosOfBytesToDecompress++;
		}
	}


	private Modes mode;
	private HashMap<Byte, Integer> decompressedFrequencies;
	private ArrayList<Byte> decompressedData;
	private ArrayList<Byte> resultDecodedData;
	//four necessary accumulated bytes required for length
	private ArrayList<Byte> accumulatedBytes;
	//used in DecompressMethaInfo as byte with its frequency
	private Byte byte_;
	private int curPosOfBytesToDecompress;
	private int lengthOfMethaInfo;
	private int lengthOfData;
	private int isEvenFlag;

	private CodeTree codeTree;
	private FrequencyTable frequencies;
	private Decoder decoder;
	private InternalNode currentNode;

	private Logger logger;



}
