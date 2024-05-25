package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Optional;

@CheckData(name = "BadpacketsBookJsonCompound")
public class BadpacketsBookJsonCompound extends Check implements PacketCheck {
    public BadpacketsBookJsonCompound(GrimPlayer player) { super(player); }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(player.disableGrim)return;

        if(event.getPacketType().equals(PacketType.Play.Client.UPDATE_SIGN)) {
            WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event);
            Optional<String> badString = Arrays.stream(packet.getTextLines()).filter(s -> s.toLowerCase().contains("run_command")).findAny();
            if(badString.isPresent()) {
                flagAndAlert("Sign contains run_command");
                event.setCancelled(true);
                player.onPacketCancel();
                player.disconnect(Component.text("Legyél szíves ezt nem itt gyakorolni."));
            }
            return;
        }


        if(!event.getPacketType().equals(PacketType.Play.Client.EDIT_BOOK)) return;


        WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);
        Optional<String> badString = packet.getPages().stream().filter(s -> s.toLowerCase().contains("run_command")).findFirst();

        if(badString.isPresent()) {
            flagAndAlert("Book contains run_command");
            event.setCancelled(true);
            player.onPacketCancel();
            player.disconnect(Component.text("Legyél szíves ezt nem itt gyakorolni."));
        }



    }


}