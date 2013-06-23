package gui.views;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis1Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLabel;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSetUsage;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialSelect;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociates;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociatesMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDecomposes;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefines;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSite;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpace;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWallStandardCase;
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
				
				
				String namenGeschosse = new String();
				
				
				List<IfcBuildingStorey> geschossListe = new ArrayList<>(anzahlGeschosse);
				Comparator<IfcBuildingStorey> compareStorey = new StoreySort();
				Collections.sort(geschossListe, compareStorey);

				for (IfcBuildingStorey akt : geschossListe)
					namenGeschosse += akt.getName() + ", ";
				
				Meldung += " \n Namen der Geschosse: " + namenGeschosse;
				Meldung += " \n Gel�nde: ";
				try {
					IfcProject ifcProject = ifcModel.getIfcProject();
					SET<IfcRelDecomposes> ifcObjectDefinition = ifcProject.getIsDecomposedBy_Inverse();
					for (IfcRelDecomposes siteDecomp : ifcObjectDefinition)
					{
						IfcSite site = (IfcSite) ifcObjectDefinition.iterator().next().getRelatedObjects().iterator().next();
						Meldung += site.getName();
						SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();
						for (IfcRelDecomposes buildingIt : buildingList)
						{
							IfcBuilding building = (IfcBuilding) buildingIt.getRelatedObjects().iterator().next();
							Meldung += "\n Gebäude: " + building.getName();
							
							SET<IfcObjectDefinition> storeyList = building.getIsDecomposedBy_Inverse().iterator().next().getRelatedObjects();
							
							for (IfcObjectDefinition storeyDefinition : storeyList)
							{
								IfcBuildingStorey storey = (IfcBuildingStorey) storeyDefinition;
								Meldung += "\n Geschoss: " + storey.getName() + "\n";
								
																
								SET<IfcProduct> containedInStoreyList = storey.getContainsElements_Inverse().iterator().next().getRelatedElements();
								
								SET<IfcWallStandardCase> wallList = null;
								SET<IfcWindow> windowList = null;
								SET<IfcDoor> doorList = null;
								
								for (IfcProduct containedInStorey : containedInStoreyList)
								{
									
									switch (containedInStorey.getClass().getName()) 
									{
										case "ifc2x3javatoolbox.ifc2x3tc1.IfcWallStandardCase": 
											{
												IfcWallStandardCase wall = (IfcWallStandardCase) containedInStorey;
//												wallList.add(wall);
												IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
												IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
												IfcLabel material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial().getName();
												Meldung += "\n Switch Wall" + wall.getName() + material;
												break;
											}
										default: Meldung += "\n nicht in switch" + containedInStorey.getClass().getName();
									}
									
									
									
//									IfcSpace spatialStructure = (IfcSpace) containedInStoreyList;
//									Meldung += ", Space: " + space.getName();								
//								
//									SET<IfcObject> wallDefinition = space.getIsDefinedBy_Inverse().iterator().next().getRelatedObjects();
//							
//									for (IfcObject wallList : wallDefinition)
//									{
//										IfcWall wall = (IfcWall) wallList;
//										Meldung += "\n Wand: " + wall.getName();	
//									}
								}
								
//								SET<IfcObjectDefinition> spaceDefinition = storey.getIsDecomposedBy_Inverse().iterator().next().getRelatedObjects();
//								
//								for (IfcObjectDefinition spaceList : spaceDefinition)
//								{
//									IfcSpace space = (IfcSpace) spaceList;
//									Meldung += ", Space: " + space.getName();								
//								
//									SET<IfcObject> wallDefinition = space.getIsDefinedBy_Inverse().iterator().next().getRelatedObjects();
//							
//									for (IfcObject wallList : wallDefinition)
//									{
//										IfcWall wall = (IfcWall) wallList;
//										Meldung += "\n Wand: " + wall.getName();	
//									}
//								}
							}
							
							
//							Meldung += "\n kont�ner: " + storeyContainer.iterator().next().getName() + storeyContainer.size();
//							for (IfcRelDecomposes storeyList : storeyContainer)
//							{
//								//for (IfcBuildingStorey storey : storeyList.getRelatedObjects())
//								Meldung += "\n StoreyContainer: " + storeyList.getRelatedObjects().iterator().next().getName();
//								
//							}
							/*for (int i=0;  i< storeyList.size();i++)
							{
								Meldung += "\n " + storeyList.iterator().next().getName();
								//IfcBuildingStorey storey = (IfcBuildingStorey) storeyList.iterator().next().getRelatedObjects();
								//Meldung += "\n Stockwerk: " + storey.getName();
							}*/
						}
						
						
					}
					/*SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();

					
					String objectliste = new String();
					for (IfcRelDecomposes object : buildingList)
					{
						IfcBuilding building = (IfcBuilding) object.getRelatedObjects().iterator().next();
						objectliste += building.getName();
					}
					
					Meldung += objectliste;
			*/	} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
