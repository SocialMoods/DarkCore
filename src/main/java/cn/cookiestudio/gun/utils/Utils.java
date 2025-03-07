package cn.cookiestudio.gun.utils;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import java.math.BigDecimal;
import java.util.Arrays;

public class Utils {
   private Utils() {
   }

   public static void sendParticle(String identifier, Position pos, Player[] showPlayers) {
      Arrays.stream(showPlayers).forEach((player) -> {
         if (player.isOnline()) {
            SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
            packet.identifier = identifier;
            packet.dimensionId = pos.getLevel().getDimension();
            packet.position = pos.asVector3f();

            try {
               player.dataPacket(packet);
            } catch (Throwable var5) {
            }
         }

      });
   }

   public static double rand(double min, double max) {
      return min == max ? max : min + Math.random() * (max - min);
   }

   public static int toInt(Object object) {
      return (new BigDecimal(object.toString())).intValue();
   }
}
