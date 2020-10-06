import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public final class BitInputStream implements AutoCloseable {

	private InputStream input;

	private int currentByte;

	//Remaining bits in current byte
	private int numBitsRemaining;
	

	public BitInputStream(InputStream in) {
		input = Objects.requireNonNull(in);
		currentByte = 0;
		numBitsRemaining = 0;
	}



	public int readBit() {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			try {
				currentByte = input.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		numBitsRemaining--;
		return (currentByte >>> numBitsRemaining) & 1;
	}


	public int read(){
		int result = readBit();
		return result;
	}


	public void close(){
		try {
			input.close();
			currentByte = -1;
			numBitsRemaining = 0;
		}catch(IOException e){
			System.out.println("close file error");
		}
	}
	
}
