# EmpVeh_data_visualization

This project is a Java-based data visualization tool for analyzing emotion recognition data from vehicle simulations. It allows users to view frame-by-frame video data alongside emotion confidence scores and validate the ground truth.

## Prerequisites

*   **Java Development Kit (JDK) 21** or higher.
*   **Maven** (for building the project).
*   **Java IDE**.

## Installation & Setup

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/MonykTobias/EmpVeh_data_visualization 
    cd EmpVeh_data_visualization
    ```

2.  **Build the Project**
    Use Maven to clean and install dependencies:
    ```bash
    mvn clean install
    ```

3.  **Run the Application**
    You can run the application directly from your IDE by executing the `main` method in `src/main/java/at/fhtw/Main.java`.

    Or via Maven:
    ```bash
    mvn exec:java
    ```

## Usage Guide

1.  **Launch the App**: Upon starting, you will see a configuration screen.
2.  **Select Data**:
    *   **Image Folder**: Browse and select the directory containing the simulation frame images (e.g., `frame_000000.jpg`).
    *   **Data CSV**: Browse and select the CSV file containing the emotion data export (e.g., `2025-xx-xx.csv`).
3.  **Load Data**: Click "Load Data". The application will validate the paths and load the visualization view.
    *   *Note*: If a `validation.csv` does not exist in the CSV's directory, a new one will be created automatically.
4.  **Visualization Interface**:
    *   **Image Panel**: Shows the current video frame.
    *   **Info Panel**: Displays emotion data (Neutral, Happy, Surprise, Anger) and validation status.
    *   **Plot Panel**: Shows a timeline of emotion confidence scores. Click on the plot to jump to a specific frame.
    *   **Control Panel**:
        *   **Navigation**: Use `<` and `>` buttons or enter an ID to jump to a frame.
        *   **Playback**: Use "Play/Pause" to animate through frames. Adjust speed with the "Speed" field.
        *   **Select Plot**: Switch between "MultiLine Plot" and "Stacked Plot".
        *   **Save Validation**: Saves your validation work to `validation.csv`.

## Validation Workflow

1.  Navigate to a frame.
2.  Observe the image and the predicted emotion.
3.  **Validate**:
    *   Select the **Real Emotion** from the dropdown.
    *   Optionally add a **Comment**.
    *   Click **Validate Frame**. The status dot will turn **Green**.
4.  **Batch Validation**:
    *   To validate a range of frames with the same emotion (e.g., a continuous smile), enter the end frame ID in the **To ID** field and click **Validate Frame(s)**.
5.  **Save**: Click **Save Validation** in the control panel to persist your changes to the CSV file.

## Project Structure

*   `src/main/java/at/fhtw/model`: Data models for Input and Validation.
*   `src/main/java/at/fhtw/view`: UI components (DetailView, Plots, Panels).
*   `src/main/java/at/fhtw/controller`: Logic for handling user interactions and data loading.
