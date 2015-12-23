package com.phenix.utils;

import java.io.IOException;
import java.io.InputStream; 
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
import java.util.Properties; 

import javax.sql.DataSource; 

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class MySQLUtils {
	private static Connection connection = null;
	
	private MySQLUtils(){
	
	}
	
	/**
     * @return DataSource
     * @throws Exception
     */ 
	public static DataSource getDataSourceFromPro(){
        Properties prop = new Properties(); 
        InputStream in = MySQLUtils.class.getClassLoader().getResourceAsStream("dbcpconfig.properties");        
        try {
			prop.load(in);
		} catch (IOException e) {
			System.out.println("数据库配置文件加载失败");
		} 
        try {
			return BasicDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			System.out.println("数据源创建失败");
		} 
        return null;
	}
	
	/**
     * @return Connection from properties
     * 通过缓冲池连接，适用于访问量大的项目
     * @throws Exception 
     */ 
    public static Connection getConnectionFromPro(){ 
        try {
			return getDataSourceFromPro().getConnection();
		} catch (SQLException e) {
			System.out.println("数据库连接失败");
		} 
        return null;
    }
    
    /**
     * @return Statement
     * @throws Exception 
     */ 
    public static Statement getStatementFromPro(){ 
        try {
			return getConnectionFromPro().createStatement();
		} catch (SQLException e) {
			System.out.println("Statement对象创建失败");
		} 
        return null;
    }   
    
    /**
     * @return ResultSet
     * @throws Exception 
     */ 
    public static ResultSet getResultSetFromPro(String QuerySql){ 
        try {
			return getStatementFromPro().executeQuery(QuerySql);
		} catch (SQLException e) {
			System.out.println("数据库查询失败");
		} 
        return null;
    } 
    
    /**
     * @return connection
     * 直接连接，不采用缓冲池
     */
    public static Connection getConnection(String driverName,String 
    		dbUrl,String username,String password){ 
    	try { 
    		Class.forName(driverName); 
    		Properties properties=new Properties(); 
    		properties.put("username", username); 
    		properties.put("password", password); 
    		connection=DriverManager.getConnection(dbUrl,properties); 
    	} catch (ClassNotFoundException e) { 
    		e.printStackTrace(); 
    		} catch (SQLException e) { 
    			System.out.println("数据库连接失败");
    		   	} 
    	return connection; 
    } 
    
    /**
     * @param rs
     * @param st
     * @param ct
     */ 
    public static void close(ResultSet res, Statement state, Connection connection) { 
    	try { 
    		if (res != null) 
    		    	res.close(); 
    	} catch (SQLException e) { 
    		e.printStackTrace(); 
    	} finally {
    		try { 
    			if (state != null) 
    		    	state.close(); 
    		  	} catch (SQLException e) { 
    		    	e.printStackTrace(); 
    		    	} finally { 
    		       	if (connection != null) 
    		        	try { 
    		           	connection.close(); 
    		        	} catch (Exception e) { 
    		            	e.printStackTrace(); 
    		        	} 
    			} 
    		} 
   	} 
    
    public static void main(String[] args) throws Exception{
    	ResultSet rs= MySQLUtils.getResultSetFromPro("select * from techsearch_schema.qa_knowledge_base where proj_author = \"也发\"");
    	System.out.println(rs);
    	if(rs==null)System.out.println("is null");
    	while(rs.next())
    	{
    		System.out.println("is not null");
    		System.out.println(rs.getString(0));
    	}
    }
    
}