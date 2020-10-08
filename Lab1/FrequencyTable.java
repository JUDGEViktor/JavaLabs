import java.util.*;


public class FrequencyTable {

	private final int FREQUENCIES_ARRAY_LENGTH_SIZE = 257;

	private final int EOF_SYMBOL = 256;

	private int[] frequencies;

	FrequencyTable(){
		frequencies = new int[FREQUENCIES_ARRAY_LENGTH_SIZE];
	}

	public void CountFrequencies(ByteReader byteReader)  {
		int byte_ = byteReader.Read();
		while (byte_ != -1) {
			Increment(byte_);
			byte_ = byteReader.Read();
		}
		Increment(EOF_SYMBOL);
		byteReader.Reset();
	}

	public int GetSymbolLimit() {
		return frequencies.length;
	}

	public int[] GetFrequencies() { return frequencies; }

	public void Increment(int symbol) {
		frequencies[symbol]++;
	}
	
}
