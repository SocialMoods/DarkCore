package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.guns.achieve.ItemGunM3;
import cn.cookiestudio.gun.utils.BVector3;
import cn.cookiestudio.gun.utils.SoundUtil;
import cn.cookiestudio.gun.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoor;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AnimateEntityPacket;
import cn.nukkit.network.protocol.CameraShakePacket;
import cn.nukkit.network.protocol.CompletedUsingItemPacket;
import cn.nukkit.potion.Effect;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

@Getter
public class GunData {
   private static final Random RANDOM = new Random(System.currentTimeMillis());
   private int magSize;
   private double fireCoolDown;
   private double reloadTime;
   private int slownessLevel;
   private int slownessLevelAim;
   private double hitDamage;
   private double range;
   private double recoil;
   private String particle;
   private GunData.FireParticle fireParticle;
   private String gunName;
   private String magName;
   private String fireSound;
   private String magInSound;
   private String magOutSound;
   private String reloadAnimationTP;
   private String reloadAnimationFP;
   private String animationControllerTP;
   private String animationControllerFP;
   private int gunId;
   @Setter
   private int ammo;
   private int magId;
   private double fireSwingIntensity;
   private double fireSwingDuration;

   public GunData(int gunId, int magId, String gunName, String magName, int magSize, double fireCoolDown, double reloadTime, int slownessLevel, int slownessLevelAim, double hitDamage, double range, double recoil, String particle, double fireSwingIntensity, double fireSwingDuration) {
      this.gunId = gunId;
      this.magId = magId;
      this.gunName = gunName;
      this.magSize = magSize;
      this.fireCoolDown = fireCoolDown;
      this.reloadTime = reloadTime;
      this.slownessLevel = slownessLevel;
      this.slownessLevelAim = slownessLevelAim;
      this.fireSwingIntensity = fireSwingIntensity;
      this.fireSwingDuration = fireSwingDuration;
      this.hitDamage = hitDamage;
      this.range = range;
      this.particle = particle;
      this.magName = magName;
      this.recoil = recoil;
      this.fireParticle = new GunData.FireParticle();
      this.fireSound = gunName + "_fire";
      this.magInSound = gunName + "_magin";
      this.magOutSound = gunName + "_magout";
      this.reloadAnimationFP = "animation." + gunName + ".first_person.reload";
      this.reloadAnimationTP = "animation." + gunName + ".third_person.reload";
      this.animationControllerFP = "controller.animation.\" + gunName + \".first_person";
      this.animationControllerTP = "controller.animation." + gunName + ".third_person";
   }

   public void fire(Player player, ItemGunBase gunType) {

      if (gunType.getAmmoCount() == 0) {
         gunType.reload(player);
         return;
      }

      SoundUtil.playSound(player, this.getFireSound(), 1.0F, 1.0F);
      if (player.isSprinting()) {
         player.setSprinting(false);
         player.sendMovementSpeed(player.getMovementSpeed());
      }

      CameraShakePacket pk = new CameraShakePacket();
      pk.duration = (float) fireSwingDuration;
      pk.intensity = (float) fireSwingIntensity;
      pk.shakeAction = CameraShakePacket.CameraShakeAction.ADD;
      pk.shakeType = CameraShakePacket.CameraShakeType.ROTATIONAL;

      player.dataPacket(pk);

      Player[] showParticlePlayers = (Player[])Server.getInstance().getOnlinePlayers().values().stream().filter((p) -> {
         return GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(p.getName()).isOpenTrajectoryParticle();
      }).toArray(Player[]::new);
      if (gunType instanceof ItemGunM3) {
         Location original = player.clone();

         for(int i = 0; i < 10; ++i) {
            player.yaw += (double)(RANDOM.nextInt(11) - 5);
            player.pitch += (double)(RANDOM.nextInt(11) - 5);
            this.fireParticle.accept((Position)player, (Player[])showParticlePlayers);
            player.setRotation(original.getYaw(), original.getPitch());
         }
      } else {
         this.fireParticle.accept((Position)player, (Player[])showParticlePlayers);
      }

      if (this.recoil != 0.0D) {
         Vector3 recoilPos = this.getRecoilPos(player, this.recoil);
         player.setMotion(recoilPos);
      }
   }

   public void startReload(Player player) {
      playReloadAnimation(player);
      SoundUtil.playSound(player, this.magOutSound, 1.0F, 1.0F);
   }

   public void reloadFinish(EntityHuman entityHuman) {
      SoundUtil.playSound(entityHuman, this.magInSound, 1.0F, 1.0F);
   }

