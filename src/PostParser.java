import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
/**
 * 
 * @author Niclas Scheuing
 *	HTML code parser which extracts posts of the forum's HTML code.
 */

public class PostParser {
	/**
	 * empty constructor indicates, that this class could be static. Feel free to change, I'm to lazy.
	 */
	public PostParser(){	
	}
	/**
	 * Extracts the posts and its authors username out of the <code>input</code> String and returns a list of all posts.
	 * @param input String containing HTML code of one or more posts
	 * @return ArrayList<Post> containing the posts of <code>input</code>
	 */
	/*public static ArrayList<Post> parsePosts(String input){
		ArrayList<Post> posts = new ArrayList<Post>();
		
		Scanner scan = new Scanner(input);
		scan.useDelimiter("class=\"postauthor\"");
		int start = 0;
		int end = 0;
		String passage;
		String author, text;
		scan.next(); //skip first part, as this is only headers and stuff, but no posts yet.
		//search for specific patterns, which surround the needed information author and post-text.
		//modify this loop to get more information (e.g. date, avatar) for each post.
		while(scan.hasNext()){
			passage = scan.next(); 
			start = passage.indexOf('>');
			end = passage.indexOf('<');
			author = passage.substring(start+1, end);
			
			passage = passage.substring(passage.indexOf("class=\"postbody\""));
			start =passage.indexOf('>');
			end = passage.indexOf("</div>");
			text = passage.substring(start+1,end);
			text = removeHTMLCodes(text);
			if (!author.equals("Anzeige"))
				posts.add(new Post(author, text));
		}
		scan.close();
		return posts;
	}*/
	
	public static ArrayList<Post> parsePosts(String input){
		ArrayList<Post> posts = new ArrayList<Post>();
		String authPre = "<span class=\"name\"";
		String authPost = "<span class=\"postdetails poster-profile\"";
		String authMiddle = "(.+?strong>){2}(.+?)(</strong.+?){2}";
		
		String postPre = "<div class=\"postbody\"><div>";
		String postPost = "</div><div class=\"clear\"></div>";
		String postMiddle = "(.+?)";
		
		Pattern authorPat = Pattern.compile(authPre+authMiddle+authPost);
		Pattern postPat = Pattern.compile(postPre+postMiddle+postPost);

		
		Matcher authorMatcher = authorPat.matcher(input);
		Matcher postMatcher = postPat.matcher(input);
		
		/*authorMatcher.find();
		postMatcher.find();
		
		System.out.println(authorMatcher.group(2));
		System.out.println(postMatcher.group(1));*/
	
		String author, text;
		while(authorMatcher.find() && postMatcher.find()){
			author = authorMatcher.group(2);
			text = postMatcher.group(1);
			text = removeHTMLCodes(text);
			posts.add(new Post(author, text));
		}
		return posts;
	}
	
	/**
	 * Transforms HTML code into readable plaintext. 
	 * @param str HTML-String
	 * @return String hopefully not containing any HTML-code snippets anymore. Some tags are removed manually, so check your text for missed HTML-tags.
	 */
	private static String removeHTMLCodes(String str){
		//replaces HTML code for special characters (e.g. 'ä') with the corresponding plain text character.
		str = StringEscapeUtils.unescapeHtml4(str);
		// manually remove some codes (e.g. bold, italic font. Or similies)
		str = str.replaceAll("<em>", "");
		str = str.replaceAll("</em>", "");
		str = str.replaceAll("<span style=\"text-decoration: underline\">", "");
		str = str.replaceAll("</span>", "");
		str = str.replaceAll("<img src=\"./images/smilies/icon_e_biggrin.gif\" alt=\":D\" title=\"Very Happy\" />","");
		return str;
	}
}
