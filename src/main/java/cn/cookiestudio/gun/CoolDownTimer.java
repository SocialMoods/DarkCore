package cn.cookiestudio.gun;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CoolDownTimer {
   private final Map<EntityHuman, CoolDownTimer.CoolDown> coolDownMap = new ConcurrentHashMap();

   public CoolDownTimer() {
      Server server = Server.getInstance();
      GunPlugin plugin = GunPlugin.getInstance();
      server.getPluginManager().registerEvents(new CoolDownTimer.CoolDownTimerListener(), plugin);
      server.getScheduler().scheduleRepeatingTask(plugin, () -> {
         this.coolDownMap.forEach((human, coolDown) -> {
            --coolDown.coolDownTick;
            if (coolDown.coolDownTick <= 0) {
               this.finish(human);
            }

         });
      }, 1);
   }

   public CoolDownTimer.Operator interrupt(EntityHuman human) {
      CoolDownTimer.CoolDown coolDown = this.coolDownMap.get(human);
      if (coolDown == null) {
         return CoolDownTimer.Operator.NO_ACTION;
      } else {
         CoolDownTimer.Operator operator = coolDown.onInterrupt.get();
         if (operator == CoolDownTimer.Operator.INTERRUPT) {
            this.coolDownMap.remove(human);
         }

         return operator;
      }
   }

   public boolean isCooling(EntityHuman human) {
      return this.coolDownMap.containsKey(human);
   }

   public void finish(EntityHuman human) {
      CoolDownTimer.CoolDown coolDown = (CoolDownTimer.CoolDown)this.coolDownMap.get(human);
      if (coolDown != null) {
         coolDown.onFinish.run();
         this.coolDownMap.remove(human);
      }

   }

   public void addCoolDown(EntityHuman human, int coolDownTick, Runnable onFinish, Supplier<CoolDownTimer.Operator> onInterrupt, CoolDownTimer.Type type) {
      this.coolDownMap.put(human, new CoolDownTimer.CoolDown(coolDownTick, onFinish, onInterrupt, type));
   }

   public Map<EntityHuman, CoolDownTimer.CoolDown> getCoolDownMap() {
      return this.coolDownMap;
   }

   private class CoolDownTimerListener implements cn.nukkit.event.Listener {
      @EventHandler
      public void onPlayerInterruptCoolDown(PlayerItemHeldEvent event) {
         EntityHuman player = event.getPlayer();
         if (CoolDownTimer.this.coolDownMap.containsKey(player) && CoolDownTimer.this.interrupt(player) == CoolDownTimer.Operator.CANCELLED_EVENT) {
            event.setCancelled();
         }

      }
   }

   public static class CoolDown {
      private int coolDownTick;
      private final Runnable onFinish;
      private final Supplier<CoolDownTimer.Operator> onInterrupt;
      private final CoolDownTimer.Type type;

      public CoolDown(int coolDownTick, Runnable onFinish, Supplier<CoolDownTimer.Operator> onInterrupt, CoolDownTimer.Type type) {
         this.coolDownTick = coolDownTick;
         this.onFinish = onFinish;
         this.onInterrupt = onInterrupt;
         this.type = type;
      }

      public int getCoolDownTick() {
         return this.coolDownTick;
      }

      public Runnable getOnFinish() {
         return this.onFinish;
      }

      public Supplier<CoolDownTimer.Operator> getOnInterrupt() {
         return this.onInterrupt;
      }

      public CoolDownTimer.Type getType() {
         return this.type;
      }
   }

   public static enum Operator {
      INTERRUPT,
      NO_ACTION,
      CANCELLED_EVENT;

      // $FF: synthetic method
      private static CoolDownTimer.Operator[] $values() {
         return new CoolDownTimer.Operator[]{INTERRUPT, NO_ACTION, CANCELLED_EVENT};
      }
   }

   public static enum Type {
      RELOAD,
      FIRECOOLDOWN;

      // $FF: synthetic method
      private static CoolDownTimer.Type[] $values() {
         return new CoolDownTimer.Type[]{RELOAD, FIRECOOLDOWN};
      }
   }
}
