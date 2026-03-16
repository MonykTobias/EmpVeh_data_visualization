package at.fhtw.controller;

import at.fhtw.model.InputData;
import at.fhtw.model.InputDataTable;
import at.fhtw.model.Validation;
import at.fhtw.model.ValidationTable;
import at.fhtw.model.helpers.CsvConverter;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.View;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {
    private JFrame mainFrame;
    private View currentView;
    private JPanel contentPanel;
    private JTextField folderPathField;
    private JTextField csvPathField;
    private JButton loadDataButton;
    private JButton saveButton;

    public Controller() {
        // Constructor can be used for initialization if needed
    }

    public void start() {
        mainFrame = new JFrame("Data Visualization App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        //mainFrame.setSize(1200, 800);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen with borders

        // Create the top navigation/selection bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        folderPathField = new JTextField(30);
        csvPathField = new JTextField(30);
        JButton browseFolderButton = new JButton("Browse Folder");
        JButton browseCsvButton = new JButton("Browse CSV");
        loadDataButton = new JButton("Load Data");
        saveButton = new JButton("Save Validation");

        topPanel.add(new JLabel("Image Folder:"));
        topPanel.add(folderPathField);
        topPanel.add(browseFolderButton);
        topPanel.add(new JLabel("Data CSV:"));
        topPanel.add(csvPathField);
        topPanel.add(browseCsvButton);
        topPanel.add(loadDataButton);
        topPanel.add(saveButton);

        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Create the central panel that will hold the dynamic views
        contentPanel = new JPanel(new BorderLayout());
        mainFrame.add(contentPanel, BorderLayout.CENTER);

        // Add listeners for the browse buttons
        browseFolderButton.addActionListener(e -> selectFolder());
        browseCsvButton.addActionListener(e -> selectCsv());

        // Add listener for the main load button
        loadDataButton.addActionListener(e -> {
            String folderPath = folderPathField.getText();
            String csvPath = csvPathField.getText();
            onDataSelected(folderPath, csvPath);
        });

        saveButton.addActionListener(e -> saveValidation());

        // Add listeners to text fields to validate paths on change
        folderPathField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validatePaths());
        csvPathField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validatePaths());

        // Initially, the buttons are disabled
        loadDataButton.setEnabled(false);
        saveButton.setEnabled(false);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            folderPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            csvPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void validatePaths() {
        String folderPath = folderPathField.getText();
        String csvPath = csvPathField.getText();

        File folder = new File(folderPath);
        File csv = new File(csvPath);

        boolean isFolderValid = folder.exists() && folder.isDirectory();
        boolean isCsvValid = csv.exists() && csv.isFile() && csvPath.toLowerCase().endsWith(".csv");

        loadDataButton.setEnabled(isFolderValid && isCsvValid);
        saveButton.setEnabled(currentView instanceof DetailView);
    }

    private void saveValidation() {
        if (currentView instanceof DetailView) {
            DetailView detailView = (DetailView) currentView;
            String csvPath = csvPathField.getText();
            String validationCsvPath = csvPath.substring(0, csvPath.lastIndexOf(File.separator)) + File.separator + "validation.csv";
            try {
                CsvConverter<ValidationTable> converter = new CsvConverter<>(ValidationTable.class);
                String content = converter.serialize(detailView.getValidationTable());
                Files.writeString(Paths.get(validationCsvPath), content);
                JOptionPane.showMessageDialog(mainFrame, "Validation saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error saving validation: " + ex.getMessage());
            }
        }
    }

    // when successfully selected a valid path replace current view with DetailView
    private void onDataSelected(String folderPath, String csvPath) {
        System.out.println("Data selected:");
        System.out.println("Folder: " + folderPath);
        System.out.println("CSV: " + csvPath);
        // get Data
        try{
            // get Input Table
            String csvContent = Files.readString(Paths.get(csvPath));
            CsvConverter<InputDataTable> csvConverter = new CsvConverter<>(InputDataTable.class);
            InputDataTable table = csvConverter.deserialize(csvContent);
            System.out.println("Input Table: Loaded rows: " + table.getInputTable().size());
            
            // get Validation Table (Same folder as Input Table)
            String validationCsvPath = csvPath.substring(0, csvPath.lastIndexOf(File.separator)) + File.separator + "validation.csv";
            File validationFile = new File(validationCsvPath);
            ValidationTable validationTable;
            CsvConverter<ValidationTable> validationTableCsvConverter = new CsvConverter<>(ValidationTable.class);
            
            if (validationFile.exists()) {
                csvContent = Files.readString(Paths.get(validationCsvPath));
                validationTable = validationTableCsvConverter.deserialize(csvContent);
                System.out.println("Validation Table: Loaded rows: " + validationTable.getValidationTable().size());
            } else {
                System.out.println("Validation Table not found. Creating a new one.");
                validationTable = new ValidationTable();
                
                // Initialize validation table with default values for all frames
                for (InputData inputData : table.getInputTable()) {
                    Validation validation = new Validation(inputData);
                    validationTable.getValidationTable().put(inputData.getId(), validation);
                }
                
                // Save the new initialized table to the file
                String serializedTable = validationTableCsvConverter.serialize(validationTable);
                Files.writeString(Paths.get(validationCsvPath), serializedTable);
                System.out.println("Created new validation.csv at: " + validationCsvPath);
            }
            
            if(!table.getInputTable().isEmpty()){
                replaceView(new DetailView(table, validationTable, folderPath));
                saveButton.setEnabled(true);
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(mainFrame, "Error when loading this Path.");
            e.printStackTrace();
            return;
        }
        // Output if successful
        JOptionPane.showMessageDialog(mainFrame, "Data loaded successfully!\nFolder: " + folderPath + "\nCSV: " + csvPath);
    }

    public void replaceView(View newView) {
        contentPanel.removeAll(); // Clear the previous view
        JComponent viewComponent = newView.load(); // Load the new view, which returns its main component
        contentPanel.add(viewComponent, BorderLayout.CENTER); // Add the new component
        this.currentView = newView;
        contentPanel.revalidate(); // Re-layout the panel
        contentPanel.repaint(); // Repaint the panel
    }

    // This is a helper functional interface to simplify DocumentListener implementation
    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }
        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }
        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update(e);
        }
    }
}
