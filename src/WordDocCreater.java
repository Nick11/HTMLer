
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
/**
 * 
 * @author Niclas Scheuing
 * Creats a new .doc file with posts as content and a given formatting.
 */
public class WordDocCreater {
	/**
	 * Creates the file <code>threadName</code>.doc in the programs folder.
	 * @param posts list of {@link Post posts} to write into the .doc file
	 */
	 public static void write(ArrayList<Post> posts, String threadName) {
	        XWPFDocument document = new XWPFDocument();
	        XWPFParagraph para;
	        XWPFRun authorRun;
	        XWPFRun textRun;
	        Scanner scan;
	       
	        XWPFParagraph titel= document.createParagraph();
	        //titel
	        XWPFRun titelRun = titel.createRun();
	        titelRun.setBold(true);
	        titelRun.setFontSize(20);
	        titelRun.setText(threadName);
	        //new paragraph for each post
	        //TODO: to change the document formatations, change the following lines
	    	for(Post post: posts){
	    		//authors name
	    		para = document.createParagraph();
	    		authorRun = para.createRun();
	    		authorRun.setItalic(true);
	    		authorRun.setText(post.getAuthor());
	    		authorRun.addBreak();
	    		//post's content resp. text
	    		textRun = para.createRun();
	    		scan = new Scanner(post.getText());
	    		scan.useDelimiter("<br />");
	    		while(scan.hasNext()){
	    			textRun= para.createRun();
	    			textRun.setText(scan.next());
	    			if(scan.hasNext())	//no break at the last line
	    				textRun.addBreak();
	    		}
	    	}
	        createFile(threadName, document);
	    }
	 /**
	  * creates a .doc file with <code>document</code> as content and <code>threadName</code> as filename
	  * @param threadName name of the file
	  * @param document the file's content
	  */
	private static void createFile(String threadName, XWPFDocument document) {
		FileOutputStream outStream = null;
		try {
		    outStream = new FileOutputStream(""+threadName+".doc");
		} catch (Exception e) {
		    e.printStackTrace();
		}
 
		try {
		    document.write(outStream);
		    outStream.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	 
}