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
				this.tableName = "techsearch.enterprise_project_affleadpro_name";
				break;
			case "pWorkAff":
				this.tableName = "techsearch.people_enterprise_pworkaff_name";
				break;
			case "pLeadPro":
				this.tableName = "techsearch.people_project_pleadpro_name";
				break;
			default:
				return null;
		}
		if(tripleQuery.entity_1.equals("?x"))
			return  "select e1 from " + tableName + " where e2=" + "\"" + 
				tripleQuery.entity_2 + "\"";
		if(tripleQuery.entity_2.equals("?x"))
			return  "select e2 from " + tableName + " where e1=" + "\"" + 
				tripleQuery.entity_1 + "\"";
		else
			return null;
	}
	public List<String> getFacts(Triple tripleQuery) throws SQLException
	{
		List<String> facts = new ArrayList<String>();
		String sql = getEntitySearchSql(tripleQuery);
		System.out.println(sql);
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
