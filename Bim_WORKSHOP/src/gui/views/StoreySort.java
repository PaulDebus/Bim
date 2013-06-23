package gui.views;

import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;

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
