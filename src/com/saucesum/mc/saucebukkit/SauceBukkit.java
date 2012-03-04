package com.saucesum.mc.saucebukkit;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SauceBukkit extends JavaPlugin implements Listener, SauceListener {

	private Logger log = Logger.getLogger("Minecraft");

	private SauceSocket socket;

	@Override
	public void onEnable() {
		log.info("SauceBukkit enabled");
		socket = new SauceSocket("localhost", 8455);
		socket.setSauceListener(this);

		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		log.info("SauceBukkit disabled");
		socket.stop();
	}

	@Override
	public void emitReceived(Emit e) {
		if ("say".equals(e.getType())) {
			String msg = e.get("msg");
			getServer().broadcastMessage("<" + ChatColor.GREEN + "SauceBot" + ChatColor.WHITE + "> " + ChatColor.YELLOW + msg);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent event) {
		String username = event.getPlayer().getName().toLowerCase();
		String msg = event.getMessage();
		Emit e = Emit.newMsg("ravn_tm", username, msg);
		socket.send(e);
	}

	public static void main(String[] args) {
		ChatColor[] colors = ChatColor.values();
		for (ChatColor color : colors) {
			System.out.println(color.name() + ": '" + color.toString() + "'");
		}
	}



}
