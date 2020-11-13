package com.Viktor.main;

import java.util.logging.Level;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("pipeLine");
        if(args.length != 1) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_COMMAND_LINES_PARAMETERS.name());
            return;
        } else {
            Manager manager = new Manager(args[0], logger);
            manager.Run();
        }
    }
}
