package org.thefruitbox.fbevents.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.thefruitbox.fbevents.Main;
import org.thefruitbox.fbevents.runnables.SendDailyEventVote;

public class fbeForceVote implements CommandExecutor{
	
	//Main instance
	private Main mainClass = Main.getInstance();
	
	SendDailyEventVote dailyVote = new SendDailyEventVote();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("fbeforcevote")) {
        	if(p.hasPermission("fbe.forcevote")) {	
        		dailyVote.runTaskTimer(mainClass, 0, 30000);
        		p.sendMessage(mainClass.prefix + "Vote started successfully!");
        	}
        }	
		return true;
	}
}
