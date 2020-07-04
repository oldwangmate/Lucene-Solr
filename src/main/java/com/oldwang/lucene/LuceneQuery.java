package com.oldwang.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * Lucene查询
 */
public class LuceneQuery {

    public IndexSearcher getIndexReader()  {
        IndexSearcher indexSearcher = null;
        try {
            Directory directory = FSDirectory.open(new File("E:\\Lucene\\temp\\index").toPath());
            IndexReader indexReader = DirectoryReader.open(directory);
            indexSearcher = new IndexSearcher(indexReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return indexSearcher;
    }

    //查询所有
    @Test
    public void queryAll() throws IOException {
        IndexSearcher indexSearcher = getIndexReader();
        Query query = new MatchAllDocsQuery();  //匹配所有
        printResult(query,indexSearcher);
        indexSearcher.getIndexReader().close();
    }

    //数值范围查询
    @Test
    public void queryRange() throws IOException {
        IndexSearcher indexSearcher = getIndexReader();
        Query query = LongPoint.newRangeQuery("fileSize", 47L, 83L); //数值区间查询
        printResult(query,indexSearcher);
        indexSearcher.getIndexReader().close();
    }

    //组合查询
    @Test
    public void testbooleanQuery() throws IOException {
        IndexSearcher indexSearcher = getIndexReader();
        BooleanQuery.Builder build = new BooleanQuery.Builder();
        Query query1 = new TermQuery(new Term("fileName","apache"));
        Query query2 = new TermQuery(new Term("fileName","lucene"));
        build.add(query1,BooleanClause.Occur.MUST);
        build.add(query2,BooleanClause.Occur.MUST);
        BooleanQuery query = build.build();
        printResult(query,indexSearcher);
        indexSearcher.getIndexReader().close();
    }

    //解析查询 条件解析对象查询
    @Test
    public void testQueryParser() throws IOException, ParseException {
        IndexSearcher indexSearcher = getIndexReader();
        //参数1 默认查询的域
        //参数2 采用的分析器
        QueryParser queryParser = new QueryParser("fileName",new IKAnalyzer());
        //查询所有 第一个*是域 第二个*表示值
        Query query = queryParser.parse("fileContent:apache");
        printResult(query,indexSearcher);
        indexSearcher.getIndexReader().close();
    }

    //解析查询 条件解析对象查询多个默认域（了解）
    @Test
    public void testMultiFieldQueryParser() throws IOException, ParseException {
        IndexSearcher indexSearcher = getIndexReader();
        String[] fields = {"fileName,fileContent"};
        //参数1 默认查询的域 设置多个
        //参数2 采用的分析器
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields,new IKAnalyzer());
        //查询所有 第一个*是域 第二个*表示值
        Query query = queryParser.parse("lucene is apache");
        printResult(query,indexSearcher);
        indexSearcher.getIndexReader().close();
    }


    public void printResult(Query query,IndexSearcher indexSearcher) throws IOException {
        TopDocs topDocs = indexSearcher.search(query, 10);
        //返回查询结果，遍历输出
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);

            //文件名称
            String fileName = document.get("fileName");
            System.out.println(fileName);
            //文件内容
            String fileContent = document.get("fileContent");
            System.out.println(fileContent);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println(filePath);
            //文件大小
            String fileSize = document.get("fileSize");
            System.out.println(fileSize);
            System.out.println("-------------------");
        }
    }
}
