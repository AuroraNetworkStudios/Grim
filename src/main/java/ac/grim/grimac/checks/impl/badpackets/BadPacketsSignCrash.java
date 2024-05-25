package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import com.sun.tools.jdi.Packet;

import java.util.Arrays;
import java.util.Optional;

@CheckData(name = "BadPacketsSignCrash")
public class BadPacketsSignCrash extends Check implements PacketCheck {
    public BadPacketsSignCrash(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.UPDATE_SIGN)) return;


        WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event);
        Optional<String> longLines = Arrays.stream(packet.getTextLines()).filter(line -> line.length() > 100).findAny();

        if(longLines.isPresent()) {
            flagAndAlert("Long sign line: " + longLines.get() + " Length: " + longLines.get().length() + " > 100");
            event.setCancelled(true);
            player.onPacketCancel();
        }
    }
}