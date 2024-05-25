package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;


@CheckData(name = "BadPacketsItemDrop")
public class BadPacketsItemDrop extends Check implements PacketCheck {
    public BadPacketsItemDrop(GrimPlayer player) {
        super(player);
    }

    private Long lastItemDrop;
    private int verbose;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.PLAYER_DIGGING)) return;

        WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
        if(!packet.getAction().equals(DiggingAction.DROP_ITEM)) return;

        if(lastItemDrop != null) {
            long delta = System.currentTimeMillis() - lastItemDrop;

            if(delta < 35) {
                verbose++;
                if(verbose > 5) {
                    flagAndAlert("Too many item drops in a short period of time.");
                }
            } else verbose -= verbose > 0 ? 1 : 0;
        }

        lastItemDrop = System.currentTimeMillis();
    }
}
