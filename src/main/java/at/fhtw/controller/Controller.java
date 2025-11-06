package at.fhtw.controller;

import at.fhtw.model.InputTable;
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

    public Controller() {
        // Constructor can be used for initialization if needed
    }

    public void start() {
        mainFrame = new JFrame("Data Visualization App");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(1200, 800);

        // Create the top navigation/selection bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        folderPathField = new JTextField(30);
        csvPathField = new JTextField(30);
        JButton browseFolderButton = new JButton("Browse Folder");
        JButton browseCsvButton = new JButton("Browse CSV");
        loadDataButton = new JButton("Load Data");

        topPanel.add(new JLabel("Image Folder:"));
        topPanel.add(folderPathField);
        topPanel.add(browseFolderButton);
        topPanel.add(new JLabel("Data CSV:"));
        topPanel.add(csvPathField);
        topPanel.add(browseCsvButton);
        topPanel.add(loadDataButton);

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

        // Add listeners to text fields to validate paths on change
        folderPathField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validatePaths());
        csvPathField.getDocument().addDocumentListener((SimpleDocumentListener) e -> validatePaths());

        // Initially, the button is disabled
        loadDataButton.setEnabled(false);

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
    }

    // when successfully selected a valid path replace current view with DetailView
    private void onDataSelected(String folderPath, String csvPath) {
        System.out.println("Data selected:");
        System.out.println("Folder: " + folderPath);
        System.out.println("CSV: " + csvPath);
        // get Data
        try{
            String csvContent = Files.readString(Paths.get(csvPath));
            CsvConverter<InputTable> csvConverter = new CsvConverter<>(InputTable.class);
            InputTable table = csvConverter.deserialize(csvContent);
            System.out.println("Loaded rows: " + table.getInputTable().size());

            if(!table.getInputTable().isEmpty()){
                replaceView(new DetailView(table, folderPath));
            }
        }catch(IOException e){
            JOptionPane.showMessageDialog(mainFrame, "Error when loading this Path.");
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
