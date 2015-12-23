package com.phenix.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.phenix.utils.MySQLUtils;

public class Fact {
	
	private static final Fact Instance = new Fact();
	
	public static Fact getInstance()
	{
		return Instance;
	}
	private Fact()
	{}
	private String tableName = "facts";
	
	private String getEntitySearchSql(Triple tripleQuery)
	{
		if(tripleQuery.entity_1.equals("?x"))
			return  "select e1 from " + tableName + " where e2=" + "\"" + 
				tripleQuery.entity_2 + "\" and " + "relation=" + "\"" + tripleQuery.relation + "\"";
		if(tripleQuery.relation.equals("?x"))
			return  "select relation from " + tableName + " where e1=" + "\"" + 
			tripleQuery.entity_1 + "\" and " + "e2=" + "\"" + tripleQuery.entity_2 + "\"";
		if(tripleQuery.entity_2.equals("?x"))
			return  "select e2 from " + tableName + " where e1=" + "\"" + 
				tripleQuery.entity_1 + "\" and " + "relation=" + "\"" + tripleQuery.relation + "\"";
		else
			return null;
	}
	public List<String> getFacts(Triple tripleQuery) throws SQLException
	{
		List<String> facts = new ArrayList<String>();
		String sql = getEntitySearchSql(tripleQuery);
		if(sql != null)
		{
			ResultSet rs = MySQLUtils.getResultSetFromPro(sql);
			while(rs.next())
			{
				facts.add(rs.getString(1));
			}
		}
		if(facts.size() > 0)
			return facts;
		return null;
	}
}
