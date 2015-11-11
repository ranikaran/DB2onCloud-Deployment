/** 
 * This class validates the below tests
 * SSHConnectionTest
 * DBSetParametersTest
 * NetworkHardeningTest
 * DBConnection50000Test
 * DBConnectionSSLTest 
 *
 */
public class DB2OnCloudDeploymentTests
{
  public static void main(String args[]) throws Exception
  {	  
	String username = Configuration.getPropValue("USERNAME");		
	String password = Configuration.getPropValue("PASSWORD");		
	String hostip = Configuration.getPropValue("HOSTIP");		
	String alternatehostip = Configuration.getPropValue("ALTERNATEHOSTIP");

	DB2OnCloudHelper.SSHConnectionTest(username,password,hostip);
	DB2OnCloudHelper.DBSetParametersTest(username, password, hostip, alternatehostip);
	DB2OnCloudHelper.NetworkHardeningTest(username, password, hostip, alternatehostip);
	DB2OnCloudHelper.DBConnection50000Test(username, password, hostip, alternatehostip);
    DB2OnCloudHelper.DBConnectionSSLTest(username, password, hostip, alternatehostip);
	}
} 
