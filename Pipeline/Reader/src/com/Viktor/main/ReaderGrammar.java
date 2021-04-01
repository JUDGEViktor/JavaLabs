package com.Viktor.main;

import java.util.ArrayList;

import java.util.logging.Logger;

class ReaderGrammar extends MyGrammar {

    private static final String[] readerGrammar = new String[1];

    enum Vocabulary {
        BUFFER_SIZE(0);

        public final int i;

        Vocabulary(int i){
            this.i = i;
        }
    }

    static{
        readerGrammar[Vocabulary.BUFFER_SIZE.i] = "bufferSize";
    }

    public ReaderGrammar(Logger logger){
        super(readerGrammar);
        this.logger = logger;
    }

    @Override
    protected Object DoSemanticAnalysis(String semantic, ArrayList<String> paramsToAnalyze) {
        Object semanticObj = null;
        if(semantic.equals(token(Vocabulary.BUFFER_SIZE.i))) {
            int bufferSize = Integer.parseInt(paramsToAnalyze.get(0));
            if (bufferSize > 0 && bufferSize % 2 == 0)
                semanticObj = bufferSize;
        }
        return semanticObj;
    }

    @Override
    protected String[] GetGrammarTokens() {
        return readerGrammar;
    }

    private Logger logger;
}
