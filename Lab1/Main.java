import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            Log.logger.log(Level.SEVERE, Log.ERRORS.ERROR_COMMAND_LINES_PARAMETERS.name());
            return;
        } else {
            Manager manager = new Manager(args[0]);
            manager.Run();
        }
    }
}
