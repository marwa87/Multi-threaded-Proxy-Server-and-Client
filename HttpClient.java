import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This is the client driver program that requests a HTTP/HTTPS URL to proxy
 * server, gets the response HTML web page and extracts images, links from the
 * page and save them in a separate folder in current directory.
 * 
 * @author
 *
 */
public class HttpClient {

	/**
	 * Receive URL content from proxy server.
	 * 
	 * @param url  the URL as string
	 * @param sock the connected Socket to proxy server
	 * @return The URL content as StringBuffer
	 */
	private static StringBuffer receiveURLContentFromServer(String url, String proxyServIp, int proxyPort) {

		StringBuffer htmlBuffer = new StringBuffer();

		try {
			// connect to the proxy server
			System.out.println("Connecting to proxy server...");
			
			Socket sock = new Socket(proxyServIp, proxyPort);

			System.out.println("Connected.\n");

			System.out.println("Receiving URL content from proxy server...");
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter out = new PrintWriter(sock.getOutputStream());

			// send the URL to the proxy server
			out.println(url);
			out.flush();

			// receive the proxy server response
			String data;
			while ((data = in.readLine()) != null) {
				htmlBuffer.append(data);
			}
			// once response received close the connection with proxy server
			sock.close();
			System.out.println("Content received (" + htmlBuffer.length() + " bytes)\n");

		} catch (UnknownHostException e) {
			System.out.println("ERROR: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		return htmlBuffer;
	}

	public static void main(String[] args) throws IOException {
		// Check proper usage of the program
		if (args.length != 4) {
			System.out.println("\nUsage: java HttpClient PROXY_SERVER_IP PROXY_SERVER_PORT URL OUTPUT_FOLDER");
			System.out.println("\nwhere the arguments are as below:");
			System.out.println("PROXY_SERVER_IP  :  The IP address or hostname of the proxy server");
			System.out.println("PROXY_SERVER_PORT:  The port of proxy server");
			System.out.println("OUTPUT_FOLDER    :  The output folder to save data for given URL\n");
			System.exit(0);
		}
		
		
		
		
//		IpAddress urlIpconversion = new IpAddress(args[2]);
//		
//		String address=urlIpconversion.ipaddressUrl();
//		Boolean flag=false;
//		Scanner scanner = new Scanner(new File("blacklist.txt"));
//		
//		if(!address.equals(""))
//		{
//		while (scanner.hasNextLine()) 
//		{
//			
//			 String line = scanner.nextLine();
//			 
//			 if(line.equals(address))
//			 {
//				 flag=true; 
//			 }
//		
//		}
//		}
//		
//		if(flag)
//		{	
//		System.out.println("The website is blacklisted");	
//		}
//		else if(address.equals(""))
//		{
//		System.out.println("Invalid URL");		
//		}
//		else
//		{
		// save the command line parameters
		String proxyServIp = args[0];
		int proxyServPort = Integer.parseInt(args[1]);
		String url = args[2];
		String outputDir = args[3];
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));

		// Verify output folder does not already exist
		File f = new File(outputDir);
		if (f.exists()) {
			System.out.println("Output folder " + f.getAbsolutePath() + " already exists!\n");
			System.exit(0);
		}

		// receive URL content from proxy server
		StringBuffer htmlBuffer = receiveURLContentFromServer(url, proxyServIp, proxyServPort);

		// parse the URL content
		if(htmlBuffer.toString().equals("The website is blacklisted"))
		{
			System.out.println("The website is blacklisted");	
		}
		else if(htmlBuffer.toString().equals("Invalid URL"))
		{
			System.out.println("Invalid URL");
		}
		else
		{
		System.out.println("Parsing content...");
		HtmlParser parser = new HtmlParser(url, htmlBuffer);
		parser.parse();
		System.out.println("Done.");

		// Display parsed data to console
		System.out.println("\n----------- Parse Results ----------");
		System.out.println("URL: " + url);
		System.out.println("\nImages: ");
		for (String imgUrl : parser.getImageUrls()) {
			System.out.println(imgUrl);
		}
		System.out.println("\nCSS: ");
		for (String cssUrl : parser.getCssUrls()) {
			System.out.println(cssUrl);
		}
		System.out.println("\nJavaScripts: ");
		for (String jsUrl : parser.getJsUrls()) {
			System.out.println(jsUrl);
		}

		// Download and save images,css, and javascripts file from URL
		downloadAndSaveURLContents(parser, url, htmlBuffer, f,fileNameWithoutExtn);
		}
	}
//	}

