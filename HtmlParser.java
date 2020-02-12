import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Utility class to parse a HTML web page content and extracts css,images,links from it
 * and save them into separate folder.
 * 
 * @author
 *
 */
public class HtmlParser {

	private String refUrl;
	private StringBuffer htmlBuffer;
	private List<String> imageUrls;
	private List<String> cssUrls;
	private List<String> jsUrls;
	private Map<String, String> resourceUrlsMap;
	
	
	public HtmlParser(String refUrl, StringBuffer htmlBuffer) {
		this.htmlBuffer = htmlBuffer;
		this.refUrl = refUrl;
		imageUrls = new ArrayList<String>();
		cssUrls = new ArrayList<String>();
		jsUrls = new ArrayList<String>();
		resourceUrlsMap = new HashMap<String, String>();
	}
	
	/**
	 * Parse the underlying html content and extract URLS for all
	 * css, images and javascripts.
	 */
	public void parse() {
		String domain = "";
		try {
			URL url = new URL(refUrl);
			if(refUrl.startsWith("http://"))
				domain = "http://" + url.getHost();
			else
				domain = "https://" + url.getHost();
		} catch (MalformedURLException e1) {
			
		}
		String html = htmlBuffer.toString();
		Document doc = Jsoup.parse(html);
		String baseUri = doc.baseUri();
		Elements images = doc.select("img[src]");
		for(Element e : images) {
			String src = e.attr("src");
			if(src.isEmpty()) continue;
			String orgUrl = src;
			if(!src.startsWith("http")) {
				src = domain + src;
			}
			if(!imageUrls.contains(src)) {
				imageUrls.add(src);
				resourceUrlsMap.put(src, orgUrl);
			}
		}
		Elements js = doc.select("script[src]");
		for(Element e : js) {
			String src = e.attr("src");
			if(src.isEmpty()) continue;
			String orgUrl = src;
			if(!src.startsWith("http")) {
				src = domain + src;
			}
			if(!jsUrls.contains(src)) {
				jsUrls.add(src);
				resourceUrlsMap.put(src, orgUrl);
			}
		}
		Elements css = doc.select("link[href]");
		for(Element e : css) {
			String src = e.attr("href");
			if(src.isEmpty()) continue;
			String orgUrl = src;
			if(!src.startsWith("http")) {
				src = domain + src;
			}
			if(!cssUrls.contains(src)) {
				cssUrls.add(src);
				resourceUrlsMap.put(src, orgUrl);
			}
		}
		/*
		css = doc.select("link[type='text/css'][href]");
		for(Element e : css) {
			String src = e.absUrl("href");
			if(!cssUrls.contains(src)  && src.length() > 0)
				cssUrls.add(src);
		}
		*/
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public List<String> getCssUrls() {
		return cssUrls;
	}

	public List<String> getJsUrls() {
		return jsUrls;
	}
	
	public Map<String, String> getResourceUrlsMap() {
		return resourceUrlsMap;
	}
	
}
