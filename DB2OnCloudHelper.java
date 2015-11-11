import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


public class DB2OnCloudHelper
{
	int i=0;
	/**
	* DBConnection50000Test()
	* This method does the below End to End testing ( port 50000 )
	* Using JDBC , port 50000 and the credentials given from the config file establishes a database connection
	* Connects to the database "HADRDB2" which was created thru' HADR script
	* creates a table , inserts data , select data, updates and deletes data and drop the table
	* Closes the database connection
	*/
	public static void DBConnection50000Test(String username, String password, String hostip,String alternatehostip) throws Exception{
		   WriteToFile(" <<< DBConnection50000 e2e Test : Started...");
		   Connection  conn = null;
		   Statement stmt = null;
		   ResultSet rs;
		   String name;
		   int age;
						
		   String jdbcurl = "jdbc:db2://" + hostip + ":50000/HADRDB2" + ":" + "clientRerouteAlternateServerName=" + alternatehostip + ";" + "clientRerouteAlternatePortNumber=50000;";
		   
		   try{
				//register JDBC driver
				Class.forName("com.ibm.db2.jcc.DB2Driver");				
				//open database connection
				conn = DriverManager.getConnection(jdbcurl,username,password);
		        WriteToFile( "Port 50000 Connection to HADR database PASSED");
			} catch (SQLException e) {
					WriteToFile("Error connecting to database");
					WriteToFile("SQL Exception: " + e);
					WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
					return;
			} catch (ClassNotFoundException e) {
					WriteToFile("Error loading driver");
					WriteToFile("Error: " + e);
					WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
					return;
			} 
				
			String tableName = "T1";
			String sqlStatement = "";
			//CREATE TABLE SQL statement 
			try {
					stmt = conn.createStatement();
					sqlStatement = "CREATE TABLE " + tableName + " (NAME VARCHAR(20), AGE INTEGER) ";
					stmt.executeUpdate(sqlStatement);
			}catch (Exception e){
							WriteToFile("Error Creating Table: " + e);
							WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
							return;
							}
				// Insert, Select , Update and Delete
			try{
					sqlStatement = "INSERT INTO " + tableName + " VALUES (\'AAA\', 50)";
					stmt.executeUpdate(sqlStatement);
			}catch (Exception e){
					WriteToFile("Error Inserting values into table: " + e);
					WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
					return;
					}
					
			try{
					sqlStatement = "SELECT * FROM " + tableName ;
					rs = stmt.executeQuery(sqlStatement);

					while ( rs.next() ){
					name = rs.getString("NAME");
					age = rs.getInt("AGE");
						//System.out.println("\n your Name is " + name);
						//System.out.println("\n Your Age is " + age);
					}
					rs.close();
					
					//update T1 set age = 60 where name = "aaa"
					sqlStatement = "UPDATE " + tableName + " SET AGE = 60 WHERE NAME = \'AAA\' ";
					stmt.executeUpdate(sqlStatement);
					
					sqlStatement = "SELECT * FROM " + tableName ;
					rs = stmt.executeQuery(sqlStatement);
					while ( rs.next() ){
					name = rs.getString("NAME");
					age = rs.getInt("AGE");
						//System.out.println("\n your Name is " + name);
						//System.out.println("\n Your Age is " + age);
					}
					rs.close();
					
					// Delete the record
					sqlStatement = "DELETE FROM " + tableName+ " WHERE NAME = \'AAA\'";
					stmt.executeUpdate(sqlStatement); 
			} catch (SQLException e){
					WriteToFile("Error executing SQL statement" + sqlStatement);
					WriteToFile("SQL Exception: " + e);
					WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
					return;
			} 
			
				// Remove the table from the database
			try {
					sqlStatement = "DROP TABLE " + tableName;
					stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
					WriteToFile("Error Dropping table: " + e);
					WriteToFile(" DBConnection50000 e2e Test FAILED : Ended >>>");
					return;
			} 
			try{
				stmt.close();
				conn.close();
				}catch (Exception e)
				{
					WriteToFile("Error in stmt/conn closing.... " +e);
					WriteToFile(" DBConnection5000 e2e Test FAILED : Ended >>>"); 
					return;
				}
			WriteToFile(" DBConnection50000 e2e Test PASSED : Ended >>>");			
		}
	
	
	/** 
	 * SSHConnectionTest()
	 * This class demonstrates a connection to a remote host via SSH
     */
	
