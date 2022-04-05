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

    public static boolean isPlayerInWater(Player p) {
        Block block = p.getLocation().getBlock();
        if((block.getRelative(BlockFace.EAST).getType().equals(Material.WATER) | block.getRelative(BlockFace.WEST).getType().equals(Material.WATER) | block.getRelative(BlockFace.NORTH).getType().equals(Material.WATER) | block.getRelative(BlockFace.SOUTH).getType().equals(Material.WATER) | block.getRelative(BlockFace.EAST).getType().equals(Material.WATER) | block.getRelative(BlockFace.WEST).getType().equals(Material.WATER) | block.getRelative(BlockFace.NORTH).getType().equals(Material.WATER) | block.getRelative(BlockFace.SOUTH).getType().equals(Material.WATER) )) {
            return true;
        }
        return false;
    }
}