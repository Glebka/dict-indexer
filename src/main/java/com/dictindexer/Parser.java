package com.dictindexer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

interface IParserEventListener
{
    public void entryFound(String word,String lemma,String code);
    public void parsingFinished(boolean hasError);
}
interface IParserEventListenerSupport<T>
{
    public void addListener(T listener);
    public void removeListener(T listener);
}
class ParserEventListenerSupport implements IParserEventListener, IParserEventListenerSupport<IParserEventListener>
{
    private LinkedList<IParserEventListener> m_listeners=new LinkedList<IParserEventListener>();
    
    @Override
    public void addListener(IParserEventListener listener) {
        m_listeners.add(listener);
    }

    @Override
    public void removeListener(IParserEventListener listener) {
        m_listeners.remove(listener);
    }
    
    @Override
    public void entryFound(String word, String lemma, String code) {
        for (IParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.entryFound(word,lemma,code);
        }
    }

    @Override
    public void parsingFinished(boolean hasError) {
        for (IParserEventListener iParserEventListener : m_listeners) {
            iParserEventListener.parsingFinished(hasError);
        }
    }
}

public class Parser implements IParserEventListenerSupport<IParserEventListener>,Runnable{

    private Path m_file_path;
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
    private ParserEventListenerSupport m_listeners=new ParserEventListenerSupport();
    private String m_last_error;
    
    public String lastError()
    {
        return m_last_error;
    }
    private void parse_line(String line)
    {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter(" ");
        if (scanner.hasNext())
        {
            String word = scanner.next();
            String lemma = scanner.next();
            String code = scanner.next();
            m_listeners.entryFound(word, lemma, code);
        }
    }
    public void setFileName(String fileName)
    {
       m_file_path=Paths.get(fileName);
    }

    @Override
    public void addListener(IParserEventListener listener) {
        m_listeners.addListener(listener);
    }

    @Override
    public void removeListener(IParserEventListener listener) {
        m_listeners.removeListener(listener);
    }

    @Override
    public void run() {
        try {
            Scanner scanner =  new Scanner(m_file_path, ENCODING.name());
            while (scanner.hasNextLine())
                parse_line(scanner.nextLine());
            m_listeners.parsingFinished(false);
        } catch (IOException ex) {
            m_last_error=ex.getMessage();
            m_listeners.parsingFinished(true);
        }
    }
}
