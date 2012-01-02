package com.gravitymonkey.minecraft.bukkit.worldtransporter;


import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldTransporter extends JavaPlugin
{
  private static final Logger log = Logger.getLogger("Minecraft");
  public static boolean isperm = false;
  public static final String LOG_TITLE = "[WorldTransporter]";
  private static final int MAX_WORLD = 16;
  
  public void onEnable() {
	this.reloadConfig();
    this.logInfo("Enabled!");

    this.reloadConfig();

    this.logInfo("number of worlds " + this.getConfig().getInt("numberOfWorlds"));
    
    if (this.getConfig().getInt("numberOfWorlds") <= 0){
    	this.logInfo("first time running WorldTransporter - creating config.yml");
    	buildConfigFromServer();
        this.saveConfig();
    } else {
    	this.logInfo("reading setup from config.yml");
    	this.logInfo("I have " + this.getConfig().getInt("numberOfWorlds") + " worlds in config");
        loadWorlds();
    }
//    this.reloadConfig();
  }
  
  public boolean isWorldVisible(String name){
	  this.logInfo("isWorldVisible gets " + name);
	  int x= this.getConfig().getInt("numberOfWorlds");
	  for (int w = 0; w < x; w++){
		  String n = (String)this.getConfig().get("worlds.world_" + w + ".name");
		  this.logInfo(w + ":" + name + " - " + n);
		  if (n.trim().toLowerCase().equals(name.toLowerCase().trim())){
			  String b = (String)this.getConfig().get("worlds.world_" + w + ".visible");
			  if (b != null){
				  if (b.toLowerCase().equals("true")){
					  return true;
				  }
			  }
		  }
	  }
	  return false;
  }
  
  private void buildConfigFromServer(){
	  //delete stuff first
	this.getConfig().set("numberOfWorlds", null);
	for (int w = 0; w < MAX_WORLD; w++){
		this.getConfig().set("worlds.world_" + w + ".name", null);
		this.getConfig().set("worlds.world_" + w + ".environment", null);
		this.getConfig().set("worlds.world_" + w + ".seed", null);
		this.getConfig().set("worlds.world_" + w + ".visible", null);
		this.getConfig().set("worlds.world_" + w, null);
	}
	
	//now rebuild again, just using the worlds loaded in the server
  	List worlds = getServer().getWorlds();
	this.getConfig().set("numberOfWorlds", worlds.size());
	for (int w = 0; w < worlds.size(); w++){
       String name = ((World)worlds.get(w)).getName();
       String environment = ((World)worlds.get(w)).getEnvironment().name();
       long seed = (((World)worlds.get(w)).getSeed());
       this.getConfig().set("worlds.world_" + w + ".name", name);
       this.getConfig().set("worlds.world_" + w + ".environment", environment);
       if (environment.toUpperCase().equals("NORMAL")){
    	   this.getConfig().set("worlds.world_" + w + ".visible", "true");
       } else {
    	   this.getConfig().set("worlds.world_" + w + ".visible", "false");    	   
       }
       this.getConfig().set("worlds.world_" + w + ".seed", seed);
       this.logInfo("found " + name + "  " + environment + "  " + seed);
     }

  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
	  
	this.logInfo("onCommand, " + label);  
    if ((sender instanceof ConsoleCommandSender)) {
    	if (label.toLowerCase().equals("wtdelete")){
    		if (args.length == 0){
	        	this.logInfo("stop running a world:");
	    		List lx = this.getServer().getWorlds();
	    		for (int w = 0; w < lx.size(); w++){
	    			World wp = (World)lx.get(w);
	    			this.logInfo(" " + (w + 1) + "]  " +  wp.getName());
	    		}
    		} else {
	    		List lx = this.getServer().getWorlds();
	    		World thisone = null;
	    		World anotherone = null;
	    		for (int w = 0; w < lx.size(); w++){
	    			World wp = (World)lx.get(w);
	    			if (args[0].equals(wp.getName())){
	    				thisone = wp;
	    			} else {
	    				anotherone = wp;
	    			}
	    		}
	    		if (thisone == null){
	    			this.logInfo("can't find world " + args[0]);
	    		} else {
	    			List p = thisone.getPlayers();
	    			int counter = 10;
	    			if (p.size() > 0){
	    				while (counter > 0){
			    			for (int q = 0; q < p.size(); q++){
			    				Player pp = (Player)p.get(q);
			    				if (counter < 3){
		    						pp.sendMessage(ChatColor.DARK_RED + ">>" + counter + " sec to " + anotherone.getName());
			    				} else {
			    					if (anotherone != null){
					    				pp.sendMessage(ChatColor.DARK_RED + "This World is closing in " + counter);				    						    					
			    					} else {
					    				pp.sendMessage(ChatColor.DARK_RED + "You will be logged off in " + counter);				    						    								    						
			    					}
			    				}
			    				if (counter == 1){
			    					if (anotherone != null){
			    						Location l = anotherone.getSpawnLocation();
			    						pp.teleport(l);
			    					} else {
			    						pp.kickPlayer("log back in to another world");
			    					}
			    				}
			    			}
			    			try {
								Thread.sleep(1000);
			    			} catch (Exception e){
			    				this.logInfo(e.toString());
			    				System.out.println(e);
			    			}
			    			counter--;
	    				}
	    			}
	    			try {
		    			boolean unloaded = this.getServer().unloadWorld(thisone, true);
		    			this.logInfo(args[0] + " unloaded from server? " + unloaded);
		    			this.buildConfigFromServer();
		    			this.saveConfig();
		    			this.reloadConfig();
	    			} catch (Exception eo){
	    				System.out.println(eo);
	    				this.logInfo(eo.toString());
	    			}
	    		}
    		}
    	}
      return false;
    }
   
    Player player = (Player)sender;
    
    if (!(sender instanceof Player)) return false;

    if (hasPerm(player, label.toLowerCase())){
      if (label.equalsIgnoreCase("wtcreate")){
    	new Create(player, args, this);        
      } else if (label.equalsIgnoreCase("wtlist")){
      	  new ListWorlds(player, this);
      } else if (label.equalsIgnoreCase("goto")){
      	  new Transport(player, args, this);
      }
    } else {
		player.sendMessage(ChatColor.DARK_RED + "You don't have permission to " + label);				    						    					
    }
    return true;
  }

  private boolean hasPerm(Player player, String node)
  {

	  if (player.isOp()){
		  return true;
	  } else {
		  Permission p = new Permission("worldtransporter." + node);
		  if (player.hasPermission(p)) {
			  return true;
		  }
	  }

	  return false;
  }

  
  public void loadWorlds() {
    this.logInfo("Loading worlds");
    int max = MAX_WORLD;//maximum number of worlds we'll try to load
    int counter = 0;
		for (int w = 0; w < max; w++){
			String name = this.getConfig().getString("worlds.world_" + w + ".name");
			if (name != null){
		        WorldCreator creator = new WorldCreator(name);
		        World ww = this.getServer().createWorld(creator);
		        if (ww != null){
//		        	ww.setDifficulty(arg0)
//		        	ww.setPVP();
		        	this.logInfo("Loaded world " + name);
		        }
		        counter++;
			} else {
				break;
			}
		}

	this.logInfo("loaded " + counter + " worlds");
	 this.getConfig().set("numberOfWorlds", "" + counter);
  }

  public void onDisable()
  {
	  this.saveConfig();
	  this.logInfo("Disabled!");
    
  }
  
  public void logInfo(String s){
	  log.info(WorldTransporter.LOG_TITLE + " : " + s);
  }
  
  
}