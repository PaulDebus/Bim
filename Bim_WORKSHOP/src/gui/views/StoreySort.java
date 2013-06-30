// Der Comparator StoreySort wird zur Sortierung von Geschossen eines Ifc-Gebäudes verwendet.
// Dazu werden die Höhen von jeweils zwei Stockwerken verglichen und das Ergebnis an Collections.sort übergeben. 

package gui.views;

import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;

import java.util.Collections;
import java.util.Comparator;

public class StoreySort implements Comparator<IfcBuildingStorey>{
	
	public int compare(IfcBuildingStorey StoreyA, IfcBuildingStorey StoreyB)
	{	
		double nummerA = StoreyA.getElevation().value;
		double nummerB = StoreyB.getElevation().value;
		
		if (nummerA < nummerB)
			return -1;
		else
			return 1;
	}

}
