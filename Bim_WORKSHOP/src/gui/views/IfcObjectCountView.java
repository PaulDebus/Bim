package gui.views;

// Einbindung aller benötigten Klassen
import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAnnotation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoor;
import ifc2x3javatoolbox.ifc2x3tc1.IfcDoorStyle;
import ifc2x3javatoolbox.ifc2x3tc1.IfcElementQuantity;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSetUsage;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPhysicalQuantity;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPositiveLengthMeasure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProduct;
import ifc2x3javatoolbox.ifc2x3tc1.IfcProject;
import ifc2x3javatoolbox.ifc2x3tc1.IfcQuantityVolume;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelAssociatesMaterial;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDecomposes;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefines;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByProperties;
import ifc2x3javatoolbox.ifc2x3tc1.IfcRelDefinesByType;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSite;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSlab;
import ifc2x3javatoolbox.ifc2x3tc1.IfcSpatialStructureElement;
import ifc2x3javatoolbox.ifc2x3tc1.IfcStair;
import ifc2x3javatoolbox.ifc2x3tc1.IfcStairTypeEnum;
import ifc2x3javatoolbox.ifc2x3tc1.IfcVolumeMeasure;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWallStandardCase;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindow;
import ifc2x3javatoolbox.ifc2x3tc1.IfcWindowStyle;
import ifc2x3javatoolbox.ifc2x3tc1.SET;
import ifc2x3javatoolbox.ifcmodel.IfcModel;
import ifc2x3javatoolbox.ifcmodel.IfcModelListener;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextArea;

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
		Integer numberofWalls = 0;
		Double volumeofWalls = 0.0;
		String wallMaterial = new String();
		String Meldung = new String();
		try {
			if (ifcModel.getCollection(IfcWindow.class) != null) {
				

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
								
								numberofWalls = 0;
								volumeofWalls = 0.0;
								
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
												numberofWalls++;
												// Abfragen des Materials: da es sich um eine Wand handelt, wird IfcMaterialLayerSetUsage verwendet
												// Speichern und Casten von IfcRelAssociatesMaterial, IfcMaterialLayerSetUsage, IfcMaterial
												IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
												IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
												IfcMaterial material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
												//Meldung += "\n Material: " + material.getName();
												
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
																	//Meldung += "\n" + volume.getName() + ": " + volumeValue.getVolumeValue();
																	volumeofWalls += volumeValue.getVolumeValue().value;
																}
															}
														}
													}
												}
									}
											
									else if (containedInStorey instanceof IfcWindow)
									{
										IfcWindow window = (IfcWindow) containedInStorey;
										
										IfcPositiveLengthMeasure width = window.getOverallHeight();
										IfcPositiveLengthMeasure height = window.getOverallHeight();
										
										Iterator<IfcRelDefines> iterator = window.getIsDefinedBy_Inverse().iterator();
										while(iterator.hasNext())
										{
											IfcRelDefines ifcRelDefines = iterator.next();
											if(ifcRelDefines instanceof IfcRelDefinesByType)
											{
												IfcRelDefinesByType definesByType = (IfcRelDefinesByType)ifcRelDefines;
												IfcWindowStyle ifcWindowStyle = (IfcWindowStyle)definesByType.getRelatingType();
												Meldung += "\n Material des Fensters: " + ifcWindowStyle.getConstructionType().value.toString();
											}
										}
										Meldung += ", Höhe: " + height.toString() + "m Breite: " + width.value + "m";
										
									}

									else if (containedInStorey instanceof IfcStair)
									{
										IfcStair stair = (IfcStair) containedInStorey;									
									
									
									IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) stair.getHasAssociations_Inverse().iterator().next();
	
									IfcMaterial material = (IfcMaterial) materialAsso.getRelatingMaterial();
									
									IfcStairTypeEnum type = stair.getShapeType();
									
									
									Meldung += "\n Treppe Material: " + material.getName() + "Typ: " + type.value;
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
									Meldung += "\n Platte" + slab.getName() + material.getName();
										}
										else Meldung += "\n nicht in switchnicht in switchnicht in switchnicht in switchnicht in switchnicht in switch" + containedInStorey.getClass().getName();
									}
								Meldung += "\n \n \n Anzahl der W�nde in diesem Geschoss: " + numberofWalls + ", Gesamtvolumen: " + Math.round(volumeofWalls*100)/100.0 + "m^3, Material der W�nde: " + wallMaterial ; 
							}
						}
									
									
								}

		}} 
			catch (Exception e) {
				Meldung = "Bitte eine ifc-Datei einlesen.";
			//e.printStackTrace();
		}
		objectCountTextArea.setText(Meldung);
		objectCountTextArea.repaint();
	}

	@Override
	public void modelContentChanged() {
		updateView();
	}

	@Override
	public void modelObjectAdded(ClassInterface object) {
	}

	@Override
	public void modelObjectRemoved(ClassInterface object) {
	}

	@Override
	public void modelObjectsAdded(Collection<ClassInterface> objects) {
	}

	@Override
	public void modelObjectsRemoved(Collection<ClassInterface> objects) {
	}

}
