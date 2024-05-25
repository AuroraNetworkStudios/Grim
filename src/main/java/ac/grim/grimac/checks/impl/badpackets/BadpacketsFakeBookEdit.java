package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;

import java.util.Arrays;
import java.util.List;

@CheckData(name = "BadpacketsFakeBookEdit")
public class BadpacketsFakeBookEdit extends Check implements PacketCheck {
    public BadpacketsFakeBookEdit(GrimPlayer player) {
        super(player);
    }

    private Long lastItemDrop;
    private int verbose;
    private final List<ItemType> books = Arrays.asList(
            ItemTypes.WRITTEN_BOOK,
            ItemTypes.WRITABLE_BOOK,
            ItemTypes.BOOK
    );

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.EDIT_BOOK)) return;

        if(books.contains(player.getInventory().getItemInHand(InteractionHand.MAIN_HAND).getType())) { return; }
        if(books.contains(player.getInventory().getItemInHand(InteractionHand.OFF_HAND).getType())) { return; }

        event.setCancelled(true);
        flagAndAlert();
        player.onPacketCancel();


    }
}
