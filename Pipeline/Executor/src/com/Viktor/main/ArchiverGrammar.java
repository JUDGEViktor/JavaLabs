package com.Viktor.main;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ArchiverGrammar extends MyGrammar{

    private static final String[] archiverGrammar = new String[1];

    enum Vocabulary {
        MODE(0);

        public final int i;

        Vocabulary(int i){
            this.i = i;
        }
    }

    enum Modes{
        COMPRESS("compress"),
        DECOMPRESS("decompress");

        public final String nameInConfig;

        Modes(String str) { this.nameInConfig = str; }
    }

    static{
        archiverGrammar[ArchiverGrammar.Vocabulary.MODE.i] = "mode";
    }

    public ArchiverGrammar(Logger logger){
        super(archiverGrammar);
        this.logger = logger;
    }

    @Override
    protected Object DoSemanticAnalysis(String semantic, ArrayList<String> paramsToAnalyze) {
        Object semanticObj = null;
        if(semantic.equals(token(ArchiverGrammar.Vocabulary.MODE.i))) {
            semanticObj = paramsToAnalyze.get(0);
        }
        return semanticObj;
    }

    @Override
    protected String[] GetGrammarTokens() {
        return archiverGrammar;
    }

    private Logger logger;
}
