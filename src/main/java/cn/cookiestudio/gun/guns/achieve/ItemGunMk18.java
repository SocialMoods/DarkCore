package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunMk18 extends ItemGunBase {
   public ItemGunMk18() {
      super("cole:mk18", "MK18", "mk18");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fMK18");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 16;
   }

   public float getDropItemScale() {
      return 0.06F;
   }
}
