package com.oldwang.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SolrManager {

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
    public void addDocument() throws Exception {
        String baseUrl = "http://localhost:8080/solr";
        //单机版
        SolrServer server = new HttpSolrServer(baseUrl);
        //添加
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id","haha");
        document.setField("name","oldwang");
        server.add(document);
        server.commit();
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteDocumet() throws IOException, SolrServerException {
        String baseUrl = "http://localhost:8080/solr";
        SolrServer server = new HttpSolrServer(baseUrl);
        //删除全部
        server.deleteByQuery("*:*",1000);
    }

    //跟新文档 更新与添加一样
    @Test
    public void updateDocumet() throws IOException, SolrServerException {
        String baseUrl = "http://localhost:8080/solr";
        SolrServer server = new HttpSolrServer(baseUrl);
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id","oldwang");
        document.setField("name","oldwang");
        server.add(document);
        server.commit();
    }

    //查询
    @Test
    public void QueryDocumet() throws IOException, SolrServerException {
        String baseUrl = "http://localhost:8080/solr";
        SolrServer server = new HttpSolrServer(baseUrl);
        // 查询关键字输入台灯，过滤条件幽默杂货 价格排序 分页 高亮
       SolrQuery solrQuery = new SolrQuery();
       //关键词
        //solrQuery.set("q","product_name:台灯");
        solrQuery.setQuery("product_name:台灯");
        //过滤条件
        solrQuery.set("fq","product_catalog_name:幽默杂货");
        //价格区间
        solrQuery.set("fq","product_price:[* TO 10]");
        //排序
        solrQuery.addSort("product_price",SolrQuery.ORDER.desc);
        //分页
        solrQuery.setStart(0);
        solrQuery.setRows(5);
        //设置默认域
        solrQuery.set("df","product_name");
        //指定查询
        solrQuery.set("fl","id,product_name");
        //设置高亮
        //打开开关
        solrQuery.setHighlight(true);
        //指定高亮的域
        solrQuery.addHighlightField("product_name");
        //前缀
        solrQuery.setHighlightSimplePre("<span style=color:red>");
        //后缀
        solrQuery.setHighlightSimplePost("</span>");
       //执行查询
        QueryResponse query = server.query(solrQuery);
        //文档结果集
        SolrDocumentList docs = query.getResults();
        //获取高亮结果
        //map k id value map
        //map k 域名 value list
        //list list.get(0)
        Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();

        //总条数
        long numFound = docs.getNumFound();
        System.out.println(numFound);
        //遍历文档
        for(SolrDocument doc :docs){
            System.out.println(doc.get("product_catalog_name"));
            System.out.println(doc.get("product_catalog"));
            System.out.println(doc.get("product_price"));
            System.out.println(doc.get("product_name"));
            System.out.println(doc.get("id"));

            //打印高亮信息
            Map<String, List<String>> map = highlighting.get(doc.get("id"));
            List<String> list = map.get("product_name");
            System.out.println(list.get(0));
        }
    }

}
