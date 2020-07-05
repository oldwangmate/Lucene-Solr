package com.oldwang.services;

import java.util.List;

import com.oldwang.dao.JdDao;
import com.oldwang.domain.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JdServiceImpl implements JdService {

	@Autowired
	private JdDao jdDao;
	//// 通过上面四个条件查询对象商品结果集
	public List<ProductModel> selectProductModelListByQuery(String queryString, String catalog_name,
															String price, String sort) throws Exception {
		return jdDao.selectProductModelListByQuery(queryString, catalog_name, price, sort);
		
	}
}
