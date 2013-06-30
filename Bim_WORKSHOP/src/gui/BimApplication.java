package gui;
import gui.views.IfcObjectCountView;
import ifc2x3javatoolbox.ifcmodel.IfcModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class BimApplication extends JFrame {

	JPanel leftPanel = null;
	JPanel rightPanel = null;
	JTabbedPane tabbedPane = null;
	JPanel statusPanel = null;
	IfcObjectCountView ifcObjectCountView = null;
	private IfcModel ifcModel = null;

	public BimApplication() {
		ifcModel = new IfcModel();
		// Inititalisieren des Ausgabefensters
		this.setSize(1024, 768); 
		this.setTitle("Bim Application"); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initMenuBar();
		initComponents();
		this.setVisible(true); 
	}
	// Öffnen der Datei
	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			this.setTitle(file.getAbsolutePath());
			try {
				this.ifcModel.readStepFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	// Erstellen der Menüleiste
	private void initMenuBar() 
	{
		JMenuBar jMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File"); 
		jMenuBar.add(fileMenu);

		JMenuItem openMenuItem = new JMenuItem("Open File");
		openMenuItem.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				openFile();
			}
		});
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}
		});

		fileMenu.add(exitMenuItem);

		this.setJMenuBar(jMenuBar);
	}
	
	private void initComponents() {
		this.setLayout(new BorderLayout()); 
		statusPanel = new JPanel(); 
		this.add(statusPanel, BorderLayout.SOUTH);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.add(ifcObjectCountView = new IfcObjectCountView(ifcModel),"Fenster Count", 0);
		JScrollPane scrollpane = new JScrollPane(ifcObjectCountView);
		scrollpane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollpane);
	}

	public static void main(String[] args) {
		new BimApplication();
	}

}
