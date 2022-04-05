package love.toad;
import org.bukkit.plugin.java.JavaPlugin;
import java.time.Instant;
import org.bukkit.*; // sigh
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.util.UUID;
import love.toad.Shit;

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

        Bukkit.broadcastMessage("RUNNING SHIT COLLECTOR");
        for(Player p : Bukkit.getOnlinePlayers()) {
            name = p.getName();
            uuid = p.getUniqueId();

            if (this.plugin.shits.containsKey(uuid)) {
                lastShit = this.plugin.shits.get(uuid);
                delta = now - lastShit;
                log.info(String.format("Retrieved %d (%d) for %s", lastShit, delta, name));
            } else {
                log.info(String.format("Stored %d for %s", now, name));
                this.plugin.shits.put(uuid, now);
            }
            log.info(p.getName());
        }
        this.plugin.rescheduleShitCollector();
    }
}
