import java.io.*;
import java.util.List;

public class Info {

    private static  FileWriter out;

    public Info(String infoFileName, int[] lengths, double comperssinoKoeff){
        try {
            out = new FileWriter(infoFileName);
            for (int length : lengths) {
                for (int j = 7; j >= 0; j--) {
                    out.write(Integer.toString((length >>> j) & 1));
                }
                out.write(" ");
            }
            out.write("\n\nCompresionKoeff = " + Double.toString(comperssinoKoeff));
            out.close();
        } catch (IOException e){
            e.getMessage();
        }
    }



}
