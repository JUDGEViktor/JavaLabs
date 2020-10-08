import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class ByteReader {
    private BufferedInputStream bis;

    ByteReader(FileInputStream fis)
    {
        try {
            bis = new BufferedInputStream(fis);
            int size = (int)fis.getChannel().size();
            if(bis.markSupported()) {
                bis.mark(size + 1);
            }
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_INPUT_OUTPUT_FILE.name());
        }
        return;
    }

    public int Read(){
        try {
            return bis.read();
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_READING_FILE.name());
        }
        return -1;
    }

    public void Close(){
        try {
            bis.close();
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_CLOSING_FILE.name());
        }
        return;
    }

    public void Reset(){
        try {
            bis.reset();
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_READING_FILE.name());
        }
        return;
    }



}
