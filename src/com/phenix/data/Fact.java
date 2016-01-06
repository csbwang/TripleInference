package com.phenix.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Fact {
	
	private static final Fact Instance = new Fact();
	public HashMap<String,String> relationViews = null;
	private Connection conn = null;
	
	//传入Connection，因为relationViews需要外部调用，在使用时必须先初始化
	public static Fact getInstance(Connection conn) throws SQLException
	{	
		if(Instance.conn == null)
			Instance.conn = conn;
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
		HashMap<String, String> relationViews = new HashMap<String, String>();
		String sql = "select * from inference_relations";
		Statement stmt = Instance.conn.createStatement();
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
	 * 将三元组转化为sql查询语句，采用实体id查询id和value
	 * @param tripleQuery
	 * @return
	 */
	private String getEntitySearchSql(FactTriple tripleQuery)
	{	
		if(!this.relationViews.containsKey(tripleQuery.relation))
			return null;
		if(tripleQuery.entity_1.id.equals("?x"))
			return  "select e1_id, e1_value from " + this.relationViews.get(tripleQuery.relation) + " where e2_id=" + "\"" + 
				tripleQuery.entity_2.id + "\"";
		if(tripleQuery.entity_2.id.equals("?x"))
			return  "select e2_id, e2_value from " + this.relationViews.get(tripleQuery.relation) + " where e1_id=" + "\"" + 
				tripleQuery.entity_1.id + "\"";
		else
			return null;
	}
	
	/**
	 * 通过tripleQuery直接从事实知识库中获取结果
	 * @param tripleQuery
	 * @return
	 * @throws SQLException
	 */
	public List<Entity> getFacts(FactTriple tripleQuery) throws SQLException
	{
		String sql = getEntitySearchSql(tripleQuery);
		if(sql == null)
			return null;
		List<Entity> facts = new ArrayList<Entity>();
		Statement stmt = Instance.conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			if(rs.getString(1)==null || rs.getString(1).equals("") || rs.getString(1).length()==0)
				continue;
			facts.add(new Entity(rs.getString(1), rs.getString(2)));
		}
		if(facts.size() > 0)
			return facts;
		return null;
	}
}
