package at.fhtw;

import at.fhtw.model.InputTable;
import at.fhtw.model.helpers.CsvConverter;
import at.fhtw.view.DetailView.DetailView;
import at.fhtw.view.MainFrame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String folderPath = "data/in/Dataset1";
        String csvFilePath = "data/in/Dataset1/2025-05-14_12-41-04.csv";
        try {
            /* READ DATA FROM FILE
            * TODO: read from userinput (folder)
            * */
            String csvContent = Files.readString(Paths.get(csvFilePath));
            CsvConverter<InputTable> csvConverter = new CsvConverter<>(InputTable.class);

            InputTable table = csvConverter.deserialize(csvContent);

            System.out.println("Loaded rows: " + table.getInputTable().size());
            DetailView detailView = new DetailView(table);

            MainFrame frame = new MainFrame(detailView);

        } catch (
                IOException e) {
            System.out.println(e.getMessage());
        }
    }
}