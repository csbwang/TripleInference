package main;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TripleSearch {
	private static final TripleSearch Instance = new TripleSearch();
	
	public static TripleSearch getInstance()
	{
		return Instance;
	}
	
	private TripleSearch(){}
	
	public List<String> search(Triple tripleQuery) throws SQLException
	{
		return Fact.getInstance().getFacts(tripleQuery);
	}
	
	public void searchWithReasoner(Triple tripleQuery) throws SQLException
	{
		Matcher.getInstance().getFacts(tripleQuery);
	}
	
	public void getAnswer(Triple tripleQuery) throws SQLException
	{
		List<String> searchResult = search(tripleQuery);
		System.out.println("直接从事实数据中查询到的结果如下：");
		if(searchResult == null)
		{
			System.out.println("没有结果");
			return;
		}
		for(String res : searchResult)
		{
				System.out.println(res);
		}
		System.out.println("根据已有事实通过关系推理得到的结果如下：");
		searchWithReasoner(tripleQuery);
	}
	
	public static void main(String[] args) throws SQLException
	{
		Scanner in = new Scanner(System.in);
		String query = null;
		for(int i = 0;i<10;i++){
			query = in.nextLine();
			String[] tmpArray = query.split(" ");
			Triple tripleQuery = new Triple(tmpArray[0], tmpArray[1], tmpArray[2]);
			TripleSearch.getInstance().getAnswer(tripleQuery);
		}
	}
}
