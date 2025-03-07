package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunM249 extends ItemGunBase {
   public ItemGunM249() {
      super("cole:m249", "M249", "m249");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fM249");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 14;
   }

   public float getDropItemScale() {
      return 0.045F;
   }
}
