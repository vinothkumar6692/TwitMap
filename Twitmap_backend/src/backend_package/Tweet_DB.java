package backend_package;

import java.io.*;
import java.sql.*;
public class Tweet_DB {
	public static Connection createConnection() throws Exception
	{
	Connection conn=null;
	String userName = "harshinee";
	String password = "harshinee";
	String hostname = "twittersentiment.cpgj1nwy7vg0.us-west-2.rds.amazonaws.com";
	String port = "3306";
	String dbName = "twitter";
	String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
	          port + "/" + dbName + "?user=" + userName + "&password=" + password;	        
	DriverManager.registerDriver(new com.mysql.jdbc.Driver());
	Class.forName("com.mysql.jdbc.Driver");
	conn=DriverManager.getConnection(jdbcUrl);
	return conn;
	}
	public static void closeConnection(Connection con)	
	{
	if(con!=null)
	{
	try{
	con.close();
	}
	catch(SQLException e){
	e.printStackTrace();
	}
	}
	}
	
}

