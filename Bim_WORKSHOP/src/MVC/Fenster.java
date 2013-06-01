package MVC;

public class Fenster {

	private boolean isOpen = false;
	private boolean isDirty = false;
	private String bezeichnung = null;

	public Fenster() {

	}

	public Fenster(boolean isOpen, boolean isDirty, String bezeichnung) {
		this.isOpen = isOpen;
		this.isDirty = isDirty;
		this.bezeichnung = bezeichnung;
		
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

}