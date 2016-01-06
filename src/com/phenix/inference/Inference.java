package com.phenix.inference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.phenix.data.Entity;
import com.phenix.data.FactTriple;
import com.phenix.utils.MySQLUtils;

import edu.ecnu.ica.techsearch.iface.InferPeopleItem;

public class Inference {
	public Connection conn = null;
	
	public Inference(Connection conn)
	{
		this.conn = conn;
	}
	
	/**
	 * 根据实体id和类型获取实体value
	 * @param id
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	private String getEntityName(String id, String type) throws SQLException
	{
		String fSql = null;
		switch(type)
		{
		case "peopleId":
			fSql = "select name from ett_people where people_id=\"";
			break;
		case "enterpriseId":
			fSql = "select name from ett_interprise where enterprise_id=\"";
			break;
		case "projectId":
			fSql = "select name from ett_project where project_id=\"";
			break;
		default:
			return null; // 没有找到对应类型的数据表，返回null
		}
		String sql = fSql + id + "\"";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			return rs.getString(1);
		}
		return null; //没有查到结果，返回null
	}
	
	/**
	 * 通过peopleId获取相关人物推理结果
	 * @param peopleId
	 * @return List<InferPeopleItem>
	 */
	public List<InferPeopleItem> peopleInfer(String peopleId)
	{
		List<InferPeopleItem> peopleTnferenceResult = new ArrayList<InferPeopleItem>();
		InferPeopleItem item = null;
		HashMap<Entity, String> inferenceResult = null;
		List<String> peopleIdList = new ArrayList<String>();
		try
		{
		String peopleName = getEntityName(peopleId, "peopleId");
		inferenceResult = Matcher.getInstance().
				getInferenceResult(new FactTriple(new Entity(peopleId, peopleName), "WorkAff", new Entity("?x", "?x")), conn);
		inferenceResult.putAll(Matcher.getInstance().
				getInferenceResult(new FactTriple(new Entity(peopleId, peopleName), "pWorkPro", new Entity("?x", "?x")), conn));
		}
		catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
		for(Map.Entry<Entity, String> entry : inferenceResult.entrySet())
		{
			if(peopleIdList.contains(entry.getKey().id))
				continue; // 人名结果去重
			item = new InferPeopleItem();
			item.peopleId = entry.getKey().id;
			item.peopleName = entry.getKey().value;
			item.explain = entry.getValue();
			peopleTnferenceResult.add(item);
			peopleIdList.add(item.peopleId);
		}
		if(peopleTnferenceResult.size()>0)
			return peopleTnferenceResult;
		return null;
	}
	
	public static void main(String[] args)
	{
//		Connection conn = MySQLUtils.getInstance().getConnection();
//		List<InferPeopleItem> res = new Inference(conn).peopleInfer("叶凡");
//		if(res==null)System.out.println("null");
//		else
//		{
//			for(InferPeopleItem item: res)
//			{
//				System.out.println(item.peopleId);
//				System.out.println(item.peopleName);
//				System.out.println(item.explain + "\n");
//			}	
//		}
	}
}
