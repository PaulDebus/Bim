package MVC;

import java.util.ArrayList;

public class Model {

	ArrayList<Fenster> fensterList = null;
	
	private ArrayList <ModelListener> listenerList = null;
	
	
	public Model(){
		fensterList = new ArrayList<>();
	}

	public void AddModelListener (ModelListener modelListener){
		if (listenerList == null) listenerList = new ArrayList<ModelListener>();
		this.listenerList.add(modelListener);
	}
	
	private void fireModelEvent(){
		if(listenerList ==null)return;
		for(ModelListener listener : listenerList)
			listener.modelActionPerformed();
	}
	
	public ArrayList<Fenster> getFensterList() {
		return fensterList;
	}

	public void setFensterList(ArrayList<Fenster> fensterList) {
		this.fensterList = fensterList;
		fireModelEvent();
	}
	
	public void addFenster(Fenster fenster){
		fensterList.add(fenster);
		fireModelEvent();
	}
	
	public void removeFenster(Fenster fenster){
		fensterList.remove(fenster);
		fireModelEvent();
	}
}
