package com.phenix.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.phenix.search.Search;

public class Fact {
	
	private static final Fact Instance = new Fact();
	public HashMap<String,String> relationViews = null;
	
	public static Fact getInstance() throws SQLException
	{	
		if(Instance.relationViews == null)
			Instance.getRelationViews();
		return Instance;
	}
	private Fact()
	{}
	
	/**
	 * 获取知识库中关系视图列表
	 * @throws SQLException
	 */
	private void getRelationViews() throws SQLException
	{
		Connection conn = Search.conn;
		HashMap<String, String> relationViews = new HashMap<String, String>();
		String sql = "select * from inference_relations";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		try {
			while(rs.next())
			{
				relationViews.put(rs.getString(2), rs.getString(3));
			}
		} catch (SQLException e) {
			System.out.println(e);
			throw e;
		}
		if(relationViews.isEmpty())
			System.out.println("事实关系类型表加载失败，请检查");
		this.relationViews = relationViews;
	}
	/**
	 * 将三元组转化为sql查询语句
	 * @param tripleQuery
	 * @return
	 */
	private String getEntitySearchSql(Triple tripleQuery)
	{	
		if(!this.relationViews.containsKey(tripleQuery.relation))
			return null;
		if(tripleQuery.entity_1.equals("?x"))
			return  "select e1_value from " + this.relationViews.get(tripleQuery.relation) + " where e2_value=" + "\"" + 
				tripleQuery.entity_2 + "\"";
		if(tripleQuery.entity_2.equals("?x"))
			return  "select e2_value from " + this.relationViews.get(tripleQuery.relation) + " where e1_value=" + "\"" + 
				tripleQuery.entity_1 + "\"";
		else
			return null;
	}
	
	/**
	 * 通过tripleQuery直接从事实知识库中获取结果
	 * @param tripleQuery
	 * @return
	 * @throws SQLException
	 */
	public List<String> getFacts(Triple tripleQuery) throws SQLException
	{
		String sql = getEntitySearchSql(tripleQuery);
		if(sql == null)
			return null;
		List<String> facts = new ArrayList<String>();
//		System.out.println(sql);
		Connection conn = Search.conn;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			if(rs.getString(1)==null || rs.getString(1).equals("") || rs.getString(1).length()==0)
				continue;
			facts.add(rs.getString(1));
		}
		if(facts.size() > 0)
			return facts;
		return null;
	}
}
