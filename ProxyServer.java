import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class implements the proxy server that accepts multiple client request
 * to handle HTTP/HTTPS web requests concurrently.
 * 
 * @author
 *
 */
public class ProxyServer {

	public static void main(String[] args) {
		
		// check proper usage of the proxy server
		if(args.length == 0) {
			System.out.println("Usage: java ProxyServer PORT");
			System.out.println("where PORT is the port at which proxy server will run.\n");
			System.exit(0);
		}
		// save the proxy server port from command line parameter
		int port = Integer.parseInt(args[0]);
				
		try {
			// create the proxy server socket
			ServerSocket servSock = new ServerSocket(port);
			System.out.println("Proxy server started at port " + port + "...\n");
			
			// loop forever to handle multiple clients concurrently
			while(true) {
	
				// receive the next client connection
				Socket clientSock = servSock.accept();
				InputStream in = clientSock.getInputStream();
				OutputStream out = clientSock.getOutputStream();
				// handle seperate therad foe each clent passing the vales of input streams and getting output streans
				
				ProxyServerThread proxyThread = new ProxyServerThread(clientSock,in,out);
				proxyThread.start();
			}
			
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
			System.exit(0);
		}
	}

	// Inner Thread subclass to handle a client request for HTTP/HTTPS
	static class ProxyServerThread extends Thread{
		
		// the connected client socket
		private Socket clientSock;
		private InputStream in;
		private OutputStream out;
		
		
		public ProxyServerThread(Socket clientSock,InputStream in, OutputStream out) {
			this.clientSock = clientSock;
		    this.in=in;
		    this.out=out;
		}
		
		@Override
		public void run() {
			System.out.println("New Thread for the new Client");
			System.out.println(clientSock.getRemoteSocketAddress().toString());
			// buffer to read client request URL
			byte[] request = new byte[1024];
			int bytesRead;
			
			try {
				// read the client requested URL
				bytesRead = in.read(request);
			
				// if any read error close the client connection
				if(bytesRead < 0) {
					clientSock.close();
					return;
				}
				// get the request URL from buffer
				String url = new String(request, 0, bytesRead);
				
				
				IpAddress urlIpconversion = new IpAddress(url);
				
				//b;ock for blacklist of the ips at server on client request client will receive the message that the particlular ip is blacklister
				
				String address=urlIpconversion.ipaddressUrl();
				Boolean flag=false;
				//readding blacklist.txt file for blocked ips if matched ip is blacklisted is shared
				Scanner scanner = new Scanner(new File("blacklist.txt"));
				
				if(!address.equals(""))
				{
				while (scanner.hasNextLine()) 
				{
					
					 String line = scanner.nextLine();
					 
					 if(line.equals(address))
					 {
						 flag=true; 
					 }
				
				}
				}
				
				if(flag)
				{		
				byte[] buff = "The website is blacklisted".getBytes();
				out.write(buff, 0, buff.length);
				out.flush();
				clientSock.close();
				}
				else if(address.equals(""))
				{
				System.out.println("Invalid URL");	
		
				byte[] buff = "Invalid URL".getBytes();
				out.write(buff, 0, buff.length);
				out.flush();
				clientSock.close();
				}
				else
				{
				if(!url.startsWith("http")) {
					url = "http://" + url;
				}
				System.out.println("received request for URL: " + url);
				
				
				// get content from the URL
				StringBuffer contentBuffer = null;
				if(url.startsWith("https://")) {
					contentBuffer = getHttpsContent(url);
				}
				else {
					contentBuffer = getHttpContent(url);
				}
				// send the URL content back to the client
				byte[] buff = contentBuffer.toString().getBytes();
				out.write(buff, 0, buff.length);
				out.flush();
				
				// close the client connection
				clientSock.close();
				}
			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
			}
		}
		
		/**
		 * Fetch html web page content for a HTTP URL.
		 * 
		 * @param httpUrl the complete HTTP URL
		 * @return the web page content as a StringBuffer
		 */
		private StringBuffer getHttpContent(String httpUrl) {
			try {
				URL url = new URL(httpUrl);
				//HttpURLConnection.setFollowRedirects(false);				
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(5000);
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuffer sBuff = new StringBuffer();
				
				while((line = br.readLine()) != null) {
					sBuff.append(line);
				}
				br.close();
				return sBuff;
			} catch (MalformedURLException e) {
				System.out.println("ERROR: " + e.getMessage());
				return null;
			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
				return null;
			}
			
		}
		
		/**
		 * Fetch html web page content for a HTTPS URL.
		 * 
		 * @param httpsUrl the complete HTTPS URL
		 * @return the web page content as a StringBuffer
		 */
		private StringBuffer getHttpsContent(String httpsUrl) {
			try {
				URL url = new URL(httpsUrl);
				//HttpsURLConnection.setFollowRedirects(true);
				HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();	
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuffer sBuff = new StringBuffer();
				
				while((line = br.readLine()) != null) {
					sBuff.append(line);
				}
				br.close();
				return sBuff;
			} catch (MalformedURLException e) {
				System.out.println("ERROR: " + e.getMessage());
				return null;
			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
				return null;
			}
		}
	}
}
