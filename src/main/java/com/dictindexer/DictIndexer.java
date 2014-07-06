package com.dictindexer;

import java.util.Stack;
/*
java -jar dict-indexer.jar -o index dictionary/adjs dictionary/adverb dictionary/conjunction dictionary/cpart dictionary/interjection dictionary/nouns dictionary/numeral dictionary/ordinal_numeral dictionary/participle dictionary/particle dictionary/preposition dictionary/pronominal_adjs dictionary/pronominal_adverb dictionary/pronoun dictionary/verbs
*/
public class DictIndexer implements IParserEventListener{
    private Indexer m_indexer;
    private Parser m_parser;
    private Stack<String> m_dicts=new Stack<String>();

    @Override
    public void entryFound(String word, String lemma, String code) {
    }

    @Override
    public void parsingFinished(boolean hasError) {
        System.out.println("Parsing finished: "+m_dicts.peek());
        m_dicts.pop();
        if(m_dicts.isEmpty())
            System.exit(0);
        parseDict();
    }
    private void parseDict()
    {
        m_parser.setFileName(m_dicts.peek());
        Thread t=new Thread(m_parser);
        t.start();
        System.out.println("Parsing started: "+m_dicts.peek());
    }
    class Hook implements Runnable
    {
        private final Indexer indexer;
        public Hook(Indexer i)
        {
            indexer=i;
        }

        @Override
        public void run() {
            indexer.closeIndex();
        }
    }

    public DictIndexer(String args[]) {
        if(args.length<3)
        {
            System.out.println("Usage: dict-indexer.jar -o INDEX_NAME DICT1[ DICT2 ...]");
            return;
        }
        for(int i=2;i<args.length;i++)
            m_dicts.push(args[i]);
        m_indexer=new Indexer(args[1]);
        m_parser=new Parser();
        m_parser.addListener(m_indexer);
        m_parser.addListener(this);
        Runtime.getRuntime().addShutdownHook(new Thread(new Hook(m_indexer)));
        parseDict();
    }
    
    public static void main(String[] args) {
        new DictIndexer(args);
    }
    
}
