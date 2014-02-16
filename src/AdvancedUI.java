import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;


public class AdvancedUI extends JFrame {
	
	
	private JTextField urlField;
	private JTextField usernameField;
	private JTextField passwordField;
	private JTextField titleField;
	private HTMLer htmler;
	private JProgressBar bar;
	private JLabel progressLabel;
	private JTextField startField;
	private JTextField endField;
	private JPanel mainPanel;
	private int pages = 1;
	private JButton ok;
	private Component cancel;
	private JFrame progressFrame;

	public AdvancedUI(HTMLer htmler){
		super();
		this.htmler = htmler;
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		this.setLocationRelativeTo(null); 
		init();
	}
	
	private void init(){
		this.setAutoRequestFocus(true);
		this.setMinimumSize(new Dimension(600,100));
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(8,2));
		
		
		urlField = new JTextField();
		usernameField = new JTextField();
		passwordField = new JTextField();
		titleField = new JTextField();
		startField= new JTextField();
		endField = new JTextField();
		
		ok = new JButton("ok");
		ok.addMouseListener(new MouseListener(){

			@Override
			public void mouseReleased(MouseEvent e){
				if(checkFieldsComplete()){
					hideButtons();
					setProgressComponents();
					readFields();
				}
			}
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
		});
		
		cancel = new JButton("cancel");
		cancel.addMouseListener(new MouseListener(){

			@Override
			public void mouseReleased(MouseEvent e){
				System.exit(NORMAL);
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
		});
		
		progressLabel = new JLabel("0/"+"?");
		bar = new JProgressBar(0,1);
		bar.setValue(0);
		
		
		mainPanel.add(new JLabel("URL (has to start with http:"));
		mainPanel.add(urlField);
		mainPanel.add(new JLabel("username (only if login required)"));
		mainPanel.add(usernameField);
		mainPanel.add(new JLabel("password (only if login required)"));
		mainPanel.add(passwordField);
		mainPanel.add(new JLabel("Thread title"));
		mainPanel.add(titleField);
		mainPanel.add(new JLabel("start page"));
		mainPanel.add(startField);
		mainPanel.add(new JLabel("end page"));
		mainPanel.add(endField);
		
		mainPanel.add(ok);
		mainPanel.add(cancel);
		
		this.setContentPane(mainPanel);
		this.pack();
	}
	
	private void hideButtons(){
		ok.setVisible(false);
		cancel.setVisible(false);
	}
	
	public void updateStatusPanel(int page){
		bar.setValue(page);
		progressLabel.setText(page+"/"+pages );
	}
	
	private void setProgressComponents() {
		int start = Integer.parseInt(this.startField.getText());
		int end = Integer.parseInt(this.endField.getText());
		this.pages = end-start+1;
		
		progressFrame = new JFrame();
		JPanel pane = new JPanel();
		pane.add(bar);
		pane.add(progressLabel);
		progressFrame.setContentPane(pane);
		progressFrame.setTitle("HTMLer");
		progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		progressFrame.setMinimumSize(new Dimension(300,100));
		progressFrame.setVisible(true);
		
		updateStatusPanel(0);
	}
	
	private void readFields(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("url", this.urlField.getText());
		map.put("username", this.usernameField.getText());
		map.put("password", this.passwordField.getText());
		map.put("title", this.titleField.getText());
		map.put("start", startField.getText());
		map.put("end", endField.getText());
		htmler.process(map);
		
	}
	private boolean checkFieldsComplete(){
		return	!this.urlField.getText().equals("") 
				&& this.urlField.getText().startsWith("http://") 
				&& !this.startField.getText().equals("")
				&& !this.endField.getText().equals("");
	}
	
	public void done(){
		System.exit(NORMAL);
	}
}