	private static StringBuffer replaceHtmlResourcesWithLocalFiles(HtmlParser parser, StringBuffer htmlBuffer) {
		return null;
	}
	
	
	/**
	 * Download all URL web page resources (css/images/javascripts) to output
	 * folder.
	 * 
	 * @param parser     the HTMLParser that contains resource links
	 * @param url        the source URL
	 * @param htmlBuffer the StringBuffer containing the URL html
	 * @param outputDir  the output folder path
	 */
	private static void downloadAndSaveURLContents(HtmlParser parser, String url, StringBuffer htmlBuffer,
			File outputDir,String fn) {
		outputDir.mkdir();
		String outputDirPath = outputDir.getAbsolutePath();		
		System.out.println("File Name:"+fn);
		// save all JS files in a folder "js" inside output folder
		File jsDir = new File(outputDirPath + "/js");
		jsDir.mkdir();
		System.out.println("\nDownloading and saving javascript files...");
		for (String jsUrl : parser.getJsUrls()) {
			String jsFilename = jsUrl.substring(jsUrl.lastIndexOf('/') + 1);
			String outputJsFilePath = jsDir.getAbsolutePath() + "/" + jsFilename;
			saveResource(jsUrl, parser.getResourceUrlsMap().get(jsUrl), outputJsFilePath, htmlBuffer);
		}
		System.out.println("Done.");

		// save all css files in a folder "css" inside output folder
		File cssDir = new File(outputDirPath + "/css");
		cssDir.mkdir();
		System.out.println("\nDownloading and saving css files...");
		for (String cssUrl : parser.getCssUrls()) {
			String cssFilename = cssUrl.substring(cssUrl.lastIndexOf('/') + 1);
			String outputCssFilePath = cssDir.getAbsolutePath() + "/" + cssFilename;
			saveResource(cssUrl, parser.getResourceUrlsMap().get(cssUrl), outputCssFilePath, htmlBuffer);
		}
		System.out.println("Done.");

		// save all image files in a folder "images" inside output folder
		File imagesDir = new File(outputDirPath + "/images");
		imagesDir.mkdir();
		System.out.println("\nDownloading and saving image files...");
		for (String imgUrl : parser.getImageUrls()) {
			String imgFilename = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
			String outputImageFilePath = imagesDir.getAbsolutePath() + "/" + imgFilename;
			saveResource(imgUrl, parser.getResourceUrlsMap().get(imgUrl), outputImageFilePath, htmlBuffer);
		}
		
		// save the web page in a file named webpage.html inside output folder
		String webPageOutputFilename = outputDirPath +"/"+ fn+".html";
		saveWebPage(htmlBuffer, webPageOutputFilename);
		
		System.out.println("Done.");
	}

	/**
	 * Save web page content to an output file.
	 * 
	 * @param htmlBuffer     the StringBuffer containing web page content
	 * @param outputFilename the path of output file
	 */
	private static void saveWebPage(StringBuffer htmlBuffer, String outputFilename) {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFilename));
			pw.println(htmlBuffer.toString());
			pw.close();
		} catch (IOException e) {
			System.out.println("File I/O Error: " + e.getMessage());
		}
	}

	/**
	 * Save a css/javascript/image file with given URL to output file.
	 * 
	 * @param resourceUrl    the URL string of the resource
	 * @param outputFilename the path of the output file.
	 */
	private static void saveResource(String resourceUrl, String orgResourceUrl, String outputFilename, StringBuffer htmlBuffer) {
		String filename = outputFilename.substring(outputFilename.lastIndexOf('/')+1);
		String localResource = null;
		if(filename.toLowerCase().endsWith(".css")) {
			localResource = "./css/"+filename;
		}
		else if(filename.toLowerCase().endsWith(".js")) {
			localResource = "./js/"+filename;
		}
		else {
			localResource = "./images/"+filename;
		}
		try {
			URL url = new URL(resourceUrl);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			FileOutputStream fout = new FileOutputStream(new File(outputFilename));
			byte[] buff = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
				fout.write(buff, 0, bytesRead);
			}
			fout.close();
			bis.close();
			int index = htmlBuffer.indexOf(orgResourceUrl);
			if(index >= 0)
				htmlBuffer.replace(index, index+orgResourceUrl.length(), localResource);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: failed to download " + resourceUrl);
		} catch (IOException e) {
			System.out.println("ERROR: failed to download " + resourceUrl);
		}
	}
}
