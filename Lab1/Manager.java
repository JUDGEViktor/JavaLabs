import java.io.IOException;
import java.util.logging.Level;

public class Manager {

    Manager(String configName){
        try {
            Config config = new Config(configName);
            String[] tokens = config.readConfig();
            if (tokens.length != config.numParameters) {
                Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_CONFIG.name());
                return;
            }
            ParamAnalyzer params = new ParamAnalyzer(tokens);
            sizeOfSection = params.GetSize();
            sizeOfFile = (int) params.GetFileInputStream().getChannel().size();
            reader = new Reader(params.GetFileInputStream(), sizeOfSection);
            writer = new Writer(params.GetFileOutputStream());
            if (params.GetMode().equalsIgnoreCase(Config.Modes.COMPRESS.name)) {
                curMethod = new Compressor();
            } else if (params.GetMode().equalsIgnoreCase(Config.Modes.DECOMPRESS.name)) {
                curMethod = new Decompressor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void Run() {
        byte[] readedBytes;
        byte[] appliedBytes;
        int counter = 0;
        while(counter < sizeOfFile){
            readedBytes = reader.Read();
            counter += sizeOfSection;
            appliedBytes = curMethod.Run(readedBytes);
            if(appliedBytes != null)
                writer.Write(appliedBytes);
        }
        reader.Close();
        writer.Close();
        return;
    }


    private Reader reader;
    private Writer writer;
    private Method curMethod;
    private int sizeOfSection;
    private int sizeOfFile;

}
