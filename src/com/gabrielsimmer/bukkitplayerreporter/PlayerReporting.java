package com.gabrielsimmer.bukkitplayerreporter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerReporting extends JavaPlugin {

	public void loadConfig(){

		this.getConfig().addDefault("email.sender", "email@domain.com");
		this.getConfig().addDefault("email.senderName", "John Smith");
		this.getConfig().addDefault("email.senderpassword", "password");
		this.getConfig().addDefault("email.reciever", "email@domain.com");
		this.getConfig().addDefault("email.recieverName", "John Smith");
		this.getConfig().addDefault("email.topic", "Bug Report");
		this.getConfig().addDefault("email.smtpServer", "smtp.gmail.com:587");
		this.getConfig().addDefault("reported", 0);
		this.getConfig().addDefault("confirmMessage", "We've gotten your report and will work on it ASAP!");

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public void onEnable(){
		loadConfig();	
		getLogger().info("Bukkit Player Reporter Enabled!");
	}
	@Override
	public void onDisable(){
		getLogger().info("Bukkit Player Reporter Disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		if (cmd.getName().equalsIgnoreCase("report") && sender instanceof Player){

			Player player = (Player) sender;

			int reportNumber = getConfig().getInt("reported");
			String sendE = getConfig().getString("email.sender");
			String sendePassword = getConfig().getString("email.senderpassword");
			String reciever = getConfig().getString("email.reciever");
			String topic = getConfig().getString("email.topic");
			String senderName = getConfig().getString("email.senderName");
			String recieverName = getConfig().getString("email.recieverName");
			String confirmationMessage = getConfig().getString("confirmMessage");
			String smtpServer = getConfig().getString("email.smtpServer");
			boolean sendReport;
			String message = null;
			
			if (player.hasPermission("report.report")){
				if (args.length == 0){
					player.sendMessage(ChatColor.RED + "Please enter a report!");
					sendReport = false;
				}
				else{
					sendReport = true;
					int k = 0;
					StringBuilder s = new StringBuilder(300);
					while (k < args.length){
						message = s.append(args[k]).append(" ").toString();
						k++;
					}
				}
				if (sendReport == true){
					try{
						SMTP.Email email = SMTP.createEmptyEmail();
						email.add("Content-Type", "text/html"); //Headers for the email (useful for html) you do not have to set them)
						email.from(senderName, sendE); //The sender of the email.
						email.to(recieverName, reciever); //The recipient of the email.
						email.subject(topic + " #" + reportNumber); //Subject of the email

						email.body(player.getName() + " sent the following report: " + message);

						SMTP.sendEmail(smtpServer, sendE, sendePassword, email, false); //Debug mode. If true the console logs the whole connection (output & input)

						reportNumber = reportNumber + 1;

						getConfig().set("reported", reportNumber);
						saveConfig();

						player.sendMessage(confirmationMessage);

					}catch(Exception e){
						player.sendMessage("You typed something wrong!");
						e.printStackTrace();
					}
				}
			}
			else if (!player.hasPermission("report.report")){
				player.sendMessage(ChatColor.RED + "You can't do that!");
			}
		}

		return false;
	}
}
