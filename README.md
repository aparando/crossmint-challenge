# Crossmint Megaverse Challenge

A Kotlin solution for the Crossmint Megaverse Challenge, demonstrating clean architecture, automated pattern generation, and robust API integration.

## 🎯 Challenge Overview

The challenge involves creating a "megaverse" in a 2D grid using the Crossmint API. It consists of two phases:

- **Phase 1**: Create an X-pattern of POLYanets on an 11x11 grid
- **Phase 2**: Create a larger megaverse (100+ entities) forming a specific shape using POLYanets, SOLoons, and ComETHs

## 🏗️ Architecture

The solution follows clean architecture principles with clear separation of concerns:

```
src/main/kotlin/com/crossmint/megaverse/
├── domain/           # Core domain models
│   ├── Position.kt           # 2D coordinate representation
│   └── AstralObject.kt       # Astral object types and implementations
├── pattern/          # Pattern generation logic
│   ├── PatternGenerator.kt           # Base pattern generator interface
│   ├── XPatternGenerator.kt          # Phase 1 X-pattern implementation
│   └── GoalMapDrivenPatternGenerator.kt # Phase 2 dynamic pattern generator
├── api/              # API client layer
│   └── CrossmintApiClient.kt # REST API client with retry logic
├── service/          # Business logic layer
│   └── MegaverseService.kt   # Orchestrates API calls and object creation
├── util/             # Utility classes
│   └── PatternVisualizer.kt  # Terminal-based pattern visualization
├── MegaverseApplication.kt           # Phase 1 main application
└── DynamicPhase2Application.kt       # Phase 2 main application
```

## 🚀 Key Features

### Phase 1: X-Pattern Generation
- **Automated Creation**: Creates POLYanets in an X-pattern on an 11x11 grid
- **Pattern Validation**: Visualizes the pattern before execution
- **Dry Run Mode**: Test the pattern without making API calls

### Phase 2: Dynamic Megaverse Creation
- **Goal-Map Driven**: Automatically reads the target pattern from the API
- **No Hardcoding**: Adapts to any changes in the API automatically
- **Scalable**: Handles 100+ entities with proper rate limiting
- **Resilient**: Includes retry logic and error handling

## 🛠️ Prerequisites

- Java 11 or higher
- Gradle 7.0 or higher

## 📦 Dependencies

- **Kotlin**: Modern Kotlin with coroutines support
- **OkHttp**: HTTP client for API communication
- **Jackson**: JSON parsing and serialization
- **SLF4J**: Logging framework
- **JUnit 5**: Testing framework

## 🚀 Quick Start

### 1. Build the Project
```bash
./gradlew build
```

### 2. Run Phase 1 (X-Pattern)
```bash
# Execute Phase 1
java -cp build/classes/kotlin/main:build/libs/crossmint-interview-1.0-SNAPSHOT.jar com.crossmint.megaverse.MegaverseApplicationKt

# Dry run mode (no API calls)
java -cp build/classes/kotlin/main:build/libs/crossmint-interview-1.0-SNAPSHOT.jar com.crossmint.megaverse.MegaverseApplicationKt --dry-run
```

### 3. Run Phase 2 (Dynamic Megaverse)
```bash
# Execute Phase 2
java -cp build/classes/kotlin/main:build/libs/crossmint-interview-1.0-SNAPSHOT.jar com.crossmint.megaverse.DynamicPhase2ApplicationKt

# Dry run mode (no API calls)
java -cp build/classes/kotlin/main:build/libs/crossmint-interview-1.0-SNAPSHOT.jar com.crossmint.megaverse.DynamicPhase2ApplicationKt --dry-run
```

## 🔧 Configuration

Update the `candidateId` in the main functions of both applications:

```kotlin
val candidateId = "your-candidate-id-here"
```

## 📊 API Endpoints

The solution uses the following Crossmint API endpoints:

- `POST /api/polyanets` - Create POLYanets
- `POST /api/soloons` - Create SOLoons with colors
- `POST /api/comeths` - Create ComETHs with directions
- `DELETE /api/polyanets` - Delete POLYanets
- `DELETE /api/soloons` - Delete SOLoons
- `DELETE /api/comeths` - Delete ComETHs
- `GET /api/map/{candidateId}/goal` - Retrieve goal map
