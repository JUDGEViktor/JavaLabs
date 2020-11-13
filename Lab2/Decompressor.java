package com.Viktor.main;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.RC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Decompressor implements IExecutor {

	enum Modes{
		DECOMPRESS_LENGTH_OF_METHA_INFO,
		DECOMPRESS_METHA_INFO,
		DECOMPRESS_LENGTH_OF_DATA,
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


	public byte[] Run(byte[] bytesToDecompress){
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
		}
	}

	@Override
	public RC setConfig(String var1) {
		configFileName = var1;
		return RC.CODE_SUCCESS;
	}

	@Override
	public RC setConsumer(IExecutable var1) {
		consumer = var1;
		return RC.CODE_SUCCESS;
	}

	@Override
	public RC setProducer(IExecutable var1) {
		producer = var1;
		return RC.CODE_SUCCESS;
	}

	@Override
	public RC execute(byte[] var1) {
		byte[] res = Run(var1);
		if(res == null) {
			logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_EXECUTION.name());
			return RC.CODE_INVALID_ARGUMENT;
		}
		return consumer.execute(res);
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

	private CodeTree codeTree;
	private FrequencyTable frequencies;
	private Decoder decoder;
	private InternalNode currentNode;

	private String configFileName;

	private IExecutable consumer;

	private IExecutable producer;

	private Logger logger;



}
