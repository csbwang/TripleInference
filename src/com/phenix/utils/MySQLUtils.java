package com.phenix.utils;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.Statement;

public class MySQLUtils {
	
	private static final MySQLUtils Instance = new MySQLUtils();
	
	public static MySQLUtils getInstance()
	{
		return Instance;
	}
	
	private MySQLUtils(){
	
	}
	
	public Connection getConnection()
	{
		String driverClassName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.10.79:3306/techsearch"; 
		String username = "techsearch";
		String password = "techsearch123";
		Connection conn = null;
		try {  
            Class.forName(driverClassName);//指定连接类型  
            conn = DriverManager.getConnection(url, username, password);//获取连接  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return conn;
	}
    
	/*
    public static void main(String[] args) throws Exception{
    	Connection conn =  MySQLUtils.getInstance().getConnection();
    	Statement stmt = conn.createStatement();
    	ResultSet rs= stmt.executeQuery("select * from inference_rules");
    	System.out.println(rs);
    	while(rs.next())
    	{
    		System.out.println(rs.getString(2));
    	}
    }
    */
    
}