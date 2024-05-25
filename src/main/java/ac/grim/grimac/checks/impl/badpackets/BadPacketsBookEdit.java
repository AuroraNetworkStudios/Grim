package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import org.bukkit.ChatColor;

import javax.annotation.Nullable;
import java.util.List;

@CheckData(name = "BadPacketsBookEdit", configName = "BadPacketsBookEdit")
public class BadPacketsBookEdit extends Check implements PacketCheck {
    public BadPacketsBookEdit(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Client.EDIT_BOOK)) return;
        if(player.disableGrim) return;

        WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);
        if(!this.checkBookPacket(packet)) {
            event.setCancelled(true); // we always cancel, too dangerous not to
            player.onPacketCancel();
        }
    }

    private boolean checkBookPacket(WrapperPlayClientEditBook packet) {
        @Nullable String title = packet.getTitle();
        List<String> pages = packet.getPages();

        int maxPageSize = getConfig().getIntElse(getConfigName() + ".maxPageSize", 100);
        int maxTitleLength = getConfig().getIntElse(getConfigName() + ".maxTitleLength", 15);
        int maxPageChatLength = getConfig().getIntElse(getConfigName() + ".maxPageChatLength", 800);


        if(pages.size() > maxPageSize) {
            flagAndAlert("Too many pages: " + pages.size());
            return false;
        }
        if(title != null) {
            if(title.length() > maxTitleLength) {
                flagAndAlert("Title too long: " + title.length());
                return false;
            }

            if(title.contains(ChatColor.COLOR_CHAR + "")) {
                flagAndAlert("Title contains color codes");
                return false;
            }

        }
        for(String page : pages) {
            if(page.length() > maxPageChatLength) {
                flagAndAlert("Page too long: " + page.length());
                return false;
            }
        }

        return true;
    }
}
