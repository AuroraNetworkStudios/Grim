package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import org.bukkit.Material;
import org.bukkit.block.Block;


@CheckData(name = "CrashRedstoneWire")
public class CrashRedstoneWire extends Check implements PacketCheck {
    public CrashRedstoneWire(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT)) return;
        if(player.bukkitPlayer == null) return;
        if(player.disableGrim) return;

        WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
        Block block = player.bukkitPlayer.getWorld().getBlockAt(packet.getBlockPosition().getX(), packet.getBlockPosition().getY(), packet.getBlockPosition().getZ());
        //if(block.getType().equals(Material.AIR)) {  }

        String blockType = block.getType().name().replace("_LEGACY", "").toLowerCase();
        if(blockType.equals("redstone_wire") || blockType.equals("redstone")) {
            Block blockBelow = player.bukkitPlayer.getWorld().getBlockAt(packet.getBlockPosition().getX(), packet.getBlockPosition().getY() - 1, packet.getBlockPosition().getZ());
            if(blockBelow.getType().equals(Material.AIR)) {
                flagAndAlert("User placed a redstone wire in an invalid location");
                event.setCancelled(true);
            }

            if(blockBelow.getType().name().contains("TRAPDOOR")) {
                flagAndAlert("User placed a redstone wire on a trapdoor, this was a possible crash");
                event.setCancelled(true);
            }
        }
    }
}
