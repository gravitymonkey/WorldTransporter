package com.gravitymonkey.minecraft.bukkit.worldtransporter;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.List;


public class ListWorlds {

	public ListWorlds(Player p, WorldTransporter wt){
      
      p.sendMessage(ChatColor.DARK_RED + "List of the Worlds:");
      List ww = wt.getServer().getWorlds();
      for (int w = 0; w < ww.size(); w++){
    	  World q = (World)ww.get(w);    	  
          p.sendMessage(ChatColor.DARK_RED + " > "+ ChatColor.GRAY + q.getName());
    	  List players = q.getPlayers();
    	  for (int a = 0; a < players.size(); a++){
    		  String ms = ((Player)players.get(a)).getDisplayName();
    		  p.sendMessage("     "+ ChatColor.GRAY + "[" + ms + "]");    		  
    	  }
      }
      p.sendMessage(ChatColor.DARK_RED + "type /goto <worldname> to go!");
      
  }


}