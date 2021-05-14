package hus31n.main;

import org.bukkit.plugin.java.JavaPlugin;

public class Auth extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		LoginListener l = new LoginListener(this);
		getServer().getPluginManager().registerEvents(l, this);
		Object[] onlines = getServer().getOnlinePlayers().toArray();
		l.connectedPlayers.registerOnlinePlayers(onlines);
		Commands cmds = new Commands(l.connectedPlayers, this);
		l.db = cmds.db;
		cmds.createCommands();
		cmds.registerCommands();
	}
	
	@Override
	public void onDisable()
	{

	}
	
}
