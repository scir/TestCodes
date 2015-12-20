<%@ page import="java.sql.*" %> 
<%@ page import="java.io.*" %> 

<%
try 
	{
			String host = "localhost" ; String port = "3306" ;  String database = "scir" ;
			String username = "root" ; String password = "secret" ;
			
            String connectionURL = "jdbc:mysql://" + host + ":" + port + "/" + database ;
            Connection connection = null; 
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
            connection = DriverManager.getConnection(connectionURL, username, password);
            if(!connection.isClosed())
                 out.println("Successfully connected to " + "MySQL server using TCP/IP...");
            connection.close();
	}catch(Exception ex){
            out.println("Unable to connect to database"+ex);
    }   

%>
