package com.Viktor.main;

import javafx.util.Pair;
import ru.spbstu.pipeline.*;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

import java.util.logging.Logger;

class Manager extends ManagerGrammar {

    public Manager(String configName, Logger logger){
        super(logger);
        configFileName = configName;
        this.logger = logger;
    }

    public RC Run(){
        try {
            RC errorCode = BuildPipeline();
            if(errorCode != RC.CODE_SUCCESS) {
                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_PIPELINE.name());
                return errorCode;
            }

            FileInputStream fis = new FileInputStream(inputFileName);
            FileOutputStream fos = new FileOutputStream(outputFileName);

            reader.setInputStream(fis);
            writer.setOutputStream(fos);

            errorCode = reader.execute();
            if(errorCode != RC.CODE_SUCCESS){
                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_PIPELINE.name());
            }

            fis.close();
            fos.close();
            return errorCode;

        } catch (IOException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_INPUT_FILE.name());
            return RC.CODE_INVALID_INPUT_STREAM;
        }
    }

    private RC BuildPipeline(){
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

                switch(grammar) {
                    case INPUT_FILE:
                        inputFileName = (String) param;
                        break;

                    case OUTPUT_FILE:
                        outputFileName = (String) param;
                        break;

                    case READER:
                        Pair<IReader, String> readerAndConfig = (Pair<IReader, String>) param;
                        reader = readerAndConfig.getKey();
                        errorCode = reader.setConfig(readerAndConfig.getValue());
                        if(errorCode != RC.CODE_SUCCESS) {
                            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                            return errorCode;
                        }
                        break;

                    case WRITER:
                        Pair<IWriter, String> writerAndConfig = (Pair<IWriter, String>) param;
                        writer = writerAndConfig.getKey();
                        errorCode = writer.setConfig(writerAndConfig.getValue());
                        if(errorCode != RC.CODE_SUCCESS) {
                            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                            return errorCode;
                        }
                        break;

                    case EXECUTOR:
                        ArrayList<Pair<IExecutor, String>> executorsAndConfigs = (ArrayList<Pair<IExecutor, String>>) param;
                        executors = new IExecutor[executorsAndConfigs.size()];
                        int k = 0;
                        for (Pair<IExecutor, String> executorAndConfig : executorsAndConfigs) {
                            executors[k] = executorAndConfig.getKey();
                            errorCode = executors[k++].setConfig(executorAndConfig.getValue());
                            if(errorCode != RC.CODE_SUCCESS) {
                                logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
                                return errorCode;
                            }
                        }
                        break;
                }
            }
        } else {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_CONFIG_FILE.name());
            return errorCode;
        }

        if(!BuildPiplineAvailable()) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_PIPELINE.name());
            return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
        }

        return LinkPipeLineElements();
    }

    private RC LinkPipeLineElements(){
        IPipelineStep[] pipeLineElements = new IPipelineStep[executors.length + 2];
        pipeLineElements[0] = reader;
        System.arraycopy(executors, 0, pipeLineElements, 1, executors.length);
        pipeLineElements[pipeLineElements.length - 1] = writer;
        for(int i = 0; i < pipeLineElements.length; i++){
            if(i > 0){
                RC errorCode = pipeLineElements[i].setProducer((IProducer)pipeLineElements[i-1]);
                if(errorCode != RC.CODE_SUCCESS)
                    return errorCode;
            }
            if(i < pipeLineElements.length - 1){
                RC errorCode =  pipeLineElements[i].setConsumer((IConsumer)pipeLineElements[i+1]);
                if(errorCode != RC.CODE_SUCCESS)
                    return errorCode;
            }
        }
        return RC.CODE_SUCCESS;
    }

    private boolean BuildPiplineAvailable(){
        return(reader != null && writer != null && executors != null);
    }

    private String inputFileName;
    private String outputFileName;
    private String configFileName;

    private IReader reader;
    private IWriter writer;
    private IExecutor[] executors;

    private Logger logger;

}
