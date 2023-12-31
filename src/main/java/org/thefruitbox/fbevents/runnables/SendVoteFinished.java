package org.thefruitbox.fbevents.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.thefruitbox.fbevents.Main;
import org.thefruitbox.fbevents.commands.fbevote;
import org.thefruitbox.fbevents.managers.DetermineEventData;

import net.md_5.bungee.api.ChatColor;

public class SendVoteFinished extends BukkitRunnable implements Listener{
    
	//Main instance
	private Main mainClass = Main.getInstance();
	
	//Create new DetermineEventData object (to get list of events)
	public DetermineEventData dev1 = new DetermineEventData();
	
	//get eventdata fileconfiguration from mainclass
	FileConfiguration eventData = mainClass.getEventData();
	
	fbevote ov1 = new fbevote();

	@Override
	public void run() {	
		
		ov1.clearAllInventories();

		//will only run if 2 or more players are online
		if(Bukkit.getServer().getOnlinePlayers().size() >= 2) {

			//get winning event
			String winningEvent = mainClass.dev1.determineEvent(mainClass.getSmallEvents(), mainClass.dev1.getList());

			//create section with winning event name
			ConfigurationSection currentEventSection = eventData.createSection("current-event");
			ConfigurationSection winningEventSection = currentEventSection.createSection(winningEvent);

			mainClass.getEventData().set("eventid", 0);

			if(!winningEvent.equals("NONE")) {

				StartEventCountdown3Min threeMin = new StartEventCountdown3Min();
				threeMin.runTaskTimer(mainClass, 0, 20);

				for(Player p : dev1.getNonParticipatingPlayers(mainClass.getEventData().getStringList("participants"))) {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FROG_TONGUE, 500F, 0.8F);
					p.sendMessage(ChatColor.RED + "You did not vote for an event "
							+ "and will not be participating!");
				}

				//send to players who joined
				for(Player p : dev1.getPlayerParticipants(mainClass.getEventData().getStringList("participants"))) {
					if(p != null) {
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FROG_TONGUE, 500F, 0.8F);
						p.sendMessage(ChatColor.GREEN + "The voting time has expired and "
								+ "the event will start in 2 minutes!");

						threeMin.addPlayer(p);

						//add participants to config file to track scores
						winningEventSection.createSection(p.getName());
						winningEventSection.set(p.getName(), 0);
					}
				}

				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(ChatColor.LIGHT_PURPLE + winningEvent
								+ " has won the vote!");
				}

				//save files
				mainClass.saveSmallEventsFile();
				mainClass.saveEventDataFile();
				mainClass.reloadEventDataFile();

				Send30SecondReminder secondReminder = new Send30SecondReminder();
				secondReminder.runTaskLater(mainClass, 3000);

			} else {
				//if no one voted. try again in 20 minutes
				Bukkit.broadcastMessage(ChatColor.RED + "Not enough players voted for an event! Trying again in 10 minutes!");
				SendDailyEventVote sendDailyEventVote = new SendDailyEventVote();
				sendDailyEventVote.runTaskLater(mainClass, 24000);
			}
		}
	}
}
