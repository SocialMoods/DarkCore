package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunM3 extends ItemGunBase {
   public ItemGunM3() {
      super("cole:m3", "M3", "m3");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fM3");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public int getSkinId() {
      return 9;
   }

   public float getDropItemScale() {
      return 0.1F;
   }
}
