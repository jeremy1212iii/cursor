# Android Sit-Up Counter App - Camera Implementation

## Project Plan

**Created:** 2025-12-05 08:11:45

### Overview
This project implements an Android application that counts sit-ups using the device camera and pose detection technology.

### Best Practice Solution Recommendation

#### Technology Stack:
1. **Camera API**: CameraX (Modern, lifecycle-aware, recommended by Google)
2. **Pose Detection**: ML Kit Pose Detection (Easy integration) or MediaPipe (Higher accuracy)
3. **Architecture**: MVVM with LiveData
4. **Language**: Kotlin (Modern Android development standard)
5. **Minimum SDK**: API 21 (Android 5.0) for broad compatibility

#### Implementation Approach:
- **Option 1 (Recommended for MVP)**: ML Kit Pose Detection
  - Pros: Easy to integrate, good accuracy, free, no model download needed
  - Cons: Slightly less accurate than MediaPipe
  
- **Option 2 (Best Accuracy)**: MediaPipe Pose
  - Pros: Higher accuracy, more pose landmarks, better for complex movements
  - Cons: Requires model download, more complex setup

### Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/situpcounter/
│   │   │   ├── MainActivity.kt
│   │   │   ├── viewmodel/
│   │   │   │   └── SitUpViewModel.kt
│   │   │   ├── camera/
│   │   │   │   ├── CameraManager.kt
│   │   │   │   └── PoseAnalyzer.kt
│   │   │   └── utils/
│   │   │       └── SitUpCounter.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   └── values/
│   │   │       └── strings.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

### Key Features
1. Real-time camera preview
2. Pose detection and tracking
3. Automatic sit-up counting
4. Visual feedback (counter display)
5. Start/Stop/Pause functionality
6. Reset counter

### Counting Logic
- Track shoulder and hip positions
- Detect upward movement (sit-up start)
- Detect return to starting position (sit-up complete)
- Use state machine to prevent double counting

### Implementation Steps
1. ✅ Create project structure and README
2. ✅ Set up Android project configuration
3. ✅ Implement CameraX integration
4. ✅ Integrate ML Kit Pose Detection
5. ✅ Implement sit-up counting algorithm
6. ✅ Create UI components
7. ✅ Add permissions and manifest configuration

---

## Implementation Details

### Core Components

#### 1. MainActivity.kt
- Main entry point of the application
- Handles camera permissions
- Manages UI lifecycle and user interactions
- Integrates CameraX, ML Kit, and ViewModel

#### 2. CameraManager.kt
- Manages CameraX lifecycle
- Configures camera preview and image analysis
- Uses front-facing camera by default
- Handles camera binding/unbinding

#### 3. PoseAnalyzer.kt
- Implements ImageAnalysis.Analyzer interface
- Processes camera frames using ML Kit Pose Detection
- Integrates with SitUpCounter for counting logic
- Provides callbacks for pose updates

#### 4. SitUpCounter.kt
- State machine implementation for counting
- Tracks shoulder and hip positions
- Detects complete sit-up cycles (down → up → down)
- Prevents double counting with state validation

#### 5. PoseOverlayView.kt
- Custom View for visualizing pose landmarks
- Draws key points (shoulders, hips) on camera preview
- Provides visual feedback for pose detection

#### 6. SitUpViewModel.kt
- MVVM architecture pattern
- Manages UI state (count, status, counting state)
- Provides LiveData for reactive UI updates

### Counting Algorithm

The sit-up detection uses a state machine with 5 states:
1. **IDLE**: Initial state, determines starting position
2. **DOWN**: Person is lying down (shoulders above hips)
3. **GOING_UP**: Transition state during upward movement
4. **UP**: Person is sitting up (shoulders at or below hip level)
5. **GOING_DOWN**: Transition state during return movement

A complete sit-up is counted when the cycle completes: DOWN → GOING_UP → UP → GOING_DOWN → DOWN

### Key Parameters
- `SHOULDER_HIP_THRESHOLD`: 0.15 (normalized coordinates)
- `MOVEMENT_THRESHOLD`: 0.05 (minimum movement to trigger state change)

### Dependencies
- CameraX: 1.3.0
- ML Kit Pose Detection: 18.0.0-beta4
- AndroidX Lifecycle: 2.6.2
- Kotlin Coroutines for async operations

---

## Implementation Log

### 2025-12-05 08:11:45
- Initial project plan created
- Technology stack selected: CameraX + ML Kit Pose Detection
- Project structure defined

### 2025-12-05 08:13:27
- ✅ Complete Android project structure created
- ✅ All Kotlin source files implemented:
  - MainActivity.kt with camera permission handling
  - CameraManager.kt for CameraX integration
  - PoseAnalyzer.kt with ML Kit Pose Detection
  - SitUpCounter.kt with state machine logic
  - PoseOverlayView.kt for visual feedback
  - SitUpViewModel.kt for MVVM architecture
- ✅ UI layout files created (activity_main.xml)
- ✅ Configuration files completed:
  - build.gradle (project and app level)
  - AndroidManifest.xml with camera permissions
  - strings.xml with all text resources
  - proguard-rules.pro for release builds
- ✅ All implementation steps completed

## Usage Instructions

### Building the Project
1. Open project in Android Studio
2. Sync Gradle files
3. Connect Android device or start emulator (API 21+)
4. Run the app

### Using the App
1. Grant camera permission when prompted
2. Position yourself in front of the camera (front-facing)
3. Lie down on the floor/mat
4. Press "Start" to begin counting
5. Perform sit-ups - the app will automatically count
6. Use "Pause" to temporarily stop counting
7. Use "Reset" to reset the counter

### Best Practices for Accurate Counting
- Ensure good lighting conditions
- Position camera so full body is visible
- Keep shoulders and hips visible to camera
- Perform sit-ups at moderate speed (not too fast)
- Maintain consistent form

## Future Enhancements
- Add calibration mode for different body types
- Implement exercise history and statistics
- Add sound feedback for each counted sit-up
- Support for multiple exercise types
- Export data functionality
- Dark mode support

### 2025-12-05 08:14:30
- ✅ Fixed custom view reference in layout XML
- ✅ Added gradle.properties for project configuration
- ✅ All code files finalized and tested for syntax
- ✅ Project ready for Android Studio import and build
