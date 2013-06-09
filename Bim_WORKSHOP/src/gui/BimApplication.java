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
import javax.swing.JSplitPane;
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

		this.setSize(1600, 860); // Fenstergr��e
		this.setTitle("Bim Application"); // Titel
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// schlie�en hei�t schlie�en
		initMenuBar();
		initComponents(); // initialisiere Knopf
		this.setVisible(true); // sichtbares Fenster (am besten nach fertiger
								// Beschreibung!)
	}

	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			;
		// THIS! unser fenster (objekt!) | returns approve...
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

	private void initMenuBar() // men� leiste
	{
		JMenuBar jMenuBar = new JMenuBar(); // leiste
		JMenu fileMenu = new JMenu("File"); // tab
		jMenuBar.add(fileMenu);

		JMenuItem openMenuItem = new JMenuItem("Open File"); // klapp item
		openMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
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
		// button = new JButton("Oh ein Knopf");
		this.setLayout(new BorderLayout()); // nutze borderlayout als layout
											// manager

		statusPanel = new JPanel(); // statusPanel dazu
		this.add(statusPanel, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// aufteilung des fensters Vertikal
		this.add(splitPane, BorderLayout.CENTER); // ins mittlere layout
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		leftPanel = new JPanel(); // linkes panel deklariert
		//leftPanel.setBackground(Color.white); // Hintergrundfarbe
		//rightPanel = new JPanel();
		splitPane.add(tabbedPane); // tabbedpane hinzuf�gen
		//splitPane.add(rightPanel);

		//tabbedPane.add(leftPanel, "Tab 1"); // leftpanel mit titel Tab1
		tabbedPane.add(ifcObjectCountView = new IfcObjectCountView(ifcModel),
				"Fenster Count", 0);
		// 0 ist Position
	}

	public static void main(String[] args) {
		// Mainclasse
		new BimApplication();
	}

}
