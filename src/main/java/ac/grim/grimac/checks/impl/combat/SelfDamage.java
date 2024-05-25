package ac.grim.grimac.checks.impl.combat;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Optional;

@CheckData(name = "SelfDamage", configName = "SelfDamage")
public class SelfDamage extends Check implements PacketCheck {
    public SelfDamage(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if(player.disableGrim) return;
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY) || player.bukkitPlayer == null) {
            return;
        }

        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        // Exit if the action is not ATTACK
        if (!wrapper.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
            return;
        }

        Optional<Vector3f> optionalVector3f = wrapper.getTarget();
        if (!optionalVector3f.isPresent()) {
            return;
        }
        Vector3f vector3f = optionalVector3f.get();

        World world = player.bukkitPlayer.getWorld();
        Entity entity = world.getEntities().stream()
                // checking distance when we could only check entity id seems sus to me
                //.filter(e -> e.getLocation().distanceSquared(new Location(world, vector3f.getX(), vector3f.getY(), vector3f.getZ())) <= 0.09) // 0.3^2 to avoid sqrt call
                .filter(e -> e.getEntityId() == wrapper.getEntityId())
                .findFirst()
                .orElse(null);

        if (entity != null && entity.getEntityId() == wrapper.getEntityId()) {
            flagAndAlert("Self damage?");
        }

    }
}


