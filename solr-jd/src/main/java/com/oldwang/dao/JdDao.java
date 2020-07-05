package com.oldwang.dao;

import java.util.List;

import com.oldwang.domain.ProductModel;

public interface JdDao {
	
	
	//// 通过上面四个条件查询对象商品结果集
	public List<ProductModel> selectProductModelListByQuery(String queryString, String catalog_name,
															String price, String sort) throws Exception ;

}
