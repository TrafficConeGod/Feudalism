package feudalism.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import feudalism.Registry;
import feudalism.Util;
import feudalism.object.GridCoord;
import feudalism.object.Realm;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GridCoord fromCoord = GridCoord.getFromLocation(event.getFrom());
        GridCoord toCoord = GridCoord.getFromLocation(event.getTo());
        if (fromCoord == toCoord) {
            return;
        }
        if (fromCoord.getOwnerOrNull() != toCoord.getOwnerOrNull()) {
            if (toCoord.hasOwner()) {
                Realm owner = toCoord.getOwnerOrNull();
                Util.sendActionBarMessage(player, owner.getName());
            } else {
                Util.sendActionBarMessage(player, "Wilderness");
            }
        }
    }
}
