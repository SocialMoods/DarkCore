package cn.cookiestudio.gun.guns.achieve;

import cn.cookiestudio.gun.guns.ItemKnifeBase;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.data.ItemCreativeCategory;
import cn.nukkit.item.customitem.data.ItemCreativeGroup;
import cn.nukkit.item.customitem.data.RenderOffsets;

import java.util.Arrays;
import java.util.List;

public class ItemKnifeCatana extends ItemKnifeBase {
   public ItemKnifeCatana() {
      super("cole:ckatana", "Катана", "ckatana", 6);
      List<String> lore = Arrays.asList(
              "                                     ",
              "§r§f- на §225%§r§f выше скорость удара если владелец бежит§r§f",
              "§r§f- с шансом §210%§r§f вызывает болевой шок у противника§r§f",
              "§r§f- с шансом §23% вы промахиваетесь§r§f"
      );

      String[] loreArray = lore.toArray(new String[0]);
      this.setLore(loreArray);
   }

   public int scaleOffset() {
      return 16;
   }

   @Override
   public CustomItemDefinition getDefinition() {
      return CustomItemDefinition
              .simpleBuilder(this, ItemCreativeCategory.EQUIPMENT)
              .creativeGroup(ItemCreativeGroup.SWORD)
              .allowOffHand(true)
              .handEquipped(true)
              .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
              .build();
   }
}
