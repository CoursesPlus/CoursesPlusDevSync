package tk.coursesplus.development.devsync;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class CoursesPlusDevSync extends JFrame implements ActionListener {
	//JPanel pane = new JPanel();
	JLabel title = new JLabel("Courses+ Dev Sync", JLabel.CENTER);
	JLabel instructions = new JLabel("<html><center>This program syncs the different folders in the Courses+ directory with each other. This is required for cross-browser development.</center></html>", JLabel.CENTER);
	JButton startBtn = new JButton("Start!");
	JTextArea log = new JTextArea();
	JScrollPane scroll;
	
	public CoursesPlusDevSync() {
		super("Courses+ Dev Sync");
		
		setBounds(100,100,600,600);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridLayout(3, 1));
	    
	    // Panel elements

	    JPanel top = new JPanel();
	    top.setLayout(new GridLayout(2, 1));

    	Font f = new Font("IDONOTEXISTHAHAHAHAHAHAHAH", Font.PLAIN, 48); // use the default font
		    title.setFont(f);
		    top.add(title);
		    
		    top.add(instructions);
		    
	    add(top);

	    JPanel middle = new JPanel();
	    middle.setLayout(new GridLayout(2, 1));
	    
		    startBtn.addActionListener(this);
		    middle.add(startBtn, BorderLayout.AFTER_LAST_LINE);
		
		add(middle);

		
			log.setFont(new Font("Monaco", Font.PLAIN, 12));
			log.setEditable(false);
	        DefaultCaret caret = (DefaultCaret) log.getCaret();
	        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			scroll = new JScrollPane(log);
		    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    add(scroll);
	    
	    // End panel elements
	    
	    setVisible(true);
	    
	    addLogEntry("Courses+ Dev Sync ready!");
	}
	
	public void actionPerformed(ActionEvent event)
	  {
	    Object source = event.getSource();
	    if (source == startBtn)
	    {
		    addLogEntry("Starting sync...");
		    JOptionPane.showMessageDialog(null,"Hello!","Important message",
		    JOptionPane.PLAIN_MESSAGE); setVisible(true);  // show something
	    }
	  }
	
	public void addLogEntry(String str) {
		this.log.append("[LOG] " + str + "\n");
	}
	
	public static void main(String[] args) {
		new CoursesPlusDevSync();
	}

}
