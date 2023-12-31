package org.thefruitbox.fbevents.events.craftingevents;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.thefruitbox.fbevents.runnables.UpdateScoreboard;
import org.thefruitbox.fbevents.smalleventmanager.DailyEvents;

public class CountlessCakes extends DailyEvents implements Listener {
	
	@EventHandler
	public void craftBread(CraftItemEvent event) {
		
		Player p = (Player) event.getWhoClicked();
		
		ItemStack craftedItem = event.getInventory().getResult();
		
		Inventory inv = event.getInventory();
		
		ClickType clickType = event.getClick();
		
		int realAmount = craftedItem.getAmount();
			
		boolean contains = dev1.getPlayerParticipants(mainClass.getEventData().getStringList("participants")).contains(p);
		
		if(contains) {
			
			if(craftedItem.getType() == Material.CAKE) {
				
				if(clickType.isShiftClick()) {
					int lowerAmount = craftedItem.getMaxStackSize() + 1000;
					for(ItemStack actualItem : inv.getContents()) {
						if(!actualItem.getType().isAir() && lowerAmount > actualItem.getAmount() && !actualItem.getType().equals(craftedItem.getType())) {
							lowerAmount = actualItem.getAmount();
						}
						
						realAmount = lowerAmount * craftedItem.getAmount();
					}
				}
			
			int currentScore = winningEventSection.getInt(p.getName());
			int newScore = currentScore += realAmount;
			winningEventSection.set(p.getName(), newScore);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
			mainClass.saveEventDataFile();
			
			UpdateScoreboard updateScoreboard = new UpdateScoreboard();
			updateScoreboard.run();
			
			}
		}
	}
}
