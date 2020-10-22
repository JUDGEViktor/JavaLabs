import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class Reader {

    Reader(FileInputStream fis, int size_)
    {
        bis = new BufferedInputStream(fis);
        size = size_;
        return;
    }

    public byte[] Read(){
        ArrayList<Byte> bytes = new ArrayList<>();
        try {
            int count = 0;
            byte byte_;
            int tmp;
            while(count < size){
                tmp = bis.read();
                if(tmp == -1)
                    break;
                byte_ = (byte)tmp;
                bytes.add(byte_);
                count++;
            }
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_READING_FILE.name());
        }
        byte[] res = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++)
            res[i] = bytes.get(i);
        return res;
    }

    public void Close(){
        try {
            bis.close();
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_CLOSING_FILE.name());
        }
        return;
    }

    private BufferedInputStream bis;
    private int size;

}