   public void emptyGun(EntityHuman entityHuman) {
      SoundUtil.playSound(entityHuman, "empty_gun", 1.0F, 1.0F);
   }

   public Vector3 getRecoilPos(EntityHuman entityHuman, double length) {
      Vector3 pos = BVector3.fromLocation(entityHuman, length).rotate(180.0D, 0.0D).addToPos();
      pos.y = entityHuman.y;
      return pos;
   }

//   public void playReloadAnimation(Player player) {
//      // первое лицо
//      AnimateEntityPacket packetTP = new AnimateEntityPacket();
//      packetTP.setAnimation(reloadAnimationFP);
//      packetTP.setNextState("animation." + this.gunName + ".first_person.holding");
//      packetTP.setStopExpression("hold_on_last_frame");
//      packetTP.setController(animationControllerFP);
//      packetTP.setBlendOutTime(0.1f);
//      packetTP.getEntityRuntimeIds().add(player.getId());
//      player.dataPacket(packetTP);
//
//      // третье лицо
//      AnimateEntityPacket packetFP = new AnimateEntityPacket();
//      packetFP.setAnimation(reloadAnimationTP);
//      packetFP.setNextState("animation." + this.gunName + ".third_person.holding");
//      packetFP.setStopExpression("hold_on_last_frame");
//      packetFP.setController(animationControllerTP);
//      packetFP.setBlendOutTime(0);
//      packetFP.getEntityRuntimeIds().add(player.getId());
//      player.dataPacket(packetFP);
//   }
//
//   public void playFireAnimation(Player player) {
//      // первое лицо огонь
//      AnimateEntityPacket packetTP = new AnimateEntityPacket();
//      packetTP.setAnimation("animation." + this.gunName + ".third_person.fire");
//      packetTP.setNextState("animation." + this.gunName + ".first_person.holding");
//      packetTP.setStopExpression("");
//      packetTP.setController(animationControllerFP);
//      packetTP.setBlendOutTime(0.1f);
//      packetTP.getEntityRuntimeIds().add(player.getId());
//      player.dataPacket(packetTP);
//
//      // третье лицо огонь
//      AnimateEntityPacket packetFP = new AnimateEntityPacket();
//      packetFP.setAnimation("animation." + this.gunName + ".third_person.fire");
//      packetFP.setNextState("animation." + this.gunName + ".third_person.holding");
//      packetFP.setStopExpression("");
//      packetFP.setController(animationControllerTP);
//      packetFP.setBlendOutTime(0);
//      packetFP.getEntityRuntimeIds().add(player.getId());
//      player.dataPacket(packetFP);
//   }

   public void playAnimation(Player player, String animation) {
      AnimateEntityPacket packet = new AnimateEntityPacket();
      packet.setAnimation(animation); // Название анимации
      packet.setNextState("default"); // Следующее состояние
      packet.setBlendOutTime(0.1f); // Плавный переход
      packet.setStopExpression("query.any_animation_finished"); // Остановить после завершения
      packet.setStopExpressionVersion(16777216);
      packet.setController(this.animationControllerFP);
      packet.setEntityRuntimeIds(List.of(player.getId())); // Указываем ID сущности (игрока)

      player.dataPacket(packet); // Отправляем пакет
   }

   public void playReloadAnimation(Player player) {
      playAnimation(player, "animation." + this.gunName + ".first_person.reload");
   }

   public void playFireAnimation(Player player) {
      playAnimation(player, "animation." + this.gunName + ".first_person.fire");
   }

   public void addWalkingSlownessEffect(Player player) {
      Effect effect = Effect.getEffect(2);
      effect.setAmplifier(this.slownessLevel - 1);
      effect.setVisible(false);
      effect.setDuration(Integer.MAX_VALUE);
      player.removeEffect(2);
      player.addEffect(effect);
   }

   public void addAimingSlownessEffect(Player player) {
      Effect effect = Effect.getEffect(2);
      effect.setAmplifier(this.slownessLevelAim - 1);
      effect.setVisible(false);
      effect.setDuration(Integer.MAX_VALUE);
      player.removeEffect(2);
      player.addEffect(effect);
   }

   public static GunData.GunDataBuilder builder() {
      return new GunData.GunDataBuilder();
   }

   public void setMagSize(int magSize) {
      this.magSize = magSize;
   }

   public void setFireCoolDown(double fireCoolDown) {
      this.fireCoolDown = fireCoolDown;
   }

