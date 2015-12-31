package com.phenix.inference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.phenix.data.Fact;
import com.phenix.data.Rule;
import com.phenix.data.Triple;
import com.phenix.utils.MySQLUtils;

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
	 * 返回模式规则中，用来推理的隐含实体
	 * @param rhs
	 * @return
	 */
	private String getHiddenEntity(Triple rhs)
	{
		HashMap<String, String> tripleTag = new HashMap<String, String>();
		tripleTag.put(rhs.entity_1, "e");
		tripleTag.put(rhs.entity_2, "e");
		if(!tripleTag.containsKey("e1"))return "e1";
		if(!tripleTag.containsKey("e2"))return "e2";
		if(!tripleTag.containsKey("e3"))return "e3";
		return null;
	}
	
	/**
	 * 返回推理结果的解释
	 * @param tripleTag
	 * @param rule
	 * @return
	 */
	private String getReason(HashMap<String, String> tripleTag, Rule rule)
	{
		String reason = "(" + tripleTag.get(rule.leftHandSide.get(0).entity_1) + "," + rule.leftHandSide.get(0).relation + "," + tripleTag.get(rule.leftHandSide.get(0).entity_2)
				+ ") + (" + tripleTag.get(rule.leftHandSide.get(1).entity_1) + "," + rule.leftHandSide.get(1).relation + "," + tripleTag.get(rule.leftHandSide.get(1).entity_2)
				+ ") -> (" +tripleTag.get(rule.rightHandSide.entity_1) + "," + rule.rightHandSide.relation + "," + tripleTag.get(rule.rightHandSide.entity_2) + ")";
		return reason;
	}
	
	/**
	 * 根据可用规则在数据库中检索可用的Facts
	 * @throws SQLException 
	 */
	public HashMap<String, String> getFacts(Triple tripleQuery) throws SQLException
	{
		List<Rule> vaildRules = getVaildRules(tripleQuery);
		HashMap<String, String> result = new HashMap<String, String>();
		HashMap<String, String> tripleTag = new HashMap<String, String>();
		HashMap<String, String> ruleTripleTag = new HashMap<String, String>();
		for(Rule rule : vaildRules)
		{
			List<String> eSet = null;
			Triple rhs = rule.rightHandSide;//规则结果
			tripleTag.put(rhs.entity_1, tripleQuery.entity_1);
			tripleTag.put(rhs.entity_2, tripleQuery.entity_2);
			Triple condition1=null;
			for(Triple condition: rule.leftHandSide)//规则条件
			{
				if((tripleTag.containsKey(condition.entity_1) && !tripleTag.get(condition.entity_1).equals("?x")) ||
						(tripleTag.containsKey(condition.entity_2) && !tripleTag.get(condition.entity_2).equals("?x")))
				{
					if(tripleTag.containsKey(condition.entity_1))
					{
						Triple factSearchTriple = new Triple(tripleTag.get(condition.entity_1), condition.relation, "?x");
						eSet = Fact.getInstance().getFacts(factSearchTriple);
					}
					else
					{
						Triple factSearchTriple = new Triple("?x", condition.relation, tripleTag.get(condition.entity_2));
						eSet = Fact.getInstance().getFacts(factSearchTriple);
					}
					condition1 = condition;
					break;
				}
			}
			if(eSet == null)continue;
			Triple condition2 = null;
			for(Triple condition: rule.leftHandSide)
			{
				if(condition1!=condition)
					condition2 = condition;
			}
			List<String> eSet2 = null;
			if(eSet != null)
			{
				for(String e : eSet)
				{
					if(tripleTag.containsKey(condition2.entity_1))
					{
						Triple factSearchTriple = new Triple(tripleTag.get(condition2.entity_1), condition2.relation, e);
						eSet2 = Fact.getInstance().getFacts(factSearchTriple);
					}
					else
					{
						Triple factSearchTriple = new Triple(e, condition2.relation, tripleTag.get(condition2.entity_2));
						eSet2 = Fact.getInstance().getFacts(factSearchTriple);
					}
					String firstEntity = tripleQuery.entity_1.equals("?x") ? tripleQuery.entity_2: tripleQuery.entity_1;
					for(String eTmp : eSet2)
					{
						if( (!eTmp.equals(e)) && (!eTmp.equals(firstEntity)) )
						{
							if(tripleQuery.entity_1.equals("?x"))
							{
								ruleTripleTag.put(rhs.entity_1, eTmp);
								ruleTripleTag.put(rhs.entity_2, tripleQuery.entity_2);
								
							}
							else
							{
								ruleTripleTag.put(rhs.entity_2, eTmp);
								ruleTripleTag.put(rhs.entity_1, tripleQuery.entity_1);
							}
							ruleTripleTag.put(this.getHiddenEntity(rhs), e);
//							System.out.println(eTmp);
//							System.out.println(this.getReason(ruleTripleTag, rule));
							result.put(eTmp, this.getReason(ruleTripleTag, rule));
						}
					}
				}
			}
			tripleTag.clear();
			ruleTripleTag.clear();
		}
		return result;
	}
	
	public static void main(String[] args) throws SQLException
	{
		Triple triple = new Triple("王博", "pWorkPro", "?x");
		Matcher.getInstance().getFacts(triple);
	}
}
