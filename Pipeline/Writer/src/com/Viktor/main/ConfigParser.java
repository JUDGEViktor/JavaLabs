package com.Viktor.main;

import ru.spbstu.pipeline.RC;

import java.util.logging.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

class ConfigParser {

    public static ConfigParser SetConfigParser(String fileName, String[] validTokensTypes, String delimiter, Logger logger){
        ConfigParser configParser = new ConfigParser(fileName, delimiter, logger);
        configParser.validVocabulary = new String[validTokensTypes.length];
        for(int i = 0; i < validTokensTypes.length; i++){
            configParser.validVocabulary[i] = validTokensTypes[i];
        }
        return configParser;
    }

    private ConfigParser(String fileName, String delimiter, Logger logger){
        configFileName = fileName;
        tokens = new HashMap<>();
        this.delimiter = delimiter;
        this.logger = logger;
    }

    public RC parseConfig()
    {
        try
        {
            BufferedReader buff = new BufferedReader(new FileReader(configFileName));

            for (String line = buff.readLine(); line != null; line = buff.readLine())
            {
                if(line.equals(""))
                    continue;

                String[] curStringTokens = line.split(delimiter);

                if (curStringTokens.length != 2) {
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                    return RC.CODE_CONFIG_GRAMMAR_ERROR;
                }

                String tokenKey = curStringTokens[0].trim();
                String tokenVal = curStringTokens[1].trim();
                if(Arrays.asList(validVocabulary).contains(tokenKey)) {
                    ArrayList<String> curArray = tokens.get(tokenKey);
                    if(curArray == null) {
                        curArray = new ArrayList<>();
                    }
                    curArray.add(tokenVal);
                    tokens.put(tokenKey, curArray);
                } else {
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                    return RC.CODE_CONFIG_GRAMMAR_ERROR;
                }
            }

            if(tokens.size() != validVocabulary.length) {
                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                return RC.CODE_CONFIG_GRAMMAR_ERROR;
            }
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
            return RC.CODE_FAILED_TO_READ;
        }
        return RC.CODE_SUCCESS;
    }


    public ArrayList<String> GetTokenInfo(String tokenType){
        return tokens.get(tokenType);
    }

    private String configFileName;

    private String[] validVocabulary;

    private Map<String, ArrayList<String>> tokens;

    private String delimiter;

    Logger logger;


}
