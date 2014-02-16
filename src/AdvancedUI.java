import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AdvancedUI extends JFrame {
	
	
	private JTextField urlField;
	private JTextField usernameField;
	private JTextField passwordField;
	private JTextField titleField;

	public AdvancedUI(){
		super();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		this.setLocationRelativeTo(null); 
		init();
	}
	
	public HashMap<String,String> getInfo(){
		
	}
	private void init(){
		this.setAutoRequestFocus(true);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(6,2));
		
		
		urlField = new JTextField();
		usernameField = new JTextField();
		passwordField = new JTextField();
		titleField = new JTextField();
		
		JButton ok = new JButton("ok");
		ok.addMouseListener(new MouseListener(){

			@Override
			public void mouseReleased(MouseEvent e){
				readFields();
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
		
		JButton cancel = new JButton("cancel");
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
		
		mainPanel.add(new JLabel("URL"));
		mainPanel.add(urlField);
		mainPanel.add(new JLabel("username (only if login required)"));
		mainPanel.add(usernameField);
		mainPanel.add(new JLabel("password (only if login required)"));
		mainPanel.add(passwordField);
		mainPanel.add(new JLabel("Thread title"));
		mainPanel.add(titleField);
		mainPanel.add(ok);
		mainPanel.add(cancel);
		
		
		this.setContentPane(mainPanel);
		this.pack();
	}
	
	private void readFields(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("url", this.urlField.getText());
		map.put("username", this.usernameField.getText());
		map.put("password", this.passwordField.getText());
		map.put("title", this.titleField.getText());
		re
	}
	
	public static void main(String[] args) {
		new AdvancedUI();
	}
}
