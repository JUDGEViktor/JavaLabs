import java.io.*;
import java.util.logging.Level;

public class Config {

    final static int numParameters = 4;

    final static String delimiter = ":";

    enum Modes{
        COMPRESS("compress"),
        DECOMPRESS("decompress");

        Modes(String str) {name = str;}

        public final String name;
    }

    enum Vocabulary{
        INPUT_FILE("input file", 0),
        OUTPUT_FILE("output file", 1),
        SIZE("size", 2),
        MODE("mode", 3);

        Vocabulary(String str, int i_) {name = str; i = i_;}

        public final String name;
        public final int i;
    }


    public Config(String fileName){
        configFileName = fileName;
    }

    public String[] readConfig()
    {
        try
        {
            String[] params = new String[numParameters];

            BufferedReader buff = new BufferedReader(new FileReader(configFileName));

            for (String line = buff.readLine(); line != null; line = buff.readLine())
            {
                String[] tokens = line.split(delimiter);

                if (tokens.length != 2) {
                    Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_CONFIG.name());
                    return null;
                }

                for (int i = 0; i < tokens.length; i++)
                {
                    String tokenType = tokens[i].trim();

                    if (tokenType.equalsIgnoreCase(Vocabulary.INPUT_FILE.name))
                        params[Vocabulary.INPUT_FILE.i] = tokens[++i].trim();

                    else if (tokenType.equalsIgnoreCase(Vocabulary.OUTPUT_FILE.name))
                        params[Vocabulary.OUTPUT_FILE.i] = tokens[++i].trim();

                    else if (tokenType.equalsIgnoreCase(Vocabulary.MODE.name))
                        params[Vocabulary.MODE.i] = tokens[++i].trim().toLowerCase();

                    else if (tokenType.equalsIgnoreCase(Vocabulary.SIZE.name))
                        params[Vocabulary.SIZE.i] = tokens[++i].trim().toLowerCase();
                }
            }
            return params;
        }
        catch (FileNotFoundException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
        } catch (IOException e) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
        }
        return null;
    }

    String configFileName;

}
