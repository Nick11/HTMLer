import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * @author Niclas Scheuing
 * Very simple implementation of HTTP client. Can only make one kind of requests.
 */
public class WebComm {
	
	private UI ui;
	private final int POST_PER_PAGE = 10; //TODO: change if there are other numbers (then 10) of posts per page.
	
	public WebComm(UI ui){
		this.ui = ui;
	}
	/**
	 * CAUTION! The URL is modified in a way, suitable only for a specific type of forum (probably all PHPbb forums)
	 * @param urlNonMod the URL straight out of the browser (e.g. <code>http://testforum.iphpbb3.com/forum/96048463nx42082/some-subforum-f85/thread-name-t75.html</code> no <code>-s10</code> in the end. this will be added and modified in this method.)
	 * @param password the password needed to view this thread
	 * @return String containing the whole HTML-code of the copied pages 
	 * @throws Exception some unhandled exceptions. Too lazy to take care of these.
	 */
	public  String  request(String urlNonMod, String password) throws Exception{
		//initialize the httpclient
		HttpClient httpclient = new DefaultHttpClient();
		
		//create the attachment for the POST-request in the while-loop. containing passwort (and username?)
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("password", password));
		//formparams.add(new BasicNameValuePair("username", username)); TODO: uncomment this line to make a POST-request, that requires an username. modify the UI to get the username as input as well. then hand it over via the HTMLer.
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		
		//modify the urlNonMod and prepare som technical details...
		//cut ".html" off
		String urlCut = urlNonMod.substring(0,urlNonMod.length()-5);
		String currentUrl;
		//use a StringBuilder to concatenate the HTML code of each page to one big String. this is done this way for performance reason.
		StringBuilder builder = new StringBuilder();
		
		//user input: number of pages to copy (e.g. the number of pages in this thread). The copying always starts with page 0.
		int firstPage = ui.getFirstPage()-1;//from page 2 to 4 means 2,3,4, and not 3,4
		int lastPage = ui.getLastPage(); 
		ui.initStatusPanel(lastPage-firstPage);
		
		int currentPostNr = firstPage*POST_PER_PAGE;
		while(currentPostNr < lastPage*POST_PER_PAGE){ //pages*POST_PER_PAGE because we iterate over the number of posts and not pages.
			currentUrl = urlCut+"-s"+currentPostNr+".html";
			if(currentPostNr==0)
					currentUrl=urlNonMod;
			//first HTTP-request (GET-request) which does nothing, but telling the server to redirect this client to the page, where a password (and username?) is required. As I'm not using cookies, this has to be done in each iteration.
			HttpGet httpget = new HttpGet(currentUrl);
			httpclient.execute(httpget);
			//close this request to free the httpclient and ignore the response, as we know that it contains nothing but a request to enter the password (and username?)
			httpget.abort();
			
			//the actual request, which POSTs the password (and username?) as entity
			HttpPost httppost = new HttpPost(currentUrl);
			httppost.setEntity(entity);
			
			//the response wanted with the forums content as HTML-code via a stream
			HttpResponse response = httpclient.execute(httppost);
			InputStream is = response.getEntity().getContent();
			
			//append each new page to the StringBuilder
			builder.append(streamToString(is));
			
			httppost.abort();
			is.close();
			
			currentPostNr = currentPostNr+POST_PER_PAGE;
			ui.updateStatusPanel(currentPostNr/10-firstPage+1);// offset/10 is the number of the current page
		}
		return builder.toString();
	}
	
	/**
	 * a hack to get a String out of a InputStream
	 */
	public static String streamToString(InputStream is) throws Exception{
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF8");
		String ret =writer.toString(); 
		return ret;
	}
}
