package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import com.sun.tools.jdi.Packet;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CheckData(name = "BadPacketsMeteorScan")
public class BadPacketsMeteorScan extends Check implements PacketCheck {
    public BadPacketsMeteorScan(GrimPlayer player) {
        super(player);
    }

    private static final ConcurrentHashMap<UUID, LinkedList<Command>> meteorScan = new ConcurrentHashMap<>();

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(player.disableGrim) return;
        // Automatically cleans up when the player disconnects to prevent memory leaks
        if (event.getPacketType().equals(PacketType.Play.Server.DISCONNECT)) {
            meteorScan.remove(player.playerUUID);
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(player.disableGrim) return;
        if(event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;

        GrimPlayer player = getPlayer();
        if(player.bukkitPlayer == null) {
            player.bukkitPlayer = Bukkit.getPlayer(player.playerUUID);

            if(player.bukkitPlayer == null) {
                event.setCancelled(true);
                return;
            }
        }

        WrapperPlayClientTabComplete packet = new WrapperPlayClientTabComplete(event);
        String data = packet.getText();

        Command cmd = new Command(data, player.bukkitPlayer);
        saveCommand(player.playerUUID, cmd);

        if(isUniqueOneLetterMajority(player.playerUUID)) {
            event.setCancelled(true);

            String message = "User likely using meteor massscan";

            if(player.getBrand().toLowerCase().contains("vanilla")) {
                flagAndAlert(message + " but \"has\" a vanilla client");
                return;
            }

            flagAndAlert(message);
            player.disconnect(Component.text("Bad packet, dont do this or you WILL get banned"));
        }
    }

    private boolean isUniqueOneLetterMajority(UUID playerUUID) {
        LinkedList<Command> commands = meteorScan.get(playerUUID);
        if(commands == null || commands.isEmpty()) return false;

        long oneSecondAgo = System.currentTimeMillis() - 1000;
        int uniqueCommandCount = 0;
        int oneLetterCommandCound = 0;

        for(Command cmd : commands) {
            if(cmd.getTime() < oneSecondAgo || !cmd.player.isOnline()) continue;
            if(cmd.getCommand().length() > 1) continue;

            oneLetterCommandCound++;
            if(isUniqueCommand(commands, cmd)) {
                uniqueCommandCount++;
            }
        }

        int majorityTreshold = commands.size() / 2;
        return oneLetterCommandCound > majorityTreshold && uniqueCommandCount > majorityTreshold;
    }

    private boolean isUniqueCommand(LinkedList<Command> commands, Command cmd) {
        return commands.stream().filter(c -> c.getCommand().equals(cmd.getCommand())).count() == 1;
    }

    private void saveCommand(UUID uuid, Command cmd) {
        LinkedList<Command> commands = meteorScan.computeIfAbsent(uuid, k -> new LinkedList<>());
        commands.addFirst(cmd);

        long oneSecondAgo = System.currentTimeMillis() - 1000;

        // clear the cache where users are offline or the command is older than 1 second
        commands.removeIf(c -> c.getTime() < oneSecondAgo || !c.getPlayer().isOnline());
    }

    @Getter
    static class Command {
        private final String command;
        // Changed return type to long
        private final long time;
        private final Player player;

        public Command(String cmd, Player p) {
            this.command = cmd;
            this.time = System.currentTimeMillis();
            this.player = p;
        }

    }
}
