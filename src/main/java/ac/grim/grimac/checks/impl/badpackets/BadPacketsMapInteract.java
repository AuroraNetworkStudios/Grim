package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

@CheckData(name = "BadPacketsMapInteract", configName = "BadPacketsMapInteract")
public class BadPacketsMapInteract extends Check implements PacketCheck {
    public BadPacketsMapInteract(final GrimPlayer player) {
        super(player);
    }


    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY)) { return; }

        WrapperPlayClientInteractEntity interactEntity = new WrapperPlayClientInteractEntity(event);
        if (interactEntity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) { return; }
        if(player.bukkitPlayer == null) { return; }

        ItemStack item = player.bukkitPlayer.getInventory().getItemInMainHand();

        if(!item.getType().equals(Material.MAP)) return;

        boolean trackingPosition = getConfig().getBooleanElse(getConfigName() + ".setTrackingPosition", true);

        ItemMeta meta = item.getItemMeta();
        if(meta instanceof MapMeta) {
            MapMeta mapMeta = (MapMeta) meta;
            MapView mapView = mapMeta.getMapView();
            if(mapView != null) {
                if(trackingPosition)
                    mapView.setTrackingPosition(false);
            }
        }


    }
}
