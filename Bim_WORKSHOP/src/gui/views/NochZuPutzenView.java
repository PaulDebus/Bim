package gui.views;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import MVC.Fenster;
import MVC.Model;
import MVC.ModelListener;

public class NochZuPutzenView extends JPanel implements ModelListener {

	private Model model = null;
	private JTextArea infoLabel = null;

	public NochZuPutzenView(Model model) {
		this.model = model;
		this.model.AddModelListener(this);
		infoLabel = new JTextArea("");
		infoLabel.setEditable(false);
		this.add(infoLabel);
		showWindowsToClean();
	}

	public void showWindowsToClean() {

		String Meldung = new String();

		for (Fenster actualFenster : model.getFensterList()) {
			// Fetzige schleife!
			if (actualFenster.isDirty())
				Meldung = Meldung + "" + actualFenster.getBezeichnung()
						+ " muss geputzt werden! "+ "\n";

		}
		infoLabel.setText(Meldung);
	}
	
	public void modelActionPerformed(){
		showWindowsToClean();
	}

}
