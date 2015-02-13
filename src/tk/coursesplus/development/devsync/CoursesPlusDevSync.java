package tk.coursesplus.development.devsync;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class CoursesPlusDevSync extends JFrame implements ActionListener {
	public static final String VERSION = "0.1";
	
	JLabel title = new JLabel("Courses+ Dev Sync", JLabel.CENTER);
	JLabel instructions = new JLabel("<html><center>This program syncs the different folders in the Courses+ directory with each other. This is required for cross-browser development.</center></html>", JLabel.CENTER);
	JButton startBtn = new JButton("Start!");
	JButton clearBrowserSupportBtn = new JButton("Clear destination dirs");
	JButton forceSyncBtn = new JButton("Force sync");
	public static JTextArea log = new JTextArea();
	public static WatchService watcher;
	public static Path sourceCodePath;
	JScrollPane scroll;
	SyncThread thread;
	File fileThing;
	
	public static String[] folders = { "chosen", "css", "etc", "fonts", "images", "js", "scss_gen" };
    public static String[] browsersupportfolders = { "Chrome", "CoursesPlus.safariextension", "Firefox" };
	
	public CoursesPlusDevSync() {
		super("Courses+ Dev Sync v" + VERSION);
		
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
	    
	    	startBtn.setFont(new Font("IDONOTEXISTHAHAHAHAHAHAHAH", Font.PLAIN, 24));
		    startBtn.addActionListener(this);
		    middle.add(startBtn);
		    
		    JPanel buttonTwo = new JPanel();
			    buttonTwo.setLayout(new GridLayout(1, 2));
			    
			    clearBrowserSupportBtn.setFont(new Font("IDONOTEXISTHAHAHAHAHAHAHAH", Font.PLAIN, 16));
			    clearBrowserSupportBtn.addActionListener(this);
			    buttonTwo.add(clearBrowserSupportBtn);
			    
			    forceSyncBtn.setFont(new Font("IDONOTEXISTHAHAHAHAHAHAHAH", Font.PLAIN, 16));
			    forceSyncBtn.addActionListener(this);
			    buttonTwo.add(forceSyncBtn);
			    
		    middle.add(buttonTwo);
			    
		    middle.setBorder(new TitledBorder(new EtchedBorder (), "Controls"));
		    
		    
		add(middle);
		
		
			log.setFont(new Font("Monaco", Font.PLAIN, 12));
			log.setEditable(false);
	        DefaultCaret caret = (DefaultCaret) log.getCaret();
	        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			scroll = new JScrollPane(log);
		    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		    scroll.setBorder(new TitledBorder(new EtchedBorder (), "Log"));
	    add(scroll);
	    
	    // End panel elements
	    
	    setVisible(true);
	    
	    addLogEntry("Courses+ Dev Sync version " + VERSION + " ready!");
	    
	    String osname = System.getProperty("os.name");
	    addLogEntry("Checking OS - it is " + osname + "...");
	    if (!osname.startsWith("Mac OS X")) {
	    	addLogEntry("Unsupported OS - " + osname + "!");
	    	JOptionPane.showMessageDialog(null, "Your operating system, " + osname + ", is not currently supported by this tool.\nCurrently, it's only been tested in Mac OS X.\nBad things probably will happen in other operating systems, but who knows?", "Unsupported operating system", JOptionPane.WARNING_MESSAGE);
	    }

	    String pathPlace = System.getProperty("user.home") + "/Documents/Git/CoursesPlus/CoursesPlus/";
	    fileThing = new File(pathPlace);
	    sourceCodePath = fileThing.toPath();
	    addLogEntry("Found user's home directory: " + System.getProperty("user.home"));
	    addLogEntry("Found source code directory: " + sourceCodePath.toString());
	}
	
	public void actionPerformed(ActionEvent event)
	{
	    Object source = event.getSource();
	    if (source == startBtn)
	    {
	    	if (startBtn.getText() == "Stop!") {
	    		// We should stop it!
	    		addLogEntry("Stopping watcher...");
	    		thread.stop();
	    		startBtn.setText("Start!");
	    		return;
	    	}
		    addLogEntry("Starting watcher...");
		    try {
			    if (!fileThing.isDirectory() || !fileThing.exists()) {
			    	addLogEntry("[ERROR] Source code directory is not a directory or doesn't exist!");
			    	return;
			    }
				watcher = FileSystems.getDefault().newWatchService();
				sourceCodePath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
				thread = new SyncThread();
				thread.start();
			    startBtn.setText("Stop!");
			} catch (IOException e) {
				addLogEntry("[ERROR] IOException during setup!");
				e.printStackTrace();
			}
	    }
	    if (source == clearBrowserSupportBtn) {
	    	if (JOptionPane.showConfirmDialog (null, "This will clear the browsersupport directories. You'll have to resync the folders to get the extensions to work in browsers again.", "Are you sure?", JOptionPane.YES_OPTION) == JOptionPane.OK_OPTION) {
	    		addLogEntry("Clearing browsersupport folders...");
	    		for (String browsersupportfolder : browsersupportfolders) {
	    			for (String folder : folders) {
	    				File deleteThis = new File(sourceCodePath.toString() + "/browsersupport/" + browsersupportfolder + "/" + folder);
	    				addLogEntry("Deleting " + deleteThis.getPath() + "...");
	    				if (!deleteThis.exists() || !deleteThis.isDirectory()) {
	    					addLogEntry("[WARN] " + folder + " does not exist or is not a directory, skipping...");
	    					continue;
	    				}
	    				purgeDirectory(deleteThis);
	    				deleteThis.delete();
	    			}
	    		}
	    	}
	    }
	    if (source == forceSyncBtn) {
	    	addLogEntry("Forcing sync...");
	    	syncFolders();
	    }
	  }
	
	void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory()) purgeDirectory(file);
	        file.delete();
	    }
	}
	
	public static void addLogEntry(String str) {
		log.append("[LOG] " + str + "\n");
		System.out.println("[LOG] " + str);
	}
	
	public static void main(String[] args) {
		new CoursesPlusDevSync();
	}

	public static void syncFolders() {
		try {
        	for (String browsersupportfolder : browsersupportfolders) {
        		String browserSupportPath = "browsersupport/" + browsersupportfolder;
            	for (String folder : folders) {
		            Process p = Runtime.getRuntime().exec("rsync -vur " + sourceCodePath.toString() + "/" + folder + " " + CoursesPlusDevSync.sourceCodePath.toString() + "/" + browserSupportPath );
		             
		            BufferedReader stdInput = new BufferedReader(new
		                 InputStreamReader(p.getInputStream()));
		            
		            // read the output from the command
		            String s;
		            while ((s = stdInput.readLine()) != null) {
		            	addLogEntry("[PROCESS] " + s);
		            }
            	}
        	}
        } catch (IOException x) {
        	addLogEntry("[ERROR] Process IOException! :(");
        }
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
	            
	            // run rsync and friends
	            CoursesPlusDevSync.syncFolders();
	             
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