package com.oldwang.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oldwang.domain.ProductModel;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class JdDaoImpl implements JdDao {

	// 索引库
	@Autowired
	private SolrServer solrServer;

	//// 通过上面四个条件查询对象商品结果集
	public List<ProductModel> selectProductModelListByQuery(String queryString, String catalog_name,
															String price, String sort) throws Exception {

		// 查询 关键词  过滤条件
		// 价格排序 分页 开始行 每页数 高亮 默认域 只查询指定域
		SolrQuery solrQuery = new SolrQuery();
		// 关键词
		solrQuery.setQuery(queryString);
		// 过滤条件 
		if(null != catalog_name && !"".equals(catalog_name)){
			solrQuery.set("fq", "product_catalog_name:" + catalog_name);
		}
		if(null != price && !"".equals(price)){
			//0-9   50-*
			String[] p = price.split("-");
			solrQuery.set("fq", "product_price:[" + p[0] + " TO " + p[1] + "]");
		}
		// 价格排序
		if("1".equals(sort)){
			solrQuery.addSort("product_price", ORDER.desc);
		}else{
			solrQuery.addSort("product_price", ORDER.asc);
		}
		// 分页
		solrQuery.setStart(0);
		solrQuery.setRows(16);
		// 默认域
		solrQuery.set("df", "product_keywords");
		// 只查询指定域
		solrQuery.set("fl", "id,product_name,product_price,product_picture");
		// 高亮
		// 打开开关
		solrQuery.setHighlight(true);
		// 指定高亮域
		solrQuery.addHighlightField("product_name");
		// 前缀
		solrQuery.setHighlightSimplePre("<span style='color:red'>");
		solrQuery.setHighlightSimplePost("</span>");

		// 后缀
		// 执行查询
		QueryResponse response = solrServer.query(solrQuery);
		// 文档结果集
		SolrDocumentList docs = response.getResults();

		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		// Map K id V Map
		// Map K 域名 V List
		// List list.get(0)
		// 总条数
		long numFound = docs.getNumFound();
		
		
		List<ProductModel> productModels = new ArrayList<ProductModel>();
	
		for (SolrDocument doc : docs) {
			ProductModel productModel = new ProductModel();
			
			productModel.setPid((String) doc.get("id"));
			productModel.setPrice((Float) doc.get("product_price"));
			productModel.setPicture((String) doc.get("product_picture"));
			Map<String, List<String>> map = highlighting.get(doc.get("id"));
			List<String> list = map.get("product_name");
			if(list == null){
				continue;
			}
			productModel.setName(list.get(0));
			productModels.add(productModel);
		}
		return productModels;

	}
}
