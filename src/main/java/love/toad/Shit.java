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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

public class Shit extends JavaPlugin implements Listener, CommandExecutor {
    Logger log = Logger.getLogger("Minecraft");
    public final HashMap<UUID, Long> shits = new HashMap<>();
    public static final Color BROWN = Color.fromRGB(0xD2691E);
    public static final int PISS_DELAY = 20;

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

    public void shit(Player player, boolean explosive) {
        if (ShitUtils.isPlayerInWater(player)) {
            log.info(String.format("%s is in water, cannot shit", player.getName()));
            return;
        }

        if (player.isDead()) {
            log.info(String.format("%s is dead, cannot shit", player.getName()));
            return;
        }

        if (! player.isOnline()) {
            // not sure how this happened
            return;
        }

        ItemStack is = new ItemStack(Material.BROWN_DYE);
        ItemMeta newMetaName = is.getItemMeta();
        newMetaName.setDisplayName("shit");
        is.setItemMeta(newMetaName);

        Location spawnSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-2.5));
        player.getWorld().dropItem(spawnSpot, is);

        player.setVelocity(new Vector(0, 0.4, 0).multiply(1D));
        World world = player.getWorld();
        Location fireworksSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-5));
        world.playSound(fireworksSpot, Sound.ENTITY_DONKEY_DEATH, 1.0F, 1.0F);
        if (explosive) {
            world.createExplosion(player.getLocation(), 0);

            Firework firework = (Firework) world.spawnEntity(fireworksSpot, EntityType.FIREWORK);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.setPower(0);
            fireworkMeta.addEffect(FireworkEffect.builder()
                    .withColor(BROWN, BROWN)
                    .flicker(true)
                    .build());
            firework.setFireworkMeta(fireworkMeta);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, firework::detonate, 1);
        }

        shits.put(player.getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            this.shit(player, false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // reset shit on join
        shits.put(event.getPlayer().getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();

        if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
        {
            if(itemInMainHand.getType() == Material.BROWN_DYE) {
                Bukkit.broadcastMessage(String.format("%s ate his own shit", p.getName()));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60*20, 2, true, true)); // 20 ticks a second for 60 seconds
                itemInMainHand.setAmount(itemInMainHand.getAmount()-1);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GOAT_EAT, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("piss")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location location = player.getLocation().add(player.getLocation().getDirection().multiply(2));
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0.2, 2, 0.2);
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0D, 0D, 0D);
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0D, 0D, 0D);
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0D, 0D, 0D);
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0.3, 2, 0.3)
                player.getWorld().spawnParticle(Particle.FALLING_HONEY, location, PISS_DELAY, 0D, 0D, 0D);


                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT​, 5.0F, 1.0F);
            }
        } else if (label.equalsIgnoreCase("shit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("now")) {
                        this.shit(player, false);
                        return true;
                    } else if (args[0].equalsIgnoreCase("explosive")) {
                        this.shit(player, true);
                        return true;
                    } else if (args[0].equalsIgnoreCase("players")) {
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(String.format("%s last shit %d seconds ago",
                                p.getName(), ShitUtils.getSecondsSinceEpoch() - shits.get(player.getUniqueId())));
                        }
                        return true;
                    }
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
