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
import love.toad.ShitConfig;
import java.time.Instant;
import org.bukkit.event.player.PlayerJoinEvent;
import love.toad.ShitUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;

public class Shit extends JavaPlugin implements Listener, CommandExecutor {
    private static final String HELP_MESSAGE = "Usage: /shit";

    Logger log = Logger.getLogger("Minecraft");

    // player => time of last shit in seconds since epoch
    public final HashMap<UUID, Long> shits = new HashMap<>();

    // <deuce> BROWN
    public static final Color BROWN = Color.fromRGB(0xD2691E);


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("shit").setExecutor(this);
        log.info("[Shit] Enabled");
    }

    @Override
    public void onDisable() {
        log.info("[Shit] Disabled");
    }

    public void shit(Player player, boolean explosive) {
        long lastShit = shits.get(player.getUniqueId());
        long delta = ShitUtils.getSecondsSinceEpoch() - lastShit;
        if (delta < ShitConfig.THRESHOLD) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + "You just took a shit, wait a while"));
            return;
        }

        if (ShitUtils.isPlayerInWater(player)) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + "Can't shit in the water"));
            return;
        }

        if (player.isDead()) {
            // not sure how this could happen but whatever
            return;
        }

        if (! player.isOnline()) {
            // not sure how this could happen but whatever
            return;
        }

        // take a shit
        ItemStack is = new ItemStack(Material.BROWN_DYE);
        ItemMeta newMetaName = is.getItemMeta();
        String s = player.getName().substring(player.getName().length() - 1) == "s" ? "" : "s";
        newMetaName.setDisplayName(String.format("%s'%s shit", player.getName(), s));
        is.setItemMeta(newMetaName);

        // leave a shit
        Location spawnSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-2.5));
        player.getWorld().dropItem(spawnSpot, is);

        // fling poo
        player.setVelocity(new Vector(0, 0.4, 0).multiply(1D));

        // behind player
        World world = player.getWorld();
        Location fireworksSpot = player.getLocation().add(player.getLocation().getDirection().multiply(-5));

        // hee haw
        world.playSound(fireworksSpot, Sound.ENTITY_DONKEY_DEATH, 1.0F, 1.0F);

        // we have explosive
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

        // update time of last shit
        shits.put(player.getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    // reset last shit time on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        shits.put(event.getPlayer().getUniqueId(), ShitUtils.getSecondsSinceEpoch());
    }

    // eat shit
    @EventHandler
    public void onRightClick(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        ItemStack itemInMainHand = p.getInventory().getItemInMainHand();

        if((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND)
        {
            if(itemInMainHand.getType() == Material.BROWN_DYE) {
                p.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + "You eat shit"));
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2, true, true)); // 20 ticks a second for 60 seconds
                itemInMainHand.setAmount(itemInMainHand.getAmount()-1);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GOAT_EAT, 1.0F, 1.0F);
            }
        }
    }

    // handle /shit
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("shit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    this.shit(player, false);
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("explosive")) {
                        this.shit(player, true);
                        return true;
                    } else if (args[0].equalsIgnoreCase("players")) {
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(String.format("%s last shit %d seconds ago",
                                p.getName(), ShitUtils.getSecondsSinceEpoch() - shits.get(player.getUniqueId())));
                        }
                        return true;
                    } else {
                        player.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + HELP_MESSAGE));
                    }
                } else {
                    player.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + HELP_MESSAGE));
                }
            }
        } else {
            sender.sendMessage("Not a console command");
            return false;
        }
        return true;
    }
}
