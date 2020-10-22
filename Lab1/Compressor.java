import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Compressor implements Method {

    Compressor() {}

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
    	int k = 0;
		compressedMethaInfo = new byte[frequencies.GetMethaInfoLength() + 4];
		//write number of elements in frequency table
		byte[] lenghtOfMethaInfo = encoder.EncodeInt(frequencies.GetNumOfFrequencies());
		k = WriteInArr(compressedMethaInfo , lenghtOfMethaInfo, k);

		Iterator it = frequencies.GetFrequencies().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int freq = (int)pair.getValue();
			byte byte_ = (byte)pair.getKey();
			compressedMethaInfo[k++] = byte_;
			byte[] compressedFrequency = encoder.EncodeInt(freq);
			k = WriteInArr(compressedMethaInfo, compressedFrequency, k);
		}
		return;
	}

	private void CompressData(byte[] bytesToCompress){
    	int k = 0;
		ArrayList<Byte> encodedData = encoder.Encode(codeTree, bytesToCompress);
		compressedData = new byte[encodedData.size() + 4];
		//write number of bytes in bytesToCompress
		byte[] lengthOfCompressedData = encoder.EncodeInt(bytesToCompress.length);
		k = WriteInArr(compressedData , lengthOfCompressedData, k);
		for(byte byte_ : encodedData)
			compressedData[k++] = byte_;
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


	private FrequencyTable frequencies;

    private Encoder encoder;

	private CodeTree codeTree;

	private byte[] compressedMethaInfo;

	private byte[] compressedData;

}
