package com.phenix.search;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.phenix.data.Entity;
import com.phenix.data.Fact;
import com.phenix.data.FactTriple;
import com.phenix.inference.Matcher;

public class TripleSearch {
	private static final TripleSearch Instance = new TripleSearch();
	
	public static TripleSearch getInstance()
	{
		return Instance;
	}
	
	private TripleSearch(){}
	
	/**
	 * 不通过推理，直接通过三元组从事实知识库中查找结果
	 * @param tripleQuery
	 * @return
	 * @throws SQLException
	 */
	public List<Entity> search(FactTriple tripleQuery, Connection conn) throws SQLException
	{
		return Fact.getInstance(conn).getFacts(tripleQuery);
	}
	
	/**
	 * 通过推理获取结果
	 * @param tripleQuery
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Entity, String> searchWithReasoner(FactTriple tripleQuery, Connection conn) throws SQLException
	{
		return Matcher.getInstance().getInferenceResult(tripleQuery, conn);
	}
	
	public HashMap<Entity, String> getAnswer(FactTriple tripleQuery, Connection conn) throws SQLException
	{
		HashMap<Entity, String> result = new HashMap<Entity, String>();
		List<Entity> searchResult = search(tripleQuery, conn);
		if(searchResult != null)
		{
			for(Entity sRes : searchResult)
			{
				result.put(sRes, "直接检索得到");
			}
		}
		HashMap<Entity, String> InferenceResult = searchWithReasoner(tripleQuery, conn);
		if(!InferenceResult.isEmpty())
		{
			for(Map.Entry<Entity, String> entry : InferenceResult.entrySet())
			{
				if(!result.containsKey(entry.getKey()))
				{
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}
}
