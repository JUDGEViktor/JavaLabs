import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;

public class ParamAnalyzer {
    private FileInputStream fis;
    private FileOutputStream fos;
    private String mode;

    ParamAnalyzer(String[] tokens){
        try {
            fis = new FileInputStream(tokens[Config.Vocabulary.INPUT_FILE.i]);
            fos = new FileOutputStream(tokens[Config.Vocabulary.OUTPUT_FILE.i]);
            mode = tokens[Config.Vocabulary.MODE.i];
        } catch (FileNotFoundException e){
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_INPUT_OUTPUT_FILE.name());
        }
        return;
    }

    public FileInputStream GetFileInputStream(){
        return fis;
    }

    public FileOutputStream GetFileOutputStream(){
        return fos;
    }

    public String GetMode(){ return mode; }

}
