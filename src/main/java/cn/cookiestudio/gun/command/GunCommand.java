package cn.cookiestudio.gun.command;

import cn.cookiestudio.easy4form.window.BFormWindowCustom;
import cn.cookiestudio.easy4form.window.BFormWindowSimple;
import cn.cookiestudio.easy4form.window.BFormWindowSimple.Builder;
import cn.cookiestudio.gun.GunPlugin;
import cn.cookiestudio.gun.guns.GunData;
import cn.cookiestudio.gun.guns.ItemGunBase;
import cn.cookiestudio.gun.playersetting.PlayerSettingMap;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import java.util.ArrayList;
import java.util.List;

public class GunCommand extends Command {
   public GunCommand(String name) {
      super(name, "Настройка оружий");
      this.setPermission("gun.command");
      this.commandParameters.clear();
      this.commandParameters.put("default", new CommandParameter[]{CommandParameter.newEnum("opt", new String[]{"data", "setting"})});
   }

   public boolean execute(CommandSender commandSender, String s, String[] strings) {
      if (commandSender instanceof ConsoleCommandSender) {
         commandSender.sendMessage("Эта команда не может быть использована в консоли!");
         return true;
      } else if (strings.length == 0) {
         return true;
      } else {
         Player player = (Player)commandSender;
         if (strings[0].equals("data")) {
            if (!commandSender.isOp()) {
               commandSender.sendMessage("У вас недостаточно прав для использования этой команды!");
               return true;
            } else {
               Builder simpleFormBuilder = BFormWindowSimple.getBuilder();
               simpleFormBuilder.setTitle("Выберите оружие, параметры которого вы хотите изменить:");
               GunPlugin.getInstance().getGunDataMap().values().forEach((gunData) -> {
                  simpleFormBuilder.addButton(new ElementButton(gunData.getGunName(), new ElementButtonImageData("path", "textures/items/book_writable")));
               });
               simpleFormBuilder.setResponseAction((e) -> {
                  if (e.getResponse() != null) {
                     FormResponseSimple responseSimple = (FormResponseSimple)e.getResponse();
                     String gunName = responseSimple.getClickedButton().getText();
                     Class<? extends ItemGunBase> gunClass = (Class)GunPlugin.getInstance().getStringClassMap().get(gunName);
                     GunData gunData = (GunData)GunPlugin.getInstance().getGunDataMap().get(gunClass);
                     cn.cookiestudio.easy4form.window.BFormWindowCustom.Builder customFormBuilder = BFormWindowCustom.getBuilder();
                     customFormBuilder.setTitle(gunName);
                     customFormBuilder.addElements(new ElementInput("Размер магазина", "magSize", String.valueOf(gunData.getMagSize())));
                     customFormBuilder.addElements(new ElementInput("Кулдаун огня", "fireCoolDown", String.valueOf(gunData.getFireCoolDown())));
                     customFormBuilder.addElements(new ElementInput("Время перезарядки", "reloadTime", String.valueOf(gunData.getReloadTime())));
                     customFormBuilder.addElements(new ElementInput("Уровень эффекта замедления стоя", "slownessLevel", String.valueOf(gunData.getSlownessLevel())));
                     customFormBuilder.addElements(new ElementInput("Уровень замедления при прицеливании", "slownessLevelAim", String.valueOf(gunData.getSlownessLevelAim())));
                     customFormBuilder.addElements(new ElementInput("Интенсивность тряски при стрельбе", "fireSwingIntensity", String.valueOf(gunData.getFireSwingIntensity())));
                     customFormBuilder.addElements(new ElementInput("Урон", "hitDamage", String.valueOf(gunData.getHitDamage())));
                     customFormBuilder.addElements(new ElementInput("Дальность", "range", String.valueOf(gunData.getRange())));
                     customFormBuilder.addElements(new ElementInput("Эффект частиц траектории", "particle", gunData.getParticle()));
                     customFormBuilder.addElements(new ElementInput("НЕ МЕНЯТЬ!", "magName", gunData.getMagName()));
                     customFormBuilder.addElements(new ElementInput("Отдача", "recoil", String.valueOf(gunData.getRecoil())));
                     customFormBuilder.addElements(new ElementInput("Время тряски при стрельбе", "fireSwingDuration", String.valueOf(gunData.getFireSwingDuration())));
                     customFormBuilder.setResponseAction((e1) -> {
                        if (e1.getResponse() != null) {
                           FormResponseCustom responseCustom = (FormResponseCustom)e1.getResponse();
                           gunData.setMagSize(Integer.parseInt(responseCustom.getInputResponse(0)));
                           gunData.setFireCoolDown(Double.parseDouble(responseCustom.getInputResponse(1)));
                           gunData.setReloadTime(Double.parseDouble(responseCustom.getInputResponse(2)));
                           gunData.setSlownessLevel(Integer.parseInt(responseCustom.getInputResponse(3)));
                           gunData.setSlownessLevelAim(Integer.parseInt(responseCustom.getInputResponse(4)));
                           gunData.setFireSwingIntensity(Double.parseDouble(responseCustom.getInputResponse(5)));
                           gunData.setHitDamage(Double.parseDouble(responseCustom.getInputResponse(6)));
                           gunData.setRange(Double.parseDouble(responseCustom.getInputResponse(7)));
                           gunData.setParticle(responseCustom.getInputResponse(8));
                           gunData.setMagName(responseCustom.getInputResponse(9));
                           gunData.setRecoil(Double.parseDouble(responseCustom.getInputResponse(10)));
                           gunData.setFireSwingDuration(Double.parseDouble(responseCustom.getInputResponse(11)));
                           GunPlugin.getInstance().saveGunData(gunData);
                           player.sendMessage("§aУспешно!");
                        }

                     });
                     customFormBuilder.build().sendToPlayer(player);
                  }

               });
               simpleFormBuilder.build().sendToPlayer(player);
               return true;
            }
         } else if (strings[0].equals("setting")) {
            BFormWindowCustom custom = new BFormWindowCustom("Настройки");
            PlayerSettingMap settings = GunPlugin.getInstance().getPlayerSettingPool().getPlayerSetting(player.getName());
            List<String> list = new ArrayList();
            list.add(PlayerSettingMap.FireMode.AUTO.name());
            list.add(PlayerSettingMap.FireMode.MANUAL.name());
            custom.addElement(new ElementDropdown("⊳ Режим стрельбы:", list, settings.getFireMode().ordinal()));
            custom.addElement(new ElementToggle("⊳ Включить частицы траектории", settings.isOpenTrajectoryParticle()));
            custom.addElement(new ElementToggle("⊳ Включить дым от выстрела", settings.isOpenMuzzleParticle()));
            custom.setResponseAction((e) -> {
               if (e.getResponse() != null) {
                  FormResponseCustom response = (FormResponseCustom)e.getResponse();
                  String selectedFireMode = response.getDropdownResponse(0).getElementContent();
                  if (selectedFireMode.equals(PlayerSettingMap.FireMode.AUTO.name())) {
                     settings.setFireMode(PlayerSettingMap.FireMode.AUTO);
                  } else {
                     settings.setFireMode(PlayerSettingMap.FireMode.MANUAL);
                  }

                  boolean trajectoryParticlesEnabled = response.getToggleResponse(1);
                  boolean muzzleParticlesEnabled = response.getToggleResponse(2);
                  settings.setOpenTrajectoryParticle(trajectoryParticlesEnabled);
                  settings.setOpenMuzzleParticle(muzzleParticlesEnabled);
                  GunPlugin.getInstance().getPlayerSettingPool().write(player.getName(), settings);
                  player.sendMessage("§aНастройки успешно сохранены!");
               }

            });
            custom.sendToPlayer(player);
            return true;
         } else {
            return true;
         }
      }
   }
}
