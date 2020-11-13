package com.Viktor.main;

import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.RC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class Compressor implements IExecutor {

	public Compressor(Logger logger){
		this.logger = logger;
	}

	public byte[] Run(byte[] bytesToCompress) {
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
    	int posInCompressData = 0;
		ArrayList<Byte> encodedData = encoder.Encode(codeTree, bytesToCompress);
		compressedData = new byte[encodedData.size() + 4];
		//write number of bytes in bytesToCompress
		byte[] lengthOfCompressedData = encoder.EncodeInt(bytesToCompress.length);
		posInCompressData = WriteInArr(compressedData , lengthOfCompressedData, posInCompressData);
		for(byte byte_ : encodedData)
			compressedData[posInCompressData++] = byte_;
		return;
	}

	private int WriteInArr(byte[] dest, byte[] src, int pos){
    	for(byte num : src)
    		dest[pos++] = num;
    	return pos;
	}


	private byte[] UnionCompressedMethaInfoAndData(){
    	byte[] union = new byte[compressedMethaInfo.length + compressedData.length];
    	int k = 0;
    	for(byte temp : compressedMethaInfo)
    		union[k++] = temp;
    	for(byte temp : compressedData)
    		union[k++] = temp;
    	return union;
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
    	return consumer.execute(res);
	}


	private FrequencyTable frequencies;

    private Encoder encoder;

	private CodeTree codeTree;

	private byte[] compressedMethaInfo;

	private byte[] compressedData;

	private String configFileName;

	private IExecutable consumer;

	private IExecutable producer;

	private Logger logger;

}
