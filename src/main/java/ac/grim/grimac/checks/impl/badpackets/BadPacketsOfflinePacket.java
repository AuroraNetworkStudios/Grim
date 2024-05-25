package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;

import java.util.Arrays;
import java.util.List;


@CheckData(name = "BadPacketsOfflinePacket", configName = "BadPacketsOfflinePacket")
public class BadPacketsOfflinePacket extends Check implements PacketCheck {

    private static final List<String> defaultPacketTypes = Arrays.asList(
        "CHAT_COMMAND",
        "CHAT_COMMAND_UNSIGNED",
        "CHAT_MESSAGE",
        "CHAT_ACK",
        "CHAT_PREVIEW",
        "CHAT_SESSION_UPDATE"
    );

    public BadPacketsOfflinePacket(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        String packetType = event.getPacketType().toString();
        List<String> checkedPacketTypes = getConfig().getListElse(getConfigName() + ".setTrackingPosition", defaultPacketTypes);
        if(player.disableGrim) return;

        if(
            checkedPacketTypes.contains(packetType)
            && (
                player.bukkitPlayer == null
                ||
                !player.bukkitPlayer.isOnline()
            )
        ) {
            event.setCancelled(true);
            flagAndAlert("Player is offline while sending a blacklisted packet type, Is this a problem for you?");
        }
    }
}
