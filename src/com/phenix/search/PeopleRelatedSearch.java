package com.phenix.search;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.phenix.data.Triple;

public class PeopleRelatedSearch {
private static final PeopleRelatedSearch Instance = new PeopleRelatedSearch();

public static PeopleRelatedSearch getInstance()
{
	return Instance;
}
private PeopleRelatedSearch(){}
	
public HashMap<String, String> getRelatedPeople(String peopleId) throws SQLException
{
	Triple tripleQuery = new Triple(peopleId, "WorkAff", "?x");
	return TripleSearch.getInstance().getAnswer(tripleQuery);
}

public HashMap<String, String> getRelatedProject(String peopleId) throws SQLException
{
	Triple tripleQuery = new Triple(peopleId, "pLeadPro", "?x");
	return TripleSearch.getInstance().getAnswer(tripleQuery);
}

public HashMap<String, String> getRelatedEnterprise(String peopleId) throws SQLException
{
	Triple tripleQuery = new Triple(peopleId, "pWorkAff", "?x");
	return TripleSearch.getInstance().getAnswer(tripleQuery);
}

public void  getPeopleRelatedInfo(String peopleId) throws SQLException
{
	HashMap<String, String> relatedPeople = this.getRelatedPeople(peopleId); 
	System.out.println("相关人物如下：");
	for(Map.Entry<String, String> entry : relatedPeople.entrySet())
	{
		System.out.println(entry.getKey() + "：" + entry.getValue());
	}
	
	HashMap<String, String> relatedProject = this.getRelatedProject(peopleId); 
	System.out.println("相关项目如下：");
	for(Map.Entry<String, String> entry : relatedProject.entrySet())
	{
		System.out.println(entry.getKey() + "：" + entry.getValue());
	}
	
	HashMap<String, String> relatedEnterprise = this.getRelatedEnterprise(peopleId); 
	System.out.println("相关项目如下：");
	for(Map.Entry<String, String> entry : relatedEnterprise.entrySet())
	{
		System.out.println(entry.getKey() + "：" + entry.getValue());
	}
}

}
