# My Application

Android-приложение для **просмотра прогноза погоды** с отдельными экранами, локальным хранением избранных городов и заметок в `Room`, картой, избранным и настройками.

## Требования

- **JDK 17 или новее** — для **Gradle 9.3** и **Android Gradle Plugin 9.1**
- **Android SDK**

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

## Что сдавать

- Архив с исходным кодом проекта
- Эту инструкцию по сборке
- Опционально: собранный `app-debug.apk`
