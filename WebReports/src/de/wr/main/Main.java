package de.wr.main;

import de.wr.commands.ReportCommand;
import de.wr.mysql.MySQL;
import de.wr.mysql.MySQLData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	public static Main INSTANCE;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReportCommand());
		
		

		
		new MySQLData();
		MySQL.onConnect();
	}
	
	@Override
	public void onDisable() {
		MySQL.onDisconect();
	}
	
}
