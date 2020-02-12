import java.net.*; 
import java.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader; 
  
//class for having ip from the ip addtress
class IpAddress { 
	
	public String url="";
    
	public IpAddress(String url) throws UnknownHostException 
    { 
    	this.url=url;
    } 
	
	
	
	public static void main(String args[]) throws UnknownHostException
	{
		
		try { 

			InetAddress address = InetAddress.getByName(new URL(args[0]).getHost());
			String ip = address.getHostAddress();
			System.out.println(ip);
			}
			 catch (MalformedURLException e) { 
		            System.out.println("Invalid URL"); 
	
		        }
			
		}
	
	public String ipaddressUrl() throws IOException
	{
		
		try { 
		InetAddress address = InetAddress.getByName(new URL(url).getHost());
		String ip = address.getHostAddress();
		return ip;
		}
		 catch (MalformedURLException e) { 

	            System.out.println("Invalid URL"); 
	            return "";
	        }
		
	}
} 