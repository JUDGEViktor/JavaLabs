package com.Viktor.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;


import javafx.util.Pair;
import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.IReader;
import ru.spbstu.pipeline.IWriter;


import java.io.File;
import java.util.ArrayList;

class ManagerGrammar extends MyGrammar {

    private static final String delimiter = ";";

    private static final String[] managerGrammar = new String[5];

    enum Vocabulary {
        INPUT_FILE(0),
        OUTPUT_FILE(1),
        READER(2),
        WRITER(3),
        EXECUTOR(4);

        public final int i;

        Vocabulary(int i_){
            i = i_;
        }
    }

    static{
        managerGrammar[Vocabulary.INPUT_FILE.i] = "input file";
        managerGrammar[Vocabulary.OUTPUT_FILE.i] = "output file";
        managerGrammar[Vocabulary.READER.i] = "reader";
        managerGrammar[Vocabulary.WRITER.i] = "writer";
        managerGrammar[Vocabulary.EXECUTOR.i] = "executor";
    }

    public ManagerGrammar(Logger logger){
        super(managerGrammar);
        this.logger = logger;
    }

    @Override
    protected Object DoSemanticAnalysis(String semantic, ArrayList<String> paramsToAnalyze) {
        try {
            Object semanticObj = null;
            if(semantic.equals(token(Vocabulary.INPUT_FILE.i))){
                if(new File(paramsToAnalyze.get(0)).canRead())
                    semanticObj = paramsToAnalyze.get(0);

            } else if(semantic.equals(token(Vocabulary.OUTPUT_FILE.i))){
                if(new File(paramsToAnalyze.get(0)).canWrite())
                    semanticObj = paramsToAnalyze.get(0);

            } else if(semantic.equals(token(Vocabulary.READER.i))){
                String[] tokens = paramsToAnalyze.get(0).split(delimiter);
                String className = tokens[0].trim();
                String configFileName = tokens[1].trim();
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getDeclaredConstructor(Logger.class);
                IReader reader = (IReader) constructor.newInstance(logger);
                semanticObj = new Pair<IReader, String>(reader, configFileName);

            } else if(semantic.equals(token(Vocabulary.WRITER.i))){
                String[] tokens = paramsToAnalyze.get(0).split(delimiter);
                String className = tokens[0].trim();
                String configFileName = tokens[1].trim();
                Class<?> clazz = Class.forName(className);
                Constructor<?> constructor = clazz.getDeclaredConstructor(Logger.class);
                IWriter writer = (IWriter) constructor.newInstance(logger);
                semanticObj = new Pair<IWriter, String>(writer, configFileName);

            } else if (semantic.equals(token(Vocabulary.EXECUTOR.i))){
                ArrayList<Pair<IExecutor, String>> tmpContainer  = new ArrayList<>();
                for(String paramToAnalyze : paramsToAnalyze){
                    String[] tokens = paramToAnalyze.split(delimiter);
                    String className = tokens[0].trim();
                    String configFileName = tokens[1].trim();
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> constructor = clazz.getDeclaredConstructor(Logger.class);
                    IExecutor executor = (IExecutor) constructor.newInstance(logger);
                    tmpContainer.add(new Pair<>(executor, configFileName));
                }
                semanticObj = tmpContainer;
            }
            return semanticObj;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.log(Level.SEVERE, Log.ERRORS.ERROR_WITH_PIPELINE.name());
        }
        return null;
    }

    @Override
    protected String[] GetGrammarTokens() {
        return managerGrammar;
    }

    private Logger logger;

}
