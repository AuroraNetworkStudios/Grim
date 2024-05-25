package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

@CheckData(name = "BadPacketsChunkUnloadInventory")
public class BadPacketsChunkUnloadInventory extends Check implements PacketCheck {
    public BadPacketsChunkUnloadInventory(GrimPlayer player) { super(player); }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Server.UNLOAD_CHUNK)) { return; }

        WrapperPlayServerUnloadChunk wrapper = new WrapperPlayServerUnloadChunk(event);
        if(getPlayer().bukkitPlayer == null) { return; }

        Chunk chunk = getPlayer().bukkitPlayer.getWorld().getChunkAt(wrapper.getChunkX(), wrapper.getChunkZ());

        Entity[] entities = chunk.getEntities(); 
        for(Entity entity : entities) {
            if(!(entity instanceof InventoryHolder)) { continue; }
            InventoryHolder holder = (InventoryHolder) entity;
            Inventory inventory = holder.getInventory();

            if(inventory == null) { continue; }

            List<HumanEntity> viewers = inventory.getViewers();
            for(HumanEntity viewer : viewers) {
                viewer.closeInventory();
                flagAndAlert("Chunk unload inventory exploit?");
            }
        }
    }
}