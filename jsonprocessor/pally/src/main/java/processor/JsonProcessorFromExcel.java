package processor;

import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

/**
 * Created by vaibhavvashishtha.
 */
public class JsonProcessorFromExcel {
    public static void main(String[] args) {
        try {
            String csvFileAddress = "/Users/vaibhavvashishtha/reports_json_csv/pa11y_report.csv"; //csv file address
            String xlsxFileAddress = "/Users/vaibhavvashishtha/reports_json_csv/pa11y_report.xls"; //xlsx file address
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("sheet1");
            String currentLine=null;
            int RowNum=0;
            BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
            while ((currentLine = br.readLine()) != null) {
                String str[] = currentLine.split(",");
                RowNum++;
                XSSFRow currentRow=sheet.createRow(RowNum);
                for(int i=0;i<str.length;i++){
                    currentRow.createCell(i).setCellValue(str[i]);
                }
            }

            FileOutputStream fileOutputStream =  new FileOutputStream(xlsxFileAddress);
            workBook.write(fileOutputStream);
            fileOutputStream.close();
            createPivotTable();

            System.out.println("Done");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void createPivotTable() throws Exception{
        /* Read the input file that contains the data to pivot */
        FileInputStream input_document = new FileInputStream(new File("/Users/vaibhavvashishtha/reports_json_csv/pa11y_report.xls"));
                /* Create a POI XSSFWorkbook Object from the input file */
        XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document);
                /* Read Data to be Pivoted - we have only one worksheet */
        XSSFSheet sheet = my_xlsx_workbook.getSheet("sheet1");
                /* Get the reference for Pivot Data */
        AreaReference a=new AreaReference("$A:$A");
                /* Find out where the Pivot Table needs to be placed */
        XSSFSheet new_sheet = my_xlsx_workbook.createSheet("PivotTable");
        CellReference b=new CellReference("A1");
                /* Create Pivot Table */
        XSSFPivotTable pivotTable = new_sheet.createPivotTable(a,b,sheet);
                /* Add filters */
        pivotTable.addReportFilter(0);
        pivotTable.addRowLabel(1);
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT_NUMS, 1);
                /* Write Pivot Table to File */
        FileOutputStream output_file = new FileOutputStream(new File("/Users/vaibhavvashishtha/reports_json_csv/pa11y_pivot_table.xlsx"));
        my_xlsx_workbook.write(output_file);
        input_document.close();
    }

    public static void createJsonFromPivot() throws Exception{
        /* Read the input file that contains the data to pivot */
        FileInputStream input_document = new FileInputStream(new File("/Users/vaibhavvashishtha/reports_json_csv/pa11y_pivot_table.xls"));
                /* Create a POI XSSFWorkbook Object from the input file */
        XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document);
        List<XSSFPivotTable> pivotTables = my_xlsx_workbook.getPivotTables();
        String pivotJson = "";
        for (XSSFPivotTable pivot : pivotTables) {
            pivotJson = pivot.toString();
            break;
        }
        FileOutputStream output_file = new FileOutputStream(new File("/Users/vaibhavvashishtha/reports_json_csv/pa11y_pivot_table.json"));
        output_file.write(pivotJson.getBytes());
        input_document.close();
        output_file.close();
    }
}
