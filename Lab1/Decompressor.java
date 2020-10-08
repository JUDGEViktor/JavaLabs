
import java.io.*;

public class Decompressor implements Method{

	private final int EOF_SYMBOL = 256;

	private final int ARRAY_LENGTH_SIZE = 257;


	private int[] ReadCodeLengths(ByteReader byteReader){
		int[] codeLengths = new int[ARRAY_LENGTH_SIZE];
		for (int i = 0; i < codeLengths.length; i++) {
			codeLengths[i] = byteReader.Read();
		}
		return codeLengths;
	}

	public void Run(ByteReader byteReader, ByteWriter byteWriter){
		int[] codeLengths = ReadCodeLengths(byteReader);
		CanonicalCode canonicalCode = new CanonicalCode(codeLengths);
		CodeTree codeTree = new CanonicalTree(canonicalCode.GetCodeLengths());

		Decompress(codeTree, byteReader, byteWriter);
		byteReader.Close();
		byteWriter.Close();

	}

	private void Decompress(CodeTree codeTree, ByteReader byteReader, ByteWriter byteWriter){
		Decoder decoder = new Decoder();
		while (true) {
			int symbol = GetSymbol(decoder, codeTree, byteReader);
			if (symbol == EOF_SYMBOL)
				break;
			byteWriter.Write(symbol);
		}
	}

	private int GetSymbol(Decoder decoder, CodeTree codeTree, ByteReader byteReader){
		InternalNode currentNode = codeTree.root;
		while (true) {
			int bit = decoder.ReadBit(byteReader);
			Node nextNode = null;
			if (bit == 0) {
				nextNode = currentNode.leftChild;
			}
			else if (bit == 1) {
				nextNode = currentNode.rightChild;
			}
			if (nextNode instanceof Leaf)
				return ((Leaf)nextNode).symbol;
			else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode)nextNode;
		}
	}

}
