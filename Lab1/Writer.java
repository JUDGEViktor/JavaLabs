import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class Writer {

    Writer(FileOutputStream fos) {
        bos = new BufferedOutputStream(fos);
        return;
    }

    void Write(byte[] bytes){
        try {
            for(byte byte_ : bytes){
                bos.write(byte_);
            }
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


    private BufferedOutputStream bos;
}