   public void setReloadTime(double reloadTime) {
      this.reloadTime = reloadTime;
   }

   public void setSlownessLevel(int slownessLevel) {
      this.slownessLevel = slownessLevel;
   }

   public void setSlownessLevelAim(int slownessLevelAim) {
      this.slownessLevelAim = slownessLevelAim;
   }

   public void setHitDamage(double hitDamage) {
      this.hitDamage = hitDamage;
   }

   public void setRange(double range) {
      this.range = range;
   }

   public void setRecoil(double recoil) {
      this.recoil = recoil;
   }

   public void setParticle(String particle) {
      this.particle = particle;
   }

   public void setFireParticle(GunData.FireParticle fireParticle) {
      this.fireParticle = fireParticle;
   }

   public void setGunName(String gunName) {
      this.gunName = gunName;
   }

   public void setMagName(String magName) {
      this.magName = magName;
   }

   public void setFireSound(String fireSound) {
      this.fireSound = fireSound;
   }

   public void setMagInSound(String magInSound) {
      this.magInSound = magInSound;
   }

   public void setMagOutSound(String magOutSound) {
      this.magOutSound = magOutSound;
   }

   public void setReloadAnimationTP(String reloadAnimationTP) {
      this.reloadAnimationTP = reloadAnimationTP;
   }

   public void setReloadAnimationFP(String reloadAnimationFP) {
      this.reloadAnimationFP = reloadAnimationFP;
   }

   public void setAnimationControllerTP(String animationControllerTP) {
      this.animationControllerTP = animationControllerTP;
   }

   public void setAnimationControllerFP(String animationControllerFP) {
      this.animationControllerFP = animationControllerFP;
   }

   public void setGunId(int gunId) {
      this.gunId = gunId;
   }

   public void setMagId(int magId) {
      this.magId = magId;
   }

   public void setFireSwingIntensity(double fireSwingIntensity) {
      this.fireSwingIntensity = fireSwingIntensity;
   }

   public void setFireSwingDuration(double fireSwingDuration) {
      this.fireSwingDuration = fireSwingDuration;
   }

    private class FireParticle implements BiConsumer<Position, Player[]> {
      public void accept(Position pos, Player[] showPlayers) {
         if (pos instanceof EntityHuman entityHuman) {
            Location pos1;
            if (entityHuman.isSneaking()) {
               pos1 = entityHuman.getLocation().add(0.0D, -0.15D, 0.0D);
            } else {
               pos1 = entityHuman;
            }

            Map<String, List<Position>> map = new ConcurrentHashMap<>();
            Map<Integer, Position> ammoMap = new ConcurrentHashMap<>();
            Map<Entity, Integer> hitMap = new ConcurrentHashMap<>();
            List<Position> ammoParticleList = new CopyOnWriteArrayList<>();
            List<Position> hitParticleList = new CopyOnWriteArrayList<>();
            BVector3 face = BVector3.fromLocation(pos1, 0.8D);
            Block blocked = null;
            Position blockedPos = null;

            Position hitPos;
            for(int i = 0; (double)i <= GunData.this.range * 20.0D; ++i) {
               hitPos = Position.fromObject(face.addToPos(pos1).add(0.0D, 1.62D, 0.0D), pos1.level);
               Position ammoPos = Position.fromObject(face.extend(0.08D).addToPos(pos1).add(0.0D, 1.62D, 0.0D), pos1.level);
               Block currentBlock = ammoPos.getLevelBlock();
               if (!currentBlock.canPassThrough() || currentBlock instanceof BlockDoor) {
                  blocked = currentBlock;
                  blockedPos = hitPos.clone();
                  break;
               }

               ammoMap.put(i, ammoPos);
               if (i % 4 == 0) {
                  ammoParticleList.add(ammoPos);
               }
            }

            ammoMap.forEach((key, value) -> {
               FullChunk chunk = value.getChunk();
               if (chunk != null) {
                  chunk.getEntities().values().forEach((entity) -> {
                     if (entity.getBoundingBox().isVectorInside(value) && !entity.equals(entityHuman)) {
                        if (hitMap.containsKey(entity)) {
                           if (hitMap.get(entity) > key) {
                              hitMap.put(entity, key);
                           }
                        } else {
                           hitMap.put(entity, key);
                        }
                     }

                  });
               }
            });

            hitMap.keySet().forEach((entity) -> {
               EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(entityHuman, entity, DamageCause.ENTITY_ATTACK, (float)GunData.this.hitDamage, 0.0F);
               event.setAttackCooldown(0);
               event.setKnockBack(0.2F);
               entity.attack(event);
               hitParticleList.add(ammoMap.get(hitMap.get(entity)));
            });

             for (Position position : hitParticleList) {
                 hitPos = position;
                 hitPos.getLevel().addParticle(new DestroyBlockParticle(hitPos, Block.get(152)));
             }

            if (blocked != null) {
               blocked.getLevel().addParticle(new DestroyBlockParticle(blockedPos, blocked));
            }

            map.put(GunData.this.particle, ammoParticleList);
            Position fireSmokePos = Position.fromObject(BVector3.fromLocation(pos1, 0.8D).addToPos(pos1).add(0.0D, 1.62D, 0.0D), pos1.level);
            if (entityHuman instanceof Player && GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(entityHuman.getName()).isOpenMuzzleParticle()) {
               Utils.sendParticle("minecraft:basic_flame_particle", fireSmokePos, Server.getInstance().getOnlinePlayers().values().toArray(new Player[0]));
            }
         }
      }
   }

