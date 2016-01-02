package com.phenix.search;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.phenix.data.Fact;
import com.phenix.data.Triple;
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
	public List<String> search(Triple tripleQuery) throws SQLException
	{
		return Fact.getInstance().getFacts(tripleQuery);
	}
	
	/**
	 * 通过推理获取结果
	 * @param tripleQuery
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String, String> searchWithReasoner(Triple tripleQuery) throws SQLException
	{
		return Matcher.getInstance().getFacts(tripleQuery);
	}
	
	public HashMap<String, String> getAnswer(Triple tripleQuery) throws SQLException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		List<String> searchResult = search(tripleQuery);
		if(searchResult != null)
		{
			for(String sRes : searchResult)
			{
				result.put(sRes, "直接检索得到");
			}
		}
		HashMap<String, String> InferenceResult = searchWithReasoner(tripleQuery);
		if(!InferenceResult.isEmpty())
		{
			for(Map.Entry<String, String> entry : InferenceResult.entrySet())
			{
				if(!result.containsKey(entry.getKey()))
				{
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws SQLException
	{
		Scanner in = new Scanner(System.in);
		String query = null;
		for(int i = 0;i<10;i++){
			query = in.nextLine();
			String[] tmpArray = query.split(" ");
			Triple tripleQuery = new Triple(tmpArray[0], tmpArray[1], tmpArray[2]);
			HashMap<String, String> result = TripleSearch.getInstance().getAnswer(tripleQuery);
			for(Map.Entry<String, String> entry : result.entrySet())
			{
				System.out.println(entry.getKey() + "：" + entry.getValue());
			}
			
		}
	}
}