	public static void SSHConnectionTest(String username, String password, String hostip) throws Exception{
        
		WriteToFile(" <<< SSHConnectionTest : Started...");
        Channel channel;
   		Session session;
   		
   		try{
   	         JSch jsch = new JSch();   	    
   	         session = jsch.getSession(username, hostip, 22);
   	         session.setPassword(password);
   	         session.setConfig("StrictHostKeyChecking", "no");
   	         session.connect(10*1000);
   	         channel = session.openChannel("shell");  	    	
   		     channel.connect(15 * 1000);
   		         
   	         // Wait three seconds for this demo to complete (ie: output to be streamed to us).
   	         Thread.sleep(3*1000);   	    
   	         // Disconnect (close connection, clean up system resources)
   	         channel.disconnect();
   	         session.disconnect();
   		}catch (JSchException jsche){
   			if (jsche.getMessage().trim().startsWith("timeout: socket is not established")){
   				WriteToFile(" SSHConnectionTest FAILED : INVALID HOST IP");
   				WriteToFile(" SSHConnectionTest : Ended   >>>");
   				return;
   			}else if (jsche.getMessage().trim() == "Auth fail"){
   				WriteToFile(" SSHConnectionTest FAILED : INVALID CREDENTIALS, either USERID or PASSWORD is wrong");
   				WriteToFile(" SSHConnectionTest : Ended   >>>");
   				return;
   			}else{
   				WriteToFile("SSHConnectionTest FAILED : JSchException :: " + jsche.getMessage());
   				WriteToFile(" SSHConnectionTest : Ended   >>>");
   				return;
   			}
   		} catch ( Exception e ){
   				WriteToFile(" SSHConnectionTest FAILED : Exception:: " + e.getMessage());
   				WriteToFile(" SSHConnectionTest : Ended   >>>");
   				return;
   		} 
   			WriteToFile(" SSHConnectionTest PASSED : Ended >>>");
   	} 
	
