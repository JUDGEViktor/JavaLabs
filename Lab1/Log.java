import java.util.logging.Logger;

public class Log {

    enum ERRORS{
        ERROR_COMMAND_LINES_PARAMETERS("command line parameters"),
        ERROR_CONFIG("bad config format"),
        ERROR_WITH_CONFIG_FILE("error with config file"),
        ERROR_WITH_INPUT_OUTPUT_FILE("error with input/output file"),
        ERROR_WHILE_READING_FILE("error while reading file"),
        ERROR_WHILE_CLOSING_FILE("error while closing file"),
        ERROR_WHILE_WRITING_FILE("error while reading file"),
        ERROR_WHILE_ENCODING("error while encoding"),
        ERROR_WHILE_DECODING("error while decoding");

        ERRORS(String errorName) { error = errorName; }

        public final String error;

    }

    public final static Logger logger = Logger.getLogger("HuffmanAlgorithm");

}
