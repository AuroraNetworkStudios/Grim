package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@CheckData(name = "CrashEndGateway", configName = "CrashEndGateway")
public class CrashEndGateway extends Check implements PacketCheck {
    public CrashEndGateway(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if(!event.getPacketType().equals(PacketType.Play.Server.ENTITY_TELEPORT)) { return; }
        if(player.bukkitPlayer == null) { return; }
        if(player.disableGrim) return;

        WrapperPlayServerEntityTeleport teleport = new WrapperPlayServerEntityTeleport(event);
        if(player.bukkitPlayer.getEntityId() != teleport.getEntityId()) return;

        List<String> whitelistedEntityList = getConfig().getStringListElse(getConfigName() + ".CrashEndGateway", new ArrayList<>());

        // if the entity is in the end and there is an entity riding it, cancel the event
        // this should only happen in plugin servers with some hats ithink? idfk
        if(player.bukkitPlayer.getWorld().getEnvironment().equals(World.Environment.THE_END) && !player.bukkitPlayer.isEmpty()) {

            Stream<Entity> ridingEntity = player.bukkitPlayer.getPassengers().stream().filter(e -> !whitelistedEntityList.contains(e.getType().name()));

            if(ridingEntity.findAny().isPresent()) {
                flagAndAlert("End gateway crash?");
                event.setCancelled(true);
            }

        }
    }
}
