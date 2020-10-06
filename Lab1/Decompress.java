
import java.io.*;

public final class Decompress {

	private static final int EOF_SYMBOL = 256;

	private static final int ARRAY_LENGTH_SIZE = 257;

	private static BitInputStream in;

	private static OutputStream out;

	private static CodeTree codeTree;

	private static CanonicalCode canonicalCode;

	// Main Function
	public Decompress(String inputFileName, String outputFileName) throws IOException {

		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
		out = new BufferedOutputStream(new FileOutputStream(outputFile));


		DecompressAndWrite();
	}

	private static void DecompressAndWrite (){
		try {
			canonicalCode = readCodeLengthTable();
			codeTree = canonicalCode.toCodeTree();
			decompress();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	static CanonicalCode readCodeLengthTable(){
		int[] codeLengths = new int[ARRAY_LENGTH_SIZE];
		for (int i = 0; i < codeLengths.length; i++) {
			int val = 0;
			for (int j = 0; j < 8; j++)
				val = (val << 1) | in.read();
			codeLengths[i] = val;
		}
		return new CanonicalCode(codeLengths);
	}
	

	static void decompress(){
		try {
			while (true) {
				int symbol = read();
				if (symbol == EOF_SYMBOL)
					break;
				out.write(symbol);
			}
		}catch(IOException e){
			System.out.println("read file error");
		}
	}

	static int read(){
		if (codeTree == null)
			throw new NullPointerException("Code tree is null");

		InternalNode currentNode = codeTree.root;
		while (true) {
			int temp = in.read();
			Node nextNode = null;
			if (temp == 0) {
				nextNode = currentNode.leftChild;
			}
			else if (temp == 1) {
				nextNode = currentNode.rightChild;
			}

			if (nextNode instanceof Leaf)
				return ((Leaf)nextNode).symbol;
			else if (nextNode instanceof InternalNode)
				currentNode = (InternalNode)nextNode;
		}
	}
	
}
