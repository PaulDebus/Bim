package gui.views;

import ifc2x3javatoolbox.ifc2x3tc1.IfcBuildingStorey;
import ifc2x3javatoolbox.ifc2x3tc1.IfcLabel;
import ifc2x3javatoolbox.ifc2x3tc1.IfcMaterial;

public class Bauteile {

	double Geschosshöhe;
	IfcBuildingStorey Stock;
	String Objektnamen;
	IfcLabel Material;
	int Anzahl;
	double Volumen;
	
	Bauteile (){
		Geschosshöhe = 0;
		Stock =null;
		Objektnamen = null;
		Material = null;
		Anzahl = 0;
		Volumen = 0;
	}
	Bauteile(IfcBuildingStorey Geschoss, String Objektname, IfcLabel Materialname, int Anzahlderbauteile, double Volum){
		Geschosshöhe = Geschoss.getElevation().value;
		Stock = Geschoss;
		Objektnamen = Objektname;
		Material = Materialname;
		Anzahl= Anzahlderbauteile;
		Volumen=Volum;
		
	}
	public double getGeschosshöhe() {
		return Geschosshöhe;
	}
	public void setGeschosshöhe(double geschosshöhe) {
		Geschosshöhe = geschosshöhe;
	}
	public IfcBuildingStorey getStock() {
		return Stock;
	}
	public void setStock(IfcBuildingStorey stock) {
		Stock = stock;
	}
	public String getObjektnamen() {
		return Objektnamen;
	}
	public void setObjektnamen(String objektnamen) {
		Objektnamen = objektnamen;
	}
	public IfcLabel getMaterial() {
		return Material;
	}
	public void setMaterial(IfcLabel material) {
		Material = material;
	}
	public int getAnzahl() {
		return Anzahl;
	}
	public void setAnzahl(int anzahl) {
		Anzahl = anzahl;
	}
	public double getVolumen() {
		return Volumen;
	}
	public void setVolumen(double volumen) {
		Volumen = volumen;
	}
	
	
}
