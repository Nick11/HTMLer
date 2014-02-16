import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Niclas Scheuing
 * Just the main Method, nothing else in here.
 */
public class HTMLer {
	private static AdvancedUI ui;
	private HashMap<String,String> info;
	/**
	 * @param args these args input, you know...
	 */
	public static void main(String[] args) {
		HTMLer htmler = new HTMLer();
		ui = new AdvancedUI(htmler);
	}
	
	public void process(HashMap<String,String> info){
		WebComm comm = new WebComm();
		String answer = null;
		try {
			int start = Integer.parseInt(info.get("start"));
			int end = Integer.parseInt(info.get("end"));
			answer = comm.request(info.get("url"), info.get("username"), info.get("password"), start, end, this.ui);
			//System.out.println(answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert(answer !=null); 
		ArrayList<Post> posts = PostParser.parsePosts(answer);
		WordDocCreater.write(posts, info.get("title"));
		ui.done();
	}
}
