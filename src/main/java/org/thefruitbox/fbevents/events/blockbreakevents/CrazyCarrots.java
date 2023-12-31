package org.thefruitbox.fbevents.events.blockbreakevents;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.thefruitbox.fbevents.runnables.UpdateScoreboard;
import org.thefruitbox.fbevents.smalleventmanager.DailyEvents;

import net.coreprotect.CoreProtectAPI;

public class CrazyCarrots extends DailyEvents implements Listener {

	@EventHandler
	public void breakDiamond(BlockBreakEvent e) {
		Block b = (Block) e.getBlock();
		BlockData bdata = b.getBlockData();
		Player p = e.getPlayer();
		
		Material material = b.getType();
		
		//check for block type (aka. emerald_ore, diamond ore, etc)
		if(bdata instanceof Ageable) {
			if(material == Material.CARROTS) {
				Ageable age = (Ageable) bdata;
				if(age.getAge() == age.getMaximumAge()) {
					
					boolean contains = dev1.getPlayerParticipants(mainClass.getEventData().getStringList("participants")).contains(p);

					if(contains) {
						int currentScore = winningEventSection.getInt(p.getName());
						currentScore += 1;
						winningEventSection.set(p.getName(), currentScore);
						p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
						mainClass.saveEventDataFile();
						
						UpdateScoreboard updateScoreboard = new UpdateScoreboard();
						updateScoreboard.run();
					}
				}
			}		
		}
	}
}
