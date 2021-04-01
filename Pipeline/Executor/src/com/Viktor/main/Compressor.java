package com.Viktor.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class Compressor implements Method {

	public Compressor(Logger logger){
		this.logger = logger;
	}

	@Override
	public byte[] ApplyMethod(byte[] bytesToCompress) {
		frequencies = new FrequencyTable();
		encoder = new Encoder();
		frequencies.CountFrequencies(bytesToCompress);
		codeTree = new FrequencyTree(frequencies.GetFrequencies());

		return Compress(bytesToCompress);
	}

	public byte[] Compress(byte[] bytesToCompress){

		CompressMethaInfo();
		CompressData(bytesToCompress);

		return UnionCompressedMethaInfoAndData();

	}

	private void CompressMethaInfo(){
    	int posInCompressedMethaInfo = 0;
		compressedMethaInfo = new byte[frequencies.GetMethaInfoLength() + 4];
		//write number of elements in frequency table
		byte[] lenghtOfMethaInfo = encoder.EncodeInt(frequencies.GetNumOfFrequencies());
		posInCompressedMethaInfo = WriteInArr(compressedMethaInfo , lenghtOfMethaInfo, posInCompressedMethaInfo);

		Iterator it = frequencies.GetFrequencies().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int freq = (int)pair.getValue();
			byte byte_ = (byte)pair.getKey();
			compressedMethaInfo[posInCompressedMethaInfo++] = byte_;
			byte[] compressedFrequency = encoder.EncodeInt(freq);
			posInCompressedMethaInfo = WriteInArr(compressedMethaInfo, compressedFrequency, posInCompressedMethaInfo);
		}
		return;
	}

	private void CompressData(byte[] bytesToCompress){
		ArrayList<Byte> encodedData = encoder.Encode(codeTree, bytesToCompress);

		compressedData = new ArrayList<>();

		//write number of bytes in bytesToCompress
		byte[] lengthOfCompressedData = encoder.EncodeInt(bytesToCompress.length);

		WriteInList(compressedData, lengthOfCompressedData);
		// in case when compressed data is even - write 0 for flag else write 1
		byte evenOrOddFlag;
		if(IsAllCompressedDataEven(encodedData.size())){
			evenOrOddFlag = 0;
		} else {
			evenOrOddFlag = 1;
		}
		compressedData.add(evenOrOddFlag);
		compressedData.addAll(encodedData);
		// if compressed data is odd add one extra byte
		if(evenOrOddFlag == 1)
			compressedData.add((byte) 0);
		return;
	}

	private int WriteInArr(byte[] dest, byte[] src, int pos){
    	for(byte num : src)
    		dest[pos++] = num;
    	return pos;
	}

	private void WriteInList(ArrayList<Byte> dest, byte[] src){
		for(byte num : src)
			dest.add(num);
		return;
	}

	private boolean IsAllCompressedDataEven(int lengthOfCompressedData){
		//special one byte for evenOrOdd flag
		return (compressedMethaInfo.length + lengthOfCompressedData + 4 + 1) % 2 == 0;
	}


	private byte[] UnionCompressedMethaInfoAndData(){
    	byte[] union = new byte[compressedMethaInfo.length + compressedData.size()];
    	int k = 0;
    	for(byte temp : compressedMethaInfo)
    		union[k++] = temp;
    	for(byte temp : compressedData)
    		union[k++] = temp;
    	return union;
	}


	private FrequencyTable frequencies;

    private Encoder encoder;

	private CodeTree codeTree;

	private byte[] compressedMethaInfo;

	private ArrayList<Byte> compressedData;

	private Logger logger;

}
