#!/bin/bash

# Переход в корень проекта
cd "$(dirname "$0")/.."

# Настройки проекта Anchor
MODULE=":Anchor-MobileApp:androidApp"
PACKAGE="com.chknkv.anchor.android"
ACTIVITY=".AnchorMainActivity"

ADB="${ANDROID_HOME:-$HOME/Library/Android/sdk}/platform-tools/adb"

# Поиск всех подключенных устройств
ALL_DEVICES=$("$ADB" devices | grep -v "List" | grep "device$" | awk '{print $1}')

if [ -z "$ALL_DEVICES" ]; then
    echo "❌ No devices or emulators found. Please connect a device or start an emulator."
    exit 1
fi

DEVICE_ID=""
# ПРИОРИТЕТ: Ищем физическое устройство (не содержит 'emulator-' или 'localhost'/'127.0.0.1')
for DEV in $ALL_DEVICES; do
    if [[ ! "$DEV" =~ ^emulator- ]] && [[ ! "$DEV" =~ ^localhost: ]] && [[ ! "$DEV" =~ ^127.0.0.1: ]]; then
        DEVICE_ID=$DEV
        break
    fi
done

# Если физическое устройство не найдено, берем первое доступное (эмулятор)
if [ -z "$DEVICE_ID" ]; then
    DEVICE_ID=$(echo "$ALL_DEVICES" | head -1)
    echo "🤖 Target: Emulator ($DEVICE_ID)"
else
    echo "📱 Target: Physical Device ($DEVICE_ID)"
fi

echo "🔨 Building Android App..."

# Сборка и установка
./gradlew $MODULE:installDebug -Dadb.device.arg="$DEVICE_ID" -q --no-daemon

if [ $? -eq 0 ]; then
    echo "🚀 Launching $PACKAGE..."
    "$ADB" -s "$DEVICE_ID" shell am start -n "$PACKAGE/$ACTIVITY" > /dev/null
    echo "✅ Done!"
else
    echo "❌ Build failed."
    exit 1
fi
