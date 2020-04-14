package de.marvin2k0.sit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;

public class SitPlugin extends JavaPlugin implements CommandExecutor, Listener
{
    private HashMap sits = new HashMap<String, ArmorStand>();

    @Override
    public void onEnable()
    {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.getCommand("sit").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cNur fuer Spieler!");

            return true;
        }

        Player player = (Player) sender;

        if (!player.isOnGround())
        {
            player.sendMessage("§cDas geht nur, wenn du auf dem Boden bist!");

            return true;
        }

        if (this.sits.containsKey(player.getName()))
        {
            player.sendMessage("§cDu sitzt schon!");

            return true;
        }

        Location location = player.getLocation().add(0, -1.6, 0);
        World world = location.getWorld();

        Entity armorStand = world.spawnEntity(location, EntityType.ARMOR_STAND);
        ArmorStand chair = (ArmorStand) armorStand;

        chair.addPassenger(player);
        chair.setGravity(false);
        chair.setVisible(false);

        this.sits.put(player.getName(), chair);

        return true;
    }

    @EventHandler
    public void onChair(EntityDismountEvent event)
    {
        if (event.getDismounted() instanceof ArmorStand && event.getEntity() instanceof Player)
        {
            ArmorStand armorStand = (ArmorStand) event.getDismounted();
            armorStand.remove();

            this.sits.remove(event.getEntity().getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        if (this.sits.containsKey(event.getPlayer()))
        {
            ArmorStand chair = (ArmorStand)this.sits.get(event.getPlayer().getName());
            chair.remove();

            this.sits.remove(event.getPlayer());
        }
    }
}
