package hus31n.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands
{
	JavaPlugin plugin;
	
	PlayerArray players;
	DataBase db;
	
	LoginCommand login;
	RegisterCommand register;
	LogoutCommand logout;
	
	Commands(PlayerArray players, JavaPlugin plugin)
	{
		this.plugin = plugin;
		this.db = new DataBase();
		this.players = players;
	}
	
	void createCommands()
	{
		this.login = new LoginCommand(this.players, this.db);
		this.register = new RegisterCommand(this.players, this.db);
		this.logout = new LogoutCommand(this.players);
	}
	
	void registerCommands()
	{
		plugin.getCommand("login").setExecutor(this.login);
		plugin.getCommand("logout").setExecutor(this.logout);
		plugin.getCommand("register").setExecutor(this.register);
	}
}

class LogoutCommand implements CommandExecutor
{
	PlayerArray players;
	DataBase db;
	
	LogoutCommand(PlayerArray players)
	{
		this.players = players;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (!(sender instanceof Player)) { return false; }
		
		ConnectedPlayer p = players.findPlayer((Player) sender);
		if (!p.login) { return false; }
		
		p.setLoginFalse();
		return true;
	}
	
}

class LoginCommand implements CommandExecutor
{
	PlayerArray players;
	DataBase db;
		
	LoginCommand(PlayerArray players, DataBase db)
	{
		this.players = players;
		this.db = db;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		
		if (!(sender instanceof Player)) { return false; }
	
		ConnectedPlayer p = players.findPlayer((Player) sender);
		if (p == null) { return false; }
		

		
		if (p.login) { return false; }
		if (args.length != 1)
		{
			p.player.sendMessage("sifre gir sisko");
			return false;
		}
		
		if (!db.isRegistered(p.player.getUniqueId()))
		{
			p.player.sendMessage("first register");
			return false;
		}
		
		if (!db.checkPassword(p.player.getUniqueId(), args[0]))
		{
			p.player.sendMessage("wrong pass");
			return false;
		}
		
		p.setLoginTrue();
		return true;
	}
}


class RegisterCommand implements CommandExecutor
{
	PlayerArray players;
	DataBase db;
	
	RegisterCommand(PlayerArray players, DataBase db)
	{
		this.players = players;
		this.db = db;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) { return false; }
		ConnectedPlayer p = players.findPlayer((Player) sender);
		if (p.login) { return false; }
		if (args.length != 1)
		{
			p.player.sendMessage("sifre gir sisko");
			return false;
		}
		
		if (db.isRegistered(p.player.getUniqueId()))
		{
			p.player.sendMessage("login pls");
			return false;
		}
		
		db.savePassword(p.player.getUniqueId(), args[0]);
		p.player.sendMessage("registered");
		p.setLoginTrue();
		
		return true;
	}
}