package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunBarrett extends ItemGunBase {
   public ItemGunBarrett() {
      super("cole:barrett", "Barrett", "barrett");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fBarrett");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 7;
   }

   public float getDropItemScale() {
      return 0.08F;
   }
}
