package gui.views;

// Einbindung aller benötigten Klassen
import ifc2x3javatoolbox.ifc2x3tc1.ClassInterface;
import ifc2x3javatoolbox.ifc2x3tc1.IfcAnnotation;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuilding;
import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcColumn;
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
		Double volumeofSlabs = 0.0;
		//String wallMaterial = new String();
		String wallMaterialText = new String();
		String Meldung = new String();
		ArrayList<String> slabMaterial = new ArrayList<>();
		ArrayList<String> windowMaterial = new ArrayList<>();
		ArrayList<String> doorMaterial = new ArrayList<>();
		ArrayList<String> columnMaterial = new ArrayList<>();
		ArrayList<String> wallMaterial = new ArrayList<>();
		ArrayList<Double> wallVolume = new ArrayList<>();
		String windowMeldung = new String();
		// try / catch: wenn ein Objekt eingeladen wurde, wird die Analyse durchgeführt, wenn nicht, wird ein Hinweis ausgegeben
		double volumeWall = 0;
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
								Meldung += "\n \n \n Geschoss: " + storey.getName() + "\n _____________________________________________________";
								// Speichern aller Unterwerte des aktuellen Stockwerkes in Set containedInStoreyList
								SET<IfcProduct> containedInStoreyList = ((IfcSpatialStructureElement) storey).getContainsElements_Inverse().iterator().next().getRelatedElements();
								
								// Instanziieren von Sets fÃ¼r die einzelnen Bauteile
								
								numberofWalls = 0;
								volumeofWalls = 0.0;
								wallMaterial.clear();
								wallMaterialText = "";
								wallVolume.clear();
								windowMaterial.clear();
								doorMaterial.clear();
								slabMaterial.clear();
								columnMaterial.clear();
								
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
												IfcMaterial material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
												IfcMaterial materialit = null;
												String materialString = null;
												//wallMaterial = material.getName().toString();
												//Meldung += "\n Material: " + material.getName();
												
												
												for (IfcMaterialLayer materialLayer: materialSelect.getForLayerSet().getMaterialLayers())
												{
													materialit = materialLayer.getMaterial();
													materialString += materialit + " ";
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
																	if (volumeValue.getName().getDecodedValue().equals("NetVolume"))
																	{ 
																	//Meldung += "\n" + volume.getName() + ": " + volumeValue.getVolumeValue();
																	volumeofWalls += volumeValue.getVolumeValue().value;
																	volumeWall = volumeValue.getVolumeValue().value;
																	}
																}
															}
														//Die nächsten Zeilen ordnen den verschiedenen Arten von Platten die Anzahl
														//ihres Auftretens zu und bereiten so die Ausgabe vor. In die gerade Felder
														//der Liste slabMaterial werden die Bauweisen in der Schleife geschrieben
														// und in das darauf folgende Feld wird eine 1 für jedes Auftreten geschrieben
														//für die Ausgabe wird dann die länge der ungeraden Felder als Anzahl ausgegeben
														
														
													}
												}
														
														boolean enthalten = false; 
														for (int i= 0; i<  wallMaterial.size()/2; i++) {
															int j = 2*i;
															if (wallMaterial.get(j) == material.getName().toString()){
																wallMaterial.set(j+1, wallMaterial.get(j+1)+1);
																wallVolume.set(i, wallVolume.get(i)+volumeWall);
																enthalten = true;
//																System.out.println(volumeWall);
//																System.out.println(wall.getStepLineNumber());
//																System.out.println(wallVolume);
																break;
															}
														}
														if (!enthalten) {
															wallMaterial.add(material.getName().toString());
															wallMaterial.add("1");
															wallVolume.add(volumeWall);
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
												
												//Die nächsten Zeilen ordnen den verschiedenen Arten von Fenstern die Anzahl
												//ihres Auftretens zu und bereiten so die Ausgabe vor. In die gerade Felder
												//der Liste windowMaterial werden die Bauweisen in der Schleife geschrieben
												// und in das darauf folgende Feld wird eine 1 für jedes Auftreten geschrieben
												//für die Ausgabe wird dann die länge der ungeraden Felder als Anzahl ausgegeben
												String material = ifcWindowStyle.getConstructionType().value.toString();
										boolean enthalten = false; 
										for (int i= 0; i<  windowMaterial.size()/2; i++) {
											int j = 2*i;
											if (windowMaterial.get(j) == material){
												windowMaterial.set(j+1, windowMaterial.get(j+1)+1);
												enthalten = true;
												break;
											}
										}
										if (!enthalten) {
											windowMaterial.add(material);
											windowMaterial.add("1");
										}
											}
										}
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
										Meldung += "\n \n Treppe \n Material: " + material.getName() + "\n Typ: " + type.value;
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
														//Meldung += "\n Material der TÃ¼r: " + ifcDoorStyle.getConstructionType().value;
												
														//Die nächsten Zeilen ordnen den verschiedenen Arten von Türen die Anzahl
														//ihres Auftretens zu und bereiten so die Ausgabe vor. In die gerade Felder
														//der Liste doorMaterial werden die Bauweisen in der Schleife geschrieben
														// und in das darauf folgende Feld wird eine 1 für jedes Auftreten geschrieben
														//für die Ausgabe wird dann die länge der ungeraden Felder als Anzahl ausgegeben
														String material = ifcDoorStyle.getConstructionType().value.toString();
												boolean enthalten = false; 
												for (int i= 0; i<  doorMaterial.size()/2; i++) {
													int j = 2*i;
													if (doorMaterial.get(j) == material){
														doorMaterial.set(j+1, doorMaterial.get(j+1)+1);
														enthalten = true;
														break;
													}
												}
												if (!enthalten) {
													doorMaterial.add(material);
													doorMaterial.add("1");
												
												}
													}
													
												}}
												
										else if (containedInStorey instanceof IfcAnnotation)
										{
											continue;
										}
									
										else if (containedInStorey instanceof IfcColumn)
										{
											// Casten in IfcColumn
											IfcColumn column = (IfcColumn) containedInStorey;
											// Abfragen des Materials: da es sich um eine Deckenplatte handelt, wird IfcMaterialLayerSetUsage verwendet
											// Speichern und Casten von IfcRelAssociatesMaterial, IfcMaterialLayerSetUsage, IfcMaterial
											IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) column.getHasAssociations_Inverse().iterator().next();
											
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
											
											
											//Die nächsten Zeilen ordnen den verschiedenen Arten von Stützen die Anzahl
											//ihres Auftretens zu und bereiten so die Ausgabe vor. In die gerade Felder
											//der Liste columnMaterial werden die Bauweisen in der Schleife geschrieben
											// und in das darauf folgende Feld wird eine 1 für jedes Auftreten geschrieben
											//für die Ausgabe wird dann die länge der ungeraden Felder als Anzahl ausgegeben
											boolean enthalten = false; 
											for (int i= 0; i<  columnMaterial.size()/2; i++) {
												int j = 2*i;
												if (columnMaterial.get(j) == material.getName().toString()){
													columnMaterial.set(j+1, columnMaterial.get(j+1)+1);
													enthalten = true;
													break;
												}
											}
											if (!enthalten) {
												columnMaterial.add(material.getName().toString());
												columnMaterial.add("1");
											}
												
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
									// Speichern aller Definitionen der aktuellen Wand in das Set relDefinesList
									SET<IfcRelDefines> relDefinesList = slab.getIsDefinedBy_Inverse();
									//Schleife Ã¼ber das Set relDefinesList, Initialisierung der einzelnen Elemente als Variable relDefinesIt
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
												
												//Schleife Ã¼ber das Set physQuantity, Initialisierung der einzelnen Elemente als Variable volume
												for (IfcPhysicalQuantity volume : physQuantity)
													// Auswahl der Werte, die ein Volumen speichern
													if(volume instanceof IfcQuantityVolume)
													{
														// Casten zu IfcQuantityVolume
														IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
														if (volumeValue.getName().getDecodedValue().equals("NetVolume"))
														{
														//Meldung += "\n" + volume.getName() + ": " + volumeValue.getVolumeValue();
														volumeofSlabs += volumeValue.getVolumeValue().value;
														}
													}	
											}
										}}
												
									//Die nächsten Zeilen ordnen den verschiedenen Arten von Platten die Anzahl
									//ihres Auftretens zu und bereiten so die Ausgabe vor. In die gerade Felder
									//der Liste slabMaterial werden die Bauweisen in der Schleife geschrieben
									// und in das darauf folgende Feld wird eine 1 für jedes Auftreten geschrieben
									//für die Ausgabe wird dann die länge der ungeraden Felder als Anzahl ausgegeben
									boolean enthalten = false; 
									for (int i= 0; i<  slabMaterial.size()/2; i++) {
										int j = 2*i;
										if (slabMaterial.get(j) == material.getName().toString()){
											slabMaterial.set(j+1, slabMaterial.get(j+1)+1);
											enthalten = true;
											break;
										}
									}
									if (!enthalten) {
										slabMaterial.add(material.getName().toString());
										slabMaterial.add("1");
									}
										}
									}
								
								//Hier werden die einzelnen Bauteillisten ausgegeben. Bei den Wänden die Anzahl und das Volumen. Bei
								//Fenstern, Türen und Platten wird die oben erstelle Liste ausgewertet. Jeweils zwei aufeinander 
								//folgende Felder enthalten einen Datensatz aus Name und Anzahl der Elemente, die in diesen Schleifen
								//untereinander geschrieben werden. ^
								
								for (int i=0; i< wallMaterial.size()/2; i++) {
									int j = 2*i;
									wallMaterialText += "\n    " + wallMaterial.get(j) + "	" + wallMaterial.get(j+1).length() + "	" + Math.round(volumeofWalls*100)/100.0 + "m^3";
								}
								
								Meldung += "\n \n Wände\n Anzahl: " + "		" + numberofWalls + "\n Gesamtvolumen: " + "	" + Math.round(volumeofWalls*100)/100.0 + "m^3 \n    Nach Material: " + "	" + "Anzahl" + "	"	+ "Volumen" +		 wallMaterialText ; 
								
								Meldung += "\n \n Fenster \n Material 		Anzahl: ";
								for (int i=0; i< windowMaterial.size()/2; i++) {
									int j = 2*i;
									Meldung += "\n   " + windowMaterial.get(j) + "		" + windowMaterial.get(j+1).length();
								}
								
								Meldung += "\n \n Türen \n Material 		Anzahl: ";
								for (int i=0; i< doorMaterial.size()/2; i++) {
									int j = 2*i;
									Meldung += "\n   " + doorMaterial.get(j) + "		" + doorMaterial.get(j+1).length();
								}
								
								
								Meldung += "\n \n Platten: \n Material 		 Anzahl: ";
								for (int i=0; i< slabMaterial.size()/2; i++) {
									int j = 2*i;
									Meldung += "\n   " + slabMaterial.get(j) + "		" + slabMaterial.get(j+1).length();
								}
								Meldung += "\n Gesamtvolumen der Platten: " +"	" + Math.round(volumeofSlabs*100)/100.0 + "m^3";
								if (columnMaterial.size() != 0){
								Meldung += "\n \n Stützen: \n Material 		 Anzahl: ";
								for (int i=0; i< columnMaterial.size()/2; i++) {
									int j = 2*i;
									Meldung += "\n   " + columnMaterial.get(j) + "		" + columnMaterial.get(j+1).length();
								}
							}}
						}
									
									
								}

		}} 
			catch (Exception e) {
				Meldung = "Bitte eine ifc-Datei einlesen.";
			//e.printStackTrace();
		}
		// Ausgabe des Textes
		objectCountTextArea.setText(Meldung);
		objectCountTextArea.repaint();
	}

	// Initialisierung aller Klassen des implementierten IfcModelListener
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