   public static class GunDataBuilder {
      private int gunId;
      private int magId;
      private String gunName;
      private String magName;
      private int magSize;
      private double fireCoolDown;
      private double reloadTime;
      private int slownessLevel;
      private int slownessLevelAim;
      private double hitDamage;
      private double range;
      private double recoil;
      private String particle;
      private double fireSwingIntensity;
      private double fireSwingDuration;

      GunDataBuilder() {
      }

      public GunData.GunDataBuilder gunId(int gunId) {
         this.gunId = gunId;
         return this;
      }

      public GunData.GunDataBuilder magId(int magId) {
         this.magId = magId;
         return this;
      }

      public GunData.GunDataBuilder gunName(String gunName) {
         this.gunName = gunName;
         return this;
      }

      public GunData.GunDataBuilder magName(String magName) {
         this.magName = magName;
         return this;
      }

      public GunData.GunDataBuilder magSize(int magSize) {
         this.magSize = magSize;
         return this;
      }

      public GunData.GunDataBuilder fireCoolDown(double fireCoolDown) {
         this.fireCoolDown = fireCoolDown;
         return this;
      }

      public GunData.GunDataBuilder reloadTime(double reloadTime) {
         this.reloadTime = reloadTime;
         return this;
      }

      public GunData.GunDataBuilder slownessLevel(int slownessLevel) {
         this.slownessLevel = slownessLevel;
         return this;
      }

      public GunData.GunDataBuilder slownessLevelAim(int slownessLevelAim) {
         this.slownessLevelAim = slownessLevelAim;
         return this;
      }

      public GunData.GunDataBuilder hitDamage(double hitDamage) {
         this.hitDamage = hitDamage;
         return this;
      }

      public GunData.GunDataBuilder range(double range) {
         this.range = range;
         return this;
      }

      public GunData.GunDataBuilder recoil(double recoil) {
         this.recoil = recoil;
         return this;
      }

      public GunData.GunDataBuilder particle(String particle) {
         this.particle = particle;
         return this;
      }

      public GunData.GunDataBuilder fireSwingIntensity(double fireSwingIntensity) {
         this.fireSwingIntensity = fireSwingIntensity;
         return this;
      }

      public GunData.GunDataBuilder fireSwingDuration(double fireSwingDuration) {
         this.fireSwingDuration = fireSwingDuration;
         return this;
      }

      public GunData build() {
         return new GunData(this.gunId, this.magId, this.gunName, this.magName, this.magSize, this.fireCoolDown, this.reloadTime, this.slownessLevel, this.slownessLevelAim, this.hitDamage, this.range, this.recoil, this.particle, this.fireSwingIntensity, this.fireSwingDuration);
      }

      public String toString() {
         return "GunDataBuilder(gunId=" + this.gunId + ", magId=" + this.magId + ", gunName=" + this.gunName + ", magName=" + this.magName + ", magSize=" + this.magSize + ", fireCoolDown=" + this.fireCoolDown + ", reloadTime=" + this.reloadTime + ", slownessLevel=" + this.slownessLevel + ", slownessLevelAim=" + this.slownessLevelAim + ", hitDamage=" + this.hitDamage + ", range=" + this.range + ", recoil=" + this.recoil + ", particle=" + this.particle + ", fireSwingIntensity=" + this.fireSwingIntensity + ", fireSwingDuration=" + this.fireSwingDuration + ")";
      }
   }
}
