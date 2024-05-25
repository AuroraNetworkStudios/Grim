package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import net.kyori.adventure.text.Component;

@CheckData(name = "BadPacketsInfinityMove")
public class BadPacketsInfinityMove extends Check implements PacketCheck {
    public BadPacketsInfinityMove(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(player.bukkitPlayer == null) return;

        if(
            event.getPacketType().equals(PacketType.Play.Client.PLAYER_FLYING)
            || event.getPacketType().equals(PacketType.Play.Client.STEER_BOAT)
            || event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE)
        ) {
            float x = player.bukkitPlayer.getLocation().getBlockX();
            float z = player.bukkitPlayer.getLocation().getBlockZ();
            float y = player.bukkitPlayer.getLocation().getBlockY();

            if(Double.isInfinite(x) || Double.isInfinite(z) || Double.isInfinite(y)) {
                event.setCancelled(true);
                player.onPacketCancel();
                player.disconnect(Component.text("Itt mi a terv? \n" + x + ", " + y + ", " + z));
            }
        }
    }
}
