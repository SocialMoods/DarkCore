package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.guns.ItemMagBase;

import java.util.UUID;

public class ItemGunAkm extends ItemGunBase {
   public ItemGunAkm() {
      super("cole:akm", "AKM", "akm");
      this.gunData = getGunData(this.getClass());
      this.setCustomName("§r§fAKM");
      if (!this.getNamedTag().contains("ammoCount")) {
         this.setAmmoCount(this.getGunData().getMagSize());
      } else {
         this.setAmmoCount(this.getAmmoCount());
      }
   }

   public void doInit() {
   }

   public int getSkinId() {
      return 12;
   }

   public float getDropItemScale() {
      return 1.0F;
   }

   public static class ItemMagAkm extends ItemMagBase {
      public ItemMagAkm() {
         super("cole:akm_mag", "Патроны", "akm_mag");
      }

      public int getSkinId() {
         return 13;
      }

      public float getDropItemScale() {
         return 1.0F;
      }
   }
}
