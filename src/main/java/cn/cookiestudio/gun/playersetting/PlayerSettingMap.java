package cn.cookiestudio.gun.playersetting;

import java.util.HashMap;
import java.util.Map;

public class PlayerSettingMap {
   private PlayerSettingMap.FireMode fireMode;
   private boolean openTrajectoryParticle;
   private boolean openMuzzleParticle;

   public Map<String, Object> getMap() {
      Map<String, Object> map = new HashMap();
      map.put("fireMode", this.fireMode.ordinal());
      map.put("openTrajectoryParticle", this.openTrajectoryParticle);
      map.put("openMuzzleParticle", this.openMuzzleParticle);
      return map;
   }

   PlayerSettingMap(PlayerSettingMap.FireMode fireMode, boolean openTrajectoryParticle, boolean openMuzzleParticle) {
      this.fireMode = PlayerSettingMap.FireMode.MANUAL;
      this.openTrajectoryParticle = true;
      this.openMuzzleParticle = true;
      this.fireMode = fireMode;
      this.openTrajectoryParticle = openTrajectoryParticle;
      this.openMuzzleParticle = openMuzzleParticle;
   }

   public static PlayerSettingMap.PlayerSettingMapBuilder builder() {
      return new PlayerSettingMap.PlayerSettingMapBuilder();
   }

   public PlayerSettingMap.FireMode getFireMode() {
      return this.fireMode;
   }

   public boolean isOpenTrajectoryParticle() {
      return this.openTrajectoryParticle;
   }

   public boolean isOpenMuzzleParticle() {
      return this.openMuzzleParticle;
   }

   public void setFireMode(PlayerSettingMap.FireMode fireMode) {
      this.fireMode = fireMode;
   }

   public void setOpenTrajectoryParticle(boolean openTrajectoryParticle) {
      this.openTrajectoryParticle = openTrajectoryParticle;
   }

   public void setOpenMuzzleParticle(boolean openMuzzleParticle) {
      this.openMuzzleParticle = openMuzzleParticle;
   }

   public static enum FireMode {
      AUTO,
      MANUAL;

      private static PlayerSettingMap.FireMode[] $values() {
         return new PlayerSettingMap.FireMode[]{AUTO, MANUAL};
      }

      private static PlayerSettingMap.FireMode[] $values$() {
         return new PlayerSettingMap.FireMode[]{AUTO, MANUAL};
      }

      // $FF: synthetic method
      private static PlayerSettingMap.FireMode[] $values$$() {
         return new PlayerSettingMap.FireMode[]{AUTO, MANUAL};
      }
   }

   public static class PlayerSettingMapBuilder {
      private PlayerSettingMap.FireMode fireMode;
      private boolean openTrajectoryParticle;
      private boolean openMuzzleParticle;

      PlayerSettingMapBuilder() {
      }

      public PlayerSettingMap.PlayerSettingMapBuilder fireMode(PlayerSettingMap.FireMode fireMode) {
         this.fireMode = fireMode;
         return this;
      }

      public PlayerSettingMap.PlayerSettingMapBuilder openTrajectoryParticle(boolean openTrajectoryParticle) {
         this.openTrajectoryParticle = openTrajectoryParticle;
         return this;
      }

      public PlayerSettingMap.PlayerSettingMapBuilder openMuzzleParticle(boolean openMuzzleParticle) {
         this.openMuzzleParticle = openMuzzleParticle;
         return this;
      }

      public PlayerSettingMap build() {
         return new PlayerSettingMap(this.fireMode, this.openTrajectoryParticle, this.openMuzzleParticle);
      }

      public String toString() {
         return "PlayerSettingMap.PlayerSettingMapBuilder(fireMode=" + this.fireMode + ", openTrajectoryParticle=" + this.openTrajectoryParticle + ", openMuzzleParticle=" + this.openMuzzleParticle + ")";
      }
   }
}
