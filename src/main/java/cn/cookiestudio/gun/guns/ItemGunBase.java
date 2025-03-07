package cn.cookiestudio.gun.guns;

import cn.cookiestudio.gun.CoolDownTimer;
import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.playersetting.PlayerSettingMap;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustomEdible;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.food.Food;
import cn.nukkit.item.food.FoodNormal;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public abstract class ItemGunBase extends ItemCustomEdible {


   static {
      Server.getInstance().getScheduler().scheduleRepeatingTask(GunPlugin.getInstance(), () -> {
         Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            if (player.getInventory().getItemInHand() instanceof ItemGunBase itemGun) {
               if (player.isSneaking()) {
                  itemGun.getGunData().addAimingSlownessEffect(player);
               } else {
                  itemGun.getGunData().addWalkingSlownessEffect(player);
               }
               if (!GunPlugin.getInstance().getCoolDownTimer().isCooling(player) || GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player).getType() != CoolDownTimer.Type.RELOAD) {
                  if (GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(player.getName()).getFireMode() == PlayerSettingMap.FireMode.AUTO) {
                     if (!GunPlugin.getInstance().getTask().isFiring(player)) {
                        player.sendActionBar("<" + itemGun.getAmmoCount() + ">\n§dAUTO MODE: §cOFF");
                     } else {
                        player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">\n§dAUTO MODE: §aON");
                     }
                  } else {
                    player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">");
                  }
                  return;
               }
               
               CoolDownTimer.CoolDown coolDown = GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player);
               if (coolDown.getType() == CoolDownTimer.Type.RELOAD) {
                  StringBuilder stringBuilder = getStringBuilder(itemGun, coolDown);
                  player.sendActionBar(stringBuilder.toString(), 0, 1, 0);
               }
            }
         });
      }, 1);
   }

   private static StringBuilder getStringBuilder(ItemGunBase itemGun, CoolDownTimer.CoolDown coolDown) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Перезарядка: §a");
      int bound = (int) (30.0 * ((double) coolDown.getCoolDownTick() / (itemGun.getGunData().getReloadTime() * 20)));
      for (int i = 30; i >= 1; i--) {
         if (i < bound) stringBuilder.append("|");
         if (i == bound) stringBuilder.append("|§c");
         if (i > bound) stringBuilder.append("|");
      }
      return stringBuilder;
   }

   protected GunData gunData;

   public ItemGunBase(String s, String a, String c) {

      super(s, a, c);
   }

   static {
      Server.getInstance().getPluginManager().registerEvents(new Listener(), GunPlugin.getInstance());
      Server.getInstance().getScheduler().scheduleRepeatingTask(GunPlugin.getInstance(), () -> {
         Server.getInstance().getOnlinePlayers().values().forEach(player -> {
            if (player.getInventory().getItemInHand() instanceof ItemGunBase itemGun) {
               if (player.isSneaking()) {
                  itemGun.getGunData().addAimingSlownessEffect(player);
               } else {
                  itemGun.getGunData().addWalkingSlownessEffect(player);
               }
               if (!GunPlugin.getInstance().getCoolDownTimer().isCooling(player) || GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player).getType() != CoolDownTimer.Type.RELOAD) {
                  if (GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(player.getName()).getFireMode() == PlayerSettingMap.FireMode.AUTO) {
                     if (!GunPlugin.getInstance().getTask().isFiring(player)) {
                        player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">\n§dAUTO MODE: §cOFF");
                     } else {
                        player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">\n§dAUTO MODE: §aON");
                     }
                  } else {
                     player.sendActionBar("<" + itemGun.getAmmoCount() + "/" + itemGun.getGunData().getMagSize() + ">");
                  }
                  return;
               }
               CoolDownTimer.CoolDown coolDown = GunPlugin.getInstance().getCoolDownTimer().getCoolDownMap().get(player);
               if (coolDown.getType() == CoolDownTimer.Type.RELOAD) {
                  StringBuilder stringBuilder = new StringBuilder();
                  stringBuilder.append("RELOAD: §a");
                  int bound = (int) (30.0 * ((double) coolDown.getCoolDownTick() / (itemGun.getGunData().getReloadTime() * 20)));
                  for (int i = 30; i >= 1; i--) {
                     if (i < bound) stringBuilder.append("|");
                     if (i == bound) stringBuilder.append("|§c");
                     if (i > bound) stringBuilder.append("|");
                  }
                  player.sendActionBar(stringBuilder.toString(), 0, 1, 0);
               }
            }
         });
      }, 1);
   }

   public ItemGunBase(String name) {
      super("gun:" + name, name, name);
   }

   public static GunData getGunData(Class<? extends ItemGunBase> clazz) {
      return GunPlugin.getInstance().getGunDataMap().get(clazz);
   }

   public abstract int getSkinId();

   public abstract float getDropItemScale();

   @Override
   public boolean canAlwaysEat() {
      return true;
   }

   @Override
   public Map.Entry<Plugin, Food> getFood() {
      return Map.entry(GunPlugin.getInstance(), new FoodNormal(0, 0F)
              .addRelative(this.getNamespaceId(), 0, GunPlugin.getInstance())
              .setEatingTickSupplier(() -> (int) this.getGunData().getFireCoolDown() * 20)
      );
   }

   @Override
   public boolean onClickAir(Player player, Vector3 directionVector) {
      return false;
   }

   @Override
   public boolean onUse(Player player, int ticksUsed) {
      return false;
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public CustomItemDefinition getDefinition() {
      return CustomItemDefinition
              .edibleBuilder(this, ItemCreativeCategory.EQUIPMENT)
              .creativeGroup("itemGroup.name.gun")
              .allowOffHand(true)
              .build();
   }

   public void interact(Player player) {
      if (GunPlugin.getInstance().getCoolDownTimer().isCooling(player)) {
         return;
      }
      ItemGunBase itemGun = (ItemGunBase) player.getInventory().getItemInHand();
      if (itemGun.getAmmoCount() > 0) {
         itemGun.getGunData().fire(player, itemGun);
         itemGun.setAmmoCount(itemGun.getAmmoCount() - 1);
         player.getInventory().setItem(player.getInventory().getHeldItemIndex(), itemGun);
         GunPlugin.getInstance().getCoolDownTimer().addCoolDown(player, (int) (itemGun.getGunData().getFireCoolDown() * 20), () -> {
         }, () -> CoolDownTimer.Operator.NO_ACTION, CoolDownTimer.Type.FIRECOOLDOWN);
         return;
      }
      if (itemGun.getAmmoCount() == 0) {//todo:debug
         if (itemGun.reload(player)) {
         }
      }
   }

   public boolean reload(Player player) {
      CoolDownTimer coolDownTimer = GunPlugin.getInstance().getCoolDownTimer();
      if (coolDownTimer.isCooling(player)) {
         CoolDownTimer.CoolDown coolDown = coolDownTimer.getCoolDownMap().get(player);
         if (coolDown.getType() == CoolDownTimer.Type.RELOAD)
            coolDownTimer.interrupt(player);
         return false;
      }
      if (player.getGamemode() != Player.CREATIVE && !player.getInventory().contains(Item.fromString("cole:akm_mag"))) {
         this.getGunData().emptyGun(player);
         return false;
      }

      this.getGunData().startReload(player);
      GunPlugin.getInstance().getTask().stopFire(player);
      GunPlugin.getInstance().getCoolDownTimer().addCoolDown(player, (int) (this.getGunData().getReloadTime() * 20), () -> {
         this.getGunData().reloadFinish(player);
         this.setAmmoCount(this.getGunData().getMagSize());
         player.getInventory().setItem(player.getInventory().getHeldItemIndex(), this);
         if (player.getGamemode() != Player.CREATIVE) {
            for (Map.Entry<Integer, Item> entry : player.getInventory().getContents().entrySet()) {
               Item item = entry.getValue();
               int slot = entry.getKey();
               if (item.equals(Item.fromString("cole:akm_mag"))) {
                  item.setCount(item.count - 1);
                  player.getInventory().setItem(slot, item);
                  return;
               }
            }
         }
      }, () -> CoolDownTimer.Operator.INTERRUPT, CoolDownTimer.Type.RELOAD);
      return true;
   }

   public enum GunInteractAction {
      FIRE,
      RELOAD,
      COOLING,
      EMPTY_GUN
   }


   public int getAmmoCount() {
      if (this.getNamedTag() != null) {
         return this.getNamedTag().getInt("ammoCount");
      }
      return 0;
   }

   public void setAmmoCount(int count) {
      if (this.getNamedTag() != null) {
         this.setNamedTag(this.getNamedTag().putInt("ammoCount", count));
      } else {
         this.setNamedTag(new CompoundTag().putInt("ammoCount", count));
      }
   }

   private static class Listener implements cn.nukkit.event.Listener {}
}