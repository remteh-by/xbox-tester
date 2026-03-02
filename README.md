# Xbox Controller Tester 🟢

Android-приложение для тестирования контроллера **Xbox Series X|S**.

## Функции

- 🎮 Тестирование всех кнопок (A, B, X, Y, LB/RB, Menu, View, Xbox, Share)
- 🕹️ Визуализация стиков с настройкой мёртвой зоны
- 🎯 Калибровка — min/max отслеживание + сброс
- 📊 Отображение триггеров LT/RT в реальном времени
- 🔊 Тестирование вибрации геймпада (короткая, средняя, длинная, паттерн)
- ⏱ Время удержания кнопок + счётчик нажатий
- 🐛 Raw Data дебаг для всех осей

## Установка

Скачайте APK из раздела [Releases](https://github.com/remteh-by/xbox-tester/releases).

## Разработка

```bash
# Сборка debug APK
./gradlew assembleDebug

# Сборка release APK + AAB
./gradlew assembleRelease bundleRelease
```

## Автор

**RemTeh.by** — [remteh-by](https://github.com/remteh-by)

## Лицензия

MIT License
