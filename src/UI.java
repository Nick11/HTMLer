import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author Niclas Scheuing
 * Manages all the user-interactions.
 */
public class UI {
	private JFrame progressFrame;
	private JProgressBar bar;
	private JLabel label;
	private int pages;
	
	/**
	 * could probably have static methods only...
	 */
	public UI(){
	}
	
	public String getURL() {
		String url = JOptionPane.showInputDialog("Enter URL of the threads FIRST page .");
		assert(url != null);
		return url;
	}

	public String getPassword() {
		String password = JOptionPane.showInputDialog("Enter password.");
		assert(password != null);
		return password;
	}
	public String getThreadName(){
		return JOptionPane.showInputDialog("Enter thread's name. No special characters (e.g. ?, *, \", /, * and  so on)");
	}
	/**
	 * creates a small window with a statusbar, which displays the progress of the copying process.
	 * @param pages the total number of pages the be copied.
	 */
	public void initStatusPanel(int pages){
		this.pages = pages;
		progressFrame = new JFrame();
		JPanel pane = new JPanel();
		label = new JLabel("0/"+pages);
		bar = new JProgressBar(0,pages);
		bar.setValue(0);
		pane.add(bar);
		pane.add(label);
		progressFrame.setContentPane(pane);
		progressFrame.setTitle("HTMLer");
		progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		progressFrame.setMinimumSize(new Dimension(300,100));
		progressFrame.setVisible(true);
	}
	/**
	 * updates the statusbar
	 * @param page the currently copied page's number.
	 */
	public void updateStatusPanel(int page){
		bar.setValue(page);
		label.setText(page+"/"+pages);
	}
	public void closeStatusPanel(){
		progressFrame.setVisible(false);
		progressFrame.dispose();
	}

	public int getFirstPage() {
		String noPages = JOptionPane.showInputDialog("Enter number of first page.");
		return Integer.parseInt(noPages);
	}
	public int getLastPage() {
		String noPages = JOptionPane.showInputDialog("Enter number of last page.");
		return Integer.parseInt(noPages);
	}

}
