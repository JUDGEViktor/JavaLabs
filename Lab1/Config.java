import java.io.*;

public class Config {

    private static final String INPUT_FILE_KEY_WORD = "input file";
    private static final String OUTPUT_FILE_KEY_WORD = "output file";
    private static final String INFO_FILE_KEY_WORD = "info file";
    private static final String MODE_KEY_WORD = "mode";
    private static final String SEPARATOR = "=";

    public static String INPUT_FILE_NAME;
    public static String OUTPUT_FILE_NAME;
    public static String INFO_FILE_NAME;
    public static String MODE_NAME;

    private void ReadConfig(String fileConfigName) {
        try {
            File file = new File(fileConfigName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                if(line.contains(INPUT_FILE_KEY_WORD)) {
                    INPUT_FILE_NAME = line.replace(INPUT_FILE_KEY_WORD, "").replace(SEPARATOR,"").trim();
                }
                if(line.contains(OUTPUT_FILE_KEY_WORD)) {
                    OUTPUT_FILE_NAME = line.replace(OUTPUT_FILE_KEY_WORD, "").replace(SEPARATOR,"").trim();
                }
                if(line.contains(INFO_FILE_KEY_WORD)) {
                    INFO_FILE_NAME = line.replace(INFO_FILE_KEY_WORD, "").replace(SEPARATOR,"").trim();
                }
                if(line.contains(MODE_KEY_WORD)) {
                    MODE_NAME = line.replace(MODE_KEY_WORD, "").replace(SEPARATOR,"").trim();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config(String fileConfigName){
        ReadConfig(fileConfigName);
        try {
            if (MODE_NAME.equals("compress")) {
                new Compress(INPUT_FILE_NAME, OUTPUT_FILE_NAME, INFO_FILE_NAME);
            } else if (MODE_NAME.equals("decompress")) {
                new Decompress(INPUT_FILE_NAME, OUTPUT_FILE_NAME);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
