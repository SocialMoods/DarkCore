package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunP90 extends ItemGunBase {
   public ItemGunP90() {
      super("cole:p90", "P90", "p90");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fP90");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 3;
   }

   public float getDropItemScale() {
      return 0.1F;
   }
}
