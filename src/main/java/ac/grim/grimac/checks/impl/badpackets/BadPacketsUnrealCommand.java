package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommandUnsigned;
import org.bukkit.Bukkit;

@CheckData(name = "BadPacketsUnrealCommand")
public class BadPacketsUnrealCommand extends Check implements PacketCheck {
    public BadPacketsUnrealCommand(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (
            !event.getPacketType().equals(PacketType.Play.Client.CHAT_COMMAND) &&
            !event.getPacketType().equals(PacketType.Play.Client.CHAT_COMMAND_UNSIGNED)
        ) { return; }

        if(player.disableGrim) return;

        if(player.bukkitPlayer == null) {
            player.bukkitPlayer = Bukkit.getPlayer(player.playerUUID);
            if(player.bukkitPlayer == null) {
                flagAndAlert("Player is somehow null while executing a command?");
                event.setCancelled(true);
                return;
            }

            if(!player.bukkitPlayer.isOnline()) {
                flagAndAlert("Player is offline while executing a command? THERE IS AN EXPLOIT WITH THIS WATCH OUT");
                event.setCancelled(true);
                return;
            }
        }

        if(event.getPacketType().equals(PacketType.Play.Client.CHAT_COMMAND_UNSIGNED)) {
            WrapperPlayClientChatCommandUnsigned chatCommandUnsigned = new WrapperPlayClientChatCommandUnsigned(event);
            if (this.unsigned(chatCommandUnsigned)) {
                flagAndAlert("Player is dead or offline while executing a command?");
                event.setCancelled(true);
            }
            return;
        }

        WrapperPlayClientChatCommand chatCommand = new WrapperPlayClientChatCommand(event);
        if (this.signed(chatCommand)) {
            flagAndAlert("Player is dead or offline while executing a command?");
            event.setCancelled(true);
        }
    }

    public boolean signed(WrapperPlayClientChatCommand chatCommand) {
        if(player.bukkitPlayer == null) { return false; }
        if(!player.bukkitPlayer.isOnline()) return true;
        if(player.bukkitPlayer.isDead()) return true;
        return false;
    }

    public boolean unsigned(WrapperPlayClientChatCommandUnsigned chatCommandUnsigned) {
        if(player.bukkitPlayer == null) { return false; }
        if(!player.bukkitPlayer.isOnline()) return true;
        if(player.bukkitPlayer.isDead()) return true;
        return false;
    }
}
