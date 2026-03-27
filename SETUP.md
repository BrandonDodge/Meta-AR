# HUD-Link Development Setup Guide

Step-by-step guide for team members to clone, build, and deploy the project.

## Prerequisites

### Software
1. **Unity Hub** — https://unity.com/download
2. **Unity 6 LTS** (6000.3.x) — install via Unity Hub with these modules:
   - Android Build Support
   - Android SDK & NDK Tools
   - OpenJDK
3. **Git** — https://git-scm.com
4. **Meta Quest Developer Hub** (optional) — https://developer.meta.com/downloads/quest-developer-hub

### Hardware
- **Meta Quest Pro** (or Quest 3/3S for testing)
- **USB-C data cable** (not a charge-only cable)
- **Mac or PC** for development

### Meta Developer Account
1. Create a Meta account at https://auth.meta.com if you don't have one
2. Go to https://developer.meta.com
3. Create or join a developer organization
4. Accept the developer terms

## Quest Pro Setup

### Enable Developer Mode
1. Open **Meta Quest/Horizon app** on your phone
2. Pair your Quest Pro to the app via Bluetooth
3. Menu > Devices > [your headset] > Headset Settings > **Developer Mode** > On
4. Restart the headset

### Connect to Computer
1. Plug Quest Pro into your computer via USB-C
2. Put on the headset
3. Accept the **"Allow USB debugging"** popup (check "Always allow")
4. Verify connection — open terminal and run:
   ```
   # macOS (Unity's bundled ADB):
   /Applications/Unity/Hub/Editor/6000.3.10f1/PlaybackEngines/AndroidPlayer/SDK/platform-tools/adb devices

   # Or if ADB is in PATH:
   adb devices
   ```
   You should see a device ID followed by "device" (not "unauthorized")

## Project Setup

### Clone the Repository
```bash
git clone https://github.com/BrandonDodge/Meta-XR.git
cd Meta-XR
```

### Open in Unity
1. Open **Unity Hub**
2. Click **Add** > navigate to the `Unity/` folder inside the cloned repo
3. If prompted about editor version mismatch, select your installed 6000.3.x version
4. Wait for the project to import (first time takes several minutes)

### Verify Project Configuration
1. **File > Build Settings** — confirm **Android** is the active platform. If not, select Android > Switch Platform
2. **Edit > Project Settings > XR Plug-in Management** (Android tab) — confirm **OpenXR** is checked
3. Under OpenXR settings, confirm **Meta Quest Support** is enabled

### Open the Scene
1. In the Project window, navigate to `Assets/Scenes/`
2. Double-click **HudLinkScene**
3. Hit **Play** — you should see the HUD with widgets displaying mock data

## Building and Deploying

1. Connect Quest Pro via USB-C (developer mode on, USB debugging accepted)
2. **File > Build Settings**
3. Ensure **HudLinkScene** is in the scenes list (Add Open Scenes if not)
4. Click **Build and Run**
5. Save the APK as `HudLink.apk`
6. Wait for build (5-10 min first time, faster after)
7. The app auto-launches on the headset

### Manual Install (if Build and Run fails)
```bash
adb install -r path/to/HudLink.apk
adb shell am start -n com.UnityTechnologies.com.unity.template.urpblank/com.unity3d.player.UnityPlayerActivity
```

## Project Structure

```
Meta-XR/
├── Unity/                          # Unity XR project
│   └── Assets/
│       ├── Scripts/
│       │   ├── Core/               # Scene bootstrap
│       │   ├── Data/               # Mock data providers
│       │   ├── HUD/                # HUD controller, grid layout, head follow
│       │   ├── Input/              # Controller/keyboard input
│       │   ├── Utils/              # FPS monitor, connection status
│       │   └── Widgets/            # Widget framework + implementations
│       ├── Prefabs/                # Widget prefabs
│       ├── Scenes/                 # HudLinkScene
│       └── Samples/                # Meta XR SDK samples (reference only)
├── android/                        # Android companion app (Kotlin)
│   ├── app/                        # Main app module
│   ├── core-data/                  # Domain models
│   ├── core-storage/               # Room database
│   ├── feature-health/             # Health data (mock + future Health Connect)
│   └── feature-location/           # GPS data (mock + future Fused Location)
└── Documents/                      # Sprint artifacts, architecture docs
```

## Widget Development

### Architecture
Widgets follow the `IWidget` interface → `BaseWidget` abstract class → concrete widget pattern.

```
IWidget (interface)
  └── BaseWidget (MonoBehaviour, handles slot/lifecycle)
        ├── HeartRateWidget
        ├── GPSWidget
        └── NotificationWidget
```

### Creating a New Widget

1. Create `Assets/Scripts/Widgets/MyWidget.cs`:
```csharp
using UnityEngine;
using TMPro;

namespace HudLink.Widgets
{
    public class MyWidget : BaseWidget
    {
        public override void Initialize(RectTransform slot)
        {
            base.Initialize(slot);
            WidgetStyles.CreateStyledBackground(transform, WidgetStyles.BgPrimary, WidgetStyles.AccentGreen);
            WidgetStyles.CreateHeader(transform, "★", "MY WIDGET", WidgetStyles.AccentGreen);
            // Add your UI elements...
        }

        public override void UpdateData(WidgetData data)
        {
            // Handle incoming data updates
        }
    }
}
```

2. Add a data class in `WidgetData.cs` if needed
3. Create a prefab: empty Panel under HUD Canvas > add your script > set Widget Id > drag to Assets/Prefabs/
4. Register in `HudLinkBootstrap.cs`

### Widget Constraints (from architecture doc)
- World-space Canvas only
- 22mm × 22mm minimum hit targets, 12mm spacing
- 20-40 draw calls per widget (cap 60)
- Target ≥72 FPS
- No UI occlusion checks unless needed

### Grid Slot Positions
```
TopLeft(0)    TopCenter(1)    TopRight(2)
MidLeft(3)    [SafeZone](4)   MidRight(5)
BottomLeft(6) BottomCenter(7) BottomRight(8)
```
Center slot (4) is reserved — never place widgets there.

## Troubleshooting

### "No Android devices found"
- Check USB cable (must be data-capable, not charge-only)
- Ensure developer mode is on
- Accept USB debugging prompt on headset
- Run `adb kill-server && adb start-server && adb devices`

### Build errors
- Ensure Android platform is active (File > Build Settings > Switch Platform)
- Check Console for script compilation errors
- Clean build: delete `Library/` folder and reopen project

### Widgets not showing
- Open HudLinkScene (not SampleScene)
- Check HUD Manager references in Inspector (none should be "None")
- Check widget prefab Widget Id fields match: heart_rate, gps, notifications
