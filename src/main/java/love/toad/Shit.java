package love.toad;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import java.util.logging.Logger;

public class Shit extends JavaPlugin implements Listener, CommandExecutor {
    Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("shit").setExecutor(this);
        log.info("[Shit] Enabled");
    }

    @Override
    public void onDisable() {
    }

    private shit(Player player) {
        ItemStack is = new ItemStack(Material.BROWN_DYE);
        ItemMeta newMetaName = is.getItemMeta();
        newMetaName.setDisplayName("shit");
        is.setItemMeta(newMetaName);

        Location spawnSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-2.5));
        player.getWorld().dropItem(spawnSpot, is);

        // explosive shit
        player.setVelocity(new Vector(0, 0.4, 0).multiply(1D));
        World w = player.getWorld();
        w.createExplosion(player.getLocation(), 0);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            this.shit(player);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("shit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                this.shit(player);
            }
        } else {
            sender.sendMessage("Not a console command");
            return false;
        }

        return true;
    }


}
