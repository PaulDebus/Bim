package gui.views;

// Einbindung aller benötigten Klassen
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAnnotation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAxis1Placement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoorStyle;
import ifc2x3javatoolbox.ifc2x3tc1.IfcElementQuantity;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLabel;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSetUsage;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialSelect;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPhysicalQuantity;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPositiveLengthMeasure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProperty;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPropertySetDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcQuantityVolume;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociates;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociatesMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDecomposes;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefines;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByProperties;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByType;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSite;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSlab;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpace;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpatialStructureElement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcStair;
import ifc2x3javatoolbox.ifc2x3tc1.IfcStairTypeEnum;
import ifc2x3javatoolbox.ifc2x3tc1.IfcTypeObject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcVolumeMeasure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWall;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWallStandardCase;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindowStyle;
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

// Klasse IfcObjectCountView, Unterklasse von JPanel, implementiert IfcModelListener
public class IfcObjectCountView extends JPanel implements IfcModelListener {
	
	// Initialisierung
	private IfcModel ifcModel = null;
	private JTextArea objectCountTextArea = null;
	
	// Konstruktor
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
				String Meldung = new String();
//				int anzahlFenster = ifcModel.getCollection(IfcWindow.class).size();
//				String Meldung = "Anzahl der Fenster: " + anzahlFenster;
//				int anzahlTueren = ifcModel.getCollection(IfcDoor.class).size();
//				Meldung += " \n Anzahl der Türen: " + anzahlTueren;
//				int anzahlWaende = ifcModel.getCollection(IfcWall.class).size();
//				Meldung += " \n Anzahl der Wände: " + anzahlWaende;
//				int anzahlGebaeude = ifcModel.getCollection(IfcBuilding.class).size();
//				Meldung += " \n Anzahl der Gebäude: " + anzahlGebaeude;
//				Collection<IfcBuildingStorey> anzahlGeschosse = ifcModel.getCollection(IfcBuildingStorey.class);
//				Meldung += " \n Anzahl der Stockwerke: " + anzahlGeschosse;
//				
//				String namenGeschosse = new String();
//				
//				
//				List<IfcBuildingStorey> geschossListe = new ArrayList<>(anzahlGeschosse);
//				Comparator<IfcBuildingStorey> compareStorey = new StoreySort();
//				Collections.sort(geschossListe, compareStorey);
//
//				for (IfcBuildingStorey akt : geschossListe)
//					namenGeschosse += akt.getName() + ", ";
//				
//				Meldung += " \n Namen der Geschosse: " + namenGeschosse;
//				Meldung += " \n Gel�nde: ";
				try {
					// Initialisieren des Projektes aus dem Model
					IfcProject ifcProject = ifcModel.getIfcProject();
					// Speichern aller Unterwerte von ifcProject in Set siteList
					SET<IfcRelDecomposes> siteList = ifcProject.getIsDecomposedBy_Inverse();
					//Schleife über das Set siteList, Initialisierung der einzelnen Elemente als Variable siteIt
					for (IfcRelDecomposes siteIt : siteList)
					{
						// Casten von IfcRelDecomposes zu IfcSite, Auslesen der Gelände innerhalb des Projektes, 
						// Speichern in Variable site
						IfcSite site = (IfcSite) siteIt.getRelatedObjects().iterator().next();
						Meldung += site.getName();
						
						// Speichern aller Unterwerte des aktuellen Geländes in ein Set
						SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();
						//Schleife über das Set buildingList, Initialisierung der einzelnen Elemente als Variable buildingIt
						for (IfcRelDecomposes buildingIt : buildingList)
						{
							// Casten, Auslesen der Gebäude auf dem Gelände, Speichern in Variable building
							IfcBuilding building = (IfcBuilding) buildingIt.getRelatedObjects().iterator().next();
							Meldung += "\n Gebäude: " + building.getName();
							
							// Speichern aller Unterwerte des aktuellen Gebäudes in ein Set
							// get related objects?
							SET<IfcObjectDefinition> storeyList = building.getIsDecomposedBy_Inverse().iterator().next().getRelatedObjects();
							List<IfcBuildingStorey> listedStoreys = new ArrayList<>();
							//Schleife über das Set storeyList, Initialisierung der einzelnen Elemente als Variable storeyDefinition
							for (IfcObjectDefinition storeyDefinition : storeyList)
							{
								// Casten von IfcObjectDefinition zu IfcBuildingStorey, speichern des aktuellen Stockwerkes
								listedStoreys.add((IfcBuildingStorey) storeyDefinition);
							}

							Comparator<IfcBuildingStorey> storeyCompare = new StoreySort();
							Collections.sort(listedStoreys, storeyCompare);
							for (IfcObjectDefinition storey : listedStoreys)
							{
								Meldung += "\n \n \n Geschoss: " + storey.getName() + "\n \n \n";
								// Speichern aller Unterwerte des aktuellen Stockwerkes in Set containedInStoreyList
								SET<IfcProduct> containedInStoreyList = ((IfcSpatialStructureElement) storey).getContainsElements_Inverse().iterator().next().getRelatedElements();
								
								// Instanziieren von Sets für die einzelnen Bauteile
								SET<IfcWallStandardCase> wallList = null;
								SET<IfcWindow> windowList = null;
								SET<IfcDoor> doorList = null;
								
								//Schleife über das Set containedInStorey, Initialisierung der einzelnen Elemente als Variable containedInStoreyList
								for (IfcProduct containedInStorey : containedInStoreyList)
								{
									// Unterscheidung der einzelnen Bauteile über den Namen ihrer Klasse mittels eines switch
									if (containedInStorey instanceof IfcWallStandardCase)
									{
										// falls es sich um eine Wand handelt
										{
												// Casten in IfcWallStandardCase 
												IfcWallStandardCase wall = (IfcWallStandardCase) containedInStorey;
												// Abfragen des Materials: da es sich um eine Wand handelt, wird IfcMaterialLayerSetUsage verwendet
												// Speichern und Casten von IfcRelAssociatesMaterial, IfcMaterialLayerSetUsage, IfcMaterial
												IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
												IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
												IfcMaterial material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
												Meldung += "\n Material: " + material.getName();
												
												// Speichern aller Definitionen der aktuellen Wand in das Set relDefinesList
												SET<IfcRelDefines> relDefinesList = wall.getIsDefinedBy_Inverse();
												//Schleife über das Set relDefinesList, Initialisierung der einzelnen Elemente als Variable relDefinesIt
												for (IfcRelDefines relDefinesIt : relDefinesList)
												{
													// Bedingung: nur Elemente, die als IfcRelDefinesByProperties instanziiert sind, werden weiterverwendet
													if (relDefinesIt instanceof IfcRelDefinesByProperties)
													{
														// Casten zu IfcRelDefinesByProperties
														IfcRelDefinesByProperties relDefines = (IfcRelDefinesByProperties) relDefinesIt;
														
														// Bedingung: nur Elemente, die IfcElementQuantity enthalten, werden weiterverwendet
														if (relDefines.getRelatingPropertyDefinition() instanceof IfcElementQuantity )
														{
															// Casten zu IfcElementQuantity
															IfcElementQuantity elementQuantity = (IfcElementQuantity) relDefines.getRelatingPropertyDefinition();
															// Speichern aller IfcPhysicalQuantities der aktuellen Wand in das Set physQuantity
															SET<IfcPhysicalQuantity> physQuantity = elementQuantity.getQuantities();
															
															//Schleife über das Set physQuantity, Initialisierung der einzelnen Elemente als Variable volume
															for (IfcPhysicalQuantity volume : physQuantity)
																// Auswahl der Werte, die ein Volumen speichern
																if(volume instanceof IfcQuantityVolume)
																{
																	// Casten zu IfcQuantityVolume
																	IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
																	Meldung += "\n" + volume.getName() + ": " + volumeValue.getVolumeValue();
																}
															}
														}
													}
												}
									}
											
									else if (containedInStorey instanceof IfcWindow)
									{
										IfcWindow window = (IfcWindow) containedInStorey;
										
										Iterator<IfcRelDefines> iterator = window.getIsDefinedBy_Inverse().iterator();
										while(iterator.hasNext())
										{
											IfcRelDefines ifcRelDefines = iterator.next();
											if(ifcRelDefines instanceof IfcRelDefinesByType)
											{
												IfcRelDefinesByType definesByType = (IfcRelDefinesByType)ifcRelDefines;
												IfcWindowStyle ifcWindowStyle = (IfcWindowStyle)definesByType.getRelatingType();
												Meldung += "\n Material des Fensters: " + ifcWindowStyle.getConstructionType().value;
											}
										}
									}

//									else if (containedInStorey instanceof IfcStair)
//									{
//										IfcStair stair = (IfcStair) containedInStorey;
//										
//										Iterator<IfcRelAssociatesMaterial> iterator = stair.getHasAssociations_Inverse()
//										while(iterator.hasNext())
//										{
//											IfcRelDefines ifcRelDefines = iterator.next();
//											if(ifcRelDefines instanceof IfcRelDefinesByType)
//											{
//												IfcRelDefinesByType definesByType = (IfcRelDefinesByType)ifcRelDefines;
//												IfcStair ifcWindowStyle = (IfcWindowStyle)definesByType.getRelatingType();
//												Meldung += "\n Material des Fensters: " + ifcWindowStyle.getConstructionType().value;
//											}
//										}
//									}
//									
									else if (containedInStorey instanceof IfcStair)
									{
										IfcStair stair = (IfcStair) containedInStorey;									
									
									
									IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) stair.getHasAssociations_Inverse().iterator().next();
	
									IfcMaterial material = (IfcMaterial) materialAsso.getRelatingMaterial();
									
									IfcStairTypeEnum type = stair.getShapeType();
									
									
									Meldung += "\n Treppe Material: " + material.getName() + "Typ: " + type.value;
									}
									
//									// Speichern aller Definitionen der aktuellen Wand in das Set relDefinesList
//									SET<IfcRelDefines> relDefinesList = wall.getIsDefinedBy_Inverse();
//									//Schleife über das Set relDefinesList, Initialisierung der einzelnen Elemente als Variable relDefinesIt
//									for (IfcRelDefines relDefinesIt : relDefinesList)
//									{
//										// Bedingung: nur Elemente, die als IfcRelDefinesByProperties instanziiert sind, werden weiterverwendet
//										if (relDefinesIt instanceof IfcRelDefinesByProperties)
//										{
//											// Casten zu IfcRelDefinesByProperties
//											IfcRelDefinesByProperties relDefines = (IfcRelDefinesByProperties) relDefinesIt;
//											
//											// Bedingung: nur Elemente, die IfcElementQuantity enthalten, werden weiterverwendet
//											if (relDefines.getRelatingPropertyDefinition() instanceof IfcElementQuantity )
//											{
//												// Casten zu IfcElementQuantity
//												IfcElementQuantity elementQuantity = (IfcElementQuantity) relDefines.getRelatingPropertyDefinition();
//												// Speichern aller IfcPhysicalQuantities der aktuellen Wand in das Set physQuantity
//												SET<IfcPhysicalQuantity> physQuantity = elementQuantity.getQuantities();
//												
//												//Schleife über das Set physQuantity, Initialisierung der einzelnen Elemente als Variable volume
//												for (IfcPhysicalQuantity volume : physQuantity)
//													// Auswahl der Werte, die ein Volumen speichern
//													if(volume instanceof IfcQuantityVolume)
//													{
//														// Casten zu IfcQuantityVolume
//														IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
//														Meldung += "\n" + volume.getName() + ": " + volumeValue.getVolumeValue();
//													}
//												}
//											}
//										}
//									}
//									
									
									else if (containedInStorey instanceof IfcWindow)
									{
										IfcWindow window = (IfcWindow) containedInStorey;
										
										IfcPositiveLengthMeasure width = window.getOverallHeight();
										IfcPositiveLengthMeasure hight = window.getOverallHeight();
										Meldung += "\n \n hä \n \n";
										Meldung += "\n Höhe: " + hight.toString() + "Breite: " + width.value;
										
										
										Iterator<IfcRelDefines> iterator = window.getIsDefinedBy_Inverse().iterator();
										while(iterator.hasNext())
										{
											Meldung  += "\n \n hä2 \n \n";
											IfcRelDefines ifcRelDefines = iterator.next();
											if(ifcRelDefines instanceof IfcRelDefinesByType)
											{
												Meldung  += "\n \n hä⁵ \n \n";
												IfcRelDefinesByType definesByType = (IfcRelDefinesByType)ifcRelDefines;
												IfcWindowStyle ifcWindowStyle = (IfcWindowStyle)definesByType.getRelatingType();
												Meldung += "blabla Material des Fensters: " + ifcWindowStyle.getConstructionType().value;
											}
											
										}
										
									}
												
										else if (containedInStorey instanceof IfcDoor)
										{
												IfcDoor door = (IfcDoor) containedInStorey;
												Iterator<IfcRelDefines> iterator = door.getIsDefinedBy_Inverse().iterator();
												while(iterator.hasNext())
												{
													IfcRelDefines ifcRelDefines = iterator.next();
													if(ifcRelDefines instanceof IfcRelDefinesByType)
													{
														IfcRelDefinesByType definesByType = (IfcRelDefinesByType)ifcRelDefines;
														IfcDoorStyle ifcDoorStyle = (IfcDoorStyle)definesByType.getRelatingType();
														Meldung += "\n Material der Tür: " + ifcDoorStyle.getConstructionType().value;
													}
												}
										}
												
										else if (containedInStorey instanceof IfcAnnotation)
										{
											continue;
										}
										else if (containedInStorey instanceof IfcSlab)
										{
											
											// Casten in IfcSlab 
									IfcSlab slab = (IfcSlab) containedInStorey;
									// Abfragen des Materials: da es sich um eine Deckenplatte handelt, wird IfcMaterialLayerSetUsage verwendet
									// Speichern und Casten von IfcRelAssociatesMaterial, IfcMaterialLayerSetUsage, IfcMaterial
									IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) slab.getHasAssociations_Inverse().iterator().next();
									
									IfcMaterial material = null;
									
									if (materialAsso.getRelatingMaterial() instanceof IfcMaterial)
									{
										material = (IfcMaterial) materialAsso.getRelatingMaterial();	
									}
									
									if (materialAsso.getRelatingMaterial() instanceof IfcMaterialLayerSetUsage)
									{
										IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();	
										material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
									}
//									IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
									
								// += "\n Platte: " + slab.getName() + " bestehend aus  " + material_slab + d;
									//bauteild = new Bauteile(storey, Objektname, slab.getName(), d, Volum);
												//IfcRelDefinesByType windowType = (IfcRelDefinesByType) window.getIsDefinedBy_Inverse().iterator().next();
												//IfcWindowStyle windowStyle = (IfcWindowStyle) windowType.getRelatingType();
												//String material = (String) windowStyle.getConstructionType().toString();
	//											wallList.add(wall);
	//											IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
	//											IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
	//											IfcLabel material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial().getName();
									Meldung += "\n DeckenplatteDeckenplatte" + slab.getName() + material.getName();
										}
										else Meldung += "\n nicht in switchnicht in switchnicht in switchnicht in switchnicht in switchnicht in switch" + containedInStorey.getClass().getName();
									}
							}
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