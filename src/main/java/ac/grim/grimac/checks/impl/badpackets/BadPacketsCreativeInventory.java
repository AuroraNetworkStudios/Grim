package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;

import java.util.ArrayList;

@CheckData(name = "BadPacketsCreativeInventory")
public class BadPacketsCreativeInventory extends Check implements PacketCheck {
    public BadPacketsCreativeInventory(GrimPlayer player) {
        super(player);
    }

    private static final String BYPASS_PERM = "aurora.bypass.badpacketscreativeinventory";

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.CREATIVE_INVENTORY_ACTION)) return;
        if(player.bukkitPlayer == null) return;
        if(player.bukkitPlayer.hasPermission(BYPASS_PERM)) return;
        if(player.disableGrim) return;

        WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
        ItemStack item = wrapper.getItemStack();

        if(item == null) {
            flagAndAlert("User sent null itemstack in creative inventory action.");
            event.setCancelled(true);
            return;
        }

        ItemStack newItem = ItemStack.builder().type(item.getType()).amount(1).build();

        if(item.getNBT() != null && !item.getNBT().isEmpty()) {
            flagAndAlert("User sent itemstack with NBT in creative inventory action.");
        }

        if(item.isStackable() && newItem.getMaxStackSize() < item.getAmount()) {
            flagAndAlert("User sent itemstack with amount greater than max stack size in creative inventory action.");
        }

        // we give the user his item but remove every custom NBT tag and enchant from it
        // this way we dont need to fear radius 50 creepers and stuff.
        wrapper.setItemStack(newItem);
    }
}
