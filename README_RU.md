<center>
<img src="docs/NightCodes-Title.png">
<p><b>NightCodes</b> - Плагин для Paper, дающий возможность игрокам создавать реферальные коды и получать бонусы за приглашение игроков на ваш сервер!</p>

<b><a href="README.md">English</a></b> | <u>Russian</u>
</center>

***

# 🚀 Особенности

- Хранение данных и сообщений игроков в базе данных (ну хоть не в YAML) с помощью библиотеки HikariCP:
  - [x] SQLite
  - [ ] MySQL ( #todo )
- Поддержка **HEX цветов** через MiniMessage
- Возможность **удалить или приостановить** работу кода
- Возможность **ограничить активацию** кода игрокам, которые ещё не наиграли указанное время (настраивается в `config.yml`, по умолчанию 1 час)

# 💾 Технические требования

- Java **17+**
- Paper (или форки, такие как Purpur) версии **1.18.2 и новее** <u>(не Spigot/CraftBukkit)</u>

# ⚡ Права и команды

## /referral

> [!TIP]
> Сокращённый вариант: /ref

#### Использование:
- **/referral create** - Создать реферальный код
  - Право: `nightcodes.player.create`
- **/referral delete** - Удалить реферальный код
  - Право: `nightcodes.player.delete`
- **/referral pause** - Приостановить работу реферального кода
  - Право: `nightcodes.player.pause`
- **/referral unpause** - Восстановить работу реферального кода
  - Право: `nightcodes.player.unpause`
- **/referral stats** - Получить статистику по реферальному коду
  - Право: `nightcodes.player.stats`

## /code

#### Использование:
- **/code <код>** - Активировать код
  - Право: `nightcodes.player.activate`

## /nightcodes

> [!TIP]
> Сокращённый вариант: /ncodes

#### Использование:
- **/nightcodes reload** - Перезагрузить плагин
  - Право: `nightcodes.admin.reload`



***



# ⚙ Дополнительно

### Если вы нашли баг или хотите помочь в разработке - не стесняйтесь обращаться ко мне
  - Ссылки на контакты [тут](https://drakoshaslv.ru/)

### Также (по желанию) вы можете дать мне денег:
  - [DonationAlerts](https://www.donationalerts.com/r/mrdrag0nxyt)
  - TON:
    ```
    UQAwUJ_DWQ26_b94mFAy0bE1hrxVRHrq51umphFPreFraVL2
    ```
  - ETH:
    ```
    0xf5D0Ab258B0f8EeA7EA07cF1050B35cc12E06Ab0
    ```



<center><h3>Сделано специально для <a href="https://nshard.ru">NightShard</a></h3></center>