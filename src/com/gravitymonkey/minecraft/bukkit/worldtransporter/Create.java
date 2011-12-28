package com.gravitymonkey.minecraft.bukkit.worldtransporter;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class Create
{
  public Create(Player p, String[] args, WorldTransporter wt){
	  
	  try {
		wt.logInfo("in create, with args " + args.length);
	    if (args.length < 1) {    	
	      p.sendMessage(ChatColor.DARK_RED + WorldTransporter.LOG_TITLE + " : " + ChatColor.RED + "Use /wtcreate [name] <type> <seed>.");
	      return;
	    } else {
	
	    	String name = args[0];
	    	String envs = "NORMAL";
	    	
	    	if (args.length > 1){
		    	if (args[1].equalsIgnoreCase("NETHER")){
		    		envs = "NETHER";
		    	} else if (args[1].equalsIgnoreCase("THE_END")){
		    		envs = "THE_END";
		    	} else {
		    		envs = "NORMAL";
		    	}
	    	}
	    	
	    			
	    	long seed = 0l;
	    	if (args.length > 2) {
	    		try {
	    			long ml = Long.parseLong(args[2]);
	    			seed = ml;
	    		} catch (Exception e){
	    			seed = 0l;
	    			wt.logInfo(e.toString());    	
	    		}
	    	}
	    		
	    	if (seed == 0l){
	    		seed = (new java.util.Random()).nextLong();
	    	}
	    		
	    	
	      World.Environment env = World.Environment.valueOf(envs);
	      
	      wt.logInfo(name + " " + envs + " " + seed);
	      
	      p.sendMessage(ChatColor.DARK_RED + "[WT]: " + ChatColor.RED + "Building a whole new world named \"" + name + "\"");
	      p.sendMessage(ChatColor.DARK_RED + "[WT]: " + ChatColor.RED + "Expect some lagtastic lag and possible log-outs!");
	     
	      WorldCreator cw = new WorldCreator(name);    	  
	    	  	cw.seed(seed);
	    	  	cw.environment(env);    	  
	
	      World x = p.getServer().createWorld(cw);
	    	  	
	    	  	int w = wt.getConfig().getInt("numberOfWorlds");
	            String bname = x.getName();
	            String environment = x.getEnvironment().name();
	            long fseed = x.getSeed();
	            wt.getConfig().set("worlds.world_" + w + ".name", bname);
	            wt.getConfig().set("worlds.world_" + w + ".environment", environment);
	            wt.getConfig().set("worlds.world_" + w + ".seed", fseed);
	           
	            wt.getConfig().set("numberOfWorlds",(w + 1) + "");            
	            wt.saveConfig();
	            
		        p.sendMessage(ChatColor.DARK_RED + "[WorldTransporter]: " + ChatColor.GREEN + "Done!");
	    }
	  } catch (Exception ee){
		wt.logInfo(ee.toString());
		ee.printStackTrace(System.out);
	  }
	  
	    
  }


}