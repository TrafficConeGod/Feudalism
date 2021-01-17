package feudalism.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import feudalism.Registry;
import feudalism.object.GridCoord;

public class BlockListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(
            Registry.getInstance().getEventStatus("block_place", event.getPlayer(), GridCoord.getFromLocation(event.getBlock().getLocation()))
        );
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(
            Registry.getInstance().getEventStatus("block_break", event.getPlayer(), GridCoord.getFromLocation(event.getBlock().getLocation()))
        );
    }
}
