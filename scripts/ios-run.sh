#!/bin/bash

# Переход в корень проекта
cd "$(dirname "$0")/.."

# Настройки проекта Anchor
XCODE_PROJ="Anchor-MobileApp/iosApp/iosApp.xcodeproj"
SCHEME="iosApp"
DERIVED_DATA="build/ios"

set +e

echo "🔍 Searching for devices..."

# 1. Ищем ЗАПУЩЕННЫЕ симуляторы
BOOTED_SIM=$(xcrun simctl list devices | grep "Booted" | head -1)

# 2. Ищем РЕАЛЬНО подключенные физические устройства
PHYSICAL_DEVICE=$(xcrun xctrace list devices 2>/dev/null | sed -n '/== Devices ==/,/== Simulators ==/p' | grep -i "iPhone\|iPad" | grep -v "Placeholder" | head -1)

DEVICE_UDID=""
DEVICE_NAME=""
IS_PHYSICAL=false
SDK=""

if [ -n "$BOOTED_SIM" ]; then
    # Найдено запущенный симулятор - ПРИОРИТЕТ
    DEVICE_UDID=$(echo "$BOOTED_SIM" | grep -oE '[A-F0-9-]{36}')
    DEVICE_NAME=$(echo "$BOOTED_SIM" | sed 's/ *(.*//' | xargs)
    IS_PHYSICAL=false
    DEST="platform=iOS Simulator,id=$DEVICE_UDID"
    SDK="iphonesimulator"
    echo "🤖 Target: Booted Simulator -> $DEVICE_NAME ($DEVICE_UDID)"
elif [ -n "$PHYSICAL_DEVICE" ]; then
    # Найдено физическое устройство (если нет запущенных симуляторов)
    DEVICE_UDID=$(echo "$PHYSICAL_DEVICE" | grep -oE '[A-F0-9-]{15,36}' | head -1)
    DEVICE_NAME=$(echo "$PHYSICAL_DEVICE" | sed 's/ (.*//' | sed 's/^[[:space:]]*//')
    IS_PHYSICAL=true
    DEST="id=$DEVICE_UDID"
    SDK="iphoneos"
    echo "📱 Target: Physical Device -> $DEVICE_NAME ($DEVICE_UDID)"
else
    # Если ничего не запущено и не подключено, берем первый попавшийся выключенный симулятор iPhone
    FIRST_SIM=$(xcrun simctl list devices | grep "iPhone" | grep "Shutdown" | head -1)
    if [ -n "$FIRST_SIM" ]; then
        DEVICE_UDID=$(echo "$FIRST_SIM" | grep -oE '[A-F0-9-]{36}')
        DEVICE_NAME=$(echo "$FIRST_SIM" | sed 's/ *(.*//' | xargs)
        IS_PHYSICAL=false
        DEST="platform=iOS Simulator,id=$DEVICE_UDID"
        SDK="iphonesimulator"
        echo "🤖 Target: Starting Simulator -> $DEVICE_NAME ($DEVICE_UDID)"
    else
        echo "❌ No iOS devices or simulators found."
        exit 1
    fi
fi

# Проверка на смену платформы (очистка, если собирали под другую платформу ранее)
PREV_PLATFORM_FILE="$DERIVED_DATA/.last_platform"
mkdir -p "$DERIVED_DATA"
if [ -f "$PREV_PLATFORM_FILE" ]; then
    PREV_PLATFORM=$(cat "$PREV_PLATFORM_FILE")
    if [ "$PREV_PLATFORM" != "$SDK" ]; then
        echo "🧹 Platform changed from $PREV_PLATFORM to $SDK. Cleaning..."
        rm -rf "$DERIVED_DATA"
        mkdir -p "$DERIVED_DATA"
    fi
fi
echo "$SDK" > "$PREV_PLATFORM_FILE"

echo "🔨 Building Xcode Project..."

# Сборка
if [ "$IS_PHYSICAL" = true ]; then
    SIGNING_FLAGS="-allowProvisioningUpdates"
else
    SIGNING_FLAGS="CODE_SIGNING_ALLOWED=NO CODE_SIGNING_REQUIRED=NO"
fi

xcodebuild -project "$XCODE_PROJ" \
           -scheme "$SCHEME" \
           -configuration Debug \
           -destination "$DEST" \
           -sdk "$SDK" \
           -derivedDataPath "$DERIVED_DATA" \
           $SIGNING_FLAGS \
           ONLY_ACTIVE_ARCH=YES \
           build | grep -E "error:|warning:|succeeded"

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo "📦 Preparing to launch..."
    BUNDLE_ID=$(xcodebuild -project "$XCODE_PROJ" -scheme "$SCHEME" -showBuildSettings 2>/dev/null | grep "PRODUCT_BUNDLE_IDENTIFIER" | head -1 | sed 's/.*= *//' | tr -d ' ')

    # Точный путь к .app в зависимости от SDK
    CONFIG_FOLDER="Debug-iphoneos"
    if [ "$IS_PHYSICAL" = false ]; then
        CONFIG_FOLDER="Debug-iphonesimulator"
    fi
    APP_PATH=$(find "$DERIVED_DATA/Build/Products/$CONFIG_FOLDER" -name "*.app" -type d | head -1)

    if [ "$IS_PHYSICAL" = false ]; then
        echo "📲 Launching on Simulator..."
        xcrun simctl boot "$DEVICE_UDID" 2>/dev/null
        xcrun simctl install "$DEVICE_UDID" "$APP_PATH"
        xcrun simctl launch "$DEVICE_UDID" "$BUNDLE_ID" > /dev/null
    else
        echo "📲 Installing on Physical Device..."
        if command -v ios-deploy >/dev/null 2>&1; then
            ios-deploy --id "$DEVICE_UDID" --bundle "$APP_PATH" --justlaunch
        else
            xcrun devicectl device install app --device "$DEVICE_UDID" "$APP_PATH"
            xcrun devicectl device process launch --device "$DEVICE_UDID" "$BUNDLE_ID"
        fi
    fi
    echo "✅ Done!"
else
    echo "❌ Build failed. Если проблема с 'Supported platforms', попробуйте сделать Clean в Xcode."
    exit 1
fi
