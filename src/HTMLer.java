import java.util.ArrayList;

/**
 * 
 * @author Niclas Scheuing
 * Just the main Method, nothing else in here.
 */
public class HTMLer {

	/**
	 * @param args these args input, you know...
	 */
	public static void main(String[] args) {
		UI ui = new UI();
		WebComm comm = new WebComm(ui);
		//some user input
		String url = ui.getURL();
		String  password = ui.getPassword();
		String answer = null;
		try {
			answer = comm.request(url, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert(answer !=null); 
		ArrayList<Post> posts = PostParser.parsePosts(answer);
		ui.closeStatusPanel();
		String threadName = ui.getThreadName();
		WordDocCreater.write(posts, threadName);
	}
}