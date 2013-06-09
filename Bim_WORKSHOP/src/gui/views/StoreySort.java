package gui.views;

import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;

import java.util.Comparator;

public class StoreySort implements Comparator<IfcBuildingStorey>{
	
	public int compare(IfcBuildingStorey StoreyA, IfcBuildingStorey StoreyB)
	{	
		int nummerA = StoreyA.getStepLineNumber();
		int nummerB = StoreyB.getStepLineNumber();
		
		if (nummerA < nummerB)
			return -1;
		
		return 1;
	}

}
