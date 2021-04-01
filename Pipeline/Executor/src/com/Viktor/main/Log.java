package com.Viktor.main;

class Log {

    enum ERRORS {
        ERROR_COMMAND_LINES_PARAMETERS("command line parameters"),
        ERROR_WITH_CONFIG_FILE("error with config file"),
        ERROR_WITH_INPUT_FILE("error with input file"),
        ERROR_WITH_OUTPUT_FILE("error with output file"),
        ERROR_WHILE_READING_FILE("error while reading file"),
        ERROR_WHILE_WRITING_FILE("error while reading file"),
        ERROR_WHILE_EXECUTION("error while execution"),
        ERROR_WITH_PIPELINE("error with pipeline"),
        ERROR_NO_COMMON_TYPES("error no common types between producer and consumer");

        ERRORS(String errorName) { error = errorName; }

        public final String error;

    }

}
