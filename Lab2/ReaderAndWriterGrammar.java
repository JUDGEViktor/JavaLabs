package com.Viktor.main;

import java.util.ArrayList;

import java.util.logging.Logger;

class ReaderAndWriterGrammar extends MyGrammar {

    private static final String[] readerWriterGrammar = new String[1];

    enum Vocabulary {
        BUFFER_SIZE(0);

        public final int i;

        Vocabulary(int i_){
            i = i_;
        }
    }

    static{
        readerWriterGrammar[Vocabulary.BUFFER_SIZE.i] = "size";
    }

    public ReaderAndWriterGrammar(Logger logger){
        super(readerWriterGrammar);
        this.logger = logger;
    }

    @Override
    protected Object DoSemanticAnalysis(String semantic, ArrayList<String> paramsToAnalyze) {
        Object semanticObj = null;
        if(semantic.equals(token(Vocabulary.BUFFER_SIZE.i))) {
            int bufferSize = Integer.parseInt(paramsToAnalyze.get(0));
            if (bufferSize > 0)
                semanticObj = bufferSize;
        }
        return semanticObj;
    }

    @Override
    protected String[] GetGrammarTokens() {
        return readerWriterGrammar;
    }

    private Logger logger;
}
