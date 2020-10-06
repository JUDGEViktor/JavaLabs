import java.io.*;
import java.util.List;


public final class Compress {

	private static final int EOF_SYMBOL = 256;

	private static final int FREQUENCIES_ARRAY_LENGTH_SIZE = 257;

	private static InputStream in;

	private static BitOutputStream out;

	private static CodeTree codeTree;

	private static CanonicalCode canonicalCode;

	private static FrequencyTable frequencies;

	// Main Function
	public Compress(String inputFileName, String outputFileName, String infoFileName){

		try {
			File inputFile = new File(inputFileName);
			File outputFile = new File(outputFileName);
			in = new BufferedInputStream(new FileInputStream(inputFile));
			out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

			//Read file to count symbol frequencies
			getFrequencies(inputFile);
			codeTree = frequencies.buildCodeTree();
			canonicalCode = new CanonicalCode(codeTree, frequencies.getSymbolLimit());
			// Replace code tree with canonical one
			codeTree = canonicalCode.toCodeTree();

			// Compressing and writing into file and writing some info in another file
			CompressAndWrite(infoFileName);
		}catch(FileNotFoundException e){
			System.out.println("file not found");
		}

	}
	

	private static int CountCompressedFileSize(){
		int compressedFileSize = 0;
		for(int i = 0 ; i < codeTree.getCodes().size(); i++){
			if(codeTree.getCodes().get(i) != null)
				compressedFileSize += codeTree.getCodes().get(i).size() * frequencies.get(i);
		}
		compressedFileSize += 8 * FREQUENCIES_ARRAY_LENGTH_SIZE;
		return compressedFileSize;
	}


	private static void CompressAndWrite(String infoFileName){
		try {
			writeCodeLengthTable();
			int fileSize = compress();

			int compressedfileSize = CountCompressedFileSize();
			double comperssinoKoeff = (double)compressedfileSize / fileSize;
			new Info(infoFileName, canonicalCode.getCodeLengths(), comperssinoKoeff);

			out.close();
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Returns a frequencies
	// Contains an extra entry for symbol 256 for EOF
	private static void getFrequencies(File file)  {
		try {
			frequencies = new FrequencyTable(new int[FREQUENCIES_ARRAY_LENGTH_SIZE]);
			InputStream input = new BufferedInputStream(new FileInputStream(file));
			while (true) {
				int b = input.read();
				if (b == -1)
					break;
				frequencies.increment(b);
			}
			frequencies.increment(EOF_SYMBOL);  // EOF symbol gets a frequency of 1
			input.close();
		} catch (FileNotFoundException e){
			System.out.println("file not found");
		} catch (IOException e){
			System.out.println("file error");
		}
	}


	static void writeCodeLengthTable(){
		for (int i = 0; i < canonicalCode.getSymbolLimit(); i++) {
			int val = canonicalCode.getCodeLength(i);
			for (int j = 7; j >= 0; j--)
				out.write((val >>> j) & 1);
		}
	}
	

	static int compress() {
		int fileSize = 0;
		while (true) {
			int b = 0;
			try {
				b = in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b == -1)
				break;
			fileSize += 8;
			write(b);
		}
		write(EOF_SYMBOL);  // EOF
		return fileSize;
	}


	static void write(int symbol){
		List<Integer> bits = codeTree.getCode(symbol);
		for (int b : bits)
			out.write(b);
	}
	
}
