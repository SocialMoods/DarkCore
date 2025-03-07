package cn.cookiestudio.gun.playersetting;

import cn.cookiestudio.gun.GunPlugin;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.utils.Config;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PlayerSettingPool {
   private Config config;
   private final Map<String, PlayerSettingMap> settings = new HashMap();

   public PlayerSettingPool() {
      try {
         this.init();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      Server.getInstance().getPluginManager().registerEvents(new Listener() {
         @EventHandler
         public void onPlayerJoin(PlayerJoinEvent event) {
            String name = event.getPlayer().getName();
            if (!PlayerSettingPool.this.settings.containsKey(name)) {
               PlayerSettingPool.this.cache(name);
            }

         }
      }, GunPlugin.getInstance());
      Iterator var1 = Server.getInstance().getOnlinePlayers().values().iterator();

      while(var1.hasNext()) {
         Player player = (Player)var1.next();
         this.cache(player.getName());
      }

   }

   public void init() throws Exception {
      Path p = Paths.get(GunPlugin.getInstance().getDataFolder().toString(), "playerSettings.json");
      if (!Files.exists(p, new LinkOption[0])) {
         Files.createFile(p);
      }

      this.config = new Config(p.toFile(), 1);
   }

   public PlayerSettingMap getPlayerSetting(String name) {
      return !this.settings.containsKey(name) ? this.cache(name) : (PlayerSettingMap)this.settings.get(name);
   }

   public PlayerSettingMap cache(String name) {
      if (!this.existInFile(name)) {
         PlayerSettingMap entry = PlayerSettingMap.builder().fireMode(PlayerSettingMap.FireMode.MANUAL).openTrajectoryParticle(true).openMuzzleParticle(true).build();
         this.settings.put(name, entry);
         return entry;
      } else {
         Map playerSetting = (Map)this.config.get(name);
         PlayerSettingMap e = PlayerSettingMap.builder().fireMode(PlayerSettingMap.FireMode.values()[((Double)playerSetting.get("fireMode")).intValue()]).openTrajectoryParticle((Boolean)playerSetting.get("openTrajectoryParticle")).openMuzzleParticle((Boolean)playerSetting.get("openMuzzleParticle")).build();
         this.settings.put(name, e);
         return e;
      }
   }

   public void write(String name, PlayerSettingMap entry) {
      this.config.set(name, entry.getMap());
      this.config.save();
   }

   public void writeAll() {
      Iterator var1 = this.settings.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, PlayerSettingMap> e = (Entry)var1.next();
         this.write((String)e.getKey(), (PlayerSettingMap)e.getValue());
      }

   }

   public boolean existInFile(String name) {
      return this.config.exists(name);
   }

   public void close() {
      this.writeAll();
   }
}
