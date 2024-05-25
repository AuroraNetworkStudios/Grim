package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

@CheckData(name = "BadpacketsWindowCrash")
public class BadpacketsWindowCrash extends Check implements PacketCheck {
    public BadpacketsWindowCrash(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(player.disableGrim) return;
        if(!event.getPacketType().equals(PacketType.Play.Client.CLICK_WINDOW)) return;
        if(player.bukkitPlayer == null) return;

        WrapperPlayClientClickWindow packet = new WrapperPlayClientClickWindow(event);
        InventoryView inventoryView = player.bukkitPlayer.getOpenInventory();
        int maxSlots = 0;

        if(inventoryView.getBottomInventory().getType() == InventoryType.PLAYER && inventoryView.getTopInventory().getType() == InventoryType.CRAFTING) {
            maxSlots = inventoryView.countSlots() + 4;
        } else maxSlots = inventoryView.countSlots();

        if(packet.getSlot() > maxSlots) {
            flagAndAlert("Invalid slot: " + packet.getSlot() + " Max slots: " + maxSlots);
            event.setCancelled(true);
            player.onPacketCancel();
        }
    }
}