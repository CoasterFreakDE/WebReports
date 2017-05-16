package de.wr.commands;

public enum EnumReportState {

	Open(0, "§a"),
	Moderated(1, "§9"),
	Closed(2, "§c"),
	Deleted(3, "§4");
	
	private int id;
	private String colorcode;
	
	private EnumReportState(int id, String colorcode) {
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static EnumReportState getById(int id) {
		for(EnumReportState ers : EnumReportState.values()) {
			if(ers.getId() == id) {
				return ers;
			}
		}
		return EnumReportState.Open;
	}

	public String getColorcode() {
		return colorcode;
	}

	public void setColorcode(String colorcode) {
		this.colorcode = colorcode;
	}
}
