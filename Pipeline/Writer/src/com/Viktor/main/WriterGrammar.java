package com.Viktor.main;

import java.util.ArrayList;

import java.util.logging.Logger;

class WriterGrammar extends MyGrammar {

    private static final String[] writerGrammar = new String[1];

    enum Vocabulary {
        BUFFER_SIZE(0);

        public final int i;

        Vocabulary(int i_){
            i = i_;
        }
    }

    static{
        writerGrammar[Vocabulary.BUFFER_SIZE.i] = "bufferSize";
    }

    public WriterGrammar(Logger logger){
        super(writerGrammar);
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
        return writerGrammar;
    }

    private Logger logger;
}

