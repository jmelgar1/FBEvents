package org.thefruitbox.fbevents.runnables;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.thefruitbox.fbevents.Main;
import org.thefruitbox.fbevents.managers.DetermineEventData;

import net.md_5.bungee.api.ChatColor;

public class StartEventCountdown3Min extends BukkitRunnable {
    
	//Main instance
	private Main mainClass = Main.getInstance();
	
	public DetermineEventData dev1 = new DetermineEventData();
	
	//get winning event 
	String winningEvent = mainClass.dev1.getVotedEvent(mainClass);
	
	int seconds = 180;
	
	BossBar bar = Bukkit.createBossBar("countdown", BarColor.PINK, BarStyle.SEGMENTED_10);
	
	Map<Player, BossBar> playerBossbar = new HashMap<Player, BossBar>();
	
    DecimalFormat dFormat = new DecimalFormat("00");
    
    boolean validStart = true;

	public void run() {		
		if(seconds <= 5) {
			for(String s : mainClass.getEventData().getStringList("participants")) {
				Player p = Bukkit.getServer().getPlayer(s);
				if(p != null) {
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.5F);
				}
			}
		}
		
		if((seconds -= 1) == 0) {
			for(String s : mainClass.getEventData().getStringList("participants")){
				Player p = Bukkit.getPlayer(s);
				
				//if player leaves before event starts REMOVE FROM LIST!
				if(p == null) {
					
					Bukkit.broadcastMessage(ChatColor.RED + s + " left the game before the event started and will be removed from the event!");
					
					mainClass.getEventData().set("participants", mainClass.getEventData().getStringList("participants").remove(s));
					
					mainClass.saveEventDataFile();
					
					if(mainClass.getEventData().getStringList("participants").size() < 2) {
						Bukkit.broadcastMessage(ChatColor.RED + "The event will be cancelled since there are less than two players in the event.");
						
						validStart = false;
						SendDailyEventVote sendVote = new SendDailyEventVote();
						sendVote.runTaskLater(mainClass, 3000);
						bar.removeAll();
						cancel();
					}
				}
			}
			
			if(validStart) {
				StartEvent startEvent = new StartEvent();
				startEvent.run();
				bar.removeAll();
				cancel();
			}
		} else {
			bar.setProgress(seconds / 180D);
			String minutesTimer = String.valueOf(seconds/60);
			String secondsTimer = dFormat.format(seconds % 60);
			bar.setTitle(winningEvent + " starts in " + minutesTimer + ":" + secondsTimer);
		}
	}
	
	public void addPlayer(Player p) {
		bar.addPlayer(p);
	}
	
	@EventHandler
	void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		playerBossbar.put(p, Bukkit.createBossBar("Event will begin shortly", BarColor.PINK, BarStyle.SEGMENTED_10));
	}
	
	@EventHandler
	void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		BossBar bar = playerBossbar.get(p);
		bar.addPlayer(p);
	}
}
