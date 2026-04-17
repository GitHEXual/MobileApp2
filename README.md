# My Application

Android-приложение для **просмотра прогноза погоды** с отдельными экранами, локальным хранением избранных городов и заметок в `Room`, картой (OpenStreetMap), избранным и настройками.

## Карта и геокодинг (без ключей API)

- **Тайлы карты**: [osmdroid](https://github.com/osmdroid/osmdroid) и тайлы OpenStreetMap (например Mapnik), ключ не требуется.
- **Поиск места на карте** и **координаты для произвольного «домашнего» города**: [Open-Meteo Geocoding API](https://open-meteo.com/en/docs/geocoding-api) — тот же стек, что и **прогноз погоды** (Open-Meteo Forecast), без API-ключа.

## Требования

- **JDK 17 или новее** — для **Gradle 9.3** и **Android Gradle Plugin 9.1**
- **Android SDK**

## Готовый debug APK в корне репозитория

В корне лежит **`app-debug.apk`** (собранный `assembleDebug`). 


## Сборка debug APK через CLI

Выполнять из корня проекта:

```bash
./gradlew :app:assembleDebug
```

Собранный APK появится в пути:

`app/build/outputs/apk/debug/app-debug.apk`


## Полезные команды

Проверка локальных unit-тестов:

```bash
./gradlew :app:testDebugUnitTest
```

Сборка androidTest APK:

```bash
./gradlew :app:assembleDebugAndroidTest
```

## Установка на смартфон через adb

1. Включить на смартфоне:
   - `Для разработчиков`
   - `Отладка по USB`
2. Подключить смартфон по USB и проверить, что устройство видно:

```bash
adb devices
```

В списке должно быть устройство со статусом `device`.

3. Собрать debug APK:

```bash
./gradlew :app:assembleDebug
```

4. Установить APK на устройство (из корня репозитория):

```bash
adb install -r app-debug.apk
```
