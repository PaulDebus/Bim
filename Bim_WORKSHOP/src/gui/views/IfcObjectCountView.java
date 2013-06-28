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
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayer;
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
	
	// Konstruktor: Erzeugt Objekte und belegt Attribute 
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

	// Funktion zu Ermittlung der einzelnen Bautele des Ifc-Projektes
	private void updateView() {
		Integer numberofWalls = 0;
		Double volumeofWalls = 0.0;
		String wallMaterial = new String();
		String Meldung = new String();
		// try / catch: wenn ein Objekt eingeladen wurde, wird die Analyse durchgeführt, wenn nicht, wird ein Hinweis ausgegeben
		try {
			if (ifcModel.getCollection(IfcWindow.class) != null) {
				
					// Das Projekt wird aus dem Modell ausgelesen und einer Variable zugewiesen.
					// Danach werden die im Projekt vorhandenen Gelände in einem Set zwischengespeichert. 
					IfcProject ifcProject = ifcModel.getIfcProject();
					
					// Für jedes vorhandene Gelände werden alls vorhandenen Gebäude in einem Set gespeichert.
					// Die Variablen werden dazu zum Datentyp IfcSite gecastet.
					SET<IfcRelDecomposes> siteList = ifcProject.getIsDecomposedBy_Inverse();
					for (IfcRelDecomposes siteIt : siteList)
					{
						IfcSite site = (IfcSite) siteIt.getRelatedObjects().iterator().next();
						Meldung += site.getName();
						
						// Analog werden für jedes Gelände die Gebäude gespeichert und zu IfcBuilding gecastet. 
						SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();
						for (IfcRelDecomposes buildingIt : buildingList)
						{
							IfcBuilding building = (IfcBuilding) buildingIt.getRelatedObjects().iterator().next();
							Meldung += "\n Gebäude: " + building.getName();
							// get related objects?
							
							// Analog werden für jedes Gebäude die Geschosse gespeichert, IfcBuildingStorey gecastet und zur Sortierung in eine Liste geschrieben.
							// Die Geschosse der Liste werden mittels des Comparators StoreySort nach ihrer Höhe sortiert. 
							// Dazu übergibt Collections.sort jeweils zwei Geschosse an StoreySort und sortiert sie entsprechend der Rückgabewerte.
							SET<IfcObjectDefinition> storeyList = building.getIsDecomposedBy_Inverse().iterator().next().getRelatedObjects();
							List<IfcBuildingStorey> listedStoreys = new ArrayList<>();
							for (IfcObjectDefinition storeyDefinition : storeyList)
							{
								listedStoreys.add((IfcBuildingStorey) storeyDefinition);
							}
							Comparator<IfcBuildingStorey> storeyCompare = new StoreySort();
							Collections.sort(listedStoreys, storeyCompare);

							// Alle Elemente, die im aktuellen Geschoss enthalten sind, werden in einem Set gespeichert.
							// Für jedes Element wird unterschieden, welcher Klasse es ist.
							// Entsprechend des Typs werden verschiedene weitere Analysen durchgeführt.
							for (IfcObjectDefinition storey : listedStoreys)
							{
								Meldung += "\n \n \n Geschoss: " + storey.getName() + "\n \n \n";
								numberofWalls = 0;
								volumeofWalls = 0.0;
								
								SET<IfcProduct> containedInStoreyList = ((IfcSpatialStructureElement) storey).getContainsElements_Inverse().iterator().next().getRelatedElements();
								for (IfcProduct containedInStorey : containedInStoreyList)
								{
									// Elemente der Klasse IfcWallStandardCase
									if (containedInStorey instanceof IfcWallStandardCase)
									{
												// Cast 
												IfcWallStandardCase wall = (IfcWallStandardCase) containedInStorey;
												numberofWalls++;
												
												// Materialbestimmung: Da es sich um eine Wand handelt, wird IfcMaterialLayerSetUsage verwendet.
												// Diese Klasse speichert Materialien für Elemente aus mehreren Schichten. 
												// Für jede Schicht wird das Material ausgelesen und einem gemeinsamen String hinzugefügt.
												IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
												IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
												IfcMaterial materialit = null;
												String material = null;
												
												for (IfcMaterialLayer materialLayer: materialSelect.getForLayerSet().getMaterialLayers())
												{
													materialit = materialLayer.getMaterial();
													material += materialit + " ";
												}
												
												// Volumenbestimmung: Alle Definitionen der aktuellen Wand werden gespeichert.
												// Nur Elemente der Klasse IfcRelDefinesByProperties, die IfcElementQuantity enthalten, werden betrachtet.
												// Nach einem Cast zu IfcElementQuantity werden die IfcPhysicalQuantities gespeichert.
												// Das Element, das IfcQuantityVolume, wird ausgewählt und der Wert gespeichert.
												SET<IfcRelDefines> relDefinesList = wall.getIsDefinedBy_Inverse();
												for (IfcRelDefines relDefinesIt : relDefinesList)
												{
													if (relDefinesIt instanceof IfcRelDefinesByProperties)
													{
														IfcRelDefinesByProperties relDefines = (IfcRelDefinesByProperties) relDefinesIt;
														if (relDefines.getRelatingPropertyDefinition() instanceof IfcElementQuantity )
														{
															IfcElementQuantity elementQuantity = (IfcElementQuantity) relDefines.getRelatingPropertyDefinition();
															SET<IfcPhysicalQuantity> physQuantity = elementQuantity.getQuantities();
															
															for (IfcPhysicalQuantity volume : physQuantity)
																if(volume instanceof IfcQuantityVolume)
																{
																	IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
																	volumeofWalls += volumeValue.getVolumeValue().value;
																}
															}
														}
													}
												
									}
									
									
									// Elemente der Klasse IfcWindow
									else if (containedInStorey instanceof IfcWindow)
									{
										IfcWindow window = (IfcWindow) containedInStorey;
										// Bestimmung der Abmaße IfcPositiveLengthMeasure und IfcPositiveLengthMeasure
										IfcPositiveLengthMeasure width = window.getOverallHeight();
										IfcPositiveLengthMeasure height = window.getOverallHeight();
										
										// Zur Bestimmung des Materials wird das Element IfcWindowStyleConstructionEnum aus den IfcRelDefines und dem IfcWindowStyle gewählt.
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
									
									// Elemente der Klasse IfcStair
									else if (containedInStorey instanceof IfcStair)
									{
										// Cast
										IfcStair stair = (IfcStair) containedInStorey;
										// Materialbestimmung: Über die IfcRelAssociatesMaterial wird das IfcMaterial ausgewählt.
										IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) stair.getHasAssociations_Inverse().iterator().next();
										IfcMaterial material = (IfcMaterial) materialAsso.getRelatingMaterial();
										// Die Form der Treppen ist in IfcStairTypeEnum gespeichert.
										IfcStairTypeEnum type = stair.getShapeType();
										Meldung += "\n Treppe Material: " + material.getName() + "Typ: " + type.value;
									}
									
									// Elemente der Klasse IfcDoor
									// Analog IfcWindow!
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
														Meldung += "\n Material der Tür: " + ifcDoorStyle.getConstructionType().value;													}
												}
									}
									// Elemene der Klasse IfcAnnotation werden ignoriert, da Sie keine Bauteile darstellen.
									else if (containedInStorey instanceof IfcAnnotation)
									{
										continue;
									}
									// Elemente der Klasse IfcSlab
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
