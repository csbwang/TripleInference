package com.phenix.search;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import com.phenix.utils.MySQLUtils;

public class Search {
	public static Connection conn = null;
	
	public Search(Connection conn)
	{
		Search.conn = conn;
	}
	
	public void getRelatedInfo(String entityId, int entityType) throws SQLException
	{
		switch(entityType)
		{
		case 1:
			PeopleRelatedSearch.getInstance().getPeopleRelatedInfo(entityId);
		}
	}
	
	public static void main(String[] args) throws SQLException
	{
		Connection conn =  MySQLUtils.getInstance().getConnection();
		Search s = new Search(conn);
		Scanner in = new Scanner(System.in);
		String entity = null;
		for(int i = 0;i<10;i++){
			entity = in.nextLine();
			s.getRelatedInfo(entity, 1);
		}
	}
}
