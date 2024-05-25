package ac.grim.grimac.checks.impl.aim;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.data.HeadRotation;

@CheckData(name = "AimYawPitchAcceleration", description = "Makes sure that the player's yaw and pitch acceleration looks legit", experimental = true)
public class AimYawPitchAcceleration extends Check implements RotationCheck {
    float lastDeltaYaw;

    public AimYawPitchAcceleration(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {


        float yawAcceperation = rotationUpdate.getDeltaYRot();
        float pitchAcceperation = rotationUpdate.getDeltaXRot();

        final HeadRotation from = rotationUpdate.getFrom();
        final HeadRotation to = rotationUpdate.getTo();
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());
        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());


        if(
                yawAcceperation < 1E-3
                && pitchAcceperation < 1E-3
                && (deltaPitch > 0 && yawAcceperation > 0)
                && (deltaYaw > 2)

        ) {
            return;
        }


        if (player.packetStateData.lastPacketWasTeleport) return;
        if (player.xRot < 360 && player.xRot > -360 && Math.abs(rotationUpdate.getDeltaXRot()) > 320 && Math.abs(lastDeltaYaw) < 30) {
            flagAndAlert();
        } else {
            reward();
        }

        lastDeltaYaw = rotationUpdate.getDeltaXRot();
    }
}
