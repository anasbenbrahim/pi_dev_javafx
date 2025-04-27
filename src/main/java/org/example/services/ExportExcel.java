package org.example.services;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.control.TableView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class ExportExcel {


    public class ExcelExporter {

        public static <T> void exportToExcel(TableView<T> tableView, String fileName) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                headerRow.createCell(i).setCellValue(tableView.getColumns().get(i).getText());
            }

            // Create data rows
            for (int rowIdx = 0; rowIdx < tableView.getItems().size(); rowIdx++) {
                Row row = sheet.createRow(rowIdx + 1);
                for (int colIdx = 0; colIdx < tableView.getColumns().size(); colIdx++) {
                    Object cellValue = tableView.getColumns().get(colIdx).getCellData(rowIdx);
                    if (cellValue != null) {
                        row.createCell(colIdx).setCellValue(cellValue.toString());
                    } else {
                        row.createCell(colIdx).setCellValue("");
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < tableView.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Save the file
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
