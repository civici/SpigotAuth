package hus31n.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

public class ConnectedPlayer 
{
	boolean login;
	Player player;
	ItemStack[] stack;
	
	ConnectedPlayer(Player player)
	{
		this.login = false;
		this.player = player;
	}
	
	void setLoginFalse()
	{
		this.login = false;
		if (this.player.getInventory().isEmpty())
		{
			return;
		}
		this.stack = this.player.getInventory().getContents().clone();
		this.player.getInventory().clear();
	}

	void setLoginTrue()
	{
		this.login = true;
		if (this.stack == null || this.stack.length == 0)
		{
			return;
		}
		
		for (ItemStack item : this.stack)
		{
			if (item == null)
			{
				continue;
			}
			this.player.getInventory().addItem(item);
		}
		this.stack = null;
	}
	
	void disconnectPlayer()
	{	
		if (this.login) { return; }
		if (this.stack != null)
		{
			for (ItemStack item : this.stack)
			{
				if (item == null)
				{
					continue;
				}
				this.player.getInventory().addItem(item);
			}
			this.stack = null;
		}
		
		this.login = false;
	}
}

class PlayerArray
{
	ArrayList<ConnectedPlayer> array;
	
	PlayerArray()
	{
		this.array = new ArrayList<ConnectedPlayer>();
	}
	
	boolean isLogined(Player player)
	{
		for (ConnectedPlayer p : this.array)
		{
			if (p.player == player && p.login)
			{
				return true;
			}
		}
		return false;
	}
	
	ConnectedPlayer findPlayer(Player player)
	{
		for (ConnectedPlayer p : this.array)
		{
			if (p.player == player)
			{
				return p;
			}
		}
		return null;
	}
	
	void registerOnlinePlayers(Object[] players)
	{
		for (Object player : players)
		{
			ConnectedPlayer p = new ConnectedPlayer((Player) player);
			this.array.add(p);
			p.setLoginFalse();
		}
	}
}

class LoginListener implements Listener
{ 
	PlayerArray connectedPlayers = new PlayerArray();
	JavaPlugin plugin;
	DataBase db;
	
	
	LoginListener(JavaPlugin plugin)
	{
		this.plugin = plugin;
	}
	

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		ConnectedPlayer p = new ConnectedPlayer(e.getPlayer());
		if (db.isRegistered(p.player.getUniqueId()))
		{
			p.player.sendMessage("pls login");
		} else {
			p.player.sendMessage("pls register");
		}
		
		this.connectedPlayers.array.add(p);
		p.setLoginFalse();;
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e)
	{
		ConnectedPlayer p = this.connectedPlayers.findPlayer(e.getPlayer());
		p.disconnectPlayer();
		this.connectedPlayers.array.remove(p);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if (!this.connectedPlayers.isLogined(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (!this.connectedPlayers.isLogined(e.getPlayer()))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerCommand(AsyncPlayerChatEvent e)
	{
		if (this.connectedPlayers.findPlayer(e.getPlayer()).login) { return; }
		
		e.getPlayer().sendMessage("test");
		
		if (e.getMessage().startsWith("/register") || e.getMessage().startsWith("/login") )
		{
			return;
		}
		
		e.setCancelled(true);
	}
}


class DataBase
{
	
	DataBase()
	{
		File f = new File("database.txt");
		if (!f.exists())
		{
			System.out.println("creating file");
			try {
				f.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	void savePassword(UUID id, String pass)
	{

		try {
			FileWriter fw = new FileWriter("database.txt");
			fw.write(id.toString() + " " + pass + "\n");
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	boolean checkPassword(UUID id, String passInput)
	{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("database.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		while (true)
		{
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null)
			{
				break;
			}
			
			if (line.equals(id.toString() + " " + passInput))
			{
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	boolean isRegistered(UUID id)
	{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("database.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = null;
		while (true)
		{
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null)
			{
				break;
			}
			
			if (line.contains(id.toString()))
			{
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

