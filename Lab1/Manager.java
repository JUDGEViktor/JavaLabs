import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Level;

public class Manager {

    private ByteReader byteReader;
    private ByteWriter byteWriter;
    private Method curMethod;

    Manager(String configName){
        Config config = new Config(configName);
        String[] tokens = config.readConfig();
        if (tokens.length != config.numParameters) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_CONFIG.name());
            return;
        }
        ParamAnalyzer params = new ParamAnalyzer(tokens);
        byteReader = new ByteReader(params.GetFileInputStream());
        byteWriter = new ByteWriter(params.GetFileOutputStream(), -1);
        if (params.GetMode().equalsIgnoreCase(Config.Modes.COMPRESS.name)) {
            curMethod = new Compressor();
        } else if (params.GetMode().equalsIgnoreCase(Config.Modes.DECOMPRESS.name)) {
            curMethod = new Decompressor();
        }
        return;
    }

    public void Run() {
        curMethod.Run(byteReader, byteWriter);
        byteReader.Close();
        byteWriter.Close();
        return;
    }







}
