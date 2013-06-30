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
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterialLayerSetUsage;
import ifc2x3javatoolbox.ifc2x3tc1.IfcObjectDefinition;
import ifc2x3javatoolbox.ifc2x3tc1.IfcPhysicalQuantity;
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
public class IfcObjectCountView extends JPanel implements IfcModelListener
{
    
    // Initialisierung
    private IfcModel ifcModel = null;
    private JTextArea objectCountTextArea = null;
    
    // Konstruktor: Erzeugt Objekte und belegt Attribute
    public IfcObjectCountView(IfcModel ifcModel)
    {
        
        this.ifcModel = ifcModel;
        this.ifcModel.addIfcModelListener(this);
        objectCountTextArea = new JTextArea("");
        objectCountTextArea.setEditable(false);
        this.setLayout(new BorderLayout());
        this.add(objectCountTextArea, BorderLayout.CENTER);
        this.add(objectCountTextArea);
        
        updateView();
    }
    
    // Funktion zur Ermittlung der einzelnen Bauteile des Ifc-Projektes
    private void updateView()
    {
        Integer numberofWalls = 0;
        Double volumeofWalls = 0.0;
        Double volumeofSlabs = 0.0;
        String wallMaterialText = new String();
        String Meldung = new String();
        ArrayList<String> slabMaterial = new ArrayList<>();
        ArrayList<String> windowMaterial = new ArrayList<>();
        ArrayList<String> doorMaterial = new ArrayList<>();
        ArrayList<String> columnMaterial = new ArrayList<>();
        ArrayList<String> wallMaterial = new ArrayList<>();
        ArrayList<Double> wallVolume = new ArrayList<>();
        double volumeWall = 0;
        // try / catch: wenn ein Objekt eingeladen wurde, wird die Analyse
        // durchgeführt, wenn nicht, wird ein Hinweis ausgegeben
        try
        {
            if (ifcModel.getCollection(IfcWindow.class) != null)
            {
                
                // Das Projekt wird aus dem Modell ausgelesen und einer Variable
                // zugewiesen.
                // Danach werden die im Projekt vorhandenen Gelände in einem Set
                // zwischengespeichert.
                IfcProject ifcProject = ifcModel.getIfcProject();
                SET<IfcRelDecomposes> siteList = ifcProject.getIsDecomposedBy_Inverse();
                
                // Für jedes vorhandene Gelände werden alle vorhandenen Gebäude
                // in einem Set gespeichert.
                // Die Variablen werden dazu zum Datentyp IfcSite gecastet.
                for (IfcRelDecomposes siteIt : siteList)
                {
                    IfcSite site = (IfcSite) siteIt.getRelatedObjects().iterator().next();
                    Meldung += site.getName();
                    
                    // Analog werden für jedes Gelände die Gebäude gespeichert
                    // und zu IfcBuilding gecastet.
                    SET<IfcRelDecomposes> buildingList = site.getIsDecomposedBy_Inverse();
                    for (IfcRelDecomposes buildingIt : buildingList)
                    {
                        IfcBuilding building = (IfcBuilding) buildingIt.getRelatedObjects().iterator().next();
                        Meldung += "\n Gebäude: " + building.getName();
                        
                        // Analog werden für jedes Gebäude die Geschosse
                        // gespeichert, zu IfcBuildingStorey gecastet und zur
                        // Sortierung in eine Liste geschrieben.
                        // Die Geschosse der Liste werden mittels des
                        // Comparators StoreySort nach ihrer Höhe sortiert.
                        // Dazu übergibt Collections.sort jeweils zwei Geschosse
                        // an StoreySort und sortiert sie entsprechend der
                        // Rückgabewerte.
                        SET<IfcObjectDefinition> storeyList = building.getIsDecomposedBy_Inverse().iterator().next().getRelatedObjects();
                        List<IfcBuildingStorey> listedStoreys = new ArrayList<>();
                        for (IfcObjectDefinition storeyDefinition : storeyList)
                        {
                            listedStoreys.add((IfcBuildingStorey) storeyDefinition);
                        }
                        Comparator<IfcBuildingStorey> storeyCompare = new StoreySort();
                        Collections.sort(listedStoreys, storeyCompare);
                        
                        // Alle Elemente, die im aktuellen Geschoss enthalten
                        // sind, werden in einem Set gespeichert.
                        // Für jedes Element wird unterschieden, welcher Klasse
                        // es ist.
                        // Entsprechend des Typs werden verschiedene weitere
                        // Analysen durchgeführt.
                        for (IfcObjectDefinition storey : listedStoreys)
                        {
                            Meldung += "\n \n \n Geschoss: " + storey.getName() + "\n _____________________________________________________";
                            // Speichern aller Unterklassen des aktuellen
                            // Stockwerkes in Set containedInStoreyList
                            SET<IfcProduct> containedInStoreyList = ((IfcSpatialStructureElement) storey).getContainsElements_Inverse().iterator().next().getRelatedElements();
                            
                            // Instanziieren von Sets für die einzelnen Bauteile
                            
                            numberofWalls = 0;
                            volumeofWalls = 0.0;
                            wallMaterial.clear();
                            wallMaterialText = "";
                            wallVolume.clear();
                            windowMaterial.clear();
                            doorMaterial.clear();
                            slabMaterial.clear();
                            columnMaterial.clear();
                            volumeofSlabs = 0.0;
                            
                            for (IfcProduct containedInStorey : containedInStoreyList)
                            {
                                // Elemente der Klasse IfcWallStandardCase
                                if (containedInStorey instanceof IfcWallStandardCase)
                                {
                                    // Cast
                                    IfcWallStandardCase wall = (IfcWallStandardCase) containedInStorey;
                                    numberofWalls++;
                                    
                                    // Materialbestimmung: Da es sich um eine
                                    // Wand handelt, wird
                                    // IfcMaterialLayerSetUsage verwendet.
                                    // Diese Klasse speichert Materialien für
                                    // Elemente aus mehreren Schichten.
                                    // Für jede Schicht wird das Material
                                    // ausgelesen und einem gemeinsamen String
                                    // hinzugefügt.
                                    IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) wall.getHasAssociations_Inverse().iterator().next();
                                    IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
                                    IfcMaterial material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
                                    
                                    // Volumenbestimmung: Alle Definitionen der
                                    // aktuellen Wand werden gespeichert.
                                    // Nur Elemente der Klasse
                                    // IfcRelDefinesByProperties, die
                                    // IfcElementQuantity enthalten, werden
                                    // betrachtet.
                                    // Nach einem Cast zu IfcElementQuantity
                                    // werden die IfcPhysicalQuantities
                                    // gespeichert.
                                    // Das Element IfcQuantityVolume mit dem
                                    // Namen NetVolume wird ausgewählt und die
                                    // Werte aller Wandvolumina aufsummiert.
                                    SET<IfcRelDefines> relDefinesList = wall.getIsDefinedBy_Inverse();
                                    for (IfcRelDefines relDefinesIt : relDefinesList)
                                    {
                                        if (relDefinesIt instanceof IfcRelDefinesByProperties)
                                        {
                                            IfcRelDefinesByProperties relDefines = (IfcRelDefinesByProperties) relDefinesIt;
                                            if (relDefines.getRelatingPropertyDefinition() instanceof IfcElementQuantity)
                                            {
                                                IfcElementQuantity elementQuantity = (IfcElementQuantity) relDefines.getRelatingPropertyDefinition();
                                                SET<IfcPhysicalQuantity> physQuantity = elementQuantity.getQuantities();
                                                
                                                for (IfcPhysicalQuantity volume : physQuantity)
                                                    if (volume instanceof IfcQuantityVolume)
                                                    {
                                                        IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
                                                        if (volumeValue.getName().getDecodedValue().equals("NetVolume"))
                                                        {
                                                            volumeofWalls += volumeValue.getVolumeValue().value;
                                                            volumeWall = volumeValue.getVolumeValue().value;
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                    
                                    // Die nächsten Zeilen ordnen den
                                    // verschiedenen Arten von Platten die
                                    // Anzahl
                                    // ihres Auftretens zu. In die geraden
                                    // Felder der Liste slabMaterial werden die
                                    // Bauweisen
                                    // geschrieben und das darauf folgende Feld
                                    // wird inkrementiert.
                                    // Für die Ausgabe wird dann die Länge der
                                    // ungeraden Felder als Anzahl ausgegeben
                                    
                                    boolean enthalten = false;
                                    for (int i = 0; i < wallMaterial.size() / 2; i++)
                                    {
                                        int j = 2 * i;
                                        if (wallMaterial.get(j) == material.getName().toString())
                                        {
                                            wallMaterial.set(j + 1, wallMaterial.get(j + 1) + 1);
                                            wallVolume.set(i, wallVolume.get(i) + volumeWall);
                                            enthalten = true;
                                            break;
                                        }
                                    }
                                    if (!enthalten)
                                    {
                                        wallMaterial.add(material.getName().toString());
                                        wallMaterial.add("1");
                                        wallVolume.add(volumeWall);
                                    }
                                    
                                }
                                
                                // Elemente der Klasse IfcWindow
                                else if (containedInStorey instanceof IfcWindow)
                                {
                                    IfcWindow window = (IfcWindow) containedInStorey;
                                    
                                    // Zur Bestimmung des Materials wird das
                                    // Element IfcWindowStyleConstructionEnum
                                    // aus den IfcRelDefines und dem
                                    // IfcWindowStyle gewählt.
                                    Iterator<IfcRelDefines> iterator = window.getIsDefinedBy_Inverse().iterator();
                                    while (iterator.hasNext())
                                    {
                                        IfcRelDefines ifcRelDefines = iterator.next();
                                        if (ifcRelDefines instanceof IfcRelDefinesByType)
                                        {
                                            IfcRelDefinesByType definesByType = (IfcRelDefinesByType) ifcRelDefines;
                                            IfcWindowStyle ifcWindowStyle = (IfcWindowStyle) definesByType.getRelatingType();
                                            
                                            // Analoge Bestimmung der Anzahl der
                                            // Fenster wie bei den Wänden.
                                            String material = ifcWindowStyle.getConstructionType().value.toString();
                                            boolean enthalten = false;
                                            for (int i = 0; i < windowMaterial.size() / 2; i++)
                                            {
                                                int j = 2 * i;
                                                if (windowMaterial.get(j) == material)
                                                {
                                                    windowMaterial.set(j + 1, windowMaterial.get(j + 1) + 1);
                                                    enthalten = true;
                                                    break;
                                                }
                                            }
                                            if (!enthalten)
                                            {
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
                                    // Materialbestimmung: Über die
                                    // IfcRelAssociatesMaterial wird das
                                    // IfcMaterial ausgewählt.
                                    IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) stair.getHasAssociations_Inverse().iterator().next();
                                    IfcMaterial material = (IfcMaterial) materialAsso.getRelatingMaterial();
                                    // Die Form der Treppen ist in
                                    // IfcStairTypeEnum gespeichert.
                                    IfcStairTypeEnum type = stair.getShapeType();
                                    Meldung += "\n \n Treppe \n Material: " + material.getName() + "\n Typ: " + type.value;
                                }
                                // Elemente der Klasse IfcDoor
                                // Analog IfcWindow!
                                else if (containedInStorey instanceof IfcDoor)
                                {
                                    IfcDoor door = (IfcDoor) containedInStorey;
                                    Iterator<IfcRelDefines> iterator = door.getIsDefinedBy_Inverse().iterator();
                                    while (iterator.hasNext())
                                    {
                                        IfcRelDefines ifcRelDefines = iterator.next();
                                        if (ifcRelDefines instanceof IfcRelDefinesByType)
                                        {
                                            IfcRelDefinesByType definesByType = (IfcRelDefinesByType) ifcRelDefines;
                                            IfcDoorStyle ifcDoorStyle = (IfcDoorStyle) definesByType.getRelatingType();
                                            String material = ifcDoorStyle.getConstructionType().value.toString();
                                            boolean enthalten = false;
                                            for (int i = 0; i < doorMaterial.size() / 2; i++)
                                            {
                                                int j = 2 * i;
                                                if (doorMaterial.get(j) == material)
                                                {
                                                    doorMaterial.set(j + 1, doorMaterial.get(j + 1) + 1);
                                                    enthalten = true;
                                                    break;
                                                }
                                            }
                                            if (!enthalten)
                                            {
                                                doorMaterial.add(material);
                                                doorMaterial.add("1");
                                                
                                            }
                                        }
                                        
                                    }
                                }
                                // Elemente der Klasse IfcAnnotation werden
                                // ignoriert.
                                else if (containedInStorey instanceof IfcAnnotation)
                                {
                                    continue;
                                }
                                
                                // Elemente der Klases IfcColumn
                                else if (containedInStorey instanceof IfcColumn)
                                {
                                    // Cast
                                    IfcColumn column = (IfcColumn) containedInStorey;
                                    // Materialbestimmung
                                    IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) column.getHasAssociations_Inverse().iterator().next();
                                    IfcMaterial material = (IfcMaterial) materialAsso.getRelatingMaterial();
                                    // Analog zu Wänden
                                    boolean enthalten = false;
                                    for (int i = 0; i < columnMaterial.size() / 2; i++)
                                    {
                                        int j = 2 * i;
                                        if (columnMaterial.get(j) == material.getName().toString())
                                        {
                                            columnMaterial.set(j + 1, columnMaterial.get(j + 1) + 1);
                                            enthalten = true;
                                            break;
                                        }
                                    }
                                    if (!enthalten)
                                    {
                                        columnMaterial.add(material.getName().toString());
                                        columnMaterial.add("1");
                                    }
                                    
                                }
                                // Elemente der Klasse IfcSlab
                                else if (containedInStorey instanceof IfcSlab)
                                {
                                    // Casten in IfcSlab
                                    IfcSlab slab = (IfcSlab) containedInStorey;
                                    // Materialbestimmung: Das Material von
                                    // IfcSlab wird je nach Bauteil als
                                    // IfcMaterialLayerSet oder direkt als
                                    // IfcMaterial
                                    // gespeichert. Daher muss eine
                                    // Unterscheidung getroffen werden, ob das
                                    // Material analog zu den Wänden
                                    // oder direkt abgefragt werden muss.
                                    IfcRelAssociatesMaterial materialAsso = (IfcRelAssociatesMaterial) slab.getHasAssociations_Inverse().iterator().next();
                                    IfcMaterial material = null;
                                    if (materialAsso.getRelatingMaterial() instanceof IfcMaterial)
                                    {
                                        material = (IfcMaterial) materialAsso.getRelatingMaterial();
                                    } else if (materialAsso.getRelatingMaterial() instanceof IfcMaterialLayerSetUsage)
                                    {
                                        IfcMaterialLayerSetUsage materialSelect = (IfcMaterialLayerSetUsage) materialAsso.getRelatingMaterial();
                                        material = materialSelect.getForLayerSet().getMaterialLayers().iterator().next().getMaterial();
                                    }
                                    // Volumenberechnung analog zur Wand
                                    SET<IfcRelDefines> relDefinesList = slab.getIsDefinedBy_Inverse();
                                    for (IfcRelDefines relDefinesIt : relDefinesList)
                                    {
                                        if (relDefinesIt instanceof IfcRelDefinesByProperties)
                                        {
                                            IfcRelDefinesByProperties relDefines = (IfcRelDefinesByProperties) relDefinesIt;
                                            if (relDefines.getRelatingPropertyDefinition() instanceof IfcElementQuantity)
                                            {
                                                IfcElementQuantity elementQuantity = (IfcElementQuantity) relDefines.getRelatingPropertyDefinition();
                                                SET<IfcPhysicalQuantity> physQuantity = elementQuantity.getQuantities();
                                                for (IfcPhysicalQuantity volume : physQuantity)
                                                    if (volume instanceof IfcQuantityVolume)
                                                    {
                                                        IfcQuantityVolume volumeValue = (IfcQuantityVolume) volume;
                                                        if (volumeValue.getName().getDecodedValue().equals("NetVolume"))
                                                        {
                                                            volumeofSlabs += volumeValue.getVolumeValue().value;
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                    // Analog in Listen schreiben
                                    boolean enthalten = false;
                                    for (int i = 0; i < slabMaterial.size() / 2; i++)
                                    {
                                        int j = 2 * i;
                                        if (slabMaterial.get(j) == material.getName().toString())
                                        {
                                            slabMaterial.set(j + 1, slabMaterial.get(j + 1) + 1);
                                            enthalten = true;
                                            break;
                                        }
                                    }
                                    if (!enthalten)
                                    {
                                        slabMaterial.add(material.getName().toString());
                                        slabMaterial.add("1");
                                    }
                                }
                            }
                            
                            // Hier werden die einzelnen Bauteillisten 
                            // ausgegeben. Die Listen mit Material und Auftreten
                            // werden ausgewertet.
                            // Dabei wird pro Material die Anzahl und ggf. das
                            // Gesamtvolumen ausgegeben.
                            
                            for (int i = 0; i < wallMaterial.size() / 2; i++)
                            {
                                int j = 2 * i;
                                wallMaterialText += "\n    " + wallMaterial.get(j) + "	" + wallMaterial.get(j + 1).length() + "	" + Math.round(volumeofWalls * 100) / 100.0 + "m^3";
                            }
                            
                            Meldung += "\n \n Wände\n Anzahl: " + "		" + numberofWalls + "\n Gesamtvolumen: " + "	" + Math.round(volumeofWalls * 100) / 100.0 + "m^3 \n    Nach Material: " + "	" + "Anzahl" + "	" + "Volumen" + wallMaterialText;
                            
                            Meldung += "\n \n Fenster \n Material 		Anzahl: ";
                            for (int i = 0; i < windowMaterial.size() / 2; i++)
                            {
                                int j = 2 * i;
                                Meldung += "\n   " + windowMaterial.get(j) + "		" + windowMaterial.get(j + 1).length();
                            }
                            
                            Meldung += "\n \n Türen \n Material 		Anzahl: ";
                            for (int i = 0; i < doorMaterial.size() / 2; i++)
                            {
                                int j = 2 * i;
                                Meldung += "\n   " + doorMaterial.get(j) + "		" + doorMaterial.get(j + 1).length();
                            }
                            
                            Meldung += "\n \n Platten: \n Material 		 Anzahl: ";
                            for (int i = 0; i < slabMaterial.size() / 2; i++)
                            {
                                int j = 2 * i;
                                Meldung += "\n   " + slabMaterial.get(j) + "		" + slabMaterial.get(j + 1).length();
                            }
                            Meldung += "\n Gesamtvolumen der Platten: " + "	" + Math.round(volumeofSlabs * 100) / 100.0 + "m^3";
                            if (columnMaterial.size() != 0)
                            {
                                Meldung += "\n \n Stützen: \n Material 		 Anzahl: ";
                                for (int i = 0; i < columnMaterial.size() / 2; i++)
                                {
                                    int j = 2 * i;
                                    Meldung += "\n   " + columnMaterial.get(j) + "		" + columnMaterial.get(j + 1).length();
                                }
                            }
                        }
                    }
                    
                }
                
            }
        } catch (Exception e)
        {
            Meldung = "Bitte eine ifc-Datei einlesen.";
        }
        // Anzeigen des Textes in der Textarea
        objectCountTextArea.setText(Meldung);
        objectCountTextArea.repaint();
    }
    
    // Initialisierung aller Methoden des implementierten IfcModelListener
    @Override
    public void modelContentChanged()
    {
        updateView();
    }
    
    @Override
    public void modelObjectAdded(ClassInterface object)
    {
    }
    
    @Override
    public void modelObjectRemoved(ClassInterface object)
    {
    }
    
    @Override
    public void modelObjectsAdded(Collection<ClassInterface> objects)
    {
    }
    
    @Override
    public void modelObjectsRemoved(Collection<ClassInterface> objects)
    {
    }
    
}
