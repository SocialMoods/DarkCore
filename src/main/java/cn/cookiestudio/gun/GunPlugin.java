package cn.cookiestudio.gun;

import cn.cookiestudio.gun.command.GunCommand;
import cn.cookiestudio.gun.guns.*;
import cn.cookiestudio.gun.guns.achieve.*;
import cn.cookiestudio.gun.playersetting.PlayerSettingPool;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

public class GunPlugin extends PluginBase {
   @Getter
   private static GunPlugin instance;
   @Getter
   private Config config;
   @Getter
   private final Map<Class<? extends ItemGunBase>, GunData> gunDataMap = new HashMap();
   @Getter
   private final Map<String, Class<? extends ItemGunBase>> stringClassMap = new HashMap();
   private final Map<String, Class<? extends ItemHealBase>> customItemMap = new HashMap();
   private final Map<String, Class<? extends ItemKnifeBase>> knifeMap = new HashMap();
   public final Map<ItemGunBase, Integer> ammo = new ConcurrentHashMap<>();
   @Getter
   private CoolDownTimer coolDownTimer;
   @Getter
   private PlayerSettingPool playerSettingPool;
   @Getter
   private Skin crateSkin;
   @Getter
   private Skin ammoBoxSkin;
   @Getter
   public FiringTask task;

   public GunPlugin() {
      this.stringClassMap.put("akm", ItemGunAkm.class);
      this.stringClassMap.put("awp", ItemGunAwp.class);
      this.stringClassMap.put("barrett", ItemGunBarrett.class);
      this.stringClassMap.put("m3", ItemGunM3.class);
      this.stringClassMap.put("m249", ItemGunM249.class);
      this.stringClassMap.put("mk18", ItemGunMk18.class);
      this.stringClassMap.put("mp5", ItemGunMp5.class);
      this.stringClassMap.put("p90", ItemGunP90.class);
      this.stringClassMap.put("taurus", ItemGunTaurus.class);
      this.customItemMap.put("adrenaline", ItemHealAdrenaline.class);
      this.customItemMap.put("morphine", ItemHealMorphine.class);
      this.customItemMap.put("mozgi", ItemHealMozgi.class);
      this.customItemMap.put("aptechka", ItemHealAptechka.class);
      this.knifeMap.put("knife", ItemKnife.class);
      this.knifeMap.put("catana", ItemKnifeCatana.class);
      this.knifeMap.put("machete", ItemKnifeMachete.class);
      this.customItemMap.put("mutagen", ItemHealMutagen.class);
   }

   private static byte[] getBytes(InputStream inStream) throws Exception {
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      boolean var3 = true;

      int len;
      while((len = inStream.read(buffer)) != -1) {
         outStream.write(buffer, 0, len);
      }

      outStream.close();
      inStream.close();
      return outStream.toByteArray();
   }

   @Override
   public void onEnable() {
      instance = this;
      this.playerSettingPool = new PlayerSettingPool();
      this.copyResource();
      this.config = new Config(this.getDataFolder() + "/config.yml");
      this.initCrateSkin();
      this.loadGunData();
      this.registerListener();
      this.registerCommand();
      this.coolDownTimer = new CoolDownTimer();
      new ReleaseItemListener();
      this.task = new FiringTask();
   }

