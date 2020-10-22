import java.util.*;


public class FrequencyTable {

	FrequencyTable(){
		frequencies = new HashMap<>();
	}

	FrequencyTable(HashMap<Byte, Integer> frequencies_){
		frequencies = frequencies_;
	}

	public void CountFrequencies(byte[] bytesToCountFrequenciesOf)  {
		for(byte byte_ : bytesToCountFrequenciesOf){
			Increment(byte_);
		}
	}

	public void Increment(byte symbol) {
		int freq;
		if(!frequencies.containsKey(symbol)) {
			frequencies.put(symbol, 0);
		} else{
			freq = frequencies.get(symbol);
			frequencies.put(symbol, ++freq);
		}
	}

	public int GetMethaInfoLength(){
		return frequencies.size() * 5;
	}

	public int GetNumOfFrequencies(){
		return frequencies.size();
	}

	public HashMap<Byte, Integer> GetFrequencies() { return frequencies; }

	private HashMap<Byte, Integer> frequencies;
	
}
