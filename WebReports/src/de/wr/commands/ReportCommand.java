package de.wr.commands;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Random;

import de.wr.mysql.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReportCommand extends Command
{

	public ReportCommand() {
		super("report", null, new String[] { "r", "reports"});
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender s, String[] args)
	{
		String prefix = "§cReport §7» ";
		
		if(s instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) s;
	
			/*
			 * 	 Report#1
			 * 		.reporter
			 * 		.reported	
			 * 		.message
			 * 		.open
			 * 
			 * 			Status:
			 * 			0  		   1 		  2
			 * 		  Offen   Übernommen	Fertig
			 * 
			 * 
			 */
			
			
			
			if(args.length >= 1) {
				
				if(args[0].equalsIgnoreCase("stats")) {
					sendStatistics(p);
					return;
				}
				
				if(args[0].equalsIgnoreCase("list")) {
					if(p.hasPermission("WebReports.reports") /*RankManager.hasPermission(p.getName(), "reports")*/) {
						if(args.length == 1) {
							TextComponent open = new TextComponent("§7(§aOpen§7)");
							open.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Offene Reports").create() ) );
							open.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report list open"));
							
							TextComponent moderated = new TextComponent("§7(§9Moderated§7)");
							moderated.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Moderierte Reports").create() ) );
							moderated.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report list moderated"));
							
							TextComponent closed = new TextComponent("§7(§cClosed§7)");
							closed.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Geschlossene Reports").create() ) );
							closed.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report list closed"));
							
							open.addExtra(" ");
							open.addExtra(moderated);
							open.addExtra(" ");
							open.addExtra(closed);
							
							p.sendMessage(open);
							
							return;
						}
						else if(args.length == 2) {
							String state = args[1];
							int status = -1;
							
							if(state.equalsIgnoreCase("open")) {
								status = 0;
							}
							else if(state.equalsIgnoreCase("moderated")) {
								status = 1;
							}
							else if(state.equalsIgnoreCase("closed")) {
								status = 2;
							}
							
							if(status != -1) {
								ResultSet set = MySQL.onQuery("SELECT * FROM reports WHERE status = " + status);
								
								try {
									EnumReportState ers = EnumReportState.getById(status);
									p.sendMessage(new TextComponent(" §7§o║  " + ers.name()));
									//reporter, reported, server, grund, time
									while(set.next()) {
										String reporter = set.getString("reporter");
										String reported = set.getString("reported");
										Time time = set.getTime("time");
										int id = set.getInt("id");
										
										
										TextComponent msg1 = new TextComponent("§b" + time.toString() + " §7§oReporter: §a" + reporter + " §7§oReported: §c" + reported);
										msg1.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Mehr Informationen").create() ) );
										msg1.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report list id " + id));
										p.sendMessage(msg1);
										
										
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
						else if(args.length == 3) {
							String sub = args[1];

							
							if(sub.equalsIgnoreCase("id")) {
								String idS = args[2];
								
								if(isNumb(idS)) {
									int id = Integer.parseInt(idS);
									
									ResultSet set = MySQL.onQuery("SELECT * FROM reports WHERE id = " + id);
									
									try {
										p.sendMessage(new TextComponent(" §7§o║  §bReport Informationen"));
										//reporter, reported, server, grund, time
										while(set.next()) {
											String reporter = set.getString("reporter");
											String reported = set.getString("reported");
											String grund = set.getString("grund");
											String server = set.getString("server");
											String code = set.getString("secret");
											int status = set.getInt("status");
											EnumReportState ers = EnumReportState.getById(status);
											Time time = set.getTime("time");
											
											
											p.sendMessage(new TextComponent("§b" + time.toString() + " §7§oReporter: §a" + reporter + " §7§oReported: §c" + reported));
											p.sendMessage(new TextComponent("  §6Server: §7§o" + server));
											p.sendMessage(new TextComponent("  §6Grund: §7§o" + grund));
											p.sendMessage(new TextComponent("  §6Status: §7§o" + ers.name()));
											
											if(status == 1) {
												String moderator = set.getString("moderator");
												p.sendMessage(new TextComponent("  §6Moderator: §7§o" + moderator));
											}
											
											p.sendMessage(new TextComponent("  §6WebCode: §7§o" + code + "  §7(§oSpäter§7)"));
											
											if(status != 2) {
												TextComponent closed = new TextComponent("§7(§cSchließen§7)");
												closed.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Report schließen").create() ) );
												closed.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report setState " + id + " " + 2));
												
												if(status == 0) {
													TextComponent moderated = new TextComponent("§7(§9Moderieren§7)");
													moderated.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Report moderieren").create() ) );
													moderated.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report take " + reporter + " " + code));
													
													
													moderated.addExtra(" ");
													moderated.addExtra(closed);
													
													p.sendMessage(moderated);
												}
												else {
													p.sendMessage(closed);
												}
											}
											else {
												TextComponent closed = new TextComponent("§7(§4Löschen§7)");
												closed.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Report löschen").create() ) );
												closed.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report setState " + id + " " + 3));
												p.sendMessage(closed);
												
											}
										}
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					else {
						p.sendMessage(new TextComponent("§c§oKeine Berechtigungen."));
					}
					return;
				}
				else if(args[0].equalsIgnoreCase("setState")) {
					if(p.hasPermission("WebReports.reports") /*RankManager.hasPermission(p.getName(), "reports")*/) {
						if(args.length == 3) {
							String idS = args[1];
							String stateS = args[2];
							
							if(isNumb(idS) && isNumb(stateS)) {
								int id = Integer.parseInt(idS);
								int state = Integer.parseInt(stateS);
								
								MySQL.onUpdate("UPDATE reports SET status = " + state + " WHERE id = '" + id + "'");
								EnumReportState ers = EnumReportState.getById(state);
								p.sendMessage(new TextComponent("§7§oReportstatus geändert zu " +  ers.name()));
								
								
								TextComponent closed = new TextComponent("§7(§oList§7)");
								closed.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Reports").create() ) );
								closed.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report list"));
								p.sendMessage(closed);
							}
						}
					}
					else {
						p.sendMessage(new TextComponent("§c§oKeine Berechtigungen."));
					}
					return;
				}
				
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("take")) {
						if(p.hasPermission("WebReports.reports") /*RankManager.hasPermission(p.getName(), "reports")*/) {
							String reporter = args[1];
							String secret = args[2];
							
							ResultSet set = MySQL.onQuery("SELECT * FROM reports WHERE reporter = '" + reporter + "' AND secret = '" + secret + "'");
							
							try {
								if(set.next()) {
									int status = set.getInt("status");
									String server = set.getString("server");
									
									if(status == 0) {
										/* Ist Frei -> Annehmen */
										
										MySQL.onUpdate("UPDATE reports SET status = 1 , moderator = '" + p.getName() + "' WHERE reporter = '" + reporter + "' AND secret = '" + secret + "'");
										
										TextComponent msg1 = new TextComponent(prefix + "§7§oReport übernommen: §6" + secret);
//										msg1.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Webreport").create() ) );
//										msg1.setClickEvent(new ClickEvent( ClickEvent.Action.OPEN_URL, "http://my-server.me/reports/index.php?name=" + reporter + "&secret=" + secret + "&mod=" + p.getName()));
										p.sendMessage(msg1);
										
										ServerInfo target = ProxyServer.getInstance().getServerInfo(server);
										p.connect(target);
										
									}
									else if(status == 1){
										String mod = set.getString("moderator");
										p.sendMessage(new TextComponent(prefix + "§cDer Report wurde bereits von §e" + mod +  " §cübernommen."));
									}
									else {
										p.sendMessage(new TextComponent(prefix + "§cDieser Report wurde geschlossen."));
									}
								}
								else {
									p.sendMessage(new TextComponent(prefix + "§cDieser Report wurde nicht gefunden."));
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
									
							return;
						}
						else {
							p.sendMessage(new TextComponent("§c§oKeine Berechtigungen."));
						}
					}
			}
				
				String reporter = p.getName();
				String reported = args[0];
				String message = "";
				
				if(BungeeCord.getInstance().getPlayer(reported) == null) {
					p.sendMessage(prefix + "§cDieser Spieler ist derzeit nicht online.");
					p.sendMessage(prefix + "§cBei einem schweren Vergehen reporte den User bei uns im Teamspeak.");
					return;
				}
				
			
				StringBuilder builder = new StringBuilder();
				for(int i = 1 ; i < args.length ; i++)builder.append(args[i]).append(" ");
				message = builder.toString();

				String cleanmessage = message.replaceAll(" ", "");
				
				if(isNumb(cleanmessage)) {
					int nmb = Integer.parseInt(cleanmessage);
					
					message = EnumReport.getById(nmb).message;
				}
				else {
					p.sendMessage(prefix + "§cBitte benutze: §e/report <Spieler> <Nachricht§7(Grund)§e>");
					
					p.sendMessage(new TextComponent(" §6§lGründe:"));
					
					for(EnumReport rep : EnumReport.values()) {
						p.sendMessage(new TextComponent("    §7" + rep.id + " : §c" + rep.message));
					}
					return;
				}

				
				ResultSet set = MySQL.onQuery("SELECT * FROM reports WHERE reporter = '" + reporter + "' AND reported = '" + reported + "' AND STATUS != 2");
				
				try {
					if(set.next()) {
						p.sendMessage(prefix + "§cDu hast diesen Spieler bereits reported.");
						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
								
				/* 	 Reporting Player	*/
				String server = p.getServer().getInfo().getName();
				
				char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
				StringBuilder sb = new StringBuilder();
				Random random = new Random();
				for (int i = 0; i < 8; i++) {
				    char c = chars[random.nextInt(chars.length)];
				    sb.append(c);
				}
				
				String code = sb.toString();
				
				MySQL.onUpdate("INSERT INTO reports(reporter, reported, server, grund, time, secret) VALUES('" + reporter + "', '" + reported + "', '" + server + "', '" + message + "', NOW(), '" + code + "')");
				
				
				
				p.sendMessage(prefix + "§aDu hast den Spieler §6" + reported + " §areported.");
				
				TextComponent msg1 = new TextComponent(prefix + "§7§oDein Reportcode: §6" + code);
//				msg1.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Webreport").create() ) );
//				msg1.setClickEvent(new ClickEvent( ClickEvent.Action.OPEN_URL, "http://my-server.me/reports/index.php?name=" + reporter + "&secret=" + code));
				p.sendMessage(msg1);
				
				
				for(ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
					
					if(pl.hasPermission("WebReports.reports") /*RankManager.hasPermission(pl.getName(), "reports")*/) {
						TextComponent msg = new TextComponent(prefix + " §7Der Spieler §e" + reported + " §7wurde von §6" + reporter + " §7auf §c" + server +  " §7wegen §c" + message + " §7reported." );
//						msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Übernehmen").create() ) );
//						msg.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/report take " + reporter + " " + code));
//						
						pl.sendMessage(msg);
					}
					
				}
			}
			else {
				
				p.sendMessage(prefix + "§cBitte benutze: §e/report <Spieler> <Nachricht§7(Grund)§e>");
				
				p.sendMessage(new TextComponent(" §6§lGründe:"));
				
				for(EnumReport rep : EnumReport.values()) {
					p.sendMessage(new TextComponent("    §7" + rep.id + " : §c" + rep.message));
				}
				
//				System.out.println("CMD: " + args.toString());
//				if(args.length == 1) {
//					if(args[0].equalsIgnoreCase("list")) {
//						if(RankManager.hasPermission(p.getName(), "reports")) {
//							
//							if(reports.getKeys() == null) {
//								p.sendMessage(prefix + "§cDerzeit ist kein Report offen.");
//								return;
//							}
//							
//							p.sendMessage(prefix + "§a§l§oALLE REPORTS");
//							System.out.println("Checking " + reports.getKeys());
//							
//							for(String i : reports.getKeys()) {
//								System.out.println("Checking " + i);
//								if(reports.getBoolean(i + ".open")) {
//									System.out.println(i + " ist offen.");
//									p.sendMessage(" §e" +i);
//									p.sendMessage("    §aReporter: §7" + reports.getString(i + ".reporter"));
//									p.sendMessage("    §cReported: §7" + reports.getString(i + ".reported"));
//									p.sendMessage("    §6Message: §7" + reports.getString(i + ".message"));
//								}
//								else {
//									System.out.println(i + " ist geschlossen.");
//								}
//							}
//							
//							return;
//						}
//						else {
//							p.sendMessage(prefix + "§cKeine Permission.");
//						}
//					}
//					else {
//						p.sendMessage(prefix + "§cBitte benutze: §e/report <Spieler> <Nachricht§7(Grund)§e>");
//					}
//				}
//				else {
//					p.sendMessage(prefix + "§cBitte benutze: §e/report <Spieler> <Nachricht§7(Grund)§e>");
//				}
			}
		}
	}

	public int getSize() {
		ResultSet set = MySQL.onQuery("SELECT * FROM reports WHERE status = 0");
		int i = 0;
		
		try {
			while(set.next()) {
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	public boolean isNumb(String s) {
		try {
			Integer.parseInt(s);
			return true;
		}catch(NumberFormatException ex) { 
			return false;
		}
	}
	
	
	public void sendStatistics(ProxiedPlayer p) {
		int open = 0;
		int moderated = 0;
		int closed = 0;
		int deleted = 0;
		int closedNotModerated = 0;
		
		ResultSet set = MySQL.onQuery("SELECT * FROM reports");
		
		try {
			while(set.next()) {
				int status = set.getInt("status");
				
				if(status == 0) {
					open++;
				}
				else if(status == 1) {
					moderated++;
				}
				else if(status == 2) {
					closed++;
					
					if(set.getString("moderator").equalsIgnoreCase("none")) {
						closedNotModerated++;
					}
				}
				else if(status == 3) {
					deleted++;
				}
			}
		}catch(SQLException ex) { }
		
		
		p.sendMessage(new TextComponent(" §7§l§oSTATISTICS"));
		p.sendMessage(new TextComponent("   §6Open: §7§o" + open));
		p.sendMessage(new TextComponent("   §6Moderated: §7§o" + moderated));
		p.sendMessage(new TextComponent("   §6Closed: §7§o" + closed));
		p.sendMessage(new TextComponent("   §6Deleted: §7§o" + deleted));
		p.sendMessage(new TextComponent("   §6ClosedWithoutModeration: §7§o" + closedNotModerated));
		int gesamt = open + moderated + closed + deleted;
		p.sendMessage(new TextComponent("   §6§lGesamt: §7§o" + gesamt));
	}
}
