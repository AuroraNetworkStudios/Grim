package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import org.bukkit.event.inventory.InventoryType;

@CheckData(name = "BadPacketsDoubleInventoryOpen")
public class BadPacketsDoubleInventoryOpen extends Check implements PacketCheck {
    public BadPacketsDoubleInventoryOpen(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Server.OPEN_WINDOW)) return;
        if(player.bukkitPlayer == null) return;
        if(player.disableGrim) return;

        InventoryType inventoryType = player.bukkitPlayer.getOpenInventory().getType();
        if(inventoryType != InventoryType.PLAYER && inventoryType != InventoryType.CREATIVE && inventoryType != InventoryType.CRAFTING) {
            flagAndAlert("User is opening multiple inventories, if this is not a bug, please add an exception");
            event.setCancelled(true);
            player.bukkitPlayer.closeInventory();
        }
    }
}
