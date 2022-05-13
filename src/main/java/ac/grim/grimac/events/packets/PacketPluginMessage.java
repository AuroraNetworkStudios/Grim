package ac.grim.grimac.events.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ac.grim.grimac.GrimAPI;

public class PacketPluginMessage extends PacketListenerAbstract {
	public PacketPluginMessage() {
		GrimAPI.INSTANCE.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(GrimAPI.INSTANCE.getPlugin(), "BungeeCord");
	}	
	
    @Override
    public void onPacketReceive(final PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
            
            if (!packet.getChannelName().equals("BungeeCord") || !GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.bungeecord.receive", false)) return;
            
            ByteArrayDataInput in = ByteStreams.newDataInput(packet.getData());
            
            if (!in.readUTF().equals("GRIMAC")) return;
            
			byte[] msgbytes = new byte[in.readShort()];
			in.readFully(msgbytes);

			final String alert;

			try {
				alert = new DataInputStream(new ByteArrayInputStream(msgbytes)).readUTF();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			for (Player bukkitPlayer : GrimAPI.INSTANCE.getAlertManager().getEnabledAlerts()) {
				bukkitPlayer.sendMessage(alert);
			}
        }
    }
	
	public static void sendPluginMessage(String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("GRIMAC");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();

		try {
			new DataOutputStream(msgbytes).writeUTF(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("alerts-format-bungeecord", message).replace("%alert%", message));
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
		Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(GrimAPI.INSTANCE.getPlugin(), "BungeeCord", out.toByteArray());
	}
}