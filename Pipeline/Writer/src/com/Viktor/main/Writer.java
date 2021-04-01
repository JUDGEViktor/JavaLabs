package com.Viktor.main;

import ru.spbstu.pipeline.*;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class Writer extends  WriterGrammar implements  IWriter {

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
    public RC setConsumer(IConsumer consumer) {
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

        try {
            Object container = producerMediator.getData();

            if(container == null) {
                this.bos.close();
                return RC.CODE_SUCCESS;
            } else {
                byte[] bytesToProcess = (byte[]) container;

                int i = 0;
                while(i*bufferSize + bufferSize <= bytesToProcess.length - 1){
                    this.bos.write(bytesToProcess, i*bufferSize, bufferSize);
                    i++;
                }
                this.bos.write(bytesToProcess, i*bufferSize, abs(bytesToProcess.length - i*bufferSize));
                this.bos.flush();
            }

            return RC.CODE_SUCCESS;

        } catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_OUTPUT_FILE.name());
            return RC.CODE_FAILED_TO_WRITE;
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
        } else{
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

    private String configFileName;

    private BufferedOutputStream bos;

    private int bufferSize;

    private Logger logger;

    private IMediator producerMediator;

    private TYPE[] supportedInputTypes = {TYPE.BYTE};

}
