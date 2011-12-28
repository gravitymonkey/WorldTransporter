package com.gravitymonkey.minecraft.bukkit.worldtransporter;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class Transport {

	public Transport(Player p, String[] args, WorldTransporter wt){

	 if (args == null || args.length < 1){
		 p.sendMessage(ChatColor.DARK_RED + "> " + ChatColor.GRAY + " type /wtlist to list worlds");
		 p.sendMessage(ChatColor.DARK_RED + "> " + ChatColor.GRAY + " type /goto <worldname> to go!");
		 return;
	 } else {
		 String name = args[0];
		 wt.logInfo("requesting world " + name);
		World wx = wt.getServer().getWorld(name);
		if (wx != null){
			PlayerInventory i = p.getInventory();
			ItemStack[] ii = i.getContents();
			int totalItems = 0;
			for (int w = 0; w < ii.length; w++){
				ItemStack is = ii[w];
				if (is != null){
					totalItems = totalItems + is.getAmount();
				}
			}
			if (totalItems > 0){
		      p.sendMessage(ChatColor.DARK_RED + "You can't travel with items.");
		      p.sendMessage(ChatColor.DARK_RED + "Please store your stuff in a chest.");
			} else {
	          Location loc = wx.getSpawnLocation();
	          p.teleport(loc);
	          i.clear();
	          p.sendMessage(ChatColor.DARK_RED + "TRANSPORTED: " + ChatColor.GRAY + "Welcome to " + args[0]);
			}
			
		} else {
			 p.sendMessage(ChatColor.DARK_RED + "> " + ChatColor.GRAY + "No world named " + args[0]);
			 p.sendMessage(ChatColor.DARK_RED + "> " + ChatColor.GRAY + " type /wtlist to list worlds");			
		}
      }
  }


}