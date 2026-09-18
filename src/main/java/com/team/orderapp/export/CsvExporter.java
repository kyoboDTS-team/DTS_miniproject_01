package com.team.orderapp.export;

import com.team.orderapp.product.Product;
import com.team.orderapp.stock.StockAdjustmentHistory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 관리자 CSV 파일 저장 기능
 */
public class CsvExporter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    // ============================================================
    // 상품 CSV 저장
    // ============================================================
    public void Export(List<Product> products, Path filePath) throws IOException {
        PrepareDirectory(filePath);

        try (BufferedWriter writer = OpenWriter(filePath)) {
            WriteHeader(writer,
                    "product_code,category_id,product_name,price,reorder_level,requires_serial");

            for (Product product : products) {
                writer.write(
                        Escape(product.getProductCode()) + "," +
                                Escape(product.getCategoryId()) + "," +
                                Escape(product.getProductName()) + "," +
                                Escape(product.getPrice()) + "," +
                                Escape(product.getReorderLevel()) + "," +
                                Escape(product.getRequiresSerial())
                );
                writer.newLine();
            }
        }
    }


    // ============================================================
    // 재고 현황 CSV 저장
    // ============================================================
    public void ExportStockStatus(List<Product> products, Path filePath) throws IOException {
        PrepareDirectory(filePath);

        try (BufferedWriter writer = OpenWriter(filePath)) {
            WriteHeader(writer,
                    "product_code,product_name,stock_quantity,reorder_level,stock_status,requires_serial");

            for (Product product : products) {
                writer.write(
                        Escape(product.getProductCode()) + "," +
                                Escape(product.getProductName()) + "," +
                                Escape(product.getStockQuantity()) + "," +
                                Escape(product.getReorderLevel()) + "," +
                                Escape(GetStockStatus(product)) + "," +
                                Escape(product.getRequiresSerial())
                );
                writer.newLine();
            }
        }
    }


    // ============================================================
    // 재고 변경 이력 CSV 저장
    // ============================================================
    public void ExportAdjustmentHistory(
            List<StockAdjustmentHistory> histories, Path filePath) throws IOException {

        PrepareDirectory(filePath);

        try (BufferedWriter writer = OpenWriter(filePath)) {
            WriteHeader(writer,
                    "adjusted_at,product_code,product_name,quantity_delta,reason,adjusted_by");

            for (StockAdjustmentHistory history : histories) {
                String adjustedAt = history.getAdjustedAt() == null
                        ? ""
                        : history.getAdjustedAt().format(DATE_TIME_FORMATTER);

                writer.write(
                        Escape(adjustedAt) + "," +
                                Escape(history.getProductCode()) + "," +
                                Escape(history.getProductName()) + "," +
                                Escape(history.getQuantityDelta()) + "," +
                                Escape(history.getReason()) + "," +
                                Escape(history.getAdjustedByEmail())
                );
                writer.newLine();
            }
        }
    }


    // ============================================================
    // 시리얼 현황 CSV 저장
    // ============================================================
    public void ExportSerialStatus(List<SerialCsvRow> rows, Path filePath) throws IOException {
        PrepareDirectory(filePath);

        try (BufferedWriter writer = OpenWriter(filePath)) {
            WriteHeader(writer,
                    "product_code,product_name,serial_number,unit_status,created_at");

            for (SerialCsvRow row : rows) {
                String createdAt = row.getCreatedAt() == null
                        ? ""
                        : row.getCreatedAt().format(DATE_TIME_FORMATTER);

                writer.write(
                        Escape(row.getProductCode()) + "," +
                                Escape(row.getProductName()) + "," +
                                Escape(row.getSerialNumber()) + "," +
                                Escape(row.getUnitStatus()) + "," +
                                Escape(createdAt)
                );
                writer.newLine();
            }
        }
    }


    // ============================================================
    // 재고 상태 판단
    // ============================================================
    private String GetStockStatus(Product product) {
        int stock = product.getStockQuantity();
        int reorderLevel = product.getReorderLevel();

        if (stock == 0) {
            return "품절";
        }

        if (stock <= reorderLevel) {
            return "부족";
        }

        return "정상";
    }


    // ============================================================
    // CSV 저장 폴더 생성
    // ============================================================
    private void PrepareDirectory(Path filePath) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
    }


    // ============================================================
    // UTF-8 CSV Writer 생성
    // ============================================================
    private BufferedWriter OpenWriter(Path filePath) throws IOException {
        return Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
    }


    // ============================================================
    // UTF-8 BOM 및 CSV 헤더 작성
    // ============================================================
    private void WriteHeader(BufferedWriter writer, String header) throws IOException {
        // Excel 한글 깨짐 방지를 위해 UTF-8 BOM 사용
        writer.write("\uFEFF");
        writer.write(header);
        writer.newLine();
    }


    // ============================================================
    // CSV 특수문자 처리
    // ============================================================
    private String Escape(Object value) {
        if (value == null) {
            return "";
        }

        String text = String.valueOf(value).replace("\"", "\"\"");

        if (text.contains(",") || text.contains("\"")
                || text.contains("\n") || text.contains("\r")) {
            return "\"" + text + "\"";
        }

        return text;
    }
}