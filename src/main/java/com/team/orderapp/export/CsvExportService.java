package com.team.orderapp.export;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;
import com.team.orderapp.product.ProductUnit;
import com.team.orderapp.product.ProductUnitDao;
import com.team.orderapp.stock.StockAdjustmentDao;
import com.team.orderapp.stock.StockAdjustmentHistory;

import org.apache.ibatis.session.SqlSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 관리자용 CSV Export Service
 */
public class CsvExportService {

    private static final Path CSV_DIRECTORY = Paths.get("csv");

    private final CsvExporter csvExporter;

    public CsvExportService() {
        this.csvExporter = new CsvExporter();
    }


    // ============================================================
    // 전체 재고 현황 CSV 저장
    // ============================================================
    public Path ExportAllStock(String fileName) throws Exception {
        try (SqlSession session = OpenSession()) {
            ProductDao productDao = session.getMapper(ProductDao.class);

            List<Product> products = productDao.FindAll();
            Path filePath = ResolveFilePath(fileName, "stock_status.csv");

            csvExporter.ExportStockStatus(products, filePath);
            return filePath.toAbsolutePath();
        }
    }


    // ============================================================
    // 재고 부족 상품 CSV 저장
    // ============================================================
    public Path ExportLowStock(String fileName) throws Exception {
        try (SqlSession session = OpenSession()) {
            ProductDao productDao = session.getMapper(ProductDao.class);

            List<Product> products = productDao.FindLowStockProducts();
            Path filePath = ResolveFilePath(fileName, "low_stock.csv");

            csvExporter.ExportStockStatus(products, filePath);
            return filePath.toAbsolutePath();
        }
    }


    // ============================================================
    // 품절 상품 CSV 저장
    // ============================================================
    public Path ExportOutOfStock(String fileName) throws Exception {
        try (SqlSession session = OpenSession()) {
            ProductDao productDao = session.getMapper(ProductDao.class);

            List<Product> products = productDao.FindOutOfStockProducts();
            Path filePath = ResolveFilePath(fileName, "out_of_stock.csv");

            csvExporter.ExportStockStatus(products, filePath);
            return filePath.toAbsolutePath();
        }
    }


    // ============================================================
    // 재고 변경 이력 CSV 저장
    // ============================================================
    public Path ExportAdjustmentHistory(String fileName) throws Exception {
        try (SqlSession session = OpenSession()) {
            StockAdjustmentDao dao = session.getMapper(StockAdjustmentDao.class);

            List<StockAdjustmentHistory> histories = dao.FindAllHistory();
            Path filePath = ResolveFilePath(fileName, "stock_adjustment_history.csv");

            csvExporter.ExportAdjustmentHistory(histories, filePath);
            return filePath.toAbsolutePath();
        }
    }


    // ============================================================
    // 전체 시리얼 현황 CSV 저장
    // ============================================================
    public Path ExportSerialStatus(String fileName) throws Exception {
        try (SqlSession session = OpenSession()) {
            ProductDao productDao = session.getMapper(ProductDao.class);
            ProductUnitDao productUnitDao = session.getMapper(ProductUnitDao.class);

            List<Product> products = productDao.FindAll();
            List<SerialCsvRow> rows = new ArrayList<>();

            for (Product product : products) {
                if (!Boolean.TRUE.equals(product.getRequiresSerial())) {
                    continue;
                }

                List<ProductUnit> units =
                        productUnitDao.FindByProductId(product.getProductId());

                for (ProductUnit unit : units) {
                    SerialCsvRow row = new SerialCsvRow();
                    row.setProductCode(product.getProductCode());
                    row.setProductName(product.getProductName());
                    row.setSerialNumber(unit.getSerialNumber());
                    row.setUnitStatus(unit.getUnitStatus());
                    row.setCreatedAt(unit.getCreatedAt());

                    rows.add(row);
                }
            }

            Path filePath = ResolveFilePath(fileName, "serial_status.csv");

            csvExporter.ExportSerialStatus(rows, filePath);
            return filePath.toAbsolutePath();
        }
    }


    // ============================================================
    // CSV 파일 경로 생성
    // ============================================================
    private Path ResolveFilePath(String fileName, String defaultFileName) {
        String name = fileName == null ? "" : fileName.trim();

        if (name.isBlank()) {
            name = defaultFileName;
        }

        if (!name.toLowerCase().endsWith(".csv")) {
            name += ".csv";
        }

        // 파일명만 허용하여 다른 경로에 저장되는 것을 방지
        name = Paths.get(name).getFileName().toString();

        return CSV_DIRECTORY.resolve(name);
    }


    // ============================================================
    // SqlSession 생성
    // ============================================================
    private SqlSession OpenSession() {
        SqlSession session = DbConnectionFactory.OpenSession();

        if (session == null) {
            throw new IllegalStateException("DB 연결 설정이 초기화되지 않았습니다.");
        }

        return session;
    }
}