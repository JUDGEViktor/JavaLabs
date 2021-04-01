package com.Viktor.main;

import ru.spbstu.pipeline.*;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class Reader extends ReaderGrammar implements IReader {

    public Reader(Logger logger) {
        super(logger);
        this.logger = logger;
        this.isEndOfPipeline = false;
    }

    @Override
    public RC execute(){
        try {

            while (!isEndOfPipeline) {
                readedBytes = new byte[bufferSize];
                int numOfReadedBytes = bis.read(readedBytes, 0, bufferSize);

                if (numOfReadedBytes == -1) {
                    isEndOfPipeline = true;
                } else {

                    if (numOfReadedBytes != bufferSize)
                        readedBytes = Arrays.copyOfRange(readedBytes, 0, numOfReadedBytes);

                    if (readedBytes == null) {
                        logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_READING_FILE.name());
                        return RC.CODE_FAILED_TO_READ;
                    }
                }
                RC errorCode = consumer.execute();
                if (errorCode != RC.CODE_SUCCESS) {
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_EXECUTION.name());
                    return errorCode;
                }
            }
            RC errorCode = consumer.execute();
            if (errorCode != RC.CODE_SUCCESS) {
                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_EXECUTION.name());
                return errorCode;
            }
            return RC.CODE_SUCCESS;

        } catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_INPUT_FILE.name());
            return RC.CODE_FAILED_TO_READ;
        }
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
    public RC setConsumer(IConsumer consumer){
        this.consumer = consumer;
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setProducer(IProducer p){
        return RC.CODE_SUCCESS;
    }

    @Override
    public TYPE[] getOutputTypes(){
        return supportedOutputTypes;
    }

    @Override
    public IMediator getMediator(TYPE type){
        return new Mediator(type);
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




    class Mediator implements IMediator {

        Mediator(TYPE necessaryType){
            this.necessaryType = necessaryType;
        }

        private Object ConvertToNecessaryType(){
            //in case when all file was readed
            if(isEndOfPipeline)
                return null;
            ByteBuffer byteBuffer = ByteBuffer.wrap(readedBytes);
            Object dataInNecessaryType = null;
            switch(necessaryType){
                case BYTE:
                    dataInNecessaryType = readedBytes.clone();
                    break;

                case SHORT:
                    int sizeOfShort = readedBytes.length / 2;
                    short[] arrOfShort = new short[sizeOfShort];
                    for(int i = 0; i < sizeOfShort; i++) {
                        arrOfShort[i] = byteBuffer.getShort();
                    }
                    dataInNecessaryType = arrOfShort;
                    break;

                case CHAR:
                    int sizeOfChar = readedBytes.length / 2;
                    char[] arrOfChar = new char[sizeOfChar];
                    for(int i = 0; i < sizeOfChar; i++) {
                        arrOfChar[i] = byteBuffer.getChar();
                    }
                    dataInNecessaryType = arrOfChar;
                    break;
            }
            return dataInNecessaryType;
        }


        @Override
        public Object getData() {

            return ConvertToNecessaryType();

        }

        private TYPE necessaryType;

    }


    private Boolean isEndOfPipeline;

    private String configFileName;

    private BufferedInputStream bis;

    private IConsumer consumer;

    private int bufferSize;

    private byte[] readedBytes;

    private TYPE[] supportedOutputTypes = TYPE.values();

    private Logger logger;

}
