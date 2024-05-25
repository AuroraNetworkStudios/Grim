package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import org.bukkit.event.inventory.InventoryType;


@CheckData(name = "BadPacketsBlockBreakInventory")
public class BadPacketsBlockBreakInventory extends Check implements PacketCheck {
    public BadPacketsBlockBreakInventory(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.PLAYER_DIGGING)) return;
        if(player.bukkitPlayer == null) return;
        if(player.disableGrim) return;

        InventoryType inventoryType = player.bukkitPlayer.getOpenInventory().getType();
        if(inventoryType != InventoryType.PLAYER && inventoryType != InventoryType.CREATIVE && inventoryType != InventoryType.CRAFTING) {
            flagAndAlert("User is sending digging packets while in an inventory.");
            player.bukkitPlayer.closeInventory();
        }
    }
}
