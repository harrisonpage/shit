package love.toad;
import org.bukkit.plugin.java.JavaPlugin;
import java.time.Instant;
import org.bukkit.*; // sigh
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.util.UUID;
import love.toad.Shit;
import love.toad.ShitConfig;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;

public class ShitCollector implements Runnable {
    private final Shit plugin;
    Logger log = Logger.getLogger("Minecraft");

    public ShitCollector(Shit plugin) {
        this.plugin = plugin;
    }

    public void run() {
        long now = Instant.now().toEpochMilli() / 1000;
        long lastShit;
        UUID uuid;
        String name;
        long delta;

        for(Player p : Bukkit.getOnlinePlayers()) {
            // players do not have to shit in the nether
            if (p.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                continue;
            }
            name = p.getName();
            uuid = p.getUniqueId();

            if (this.plugin.shits.containsKey(uuid)) {
                lastShit = this.plugin.shits.get(uuid);
                delta = now - lastShit;
                log.info(String.format("Retrieved %d (%d) for %s", lastShit, delta, name));
                if (delta > ShitConfig.THRESHOLD) {
                    p.spigot().sendMessage(TextComponent.fromLegacyText(ChatColor.of(ShitConfig.SHIT_COLOR) + " You need to shit"));
                }
            } else {
                log.info(String.format("Stored %d for %s", now, name));
                this.plugin.shits.put(uuid, now);
            }
        }
        this.plugin.rescheduleShitCollector();
    }
}
