
public class Compressor implements Method {

	public void Run(ByteReader byteReader, ByteWriter byteWriter) {
		FrequencyTable frequencies = new FrequencyTable();
		frequencies.CountFrequencies(byteReader);
		CodeTree codeTree = new FrequencyTree(frequencies.GetFrequencies());

		CanonicalCode canonicalCode = new CanonicalCode();
		canonicalCode.CountCodeLengths(codeTree, frequencies.GetSymbolLimit());
		codeTree = new CanonicalTree(canonicalCode.GetCodeLengths());

		EncodeCodeLengths(canonicalCode, byteWriter);
		Compress(codeTree, byteReader, byteWriter);

	}

	private void Compress(CodeTree codeTree, ByteReader byteReader, ByteWriter byteWriter) {
		Encoder encoder = new Encoder();
		int byteToEncode;
		byteToEncode = byteReader.Read();
		while (byteToEncode != -1) {
			encoder.Encode(codeTree, byteToEncode, byteWriter);
			byteToEncode = byteReader.Read();
		}
		encoder.EncodeEnd(codeTree, byteWriter);
	}

	private void EncodeCodeLengths(CanonicalCode canonicalCode, ByteWriter byteWriter) {
		for (int i = 0; i < canonicalCode.getSymbolLimit(); i++) {
			int codeLength = canonicalCode.getCodeLength(i);
			byteWriter.Write(codeLength);
		}
	}

}
