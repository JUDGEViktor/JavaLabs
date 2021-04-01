package com.Viktor.main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        fh = new FileHandler("log.log");
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.setUseParentHandlers(false);
        if (args.length != 1) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_COMMAND_LINES_PARAMETERS.name());
            return;
        } else {
            Manager manager = new Manager(args[0], logger);
            manager.Run();
        }
    }
}
