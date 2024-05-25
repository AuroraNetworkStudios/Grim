package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;

@CheckData(name = "BadPacketsNoResponse")
public class BadPacketsNoResponse extends Check implements PacketCheck {
    public BadPacketsNoResponse(final GrimPlayer player) {
        super(player);
    }

    private short lastConfirm = Short.MAX_VALUE;


    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.WINDOW_CONFIRMATION)) return;
        if(player.disableGrim) return;

        final WrapperPlayClientWindowConfirmation wrapper = new WrapperPlayClientWindowConfirmation(event);
        final short actionID = wrapper.getActionId();

        if(wrapper.getWindowId() != 0 || player.didWeSendThatTrans.contains(actionID) || actionID > 0) return;

        if(lastConfirm != Short.MAX_VALUE && actionID != lastConfirm - 1) {
            flagAndAlert("User sent wrong WINDOW_CONFIRMATION packet ActionID=" + actionID);
        }

        lastConfirm = actionID;
    }
}
