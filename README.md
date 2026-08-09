# AI Agent Android App

这是一个原生的 Android AI 聊天应用，支持对接 OpenAI API。

## 功能特点

- 🤖 原生 Kotlin 开发
- 💬 实时 AI 对话
- 🎨 Material Design UI
- 🔌 支持 OpenAI/兼容 API
- 📦 GitHub Actions 自动编译 APK

## 配置 API Key

在 `app/src/main/java/com/aibot/agent/MainActivity.kt` 中修改：

```kotlin
private var apiKey = "YOUR_API_KEY_HERE"  // 替换为你的 API Key
```

支持的 API：
- OpenAI: `https://api.openai.com/v1/chat/completions`
- 其他兼容 OpenAI 格式的 API

## 编译 APK

### 方法 1：GitHub Actions（推荐）

1. Fork 或推送代码到你的 GitHub 仓库
2. 进入 Actions 页面
3. 点击 "Build AI Agent APK"
4. 点击 "Run workflow"
5. 完成后下载 APK

### 方法 2：本地编译

```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

## 项目结构

```
ai-agent-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/aibot/agent/
│   │   │   ├── MainActivity.kt
│   │   │   └── MessageAdapter.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── .github/workflows/
│   └── build.yml
└── build.gradle.kts
```

## 许可证

MIT License
