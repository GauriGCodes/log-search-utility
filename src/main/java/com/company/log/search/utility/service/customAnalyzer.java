package com.company.log.search.utility.service;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
public class customAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer source = new StandardTokenizer();
        TokenStream filter = new WordDelimiterGraphFilter(source, WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE | WordDelimiterGraphFilter.PRESERVE_ORIGINAL, null);
        filter = new LowerCaseFilter(filter);
        return new TokenStreamComponents(source, filter);
    }
}
