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
		String relation = tripleQuery.relation;
		switch (relation)
		{
			case "affLeadPro":
				this.tableName = "enterprise_project_affleadpro";
				break;
			case "pWorkAff":
				this.tableName = "people_enterprise_pworkaff";
				break;
			case "pLeadPro":
				this.tableName = "people_project_pleadpro";
				break;
			default:
				return null;
		}
		if(tripleQuery.entity_1.equals("?x"))
			return  "select e1_id from " + tableName + " where e2_id=" + "\"" + 
				tripleQuery.entity_2 + "\"";
		if(tripleQuery.entity_2.equals("?x"))
			return  "select e2_id from " + tableName + " where e1_id=" + "\"" + 
				tripleQuery.entity_1 + "\"";
		else
			return null;
	}
	public List<String> getFacts(Triple tripleQuery) throws SQLException
	{
		List<String> facts = new ArrayList<String>();
		String sql = getEntitySearchSql(tripleQuery);
//		System.out.println(sql);
		if(sql != null)
		{
			ResultSet rs = MySQLUtils.getResultSetFromPro(sql);
			while(rs.next())
			{
				if(rs.getString(1).equals("") || rs.getString(1)==null || rs.getString(1).length()==0)
					continue;
				facts.add(rs.getString(1));
			}
		}
		if(facts.size() > 0)
			return facts;
		return null;
	}
}
