package com.phenix.inference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.phenix.data.Entity;
import com.phenix.data.Fact;
import com.phenix.data.Rule;
import com.phenix.data.FactTriple;
import com.phenix.data.RuleTriple;
import com.phenix.utils.MySQLUtils;

import edu.ecnu.ica.techsearch.iface.InferPeopleItem;

public class Matcher {
	private static final Matcher Instance = new Matcher();
	private List<Rule> ruleBase = null;
	
	public static Matcher getInstance()
	{
		return Instance;
	}
	
	private Matcher(){}
	
	/**
	 * 初始化规则库
	 */
	private void initRuleBase(Connection conn)
	{
		List<Rule> ruleBase = new ArrayList<Rule>();
		String sql = "select * from inference_rules";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				ruleBase.add(new Rule(rs.getString(2), rs.getString(3)));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		this.ruleBase = ruleBase;
	}
	
	/**
	 * 根据tripleQuery寻找可以使用的Rules
	 * @param triple
	 * @return
	 */
	private List<Rule> getVaildRules(FactTriple tripleQuery, Connection conn)
	{
		if(this.ruleBase == null)
			this.initRuleBase(conn);
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
	private String getHiddenEntity(RuleTriple rhs)
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
	 * @param ruleTripleTag
	 * @param rule
	 * @return
	 */
	private String getReason(HashMap<String, Entity> ruleTripleTag, Rule rule)
	{
		String reason = "(" + ruleTripleTag.get(rule.leftHandSide.get(0).entity_1).value + "," + rule.leftHandSide.get(0).relation + "," + ruleTripleTag.get(rule.leftHandSide.get(0).entity_2).value
				+ ") + (" + ruleTripleTag.get(rule.leftHandSide.get(1).entity_1).value + "," + rule.leftHandSide.get(1).relation + "," + ruleTripleTag.get(rule.leftHandSide.get(1).entity_2).value
				+ ") -> (" +ruleTripleTag.get(rule.rightHandSide.entity_1).value + "," + rule.rightHandSide.relation + "," + ruleTripleTag.get(rule.rightHandSide.entity_2).value + ")";
		return reason;
	}
	
	/**
	 * 根据可用规则进行推理
	 * @throws SQLException 
	 */
	public HashMap<Entity, String> getInferenceResult(FactTriple tripleQuery, Connection conn) throws SQLException
	{
		//获取可用规则
		List<Rule> vaildRules = getVaildRules(tripleQuery, conn);
		//获取推理结果
		HashMap<Entity, String> result = new HashMap<Entity, String>();
		
		
		//测试
		List<InferPeopleItem> techsearchResult = new ArrayList<InferPeopleItem>();
		InferPeopleItem ip = new InferPeopleItem();
		ip.peopleId = "a";
		ip.peopleName = "a_name";
		ip.explain = "aaaaa";
		techsearchResult.add(ip);
		
		
		HashMap<String, Entity> tripleTag = new HashMap<String, Entity>();
		//记录映射，方便写出推理依据
		HashMap<String, Entity> ruleTripleTag = new HashMap<String, Entity>();
		//记录推理获得的实体id列表，方便去重
		List<String> entityIdList = new ArrayList<String>();
		outer:
		for(Rule rule : vaildRules)
		{
			if(!tripleTag.isEmpty())
				tripleTag.clear();
			if(!ruleTripleTag.isEmpty())
				ruleTripleTag.clear();
			List<Entity> eSet = null;
			RuleTriple rhs = rule.rightHandSide;//规则结果
			tripleTag.put(rhs.entity_1, tripleQuery.entity_1);
			tripleTag.put(rhs.entity_2, tripleQuery.entity_2);
			RuleTriple condition1=null;
			for(RuleTriple condition: rule.leftHandSide)//规则条件
			{
				//此条件中的关系不存在，则放弃这条规则
				if(!Fact.getInstance(conn).relationViews.containsKey(condition.relation))
					continue outer;
				if((tripleTag.containsKey(condition.entity_1) && !tripleTag.get(condition.entity_1).id.equals("?x")) ||
						(tripleTag.containsKey(condition.entity_2) && !tripleTag.get(condition.entity_2).id.equals("?x")))
				{
					if(tripleTag.containsKey(condition.entity_1))
					{
						FactTriple factSearchTriple = new FactTriple(tripleTag.get(condition.entity_1), condition.relation, new Entity("?x", "?x"));
						eSet = Fact.getInstance(conn).getFacts(factSearchTriple);
					}
					else
					{
						FactTriple factSearchTriple = new FactTriple(new Entity("?x", "?x"), condition.relation, tripleTag.get(condition.entity_2));
						eSet = Fact.getInstance(conn).getFacts(factSearchTriple);
					}
					//如果这个condition是可查询的triple，则先使用这个条件，推出循环
					condition1 = condition;
					break;
				}
			}
			if(eSet == null)continue;
			RuleTriple condition2 = null;
			for(RuleTriple condition: rule.leftHandSide)
			{
				if(condition1!=condition)
					condition2 = condition;
			}
			//此条件中的关系不存在，则放弃这条规则
			if(!Fact.getInstance(conn).relationViews.containsKey(condition2.relation))
				continue;
			List<Entity> eSet2 = null;
			if(eSet != null)
			{
				for(Entity e : eSet)
				{
					if(tripleTag.containsKey(condition2.entity_1))
					{
						FactTriple factSearchTriple = new FactTriple(tripleTag.get(condition2.entity_1), condition2.relation, e);
						eSet2 = Fact.getInstance(conn).getFacts(factSearchTriple);
					}
					else
					{
						FactTriple factSearchTriple = new FactTriple(e, condition2.relation, tripleTag.get(condition2.entity_2));
						eSet2 = Fact.getInstance(conn).getFacts(factSearchTriple);
					}
					Entity firstEntity = tripleQuery.entity_1.id.equals("?x") ? tripleQuery.entity_2: tripleQuery.entity_1;
					for(Entity eTmp : eSet2)
					{
						if( (!eTmp.equals(e)) && (!eTmp.equals(firstEntity)) )
						{
							if(entityIdList.contains(eTmp.id))
								continue;
							if(tripleQuery.entity_1.id.equals("?x"))
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
							entityIdList.add(eTmp.id);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws SQLException
	{
		/*Connection conn = MySQLUtils.getInstance().getConnection();
		FactTriple triple = new FactTriple(new Entity("叶凡", "lalal"), "pWorkPro", new Entity("?x", "?x"));
		Matcher.getInstance().getInferenceResult(triple, conn);*/
	}
}
