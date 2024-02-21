package com.company.log.search.utility.repository;

import com.company.log.search.utility.model.LuceneConstants;
import com.company.log.search.utility.service.customAnalyzer;
import com.company.log.search.utility.service.filterFile;
import jakarta.annotation.PreDestroy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LuceneIndexImpl{
    private IndexWriter writer;
    private IndexWriterConfig indexWriterConfig;
    private Directory indexDirectory;
    private Analyzer analyzer;

    public LuceneIndexImpl(@Value("${lucene.index.location}") String indexPath){
        try {
            Path path = Paths.get("Indexes");
            this.indexDirectory = FSDirectory.open(path);
            this.analyzer = new customAnalyzer();
            this.indexWriterConfig = new IndexWriterConfig(analyzer);
            this.writer = new IndexWriter(indexDirectory,indexWriterConfig);
            createIndex(indexPath);
            writer.commit();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @PreDestroy
    public void onExit() throws IOException {
       writer.close();
    }

    public Document getDocument(File f) throws IOException {
        Document document = new Document();
        document.add(new TextField(LuceneConstants.CONTENTS,new FileReader(f)));
        document.add(new StringField(LuceneConstants.FILE_NAME,f.getName(), Field.Store.YES));
        document.add(new StringField(LuceneConstants.FILE_PATH,f.getCanonicalPath(),Field.Store.YES));
        return document;
    }

    public void createIndex(String indexPath){
        File directoryPath = new File(indexPath);
        FileFilter fileFilter = new filterFile();
        File [] listFiles = directoryPath.listFiles(fileFilter);
        if(listFiles!=null) {
            for (File file : listFiles) {
                try {
                    Document document = getDocument(file);
                    System.out.println("Indexing file - "+file.getName());
                    writer.addDocument(document);
                }catch (IOException e){
                    System.out.println(e.getMessage());
                }
            }

        }
    }

    public List<String> search(String searchQuery) throws IOException, ParseException {
        Query query = new QueryParser("contents",this.analyzer).parse(searchQuery);
        IndexReader indexReader = DirectoryReader.open(this.indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query,LuceneConstants.MAX_SEARCH);
        List<String> documentNames = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
             documentNames.add(indexSearcher.doc(scoreDoc.doc).get(LuceneConstants.FILE_NAME));
        }

        return documentNames;
    }

    public IndexWriter getWriter() {
        return writer;
    }

    public void setWriter(IndexWriter writer) {
        this.writer = writer;
    }

    public IndexWriterConfig getIndexWriterConfig() {
        return indexWriterConfig;
    }

    public void setIndexWriterConfig(IndexWriterConfig indexWriterConfig) {
        this.indexWriterConfig = indexWriterConfig;
    }

    public Directory getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(Directory indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }
}
