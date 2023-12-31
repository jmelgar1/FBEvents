package org.thefruitbox.fbevents;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.thefruitbox.fbevents.bukkitevents.PlayerEvents;
import org.thefruitbox.fbevents.commands.fbehelp;
import org.thefruitbox.fbevents.commands.fbevote;
import org.thefruitbox.fbevents.commands.admin.configureStats;
import org.thefruitbox.fbevents.commands.admin.configureStatsBC;
import org.thefruitbox.fbevents.commands.admin.configureStatsCT;
import org.thefruitbox.fbevents.commands.admin.configureStatsMA;
import org.thefruitbox.fbevents.commands.admin.configureStatsRVB;
import org.thefruitbox.fbevents.commands.admin.fbeEndVote;
import org.thefruitbox.fbevents.commands.admin.fbeForceVote;
import org.thefruitbox.fbevents.commands.admin.fbeGiveXP;
import org.thefruitbox.fbevents.commands.admin.fbeSetXP;
import org.thefruitbox.fbevents.commands.admin.fbereload;
import org.thefruitbox.fbevents.commands.fbprofile;
import org.thefruitbox.fbevents.managers.DetermineEventData;
import org.thefruitbox.fbevents.runnables.EndEvent;
import org.thefruitbox.fbevents.runnables.SendDailyEventVote;
import org.thefruitbox.fbevents.runnables.SendVoteFinished;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener{

    //Main instance
    private static Main instance;

    //vault instance
    public Economy econ;

    //main config.yml
    FileConfiguration config;
    File cfile;

    //player data file
    private File playerDataFile;
    private FileConfiguration playerData;

    //small events file
    private File smallEventsFile;
    private FileConfiguration smallEvents;

    //event data file
    private File eventDataFile;
    private FileConfiguration eventData;

    public DetermineEventData dev1 = new DetermineEventData();

    //OnlyVanilla Prefix
    public String prefix = ChatColor.LIGHT_PURPLE + "[" +
            ChatColor.RED + "" + ChatColor.BOLD + "F" +
            ChatColor.GREEN + "" + ChatColor.BOLD + "B" +
            ChatColor.LIGHT_PURPLE + "] ";

    public ChatColor spongeColor = net.md_5.bungee.api.ChatColor.of("#dfff00");

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        return econ != null;
    }

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("(!) FBEvents Enabled");

        if (!setupEconomy()) {
            getLogger().severe("Vault dependency not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //get main config
        this.saveDefaultConfig();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        cfile = new File(getDataFolder(), "config.yml");

        //create playerdatafiles
        createPlayerDataFile();

        //create smalleventfiles
        createSmallEventsFile();

        //create eventdata
        createEventDataFile();

        //create eventdata
        createEventDataFile();

        //shuffle events and clear participation list
        dev1.ShuffleEvents(smallEvents);
        System.out.println("[FBEvents] Events Shuffled");
        dev1.clearParticipationList(instance);

        //plugin commands
        this.getCommand("fbprofile").setExecutor(new fbprofile());
        this.getCommand("fbevote").setExecutor(new fbevote());
        this.getCommand("fbehelp").setExecutor(new fbehelp());

        //admin commands
        this.getCommand("configurestats").setExecutor(new configureStats());
        this.getCommand("configurestatsbc").setExecutor(new configureStatsBC());
        this.getCommand("configurestatsct").setExecutor(new configureStatsCT());
        this.getCommand("configurestatsma").setExecutor(new configureStatsMA());
        this.getCommand("configurestatsrvb").setExecutor(new configureStatsRVB());
        this.getCommand("fbereload").setExecutor(new fbereload());
        this.getCommand("fbeforcevote").setExecutor(new fbeForceVote());
        this.getCommand("fbeendvote").setExecutor(new fbeEndVote());
        this.getCommand("fbegivexp").setExecutor(new fbeGiveXP());
        this.getCommand("fbesetxp").setExecutor(new fbeSetXP());

        //register events
        getServer().getPluginManager().registerEvents(new fbprofile(), this);

        //admin events
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new configureStats(), this);
        getServer().getPluginManager().registerEvents(new configureStatsCT(), this);
        getServer().getPluginManager().registerEvents(new configureStatsBC(), this);
        getServer().getPluginManager().registerEvents(new configureStatsMA(), this);
        getServer().getPluginManager().registerEvents(new configureStatsRVB(), this);
        getServer().getPluginManager().registerEvents(new fbevote(), this);

        //runnable events
        getServer().getPluginManager().registerEvents(new SendVoteFinished(), this);

        //create new runnable to start the event cycle
        SendDailyEventVote dailyVote = new SendDailyEventVote();
        dailyVote.run();
    }

    @Override
    public void onDisable() {
        System.out.println("(!) FBEvents Disabled");

        //end event cycle
        EndEvent endEvent = new EndEvent();
        endEvent.run();

    }

    //Main instance
    public static Main getInstance() {
        return instance;
    }

    //PLAYER DATA FILE
    public void savePlayerDataFile() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Couldn't save playerdata.yml");
        }
    }

    public void reloadPlayerDataFile() {
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

    }

    public FileConfiguration getPlayerData() {
        return this.playerData;
    }

    private void createPlayerDataFile() {
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if(!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            saveResource("playerdata.yml", false);
            System.out.println("(!) playerdata.yml created");
        }

        playerData = new YamlConfiguration();
        try {
            playerData.load(playerDataFile);
            System.out.println("(!) playerdata.yml loaded");
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    //SMALL EVENTS FILE
    public void saveSmallEventsFile() {
        try {
            smallEvents.save(smallEventsFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Couldn't save smallevents.yml");
        }
    }

    private void createSmallEventsFile() {
        smallEventsFile = new File(getDataFolder(), "smallevents.yml");
        if(!smallEventsFile.exists()) {
            smallEventsFile.getParentFile().mkdirs();
            saveResource("smallevents.yml", false);
            System.out.println("(!) smallevents.yml created");
        }

        smallEvents = new YamlConfiguration();
        try {
            smallEvents.load(smallEventsFile);
            System.out.println("(!) smallevents.yml loaded");
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getSmallEvents() {
        return this.smallEvents;
    }

    //EVENT DATA FILE
    public void saveEventDataFile() {
        try {
            eventData.save(eventDataFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Couldn't save eventdata.yml");
        }
    }

    public void reloadEventDataFile() {
        eventData = YamlConfiguration.loadConfiguration(eventDataFile);
    }

    private void createEventDataFile() {
        eventDataFile = new File(getDataFolder(), "eventdata.yml");
        if(!eventDataFile.exists()) {
            eventDataFile.getParentFile().mkdirs();
            saveResource("eventdata.yml", false);
            System.out.println("(!) eventdata.yml created");
        }

        eventData = new YamlConfiguration();
        try {
            eventData.load(eventDataFile);
            System.out.println("(!) eventdata.yml loaded");
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getEventData() {
        return this.eventData;
    }

    //send event notification every 20 minutes
    public void runEventNotif20Minutes() {
        SendDailyEventVote dailyVote = new SendDailyEventVote();

        //checks every 5 minutes
        dailyVote.runTaskLater(this, 6000);
    }
}