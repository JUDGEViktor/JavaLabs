import java.io.*;
import java.util.logging.Level;

public class ByteWriter {
    private BufferedOutputStream bos;

    ByteWriter(FileOutputStream fos, int size) {
        bos = new BufferedOutputStream(fos);
        return;
    }

    void Write(int byte_){
        try {
            bos.write(byte_);
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_WRITING_FILE.name());
        }
        return;
    }

    void Close(){
        try {
            bos.close();
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_CLOSING_FILE.name());
        }
        return;
    }

}
