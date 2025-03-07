package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;

public class ItemGunMp5 extends ItemGunBase {
   public ItemGunMp5() {
      super("cole:mp5", "MP5", "mp5");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fMP5");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 18;
   }

   public float getDropItemScale() {
      return 0.08F;
   }
}
