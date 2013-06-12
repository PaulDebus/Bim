package gui.views;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis1Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDecomposes;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSite;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc2x3javatoolbox.ifc2x3tc1.SET;
import ifc2x3javatoolbox.ifcmodel.IfcModel;
import ifc2x3javatoolbox.ifcmodel.IfcModelListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import MVC.Fenster;
import gui.views.StoreySort;

public class IfcObjectCountView extends JPanel implements IfcModelListener {

	private IfcModel ifcModel = null;
	private JTextArea objectCountTextArea = null;
	TableModel dataModel = new AbstractTableModel() {
        public int getColumnCount() { return 10; }
        public int getRowCount() { return 10;}
        public Object getValueAt(int row, int col) { return new Integer(row+col);}
    };
	private JTable bauteilTable =  new JTable(dataModel);

	public IfcObjectCountView(IfcModel ifcModel) {

		this.ifcModel = ifcModel;
		this.ifcModel.addIfcModelListener(this);
		objectCountTextArea = new JTextArea("");
		objectCountTextArea.setEditable(false);
		this.setLayout(new BorderLayout());
		this.add(objectCountTextArea, BorderLayout.CENTER);
		this.add(objectCountTextArea);
		this.add(bauteilTable,BorderLayout.SOUTH);
		
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
				
				
				String namenGeschosse = new String();
				
				
				List<IfcBuildingStorey> geschossListe = new ArrayList<>(anzahlGeschosse);
				Comparator<IfcBuildingStorey> compareStorey = new StoreySort();
				Collections.sort(geschossListe, compareStorey);

				for (IfcBuildingStorey akt : geschossListe)
					namenGeschosse += akt.getName() + ", ";
				
				Meldung += " \n Namen der Geschosse: " + namenGeschosse;
				Meldung += " \n ifcProject Decomposed: ";
				try {
					IfcProject ifcProject = ifcModel.getIfcProject();
					SET<IfcRelDecomposes> ifcObjectDefinition = ifcProject.getIsDecomposedBy_Inverse();
					IfcSite site = (IfcSite) ifcObjectDefinition.iterator().next().getRelatedObjects().iterator().next();
					SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();

					
					String objectliste = new String();
					for (IfcRelDecomposes object : buildingList)
					{
						IfcBuilding building = (IfcBuilding) object.getRelatedObjects().iterator().next();
						objectliste += building.getName();
					}
					
					Meldung += objectliste;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int i = 0;
				for(IfcBuildingStorey aktGeschoss : geschossListe)
				{
					i++;
					bauteilTable.setValueAt(aktGeschoss.getName(), i, 1);
					bauteilTable.setValueAt(aktGeschoss.getDescription() , i, 2);
					bauteilTable.setValueAt(aktGeschoss.getElevation(), i, 3);
					dataModel.setValueAt(aktGeschoss.getElevation(), i, 3);
					//Meldung += " \n" + i;

				}
				dataModel.setValueAt("blablabla", 8, 8);
				bauteilTable.setValueAt("ahge�whkaewjg", 9, 9);
				bauteilTable.repaint();
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
