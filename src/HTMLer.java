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
		String url = "http://rpgame.forumieren.com/t186-fabula";//ui.getURL();
		String user = "Hellomania";
		String  password = "Banane";//ui.getPassword();
		String answer = null;
		try {
			answer = comm.request(url, user, password);
			//System.out.println(answer);
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
