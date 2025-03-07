package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemKnifeBase;

import java.util.Arrays;
import java.util.List;

public class ItemKnife extends ItemKnifeBase {
   public ItemKnife() {
      super("cole:cknife", "Нож", "cknife", 4);
      List<String> lore = Arrays.asList(
              "                                     ",
              "§r§f- с шансом §220%§r§f вызывает кровотечение§r§f",
              "§r§f- случайно увеличивает урон в зависимости от §2уровня§r§f",
              "§r§f- с шансом §25%§r§f вы можете пораниться свои же ножом§r§f"
      );

      String[] loreArray = lore.toArray(new String[0]);
      this.setLore(loreArray);
   }
}
