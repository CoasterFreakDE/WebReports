package de.wr.commands;

public enum EnumReport {

	Clientmodifikationen(0, "Clientmodifikationen"),
	Bugusing(1, "Bugusing"),
	Teaming(2, "Teaming"),
	Verhalten(3, "Verhalten"),
	Werbung(4, "Werbung"),
	Username(5, "Username"),
	Spawntrapping(6, "Spawntrapping");
	
	
	int id;
	String message;
	
	EnumReport(int id, String message) {
		this.id = id;
		this.message = message;
	}
	
	
	public static EnumReport getById(int id) {
		for(EnumReport rep : EnumReport.values()) {
			if(rep.id == id) {
				return rep;
			}
		}
		
		return Verhalten;
	}
}
