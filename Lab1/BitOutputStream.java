import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


public final class BitOutputStream implements AutoCloseable {

	private OutputStream output;

	private int currentByte;
	
	// Number of bits already stored in byte
	private int numBitsFilled;

	public BitOutputStream(OutputStream out) {
		output = Objects.requireNonNull(out);
		currentByte = 0;
		numBitsFilled = 0;
	}

	public void write(int b){
		try {
			if (b != 0 && b != 1)
				throw new IllegalArgumentException("Argument must be 0 or 1");
			currentByte = (currentByte << 1) | b;
			numBitsFilled++;
			if (numBitsFilled == 8) {
				output.write(currentByte);
				currentByte = 0;
				numBitsFilled = 0;
			}
		} catch(IOException e){
			System.out.println("write file error");
		}
	}

	public void close(){
		try {
			while (numBitsFilled != 0)
				write(0);
			output.close();
		}catch(IOException e){
			System.out.println("close file error");
		}
	}
	
}
