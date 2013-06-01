package gui.views;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis1Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc2x3javatoolbox.ifcmodel.IfcModel;
import ifc2x3javatoolbox.ifcmodel.IfcModelListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import MVC.Fenster;

public class IfcObjectCountView extends JPanel implements IfcModelListener {

	private IfcModel ifcModel = null;
	private JTextArea objectCountTextArea = null;

	public IfcObjectCountView(IfcModel ifcModel) {

		this.ifcModel = ifcModel;
		this.ifcModel.addIfcModelListener(this);
		objectCountTextArea = new JTextArea("");
		objectCountTextArea.setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(objectCountTextArea, BorderLayout.CENTER);
		this.add(objectCountTextArea);
		updateView();
	}

	private void updateView() {

		try {
			if (ifcModel.getCollection(IfcWindow.class) != null) {
				int anzahlFenster = ifcModel.getCollection(IfcWindow.class).size();
				String Meldung = "Anzahl der Fenster: " + anzahlFenster;
				int anzahlTueren = ifcModel.getCollection(IfcDoor.class).size();
				Meldung += " \n Anzahl der Türen: " + anzahlTueren;
				int anzahlWaende = ifcModel.getCollection(IfcWall.class).size();
				Meldung += " \n Anzahl der Wände: " + anzahlWaende;
				int anzahlGebaeude = ifcModel.getCollection(IfcBuilding.class).size();
				Meldung += " \n Anzahl der Gebäude: " + anzahlGebaeude;
				Collection<IfcBuildingStorey> anzahlGeschosse = ifcModel.getCollection(IfcBuildingStorey.class);
				Meldung += " \n Anzahl der Stockwerke: " + anzahlGeschosse;
				objectCountTextArea.setText(Meldung);
			objectCountTextArea.repaint();
		}} 
			catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void modelContentChanged() {
		// TODO Auto-generated method stub
		updateView();
		
	}

	@Override
	public void modelObjectAdded(ClassInterface object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelObjectRemoved(ClassInterface object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelObjectsAdded(Collection<ClassInterface> objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modelObjectsRemoved(Collection<ClassInterface> objects) {
		// TODO Auto-generated method stub
		
	}

}
