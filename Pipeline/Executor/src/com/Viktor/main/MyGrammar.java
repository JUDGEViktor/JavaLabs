package com.Viktor.main;

import ru.spbstu.pipeline.BaseGrammar;

import java.util.ArrayList;

abstract class MyGrammar extends BaseGrammar {

    protected MyGrammar(String[] grammarTokens){
        super(grammarTokens);
    }

    protected abstract String[] GetGrammarTokens();

    protected abstract Object DoSemanticAnalysis(String semantic, ArrayList<String> paramsToAnalyze);

}
