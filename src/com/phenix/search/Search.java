package com.phenix.search;

import java.sql.SQLException;
import java.util.Scanner;

public class Search {
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
		Search s = new Search();
		Scanner in = new Scanner(System.in);
		String entity = null;
		for(int i = 0;i<10;i++){
			entity = in.nextLine();
			s.getRelatedInfo(entity, 1);
		}
	}
}
