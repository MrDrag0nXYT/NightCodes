package zxc.mrdrag0nxyt.nightcodes.config;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Messages extends AbstractConfig {

    private Component onlyForPlayers, noPermission, databaseError,
            referralDeleted, referralPaused, referralUnPaused, referralAlreadyPaused, referralAlreadyUnPaused, referralExists, referralNotExist, referralStatePaused, referralStateUnPaused,
            codeNotFound, codeActivated, codeAlreadyActivated, codeCannotActivateOwn, codeCannotActivateByPlayedTime,
            adminReloaded;
    private List<Component> referralUsage, referralCreated, referralStats, codeUsage, adminUsage;
    private String autocompletePlaceholder;

    public Messages(NightCodes plugin) {
        super(plugin, "messages.yml");
    }

    @Override
    protected void updateConfig() {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        onlyForPlayers = miniMessage.deserialize(
                checkValue("global.only-for-players", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Эта команда <#d45079>не доступна</#d45079> из консоли!")
        );
        noPermission = miniMessage.deserialize(
                checkValue("global.no-permission", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>У вас <#d45079>недостаточно прав</#d45079> для выполнения этой команды!")
        );
        databaseError = miniMessage.deserialize(
                checkValue("global.database-error", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>При выполнении действия в базе данных <#d45079>произошла ошибка</#d45079>!")
        );

        // /referral

        referralUsage = checkValue("referral.usage", List.of(
                "",
                " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Информация",
                "  <#c0c0c0>‣ <#745c97>/referral create <#c0c0c0>- <#fcfcfc>создать промо-код",
                "  <#c0c0c0>‣ <#745c97>/referral delete <#c0c0c0>- <#fcfcfc>удалить промо-код",
                "  <#c0c0c0>‣ <#745c97>/referral pause <#c0c0c0>- <#fcfcfc>приостановить использование промо-кода",
                "  <#c0c0c0>‣ <#745c97>/referral unpause <#c0c0c0>- <#fcfcfc>восстановить использование промо-кода",
                "  <#c0c0c0>‣ <#745c97>/referral stats <#c0c0c0>- <#fcfcfc>посмотреть статистику промо-кода",
                ""
        )).stream().map(miniMessage::deserialize).collect(Collectors.toList());

        referralCreated = checkValue("referral.created", List.of(
                "",
                " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Промо-код <#745c97>%player%</#745c97> успешно создан!",
                "  <#c0c0c0>‣ <#fcfcfc>Для активации игрокам необходимо использовать <#745c97>/code %player%</#745c97>",
                ""
        )).stream().map(miniMessage::deserialize).collect(Collectors.toList());

        referralDeleted = miniMessage.deserialize(
                checkValue("referral.deleted", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Промо-код <#745c97>%player%</#745c97> успешно удалён!")
        );

        referralPaused = miniMessage.deserialize(
                checkValue("referral.paused", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Активация промо-кода <#745c97>%player%</#745c97> успешно приостановлена!")
        );

        referralUnPaused = miniMessage.deserialize(
                checkValue("referral.unpaused", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Активация промо-кода <#745c97>%player%</#745c97> успешно восстановлена!")
        );

        referralAlreadyPaused = miniMessage.deserialize(
                checkValue("referral.already.paused", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Активация промо-кода <#745c97>%player%</#745c97> <#d45079>уже была приостановлена</#d45079>!")
        );

        referralAlreadyUnPaused = miniMessage.deserialize(
                checkValue("referral.already.unpaused", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Активация промо-кода <#745c97>%player%</#745c97> <#d45079>уже была восстановлена</#d45079>!")
        );

        referralExists = miniMessage.deserialize(
                checkValue("referral.exists", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>У вас уже есть промо-код <#745c97>%player%</#745c97>")
        );

        referralNotExist = miniMessage.deserialize(
                checkValue("referral.not-exist", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>У вас ещё нет промо-кода! Создайте его через <#745c97>/referral create</#745c97>")
        );

        referralStatePaused = miniMessage.deserialize(
                checkValue("referral.state.paused", "<#d45079>приостановлено</#d45079>")
        );
        referralStateUnPaused = miniMessage.deserialize(
                checkValue("referral.state.unpaused", "<#ace1af>активно</#ace1af>")
        );

        referralStats = checkValue("referral.stats", List.of(
                "",
                " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Статистика использования промо-кода <#745c97>%player%</#745c97>:",
                "  <#c0c0c0>‣ <#fcfcfc>Промокод использован <#745c97>%count%</#745c97> раз",
                "  <#c0c0c0>‣ <#fcfcfc>Использование промо-кода <#745c97>%state%</#745c97>",
                ""
        )).stream().map(miniMessage::deserialize).collect(Collectors.toList());

        // /code

        codeUsage = checkValue("code.usage", List.of(
                "",
                " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Информация",
                "  <#c0c0c0>‣ <#745c97>/code код <#c0c0c0>- <#fcfcfc>активировать промо-код",
                ""
        )).stream().map(miniMessage::deserialize).collect(Collectors.toList());

        codeNotFound = miniMessage.deserialize(
                checkValue("code.not-found", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Промо-код <#745c97>%referral_code%</#745c97> <#d45079>не найден</#d45079>")
        );

        codeActivated = miniMessage.deserialize(
                checkValue("code.activated", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Вы <#ace1af>успешно</#ace1af> активировали промо-код <#745c97>%referral_code%</#745c97>!")
        );

        codeAlreadyActivated = miniMessage.deserialize(
                checkValue("code.already-activated", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Вы <#d45079>уже активировали</#d45079> промо-код!")
        );

        codeCannotActivateOwn = miniMessage.deserialize(
                checkValue("code.cannot-activate-own-code", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Вы <#d45079>не можете</#d45079> активировать свой промо-код!")
        );

        autocompletePlaceholder = checkValue("code.autocomplete-placeholder", "промокод");

        codeCannotActivateByPlayedTime = miniMessage.deserialize(
                checkValue("code.requirements.time", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Для активации промо-кода необходимо наиграть минимум <#745c97>1 час</#745c97>")
        );

        // /nightcodes

        adminUsage = checkValue("nightcodes.usage", List.of(
                "",
                " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Информация",
                "  <#c0c0c0>‣ <#745c97>/nightcodes reload <#c0c0c0>- <#fcfcfc>перезагрузить плагин",
                ""
        )).stream().map(miniMessage::deserialize).collect(Collectors.toList());

        adminReloaded = miniMessage.deserialize(
                checkValue("nightcodes.reloaded", " <#745c97>NightCodes <#c0c0c0>› <#fcfcfc>Плагин <#ace1af>успешно</#ace1af> перезагружен")
        );
    }
}