   private void copyResource() {
      this.saveDefaultConfig();
      Path p = Paths.get(Server.getInstance().getDataPath() + "resource_packs/gun.zip");
      if (!Files.exists(p)) {
         this.getLogger().warning("未在目录" + p + "下找到材质包，正在复制，请在完成后重启服务器应用更改");

         try {
            Files.copy(this.getClass().getClassLoader().getResourceAsStream("resources/gun.zip"), p, new CopyOption[0]);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

   }

   private void loadGunData() {
      Map<String, Object> map = this.config.getAll();
      AtomicInteger id = new AtomicInteger(10000);
      Item.registerCustomItem(ItemGunAkm.ItemMagAkm.class);

      map.forEach((key, value1) -> {
          Map<String, Object> value = (Map) value1;
          GunData gunData = GunData.builder().gunId(id.get()).magId(1000 + id.get()).gunName((String) key).magName((String) value.get("magName")).hitDamage((Double) value.get("hitDamage")).fireCoolDown((Double) value.get("fireCoolDown")).magSize((Integer) value.get("magSize")).slownessLevel((Integer) value.get("slownessLevel")).slownessLevelAim((Integer) value.get("slownessLevelAim")).particle((String) value.get("particle")).reloadTime((Double) value.get("reloadTime")).range((Double) value.get("range")).recoil((Double) value.get("recoil")).fireSwingIntensity((Double) value.get("fireSwingIntensity")).fireSwingDuration((Double) value.get("fireSwingDuration")).build();
          this.gunDataMap.put((Class) this.stringClassMap.get(key), gunData);

          try {
              ItemGunBase itemGun = (ItemGunBase) ((Class) this.stringClassMap.get(key)).newInstance();
              Item.registerCustomItem(itemGun.getClass());
              this.getLogger().info("Регистрация оружия: " + itemGun.getGunData().getGunName());
          } catch (Exception var6) {
              var6.printStackTrace();
          }

          id.addAndGet(1);
      });
      this.customItemMap.forEach((key, clazz) -> {
         this.getLogger().info("Регистрация лечебного средства: " + key);

         try {
            ItemHealBase itemHeal = clazz.newInstance();
            Item.registerCustomItem(itemHeal.getClass());
         } catch (Exception var4) {
            this.getLogger().warning("Не удалось зарегистрировать лечебное средство: " + key);
            var4.printStackTrace();
         }

      });
      this.knifeMap.forEach((key, clazz) -> {
         this.getLogger().info("Регистрация оружейного средства: " + key);

         try {
            ItemKnifeBase itemHeal = clazz.newInstance();
            Item.registerCustomItem(itemHeal.getClass());
         } catch (Exception var4) {
            this.getLogger().warning("Не удалось зарегистрировать оружейное средство: " + key);
            var4.printStackTrace();
         }

      });
   }

   private void initCrateSkin() {
      this.crateSkin = new Skin();

      try {
         this.crateSkin.setTrusted(true);
         this.crateSkin.setGeometryData(new String(getBytes(getInstance().getResource("resources/model/crate/skin.json"))));
         this.crateSkin.setGeometryName("geometry.crate");
         this.crateSkin.setSkinId("crate");
         this.crateSkin.setSkinData(ImageIO.read(getInstance().getResource("resources/model/crate/skin.png")));
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

      this.ammoBoxSkin = new Skin();

      try {
         this.ammoBoxSkin.setTrusted(true);
         this.ammoBoxSkin.setGeometryData(new String(getBytes(getInstance().getResource("resources/model/ammobox/skin.json"))));
         this.ammoBoxSkin.setGeometryName("geometry.ammobox");
         this.ammoBoxSkin.setSkinId("ammobox");
         this.ammoBoxSkin.setSkinData(ImageIO.read(getInstance().getResource("resources/model/ammobox/skin.png")));
      } catch (Throwable var2) {
         var2.printStackTrace();
      }

   }

   private void registerListener() {
      Server.getInstance().getPluginManager().registerEvents(new Listener() {
         @EventHandler
         public void onDataPacketReceive(DataPacketReceiveEvent event) {
            DataPacket var3 = event.getPacket();
            if (var3 instanceof EntityEventPacket packet) {
                if (packet.event == 57) {
                  Player player = event.getPlayer();
                  if (player.getInventory().getItemInHand() instanceof ItemGunBase) {
                     event.setCancelled(true);
                  }
               }
            }

         }
      }, this);
   }

   private void registerCommand() {
      Server.getInstance().getCommandMap().register("", new GunCommand("gun"));
   }

   public void saveGunData(GunData gunData) {
      String gunName = gunData.getGunName();
      this.config.set(gunName + ".magSize", gunData.getMagSize());
      this.config.set(gunName + ".fireCoolDown", gunData.getFireCoolDown());
      this.config.set(gunName + ".reloadTime", gunData.getReloadTime());
      this.config.set(gunName + ".slownessLevel", gunData.getSlownessLevel());
      this.config.set(gunName + ".slownessLevelAim", gunData.getSlownessLevelAim());
      this.config.set(gunName + ".fireSwingIntensity", gunData.getFireSwingIntensity());
      this.config.set(gunName + ".fireSwingDuration", gunData.getFireSwingDuration());
      this.config.set(gunName + ".hitDamage", gunData.getHitDamage());
      this.config.set(gunName + ".range", gunData.getRange());
      this.config.set(gunName + ".particle", gunData.getParticle());
      this.config.set(gunName + ".magName", gunData.getMagName());
      this.config.set(gunName + ".recoil", gunData.getRecoil());
      this.config.save();
   }
}
