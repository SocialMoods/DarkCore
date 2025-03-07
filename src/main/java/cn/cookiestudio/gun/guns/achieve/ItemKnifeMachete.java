package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemKnifeBase;

import java.util.Arrays;
import java.util.List;

public class ItemKnifeMachete extends ItemKnifeBase {
   public ItemKnifeMachete() {
      super("cole:cmacheta", "Мачете", "cmacheta", 7);
      List<String> lore = Arrays.asList(
              "                                     ",
              "§r§f- с шансом §210%§r§f отравляет противника§r§f"
      );

      String[] loreArray = lore.toArray(new String[0]);
      this.setLore(loreArray);
   }
}
