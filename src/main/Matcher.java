package main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Matcher {
	private static final Matcher Instance = new Matcher();
	
	public static Matcher getInstance()
	{
		Instance.initRuleBase();
		return Instance;
	}
	
	private Matcher(){}
	private String tableName = "rules";
	private List<Rule> ruleBase;
	private List<String> entity;//用于辅助三元组合法性的判断
	
	/**
	 * 初始化规则库
	 */
	private void initRuleBase()
	{
		List<Rule> ruleBase = new ArrayList<Rule>();
		String sql = "select * from " + this.tableName;
		ResultSet rs = MySQLUtils.getResultSetFromPro(sql);
		try {
			while(rs.next())
			{
				ruleBase.add(new Rule(rs.getString(2), rs.getString(3)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ruleBase = ruleBase;
	}
	
	/**
	 * 判断此三元组查询语句是否合法，合法性包含以下三点：
	 * 1.该语句包含的实体和关系都存在于现有facts中
	 * 2.如果是查询关系时，该语句中两个实体不能相同
	 * 3.该查询语句必须包含两个有效值和一个"?x"，(?x)的位置代表了要查询的信息
	 * @param tripleQuery
	 * @return
	 */
	private boolean isLegalTripleQuery(Triple tripleQuery)
	{
		//后面补充
		return true;
	}
	
	/**
	 * 根据tripleQuery寻找可以使用的Rules
	 * @param triple
	 * @return
	 */
	private List<Rule> getVaildRules(Triple tripleQuery)
	{
		List<Rule> vaildRules = new ArrayList<Rule>();
		for(Rule rule : ruleBase)
		{
			if(tripleQuery.relation.equals(rule.rightHandSide.relation))
				vaildRules.add(rule);		
		}
		return vaildRules;
	}
	
	/**
	 * 根据可用规则在数据库中检索可用的Facts
	 * @throws SQLException 
	 */
	public void getFacts(Triple tripleQuery) throws SQLException
	{
		List<Rule> vaildRules = getVaildRules(tripleQuery);
		HashMap<String, String> tripleTag = new HashMap<String, String>();
		for(Rule rule : vaildRules)
		{
			List<String> eSet = null;
			Triple rhs = rule.rightHandSide;//规则结果
			tripleTag.put(rhs.entity_1, tripleQuery.entity_1);
			tripleTag.put(rhs.entity_2, tripleQuery.entity_2);
			for(Triple fristLeftHandSide: rule.leftHandSide)//规则条件
			{
				if((tripleTag.containsKey(fristLeftHandSide.entity_1) && !tripleTag.get(fristLeftHandSide.entity_1).equals("?x")) ||
						(tripleTag.containsKey(fristLeftHandSide.entity_2) && !tripleTag.get(fristLeftHandSide.entity_2).equals("?x")))
				{
					if(tripleTag.containsKey(fristLeftHandSide.entity_1))
					{
						Triple factSearchTriple = new Triple(tripleTag.get(fristLeftHandSide.entity_1), fristLeftHandSide.relation, "?x");
						eSet = Fact.getInstance().getFacts(factSearchTriple);
					}
					else
					{
						Triple factSearchTriple = new Triple("?x", fristLeftHandSide.relation, tripleTag.get(fristLeftHandSide.entity_2));
						eSet = Fact.getInstance().getFacts(factSearchTriple);
					}
					rule.leftHandSide.remove(fristLeftHandSide);
					break;
				}
			}
			Triple lastleftHandSide = rule.leftHandSide.get(0);
			List<String> eSet2 = null;
			if(eSet != null)
			{
				for(String e : eSet)
				{
					if(tripleTag.containsKey(lastleftHandSide.entity_1))
					{
						Triple factSearchTriple = new Triple(tripleTag.get(lastleftHandSide.entity_1), lastleftHandSide.relation, e);
						eSet2 = Fact.getInstance().getFacts(factSearchTriple);
					}
					else
					{
						Triple factSearchTriple = new Triple(e, lastleftHandSide.relation, tripleTag.get(lastleftHandSide.entity_2));
						eSet2 = Fact.getInstance().getFacts(factSearchTriple);
					}
					String firstEntity = tripleQuery.entity_1.equals("?x") ? tripleQuery.entity_2: tripleQuery.entity_1;
					for(String eTmp : eSet2)
					{
						if( (!eTmp.equals(e)) && (!eTmp.equals(firstEntity)) )
						{
							System.out.println(eTmp);
						}
					}
				}
			}
			tripleTag.clear();
		}
	}
	
	
	public static void main(String[] args) throws SQLException
	{
		Triple triple = new Triple("叶凡", "WorkAff", "?x");
		Matcher.getInstance().getFacts(triple);
	}
}
