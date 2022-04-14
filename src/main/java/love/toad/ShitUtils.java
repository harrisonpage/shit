package love.toad;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.time.Instant;

public class ShitUtils {
    public static long getSecondsSinceEpoch() {
        return Instant.now().toEpochMilli() / 1000;
    }
}
