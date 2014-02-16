import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
/**
 * 
 * @author Niclas Scheuing
 * Very simple implementation of HTTP client. Can only make one kind of requests.
 */
public class WebComm {
	
	private UI ui;
	private final int POST_PER_PAGE = 15; //TODO: change if there are other numbers (then 15) of posts per page.
	private CloseableHttpClient httpclient;
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0";
	private String currentUrl;
	private HttpClientContext localContext;
	
	public WebComm(UI ui){
		this.ui = ui;
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());
		// Create a local instance of cookie store
        

        // Create local HTTP context
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
        CookieStore cookieStore = new BasicCookieStore();
        localContext = HttpClientContext.create();
        localContext.setCookieStore(cookieStore);
        httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
        
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
		int firstPage = 0;//ui.getFirstPage()-1;//from page 2 to 4 means 2,3,4, and not 3,4
		int lastPage = 2;//ui.getLastPage(); 
		ui.initStatusPanel(lastPage-firstPage);
		
		int currentPostNr = firstPage*POST_PER_PAGE;
		while(currentPostNr < lastPage*POST_PER_PAGE){ //pages*POST_PER_PAGE because we iterate over the number of posts and not pages.
			currentUrl = urlCut+"p"+currentPostNr+urlEnd;
			
			String getAnswer = sendGet();
			if(getAnswer.equals("")){
				login(username, password);
				getAnswer = sendGet();
			}else{
				//append each new page to the StringBuilder
				builder.append(getAnswer);
			}
			
			currentPostNr = currentPostNr+POST_PER_PAGE;
			ui.updateStatusPanel(currentPostNr/POST_PER_PAGE-firstPage+1);// offset/POST_PER_PAGE is the number of the current page
		}
		return builder.toString();
	}
	
	private String login(String username, String password) throws IOException{
		//create the attachment for the POST-request
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", username));
		formparams.add(new BasicNameValuePair("password", password));
		formparams.add(new BasicNameValuePair("autologin", "on"));
		formparams.add(new BasicNameValuePair("redirect", ""));
		formparams.add(new BasicNameValuePair("query", ""));
		formparams.add(new BasicNameValuePair("login", "Login"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		
		//the actual request, which POSTs the password and username as entity
		HttpPost httppost = new HttpPost(currentUrl);
		httppost.setEntity(entity);
		httppost.setHeader("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httppost.setHeader("Accept-Language", "	de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
		httppost.setHeader("Connection", "keep-alive");
		httppost.setHeader("Accept-Encoding", "utf-8");
		//the response wanted with the forums content as HTML-code via a stream
		HttpResponse response = httpclient.execute(httppost,localContext);
		InputStream is = response.getEntity().getContent();
		
		String answer = streamToString(is);
		
		
		is.close();
		httppost.abort();
		printCookies();
		return answer;
	}
	
	private String sendGet() throws ClientProtocolException, IOException{
		HttpGet httpget = new HttpGet(this.currentUrl);
		httpget.setHeader("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpget.setHeader("Accept-Language", "	de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
		httpget.setHeader("Connection", "keep-alive");
		httpget.setHeader("Accept-Encoding", "utf-8");
		httpget.setHeader("Host", "rpgame.forumieren.com");
				
		HttpResponse response = httpclient.execute(httpget,localContext);
		HttpEntity respEntity = response.getEntity();
		
		int responseCode = response.getStatusLine().getStatusCode();
		//System.out.println(responseCode);
		assert(responseCode==200 || responseCode==302);
		if( responseCode==302){ //in case of redirection
			this.currentUrl = response.getLastHeader("Location").getValue();
		}
		InputStream is = respEntity.getContent();
		String answer = streamToString(is);
		is.close();
		httpget.abort();
		printCookies();
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
	
	private void printCookies(){
		 List<Cookie> cookies = localContext.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
            System.out.println("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("- " + cookies.get(i).toString());
            }
        }
	}
}
