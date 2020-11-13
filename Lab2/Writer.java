package com.Viktor.main;
import ru.spbstu.pipeline.*;
import java.util.logging.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class Writer extends  ReaderAndWriterGrammar implements  IWriter{

    public Writer(Logger logger){
        super(logger);
        this.logger = logger;
    }

    @Override
    public RC setOutputStream(FileOutputStream var1) {
        bos = new BufferedOutputStream(var1);
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setConfig(String var1) {
        configFileName = var1;
        return Configure();
    }

    @Override
    public RC setConsumer(IExecutable var1) {
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setProducer(IExecutable var1) {
        producer = var1;
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC execute(byte[] var1) {
        try {
            int i = 0;
            while(i*bufferSize + bufferSize <= var1.length - 1){
                bos.write(var1, i*bufferSize, bufferSize);
                i++;
            }
            bos.write(var1, i*bufferSize, abs(var1.length - i*bufferSize));
            bos.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_OUTPUT_FILE.name());
            return RC.CODE_FAILED_TO_WRITE;
        }
        return RC.CODE_SUCCESS;
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
        } else{
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
            return errorCode;
        }
        return RC.CODE_SUCCESS;
    }

    private String configFileName;
    private BufferedOutputStream bos;
    private IExecutable producer;
    private int bufferSize;
    private Logger logger;
}
