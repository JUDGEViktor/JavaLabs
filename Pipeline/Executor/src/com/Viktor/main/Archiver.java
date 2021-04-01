package com.Viktor.main;

import com.sun.javaws.IconUtil;
import ru.spbstu.pipeline.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Archiver extends ArchiverGrammar implements IExecutor {
    public Archiver(Logger logger){
        super(logger);
        this.logger = logger;
        this.isEndOfPipeline = false;
    }

    @Override
    public TYPE[] getOutputTypes(){
        return supportedOutputTypes;
    }

    @Override
    public IMediator getMediator(TYPE type){
        return new Mediator(type);
    }

    @Override
    public RC setConfig(String var1) {
        configFileName = var1;
        return Configure();
    }

    @Override
    public RC setConsumer(IConsumer consumer) {
        this.consumer = consumer;
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setProducer(IProducer producer) {
        TYPE[] producerSupportedOutputTypes = producer.getOutputTypes();
        TYPE commonType = FindCommonType(supportedInputTypes, producerSupportedOutputTypes);
        if(commonType == null){
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_NO_COMMON_TYPES.name());
            return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
        } else {
            producerMediator = producer.getMediator(commonType);
        }
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC execute() {

        Object container = producerMediator.getData();

        if(container == null) {
            isEndOfPipeline = true;
        } else {
            byte[] bytesToProcess = (byte[]) container;
            processedData = method.ApplyMethod(bytesToProcess);
            if (processedData == null) {
                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WHILE_EXECUTION.name());
                return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
            }
        }
        return consumer.execute();

    }

    private RC Configure(){
        ConfigParser configParser = ConfigParser.SetConfigParser(configFileName, GetGrammarTokens(), delimiter(), logger);

        RC errorCode = configParser.parseConfig();
        if(errorCode == RC.CODE_SUCCESS) {
            for(Archiver.Vocabulary grammar : Archiver.Vocabulary.values()){
                ArrayList<String> params = configParser.GetTokenInfo(token(grammar.i));
                Object param = DoSemanticAnalysis(token(grammar.i), params);

                if(param == null){
                    logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                    return RC.CODE_CONFIG_SEMANTIC_ERROR;
                }

                switch(grammar){
                    case MODE:
                        mode = (String)param;
                        if(mode.equals(Modes.COMPRESS.nameInConfig)){
                            method = new Compressor(logger);
                        } else if (mode.equals(Modes.DECOMPRESS.nameInConfig)){
                            method = new Decompressor(logger);
                        } else {
                            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                            return RC.CODE_CONFIG_GRAMMAR_ERROR;
                        }
                        break;
                }
            }
        } else {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
            return errorCode;
        }
        return RC.CODE_SUCCESS;
    }

    private TYPE FindCommonType(TYPE[] firstSet, TYPE[] secondSet){
        for(TYPE fromFirstSet : firstSet){
            for(TYPE fromSecondSet : secondSet){
                if (fromFirstSet == fromSecondSet)
                    return fromFirstSet;
            }
        }
        return null;
    }


    class Mediator implements IMediator {

        Mediator(TYPE necessaryType){
            this.necessaryType = necessaryType;
        }

        private Object ConvertToNecessaryType(){
            if(isEndOfPipeline)
                return null;
            ByteBuffer byteBuffer = ByteBuffer.wrap(processedData);
            Object dataInNecessaryType = null;
            switch(necessaryType){
                case BYTE:
                    dataInNecessaryType = processedData.clone();
                    break;

                case SHORT:
                    int sizeOfShort = processedData.length / 2;
                    short[] arrOfShort = new short[sizeOfShort];
                    for(int i = 0; i < sizeOfShort; i++) {
                        arrOfShort[i] = byteBuffer.getShort();
                    }
                    dataInNecessaryType = arrOfShort;
                    break;

                case CHAR:
                    int sizeOfChar = processedData.length / 2;
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

    private Logger logger;

    private Method method;

    private String mode;

    private byte[] processedData;

    private IConsumer consumer;

    private IMediator producerMediator;

    private TYPE[] supportedInputTypes = {TYPE.BYTE};

    private TYPE[] supportedOutputTypes = TYPE.values();


}
