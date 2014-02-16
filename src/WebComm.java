import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
/**
 * 
 * @author Niclas Scheuing
 * Very simple implementation of HTTP client. Can only make one kind of requests.
 */
public class WebComm {
	
	private UI ui;
	private final int POST_PER_PAGE = 15; //TODO: change if there are other numbers (then 15) of posts per page.
	private String cookies;
	private HttpClient httpclient;
	private final String USER_AGENT = "Mozilla/5.0";
	
	public WebComm(UI ui){
		this.ui = ui;
		this.httpclient = new DefaultHttpClient();
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
	}
	
	/**
	 * CAUTION! The URL is modified in a way, suitable only for a specific type of forum (probably all PHPbb forums)
	 * @param urlNonMod the URL straight out of the browser (e.g. <code>http://testforum.iphpbb3.com/forum/96048463nx42082/some-subforum-f85/thread-name-t75.html</code> no <code>-s10</code> in the end. this will be added and modified in this method.)
	 * @param password the password needed to view this thread
	 * @return String containing the whole HTML-code of the copied pages 
	 * @throws Exception some unhandled exceptions. Too lazy to take care of these.
	 */
	public  String  request(String urlNonMod, String username, String password) throws Exception{
		
		//modify the urlNonMod and prepare some technical details...
		Pattern pattern = Pattern.compile("(^.+t\\d{0,4}).*?(-.+)$");
		Matcher matcher = pattern.matcher(urlNonMod);
		matcher.find();
		
		String urlCut = matcher.group(1);
		String urlEnd = matcher.group(2);
		//System.out.println(urlCut);
		//System.out.println(urlEnd);
		
		
		
		//use a StringBuilder to concatenate the HTML code of each page to one big String. this is done this way for performance reason.
		StringBuilder builder = new StringBuilder();
		
		//user input: number of pages to copy (e.g. the number of pages in this thread). The copying always starts with page 0.
		int firstPage = ui.getFirstPage()-1;//from page 2 to 4 means 2,3,4, and not 3,4
		int lastPage = ui.getLastPage(); 
		ui.initStatusPanel(lastPage-firstPage);
		
		int currentPostNr = firstPage*POST_PER_PAGE;
		
		String currentUrl;
		while(currentPostNr < lastPage*POST_PER_PAGE){ //pages*POST_PER_PAGE because we iterate over the number of posts and not pages.
			currentUrl = urlCut+"p"+currentPostNr+urlEnd;
			
			String getAnswer = sendGet(currentUrl);
			
			Pattern loginPattern = Pattern.compile("Bitte geben Sie Benutzername und Passwort ein, um sich einzuloggen.");
			Matcher loginMatcher = loginPattern.matcher(getAnswer);
			if(loginMatcher.find()){
				String loginUrl = currentUrl;
				login(username, password, loginUrl);
			}else{
				//append each new page to the StringBuilder
				builder.append(getAnswer);
			}
			
			currentPostNr = currentPostNr+POST_PER_PAGE;
			ui.updateStatusPanel(currentPostNr/POST_PER_PAGE-firstPage+1);// offset/POST_PER_PAGE is the number of the current page
		}
		return builder.toString();
	}
	
	private String login(String username, String password, String url) throws IOException{
		//create the attachment for the POST-request
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("password", password));
		formparams.add(new BasicNameValuePair("username", username)); //TODO: uncomment this line to make a POST-request, that requires an username. modify the UI to get the username as input as well. then hand it over via the HTMLer.
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		
		//the actual request, which POSTs the password and username as entity
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(entity);
		
		//the response wanted with the forums content as HTML-code via a stream
		HttpResponse response = httpclient.execute(httppost);
		InputStream is = response.getEntity().getContent();
		
		String answer = streamToString(is);
		is.close();
		httppost.abort();
		return answer;
	}
	
	private String sendGet(String url) throws ClientProtocolException, IOException{
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("User-Agent", USER_AGENT);
		httpget.setHeader("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpget.setHeader("Accept-Language", "	de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
		
		HttpResponse response = httpclient.execute(httpget);
		int responseCode = response.getStatusLine().getStatusCode();
		//System.out.println(responseCode);
		assert(responseCode==200);
		InputStream is = response.getEntity().getContent();
		String answer = streamToString(is);
		is.close();
		// set cookies
		setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : 
	                     response.getFirstHeader("Set-Cookie").toString());
		httpget.abort();
		return answer;
	}

	/**
	 * a hack to get a String out of a InputStream
	 * @throws IOException 
	 */
	public static String streamToString(InputStream is) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF8");
		String ret =writer.toString(); 
		return ret;
	}
	
	public void setCookies(String cookies) {
		this.cookies = cookies;
	  }
}
