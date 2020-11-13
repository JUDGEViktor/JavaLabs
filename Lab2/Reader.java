package com.Viktor.main;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IReader;
import ru.spbstu.pipeline.RC;

import java.util.logging.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class Reader extends ReaderAndWriterGrammar implements IReader {

    public Reader(Logger logger) {
        super(logger);
        this.logger = logger;
    }

    @Override
    public RC setInputStream(FileInputStream var1){
        bis = new BufferedInputStream(var1);
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setConfig(String var1){
        configFileName = var1;
        return Configure();
    }

    @Override
    public RC setConsumer(IExecutable var1){
        consumer = var1;
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setProducer(IExecutable var1){return RC.CODE_SUCCESS;}

    @Override
    public RC execute(byte[] var1){
        try {
            while(true) {
                byte[] bytes = new byte[bufferSize];
                int numOfReadedBytes = bis.read(bytes, 0, bufferSize);

                if (numOfReadedBytes == -1)
                    return RC.CODE_SUCCESS;

                if (numOfReadedBytes != bufferSize)
                    bytes = Arrays.copyOfRange(bytes, 0, numOfReadedBytes);

                RC errorCode = consumer.execute(bytes);
                if(errorCode != RC.CODE_SUCCESS) {
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_EXECUTION.name());
                    return errorCode;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_INPUT_FILE.name());
            return RC.CODE_FAILED_TO_READ;
        }
    }

    private RC Configure(){
        ConfigParser configParser = ConfigParser.SetConfigParser(configFileName, GetGrammarTokens(), delimiter(), logger);

        RC errorCode = configParser.parseConfig();
        if(errorCode == RC.CODE_SUCCESS) {
            for(Vocabulary grammar : Vocabulary.values()){
                ArrayList<String> params = configParser.GetTokenInfo(token(grammar.i));
                Object param = DoSemanticAnalysis(token(grammar.i), params);

                if(param == null){
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                    return RC.CODE_CONFIG_SEMANTIC_ERROR;
                }

                switch(grammar){
                    case BUFFER_SIZE:
                        bufferSize = (int)param;
                        break;
                }
            }
        } else {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
            return errorCode;
        }
        return RC.CODE_SUCCESS;
    }


    private String configFileName;
    private BufferedInputStream bis;
    private IExecutable consumer;
    private int bufferSize;
    private Logger logger;

}
