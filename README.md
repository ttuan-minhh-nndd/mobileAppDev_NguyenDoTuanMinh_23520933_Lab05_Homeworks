# Mobile Development - Lab 5: Asynchronous Tasks and Android Services

This repository contains the complete implementation for **Lab 5** of the Mobile Development course at the University of Information Technology (UIT), under the instruction of **Tran Vinh Khiem**. The project focuses on concurrent programming architectures in Android, showcasing the execution of asynchronous background computation, long-running tasks, and multi-layered services utilizing the **Java** programming language.


## 📁 Repository Structure

```text
lab05/
├── ServiceDemoProject/      # In-Class Exercise: Background, Foreground, Bound Services, & AsyncTask
└── FitnessTrackerApp/       # Core Homework: Gamified Fitness Tracker App with WearOS Sync & Gemini AI

```


## 📱 Service Demo Project (In-Class Exercise)

### 🌟 Overview & User Journey

This application acts as an educational dashboard designed to demonstrate thread orchestration, local system logging, and client-server component interactions within an Android environment.

1. **The Core Dashboard:** When the application boots up, the user is presented with a clean user interface containing three distinct control actions.
2. **Triggering Background Work:** Tapping **"Start Background Task"** boots up an un-nested daemon thread that tracks counter points locally, writing progression updates quietly straight into the system log files (`Logcat`).
3. **Triggering Persistent Systems:** Tapping **"Start Foreground Service"** immediately promotes the tracking logic to an active operating system layer, displaying a permanent, visible system notification tray keeping the counter execution safe from system garbage-collection termination.
4. **Binding and Interacting:** Tapping **"Bind to Service"** hooks up a secure communications bridge between the foreground activity window and the core background computation loops, allowing real-time retrieval and updating of processing variables onto the main window text view.

### ⚙️ Technical Blueprint

* **Pillars of Concurrency:** Utilizes an asynchronous `AsyncTask` pipeline alongside modern thread runner implementations to demonstrate standard background lifecycle transitions safely off the primary UI thread.
* **Component Spectrum:** Features robust implementation configurations for a standard unbounded `BackgroundService`, a persistent `ForegroundService` tied to a explicit notification channel (API 26+ compatible), and a context-bound `BoundCounterService` exposing standard binder mechanisms.

---

## 🏃‍♂️ Fitness Tracker App (Core Homework)

### 🌟 Overview & User Journey

The **Fitness Tracker App** is an immersive, context-aware mobile health companion wrapped completely in a gamified **"Quest Theme"**.

1. **The Adventure Dashboard:** The user opens the app to an active, dark-themed dashboard showing a dynamic circular step completion progress bar. Real-time achievement badges and quest items animate directly onto the viewport as milestones are crossed (such as reaching 10,000 daily steps).
2. **Always-On Step Tracking:** Whether the user is actively sorting their workout menu or has the device tucked safely into their pocket, a underlying logger tracks physical steps using simulated generation modules, storing logs immediately inside a persistent structural repository.
3. **Live Status Bar Broadcasts:** Swiping down from the Android top header displays a live foreground notification card showing active steps alongside an array of rotating, high-energy motivational quotes.
4. **Smart Wrist Synchronization:** As the user walks, physical metrics are continuously mirrored directly onto a paired **WearOS Smartwatch watch face**, animating a compact circular progress ring on the wrist.
5. **AI Fitness Co-Pilot:** With a quick tap on the advice board, the accumulated steps, daily calories, and quest point variables are packaged and routed straight to the cloud via the integrated **Gemini API**. Instantly, the AI processes the data streams to output customized workout programs (e.g., *"Try a 20-min jog to boost your stamina"*) paired with custom tailored encouraging commentary based on performance milestones.

### ⚙️ Architecture & Technical Specifications

The system is constructed with strict structural modularity, cleanly allocating execution workloads onto distinct native layers:

#### 1. Data Persistence & Business Layer (Room Database)

* **`StepEntity.java`**: The database schema model tracking entry parameters including primary keys, step count snapshots, and chronological timestamps.
* **`StepDao.java`**: The structural Data Access Object mapping persistent transaction parameters like bulk logging entries and metric aggregation lookups.
* **`AppDatabase.java`**: The baseline abstraction module orchestrating thread-safe Room database persistence across localized database instances.

#### 2. Service Management Layer (Concurrent Engines)

* **`StepLoggerService.java`**: A standard background service running simulated step acquisition timers and orchestrating transactional database writes safely away from primary UI execution paths.
* **`LiveTrackingService.java`**: A foreground service configuring persistent hardware execution contexts, handling real-time step streams, and constructing system notification layouts.
* **`FitnessDataEngine.java`**: An IPC-bound service that allows components to connect via binding methods, exposing clean interfaces to query real-time caloric outputs and total recorded step intervals.

#### 3. Algorithmic Processing (Asynchronous Handlers)

* **`MetricCalculatorTask.java`**: An academic computation template subclassing a static `AsyncTask` architecture to analyze incoming aggregate counts on separated backgrounds paths using specific physiological scaling equations:

$$\text{Calories Burned} = \text{Steps} \times 0.04$$


$$\text{Quest Points Allocated} = \frac{\text{Steps}}{100}$$



#### 4. WearOS External Communication

* **`WearSyncManager.java`**: Handles remote payload packaging utilizing standard Android Wearable support libraries (`Wearable.getDataClient()`), serializing live performance states directly to a paired watch face module.

#### 5. Intelligent AI Synthesis (Gemini Integration)

* **`GeminiAiClient.java`**: Manages secure asynchronous HTTPS network interactions targeting the Gemini endpoint, securely submitting structured text inputs and deserializing the response into tailored workout prompts and custom reward text statements.

---

## 🛠️ Global Prerequisites & Compilation

To launch and build either module inside Android Studio:

1. Ensure your local environment parameters are aligned with standard Android SDK targets and Gradle configurations specified within the workspace build configurations.
2. In order for the advanced fitness engine to synthesize exercise suggestions, add a valid API authentication key within the app configuration variables to grant `GeminiAiClient` access to remote server communication.
3. Open either `ServiceDemoProject` or `FitnessTrackerApp` subfolders directly as individual root projects in Android Studio to populate proper build tree modules and auto-generate requisite lifecycle elements.