	/**
     * DBConnectionSSLTest()
	 * This class does the End to End testing ( using port 50001 )
     * Connects to the database "HADRDB2" which was created thru' HADR script
     * uses DB2 JDBC driver and port 50001 to connect to the database
     * creates a table , inserts data , select data, updates and deletes data and drop the table
	 * closes the database connection
     */
	public static void DBConnectionSSLTest(String username, String password, String hostip, String alternatehostip) throws Exception{
        WriteToFile(" <<< DBConnectionSSLTest : Started...");
        
		String keyName_arm = "sqldb_ssl.arm";
		String keyName_jks = "keystore.jks";
		
		/**
		 * Establishes sftp connection to the host machine 
		 * cd to the directory "/home/db2inst1" where the security certificate is in
		 * downloads the security certificate to the local 
		 */
    try{
		JSch jsch1 = new JSch();
        Session session = null;
        session = jsch1.getSession(username, hostip, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();        

        //change folder on the remote server /home/db2inst1
        channel.cd("/home/db2inst1");        
        		
        InputStream in = channel.get("sqldb_ssl.arm");        
        
        //WriteToFile(" Downloading the security certificate");
        // set local file        
        FileOutputStream targetFile = new FileOutputStream("sqldb_ssl.arm");

        // read contents of remote file to local
        int x=1;
        int c;
        while ( (c= in.read()) != -1 ) {
            targetFile.write(c);
            if (x==1)
               x=x+1;
        }        
        in.close();
        targetFile.close();  
        channel.disconnect();
        session.disconnect();
       // WriteToFile("ssl arm copy done");        
        /**
         * Converting the security certificate to keystore database
         */
        String c1;
      	c1= KeystoreGeneration.keystoreGen(keyName_arm, keyName_jks);
        }catch (Exception e)
        {
        	WriteToFile(" DBConnectionSSLTest : Exception-keystoreGen "+e);
        	return;        	
        }        
        	//WriteToFile(" keystore generated");
        	
        /**
         * connects to the database using the credentials given from the config file
         * uses DB2 JDBC driver, port 50001 and keystore database to establish a secure connection to the database
         * do some create, insert, update , delete operations on the database
         */
        
        Properties prop = new Properties();
    	prop.put("user", username);
    	prop.put("password", password);
    	prop.put("sslConnection", "true"); 
    	prop.put("sslTrustStoreLocation",  "keystore.jks");
    	
    	String sslurl;
    	Connection conn = null;
    	Statement stmt = null;
    	ResultSet rs;
    	String name;
    	int age;

        try{
    	   	sslurl = "jdbc:db2://" + hostip + ":50001/HADRDB2" + ":" + "clientRerouteAlternateServerName=" + alternatehostip + ";" + "clientRerouteAlternatePortNumber=50001" + ";sslconnection=true;";
    	   	Class.forName("com.ibm.db2.jcc.DB2Driver");
    	   	conn = DriverManager.getConnection(sslurl,prop);
    	   	WriteToFile( "SSL Connection to HADR database PASSED");
		
        } catch (SQLException e) {
        	WriteToFile("Error connecting to database");
        	WriteToFile("SQL Exception: " + e);
        	WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>");
			return;
        } catch (ClassNotFoundException e) {
        	WriteToFile("Error loading driver");
        	WriteToFile("Error: " + e);
        	WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>");
			return;
        } 
		
		String tableName = "T1";
		String sqlStatement = "";
		//CREATE TABLE SQL statement 
		try {
			stmt = conn.createStatement();
			sqlStatement = "CREATE TABLE " + tableName + " (NAME VARCHAR(20), AGE INTEGER) ";
			stmt.executeUpdate(sqlStatement);
			}catch (SQLException e){
					WriteToFile(" Error Creating Table: " + e);
					WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>"); 
					return;
			}
		// Insert, Select , Update and Delete
		try{
			sqlStatement = "INSERT INTO " + tableName + " VALUES (\'AAA\', 50)";
			stmt.executeUpdate(sqlStatement);			
			sqlStatement = "SELECT * FROM " + tableName ;
			rs = stmt.executeQuery(sqlStatement);
			while ( rs.next() ){
			name = rs.getString("NAME");
			age = rs.getInt("AGE");
				//System.out.println("\n your Name is " + name);
				//System.out.println("\n Your Age is " + age);
			}
			rs.close();			
			//update T1 set age = 60 where name = "aaa"
			sqlStatement = "UPDATE " + tableName + " SET AGE = 60 WHERE NAME = \'AAA\' ";
			stmt.executeUpdate(sqlStatement);
			
			sqlStatement = "SELECT * FROM " + tableName ;
			rs = stmt.executeQuery(sqlStatement);
			while ( rs.next() ){
			name = rs.getString("NAME");
			age = rs.getInt("AGE");
				//System.out.println("\n your Name is " + name);
				//System.out.println("\n Your Age is " + age);
			}
			rs.close();
			// Delete the record
			sqlStatement = "DELETE FROM " + tableName + " WHERE NAME = \'AAA\'";
			stmt.executeUpdate(sqlStatement);
		} catch (Exception e){
			WriteToFile("Error executing SQL statement insert / update / delete" + sqlStatement);
			WriteToFile("SQL Exception: " + e);
			WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>"); 
			return;
		}		
		// Remove the table from the database
		try {
			sqlStatement = "DROP TABLE " + tableName;
			stmt.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			WriteToFile(" Error Dropping table.... "+ e );
			WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>"); 
			return;
		}
		
		try{
		stmt.close();
		conn.close();
		}catch (Exception e)
		{
			WriteToFile("Error in stmt/conn closing.... " +e);
			WriteToFile(" DBConnectionSSL e2e Test FAILED : Ended >>>"); 
			return;
		}
		
		WriteToFile(" DBConnectionSSL e2e Test PASSED : Ended >>>");  
	}	
	
	/**
	 * DBSetParametersTest()
	 * This class validates the configuration parameters set thru' db2set all
	 */
	public static void DBSetParametersTest(String username, String password, String hostip, String alternatehostip) throws Exception{
        WriteToFile(" <<< DBSetParametersTest : Started...");
        
    	String db2_instdef=Configuration.getPropValue("DB2INSTDEF");
 		String db2_adminserver=Configuration.getPropValue("DB2ADMINSERVER");
 		String db2_system=Configuration.getPropValue("DB2SYSTEM");
 		String db2_autostart=Configuration.getPropValue("DB2AUTOSTART");
 		String db2_comm=Configuration.getPropValue("DB2COMM");
 		
		String url;
		Connection  conn = null;
		Statement stmt = null;
		ResultSet rs;
		String db2variablename="";
		String value="";		
		
		url = "jdbc:db2://" + hostip + ":50000/HADRDB2" + ":" + "clientRerouteAlternateServerName=" + alternatehostip + ";" + "clientRerouteAlternatePortNumber=50000;";
		//register JDBC driver
		Class.forName("com.ibm.db2.jcc.DB2Driver");
				
		String sqlStatement = "";
		
		try{
			conn = DriverManager.getConnection(url,username,password);
			stmt = conn.createStatement();
			sqlStatement= "SELECT CHAR(REG_VAR_NAME,35) AS REGVAR, CHAR(REG_VAR_VALUE,50) AS VALUE, LEVEL FROM TABLE(REG_LIST_VARIABLES())  AS REG ORDER BY 3,1";
			rs = stmt.executeQuery(sqlStatement);
			if (rs == null){
				WriteToFile("Result set is null!!");
			}
			boolean flag = true;
			String actualDB2variableValue="";
			
			while ( rs.next() ){
							
			db2variablename = rs.getString("REGVAR").trim();
			value = rs.getString("VALUE").trim();
			
			switch (db2variablename) {
				
	            case "DB2INSTDEF":
	             
	                if (db2_instdef.compareTo(value)==0) {
	            	break;
	            	
	            }else { 
	            	     flag = false;
	            	     actualDB2variableValue=db2_instdef;
	            	     
	            	     WriteToFile(" DBSetParametersTest FAILED : " + db2variablename.trim() + " : "+value.trim() +" is not matching with actual value of "+ actualDB2variableValue );
	            	     break;
	            	  }
	            
	            case "DB2ADMINSERVER":
		            if (db2_adminserver.compareTo(value)==0) {
		          		            	break;
		            	
		            }else { 
		            	    flag = false;
	            	        actualDB2variableValue=db2_adminserver;
	            	        WriteToFile(" DBSetParametersTest FAILED : " + db2variablename.trim() + " : "+value.trim() +" is not matching with actual value of "+ actualDB2variableValue );
	            	        break;
	            	      }
	            case "DB2SYSTEM":
		            if (db2_system.compareTo(value)==0) {
		            	//System.out.println("\n Variable name 5: " + db2variablename + " : "+value);
		            	break;
		            	
		            }else { 
		            	     actualDB2variableValue=db2_system;
		            	     flag = false;
		            	     WriteToFile(" DBSetParametersTest FAILED : " + db2variablename.trim() + " : "+value.trim() +" is not matching with actual value of "+ actualDB2variableValue );
		            	     break;
		            	  }
		        case "DB2AUTOSTART":
			            if (db2_autostart.compareTo(value)==0) {
			            	
			            	break;
			            	
			            }else { 
			            	
		            	        flag = false;
		            	        actualDB2variableValue=db2_autostart;
		            	        WriteToFile(" DBSetParametersTest FAILED : " + db2variablename.trim() + " : "+value.trim() +" is not matching with actual value of "+ actualDB2variableValue );
		            	        break;
		            	      }
		        case "DB2COMM":
		            if (db2_comm.compareTo(value)==0) {
		            	
		            	break;
		            	
		            }else { 
		            	
	            	        flag = false;
	            	        actualDB2variableValue=db2_comm;
	            	        WriteToFile(" DBSetParametersTest FAILED : " + db2variablename.trim() + " : "+value.trim() +" is not matching with actual value of "+ actualDB2variableValue );
	            	        break;
	            	      }					
			    } //end switch case
					
			} //end while loop
			
			if (!flag){
				//System.out.println("Test PASSED :: The value of all db2 set variables are correct!!" );
				return;	       
			       
			}	    
			 
			rs.close();	
			
		}catch(Exception e)
		{			
			WriteToFile("Exception : "+e);
			WriteToFile(" DBSetParametersTest FAILED :  Ended >>>");
			return;
		}
		
		WriteToFile(" DBSetParametersTest PASSED : Ended >>>");
	}
	
	/**
	 * NetworkHardeningTest()
	 * Negative testing, trying to connect to the database thru' blocked port, where we should get error in connecting to the database
	 * This class validates the database connection with port 50010, which is a blocked port
	 */
	public static void NetworkHardeningTest(String username, String password, String hostip, 
                                            String alternatehostip) throws Exception{
           WriteToFile(" <<< NetworkHardeningTest : Started...");
           Connection  conn = null;
           Statement stmt = null;
           ResultSet rs;
           String url = "jdbc:db2://" + hostip + ":50010/HADRDB2";
           try{
        	   //register JDBC driver
        	   Class.forName("com.ibm.db2.jcc.DB2Driver");				
        	   //open database connection
        	   conn = DriverManager.getConnection(url,username,password);
        	   
           } catch (SQLException e) {
        	   WriteToFile(" Error connecting to database");
        	   //WriteToFile(" SQL Exception: " + e);
        	   WriteToFile(" NetworkHardeningTest PASSED :  Ended >>>");
        	   return;     	   
           } catch (ClassNotFoundException e) {
        	   WriteToFile("Error loading driver: " + e);
        	   WriteToFile(" NetworkHardeningTest FAILED : Ended >>>");
        	   return;
           } catch (Exception e) {
        	   WriteToFile("Error :"+ e);
        	   WriteToFile(" NetworkHardeningTest FAILED : Ended >>>");
        	   return;
           }
           WriteToFile(" NetworkHardeningTest FAILED : Ended >>>");
	} 
	
   public static void WriteToFile(String content) {
	  FileWriter fw = null;
	  PrintWriter pw = null;
	   
	    try {
	        fw = new FileWriter(Configuration.getPropValue("LOGFILENAME"), true);
	        pw = new PrintWriter(fw);
	        pw.write(new Date(System.currentTimeMillis()) + " : "+ content +"\n");
	        pw.close();
	        fw.close();
	    } catch (IOException ex) {
	        //Logger.getLogger(FileAccessView.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	
}