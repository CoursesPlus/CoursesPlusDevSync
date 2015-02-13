package tk.coursesplus.development.devsync;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

public class CoursesPlusDevSync extends JFrame implements ActionListener {
	//JPanel pane = new JPanel();
	JLabel title = new JLabel("Courses+ Dev Sync", JLabel.CENTER);
	JLabel instructions = new JLabel("<html><center>This program syncs the different folders in the Courses+ directory with each other. This is required for cross-browser development.</center></html>", JLabel.CENTER);
	JButton startBtn = new JButton("Start!");
	public static JTextArea log = new JTextArea();
	public static WatchService watcher;
	public static Path sourceCodePath;
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
		    scroll.setBorder(new TitledBorder(new EtchedBorder (), "Logs"));
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
		    //JOptionPane.showMessageDialog(null,"Hello!","Important message",
		    //JOptionPane.PLAIN_MESSAGE); setVisible(true);  // show something
		    try {
			    String pathPlace = System.getProperty("user.home") + "/Documents/Git/CoursesPlus/CoursesPlus/";
			    File fileThing = new File(pathPlace);
			    sourceCodePath = fileThing.toPath();
			    addLogEntry("Found user's home directory: " + System.getProperty("user.home"));
			    addLogEntry("Found source code directory: " + sourceCodePath.toString());
			    if (!fileThing.isDirectory() || !fileThing.exists()) {
			    	addLogEntry("[ERROR] Source code directory is not a directory or doesn't exist!");
			    	return;
			    }
				watcher = FileSystems.getDefault().newWatchService();
				WatchKey key = sourceCodePath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
				SyncThread thread = new SyncThread();
				thread.start();
			} catch (IOException e) {
				addLogEntry("[ERROR] IOException during setup!");
				e.printStackTrace();
			}
	    }
	  }
	
	public static void addLogEntry(String str) {
		log.append("[LOG] " + str + "\n");
	}
	
	public static void main(String[] args) {
		new CoursesPlusDevSync();
	}

}

class SyncThread extends Thread {
	public void run()                       
    {            
		while (true) {
			// wait for key to be signaled
		    WatchKey key;
		    key = CoursesPlusDevSync.watcher.poll();
		    
		    if (key == null) {
		    	continue;
		    }
		    
		    for (WatchEvent<?> event: key.pollEvents()) {
		        WatchEvent.Kind<?> kind = event.kind();

		        // This key is registered only
		        // for ENTRY_CREATE events,
		        // but an OVERFLOW event can
		        // occur regardless if events
		        // are lost or discarded.
		        if (kind == StandardWatchEventKinds.OVERFLOW) {
			    	CoursesPlusDevSync.addLogEntry("Nope, overflow.");
		            continue;
		        }
		        
		        // The filename is the
		        // context of the event.
		        @SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
		        Path filename = ev.context();
		        
		        // Resolve the filename against the directory.
	            Path child = CoursesPlusDevSync.sourceCodePath.resolve(filename);
	            CoursesPlusDevSync.addLogEntry("File " + filename + " has changed!");
		    }

		    // Reset the key -- this step is critical if you want to
		    // receive further watch events.  If the key is no longer valid,
		    // the directory is inaccessible so exit the loop.
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }

		    try {
				SyncThread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}