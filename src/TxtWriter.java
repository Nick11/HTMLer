import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author Niclas Scheuing
 * A primitiv prototyp of a writer. Creates a .txt file containing the posts with a simple formatting.
 */
public class TxtWriter {

	public static void write(ArrayList<Post> posts, String threadName) throws IOException {
		FileWriter writer = new FileWriter("../"+threadName+".txt");
		BufferedWriter out = new BufferedWriter(writer);
		String nl = System.getProperty("line.separator");
		out.write(threadName);
		for(Post post: posts){
			out.write(nl+""+nl+""+post.getAuthor());
			out.write(nl+""+post.getText());
		}
		out.close();
	}

}
