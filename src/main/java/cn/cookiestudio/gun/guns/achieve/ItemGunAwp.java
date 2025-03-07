package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunAwp extends ItemGunBase {
   public ItemGunAwp() {
      super("cole:awp", "AWP", "awp");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fAWP");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 5;
   }

   public float getDropItemScale() {
      return 0.1F;
   }
}
