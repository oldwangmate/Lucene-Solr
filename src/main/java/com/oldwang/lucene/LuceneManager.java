package com.oldwang.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * 索引维护
 */
public class LuceneManager {

    public IndexWriter getIndex() throws IOException {
        Directory directory = FSDirectory.open(new File("E:\\Lucene\\temp\\index").toPath());
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        return indexWriter;
    }

    //全删除
    @Test
    public void deleteIndexAll() throws IOException {
        IndexWriter index = getIndex();
        index.deleteAll();
        index.close();
    }

    //条件删除
    @Test
    public void deleteIndex() throws IOException {
        IndexWriter index = getIndex();
        Query query = new TermQuery(new Term("fileName","apache"));
        index.deleteDocuments(query);
        index.close();
    }

    //修改
    @Test
    public void updateIndex() throws IOException {
        IndexWriter index = getIndex();
        Document document = new Document();
        document.add(new TextField("fileN","测试文件名",Field.Store.YES));
        document.add(new TextField("fileC","测试文件名",Field.Store.YES));
        index.updateDocument(new Term("fileName","lucene"),document);
        index.close();
    }
}
