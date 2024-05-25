package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;


@CheckData(name = "CrashNullAddress")
public class CrashNullAddress extends Check implements PacketCheck {
    public CrashNullAddress(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // most pointless check ever, what was i thinkingxd
        if(!PacketType.Play.Server.JOIN_GAME.equals(event.getPacketType())) { return; }
        if(player.bukkitPlayer == null) { return; }

        if(player.bukkitPlayer.getAddress() == null) {
            flagAndAlert("Player address is null?");
            event.setCancelled(true);
        }
    }
}
