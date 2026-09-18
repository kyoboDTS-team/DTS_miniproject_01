package com.team.orderapp.export;

import com.team.orderapp.common.DbConnectionFactory;
import com.team.orderapp.product.Category;
import com.team.orderapp.product.CategoryDao;
import com.team.orderapp.product.Product;
import com.team.orderapp.product.ProductDao;

import org.apache.ibatis.session.SqlSession;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 상품 CSV 저장 / 불러오기 Service
 */
public class ProductCsvService {

    // 프로젝트 루트의 csv 폴더 사용
    private static final Path CSV_DIRECTORY =
            Paths.get("csv");

    private final CsvExporter csvExporter;
    private final CsvImporter csvImporter;


    public ProductCsvService() {

        this.csvExporter =
                new CsvExporter();

        this.csvImporter =
                new CsvImporter();
    }


    // ============================================================
    // 상품 CSV 저장
    // ============================================================

    public Path ExportProducts(
            String fileName
    ) throws Exception {

        Path filePath =
                ResolveFilePath(fileName);


        try (SqlSession session = OpenSession()) {

            ProductDao productDao =
                    session.getMapper(
                            ProductDao.class
                    );


            // 현재 등록된 상품 전체 조회
            List<Product> products =
                    productDao.FindAll();


            // UTF-8 CSV 저장
            csvExporter.Export(
                    products,
                    filePath
            );


            return filePath
                    .toAbsolutePath();
        }
    }


    // ============================================================
    // 상품 CSV 불러오기
    //
    // 전 행 검증 후
    // 모두 정상일 때만 일괄 INSERT
    // ============================================================

    public int ImportProducts(
            String fileName
    ) throws Exception {

        Path filePath =
                ResolveFilePath(fileName);


        // 1. CSV 전체 파일 먼저 읽기
        List<Product> products =
                csvImporter.Import(
                        filePath
                );


        if (products.isEmpty()) {

            throw new IllegalArgumentException(
                    "등록할 상품이 없습니다."
            );
        }


        try (SqlSession session = OpenSession()) {

            ProductDao productDao =
                    session.getMapper(
                            ProductDao.class
                    );

            CategoryDao categoryDao =
                    session.getMapper(
                            CategoryDao.class
                    );


            try {

                // 2. DB에 이미 존재하는 상품 코드 가져오기
                List<Product> existingProducts =
                        productDao.FindAll();


                Set<String> existingCodes =
                        new HashSet<>();


                for (Product product :
                        existingProducts) {

                    existingCodes.add(
                            product.getProductCode()
                                    .trim()
                    );
                }


                // CSV 내부 중복 확인용
                Set<String> csvCodes =
                        new HashSet<>();


                // =================================================
                // 3. 모든 행 먼저 검증
                // =================================================

                for (int i = 0;
                     i < products.size();
                     i++) {

                    Product product =
                            products.get(i);

                    // CSV 실제 행번호
                    // 헤더가 1행이므로 데이터는 2행부터
                    int lineNumber =
                            i + 2;


                    ValidateProduct(
                            product,
                            lineNumber
                    );


                    String productCode =
                            product.getProductCode()
                                    .trim();


                    // CSV 파일 내부 중복 검사
                    if (!csvCodes.add(
                            productCode
                    )) {

                        throw new IllegalArgumentException(
                                lineNumber
                                        + "행: CSV 파일 안에서 "
                                        + "상품 코드가 중복되었습니다. "
                                        + productCode
                        );
                    }


                    // DB에 이미 존재하는 상품 검사
                    if (existingCodes.contains(
                            productCode
                    )) {

                        throw new IllegalArgumentException(
                                lineNumber
                                        + "행: 이미 등록된 상품 코드입니다. "
                                        + productCode
                        );
                    }


                    // 카테고리 존재 확인
                    Optional<Category> category =
                            categoryDao.FindById(
                                    product.getCategoryId()
                            );


                    if (category.isEmpty()) {

                        throw new IllegalArgumentException(
                                lineNumber
                                        + "행: 존재하지 않는 카테고리 ID입니다. "
                                        + product.getCategoryId()
                        );
                    }


                    // 상품은 하위 카테고리에만 등록 가능
                    if (category.get()
                            .getParentCategoryId()
                            == null) {

                        throw new IllegalArgumentException(
                                lineNumber
                                        + "행: 상위 카테고리에는 "
                                        + "상품을 등록할 수 없습니다."
                        );
                    }
                }


                // =================================================
                // 4. 모든 검증 성공 후 일괄 INSERT
                // =================================================

                for (Product product :
                        products) {

                    boolean result =
                            productDao.Insert(
                                    product
                            );


                    if (!result) {

                        throw new IllegalStateException(
                                "CSV 상품 등록 중 "
                                        + "DB 저장에 실패했습니다."
                        );
                    }
                }


                // 모든 상품이 성공해야 한 번만 commit
                session.commit();


                return products.size();


            } catch (Exception e) {

                // 한 건이라도 오류가 나면
                // 전체 상품 등록 취소
                session.rollback();

                throw e;
            }
        }
    }


    // ============================================================
    // 상품 입력값 검증
    // ============================================================

    private void ValidateProduct(
            Product product,
            int lineNumber
    ) {

        if (product.getProductCode() == null ||
                product.getProductCode()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 상품 코드가 없습니다."
            );
        }


        if (product.getProductName() == null ||
                product.getProductName()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 상품명이 없습니다."
            );
        }


        if (product.getCategoryId() == null ||
                product.getCategoryId() <= 0) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 올바른 카테고리 ID가 아닙니다."
            );
        }


        if (product.getPrice() == null ||
                product.getPrice()
                        .compareTo(
                                BigDecimal.ZERO
                        ) < 0) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 가격은 0원 이상이어야 합니다."
            );
        }


        if (product.getReorderLevel()
                == null ||
                product.getReorderLevel()
                        < 0) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 안전재고는 0 이상이어야 합니다."
            );
        }


        if (product.getRequiresSerial()
                == null) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 시리얼 관리 여부가 없습니다."
            );
        }


        // 신규 상품 기본값
        product.setProductCode(
                product.getProductCode()
                        .trim()
        );

        product.setProductName(
                product.getProductName()
                        .trim()
        );

        product.setStockQuantity(0);
        product.setSaleStatus("SELLING");
    }


    // ============================================================
    // CSV 파일 경로
    //
    // products 입력
    // → csv/products.csv
    // ============================================================

    private Path ResolveFilePath(
            String fileName
    ) {

        String name =
                fileName == null
                        ? ""
                        : fileName.trim();


        if (name.isBlank()) {

            name =
                    "products.csv";
        }


        // 확장자가 없으면 자동 추가
        if (!name.toLowerCase()
                .endsWith(".csv")) {

            name =
                    name + ".csv";
        }


        // 폴더 경로 입력 대신 파일명만 사용
        // 실수로 프로젝트 다른 파일을 건드리는 것을 방지
        name =
                Paths.get(name)
                        .getFileName()
                        .toString();


        return CSV_DIRECTORY
                .resolve(name);
    }


    // ============================================================
    // DB Helper
    // ============================================================

    private SqlSession OpenSession() {

        SqlSession session =
                DbConnectionFactory
                        .OpenSession();


        if (session == null) {

            throw new IllegalStateException(
                    "DB 연결 설정이 초기화되지 않았습니다."
            );
        }


        return session;
    }
}