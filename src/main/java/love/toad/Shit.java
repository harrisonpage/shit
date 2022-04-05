package love.toad;

import org.bukkit.*; // sigh
import org.bukkit.Color;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import java.util.logging.Logger;
import org.bukkit.scheduler.BukkitScheduler;
import java.util.HashMap;
import java.util.UUID;
import love.toad.ShitCollector;
import love.toad.ShitConfig;
import java.time.Instant;
import org.bukkit.event.player.PlayerJoinEvent;
import love.toad.ShitUtils;

public class Shit extends JavaPlugin implements Listener, CommandExecutor {
    Logger log = Logger.getLogger("Minecraft");
    public final HashMap<UUID, Long> shits = new HashMap<>();
    public static final Color BROWN = Color.fromRGB(0xD2691E);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("shit").setExecutor(this);
        log.info("[Shit] Enabled");
        this.rescheduleShitCollector();
    }

    @Override
    public void onDisable() {
        log.info("[Shit] Disabled");
    }

    public void rescheduleShitCollector() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new ShitCollector(this), ShitConfig.SCHEDULER_DELAY);
    }

    public void shit(Player player) {
        if (ShitUtils.isPlayerInWater(player)) {
            log.info(String.format("%s is in water, cannot shit", player.getName()));
            return;
        }

        ItemStack is = new ItemStack(Material.BROWN_DYE);
        ItemMeta newMetaName = is.getItemMeta();
        newMetaName.setDisplayName("shit");
        is.setItemMeta(newMetaName);

        Location spawnSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-2.5));
        player.getWorld().dropItem(spawnSpot, is);

        // explosive shit
        player.setVelocity(new Vector(0, 0.4, 0).multiply(1D));
        World world = player.getWorld();
        world.createExplosion(player.getLocation(), 0);

        Location fireworksSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-5));

        Firework firework = (Firework) world.spawnEntity(fireworksSpot, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(0);
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(BROWN, BROWN)
                .flicker(true)
                .build());
        firework.setFireworkMeta(fireworkMeta);
        world.playSound(fireworksSpot, Sound.ENTITY_DONKEY_DEATH, 1.0F, 1.0F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, firework::detonate, 1);

        shits.put(player.getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            this.shit(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // reset shit on join
        shits.put(event.getPlayer().getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("shit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1 && args[0].equalsIgnoreCase("now")) {
                    this.shit(player);
                    return true;
                }
                long lastShit = shits.get(player.getUniqueId());
                long delta = ShitUtils.getSecondsSinceEpoch() - lastShit;
                player.sendMessage(String.format("Last shit was %d seconds ago", delta));
            }
        } else {
            sender.sendMessage("Not a console command");
            return false;
        }

        return true;
    }


}
