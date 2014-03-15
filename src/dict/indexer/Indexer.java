package dict.indexer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer implements IParserEventListener{
    private IndexWriter m_index_writer;
    
    public Indexer(String indexPath) {
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);
            iwc.setOpenMode(OpenMode.CREATE);
            iwc.setRAMBufferSizeMB(256.0);
            m_index_writer = new IndexWriter(dir, iwc);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void entryFound(String word, String lemma, String code) {
        Document doc=new Document();
        doc.add(new Field("word", word, Field.Store.YES,Field.Index.NOT_ANALYZED));
        doc.add(new Field("lemma", lemma, Field.Store.YES,Field.Index.NO));
        doc.add(new Field("code", code, Field.Store.YES,Field.Index.NOT_ANALYZED));
        try {
            m_index_writer.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void closeIndex()
    {
        try {
            m_index_writer.forceMerge(1);
            m_index_writer.close(true);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void parsingFinished(boolean hasError) {
        
    }
    
}
