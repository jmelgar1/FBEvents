package org.thefruitbox.fbevents.events.blockbreakevents;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.thefruitbox.fbevents.runnables.UpdateScoreboard;
import org.thefruitbox.fbevents.smalleventmanager.DailyEvents;

import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;

public class Lumberjack extends DailyEvents implements Listener {
	
	//takes about a second to log a block. If player places diamond_ore. dont let player break that ore for 1-2 seconds.
	
	CoreProtectAPI api = getCoreProtect();

	@EventHandler
	public void breakDiamond(BlockBreakEvent e) {
		Block b = (Block) e.getBlock();
		Player p = e.getPlayer();
		
		Material material = b.getType();
		
		//check for block type (aka. emerald_ore, diamond ore, etc)
		if(material == Material.OAK_LOG || 
				material == Material.JUNGLE_LOG ||
				material == Material.SPRUCE_LOG ||
				material == Material.ACACIA_LOG ||
				material == Material.DARK_OAK_LOG ||
				material == Material.BIRCH_LOG ||
				material == Material.MANGROVE_LOG ||
				material == Material.CRIMSON_HYPHAE ||
				material == Material.WARPED_HYPHAE ||
				material == Material.STRIPPED_OAK_LOG || 
				material == Material.STRIPPED_JUNGLE_LOG ||
				material == Material.STRIPPED_SPRUCE_LOG ||
				material == Material.STRIPPED_ACACIA_LOG ||
				material == Material.STRIPPED_DARK_OAK_LOG ||
				material == Material.STRIPPED_BIRCH_LOG ||
				material == Material.STRIPPED_MANGROVE_LOG ||
				material == Material.STRIPPED_CRIMSON_HYPHAE ||
				material == Material.STRIPPED_WARPED_HYPHAE ||
				material == Material.CHERRY_LOG ||
				material == Material.STRIPPED_CHERRY_LOG) {
			
			boolean contains = dev1.getPlayerParticipants(mainClass.getEventData().getStringList("participants")).contains(p);
			
			boolean blockPlaced = false;
			
			b.getMetadata("placed");
			if(!b.getMetadata("placed").isEmpty()) {
				blockPlaced = true;
			}
			
			List<String[]> lookup = api.blockLookup(e.getBlock(), 86400);
			for(String[] result : lookup) {
				ParseResult parseResult = api.parseResult(result);
				if(parseResult.getPlayer() != null) {
					blockPlaced = true;
				}
			}
			
			if(contains) {
				if(!blockPlaced) {
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
	
	//set metadata to prevent players from gaining points from non naturally generated blocks
	@EventHandler 
	public void blockPlaced(BlockPlaceEvent e){
		Block b = e.getBlock();
		Material material = b.getType();
		
		if(material == Material.OAK_LOG || 
				material == Material.JUNGLE_LOG ||
				material == Material.SPRUCE_LOG ||
				material == Material.ACACIA_LOG ||
				material == Material.DARK_OAK_LOG ||
				material == Material.BIRCH_LOG ||
				material == Material.MANGROVE_LOG ||
				material == Material.CRIMSON_HYPHAE ||
				material == Material.WARPED_HYPHAE) {
			b.setMetadata("placed", new FixedMetadataValue(mainClass, "something"));
		}
	}
}
