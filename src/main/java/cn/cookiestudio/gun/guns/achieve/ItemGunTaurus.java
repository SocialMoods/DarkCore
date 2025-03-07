package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunTaurus extends ItemGunBase {
   public ItemGunTaurus() {
      super("cole:taurus", "Glock", "taurus");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fGlock");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 1;
   }

   public float getDropItemScale() {
      return 0.2F;
   }
}
