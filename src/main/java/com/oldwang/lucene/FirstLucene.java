package com.oldwang.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hunspell.Dictionary;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

import static org.apache.lucene.document.Field.*;


/**
 * Lucene入门
 * 创建索引
 * 查询索引
 */
public class FirstLucene {

    /**
     *  创建索引对象
     * @throws IOException IOException
     */
    @Test
    public void createLuceneIndex() throws IOException {
        Directory directory = FSDirectory.open(new File("E:\\Lucene\\temp\\index").toPath());
        //内存索引库 Directory directory1 = new RAMDirectory();
        //Analyzer analyzer = new StandardAnalyzer(); //标准分词器
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        File file = new File("E:\\Lucene\\searchsource");
        for(File files : file.listFiles()){
            //文档名称
            String name = files.getName();
            //文档路径
            String path = files.getPath();
            //文档大小
            long size = FileUtils.sizeOf(files);
            //文档内容
            String content = FileUtils.readFileToString(files);

            //创建文档对象
            Document document = new Document();

            // 创建域对象
            Field nameFile = new TextField("fileName",name,Store.YES);
            Field pathFile = new TextField("filePath",path,Store.YES);
            Field sizeFile = new TextField("fileSize",size+"",Store.YES);
            Field contentFile = new TextField("fileContent",content,Store.YES);

            //添加域对象·
            document.add(nameFile);
            document.add(pathFile);
            document.add(sizeFile);
            document.add(contentFile);

            //创建索引，并写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexWriter
        indexWriter.close();
    }

    /**
     *  查询Lucene
     * @throws IOException
     */
    @Test
    public void searchLucene() throws IOException {
        //创建Directory对象 指定索引库的位置
       Directory directory = FSDirectory.open(new File("E:\\Lucene\\temp\\index\\").toPath());

       //创建IndexReader对象  需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);

        //创建IndexSearcher对象 需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //创建TermQuery对象 指定查询的域和查询的关键词
        Term term = new Term("fileName","lucene");
        Query query = new TermQuery(term);

        //执行查询
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
//            System.out.println(fileContent);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println(filePath);
            //文件大小
            String fileSize = document.get("filePath");
            System.out.println(fileSize);
            System.out.println("-------------------");
        }
        indexReader.close();
    }

    //查看标准分析器的分词效果
    @Test
    public void testTokenStream() throws Exception {
        //创建一个标准分析器对象
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //获得tokenStream对象
        //第一个参数：域名，可以随便给一个
        //第二个参数：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("test", "高富帅 The Spring Framework provides a comprehensive programming and configuration model.");
        //添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while(tokenStream.incrementToken()) {
            //关键词的起始位置
            System.out.println("start->" + offsetAttribute.startOffset());
            //取关键词
            System.out.println(charTermAttribute);
            //结束位置
            System.out.println("end->" + offsetAttribute.endOffset());
        }
        tokenStream.close();
    }
}
