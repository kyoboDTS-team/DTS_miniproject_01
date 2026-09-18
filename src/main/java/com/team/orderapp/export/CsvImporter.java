package com.team.orderapp.export;

import com.team.orderapp.product.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 상품 CSV 파일을 읽어서 Product 객체 목록으로 변환합니다.
 *
 * 여기서는 DB INSERT를 하지 않습니다.
 * CSV 파일을 읽고 Product 객체로 변환하는 역할만 담당합니다.
 */
public class CsvImporter {

    private static final List<String> EXPECTED_HEADER =
            Arrays.asList(
                    "product_code",
                    "category_id",
                    "product_name",
                    "price",
                    "reorder_level",
                    "requires_serial"
            );


    public List<Product> Import(
            Path filePath
    ) throws IOException {

        if (!Files.exists(filePath)) {

            throw new IllegalArgumentException(
                    "CSV 파일을 찾을 수 없습니다: "
                            + filePath
            );
        }


        List<Product> products =
                new ArrayList<>();


        try (BufferedReader reader =
                     Files.newBufferedReader(
                             filePath,
                             StandardCharsets.UTF_8
                     )) {

            String header =
                    reader.readLine();


            if (header == null) {

                throw new IllegalArgumentException(
                        "CSV 파일이 비어 있습니다."
                );
            }


            // UTF-8 BOM 제거
            if (header.startsWith("\uFEFF")) {

                header =
                        header.substring(1);
            }


            ValidateHeader(header);


            String line;

            int lineNumber = 1;


            while ((line = reader.readLine())
                    != null) {

                lineNumber++;


                // 빈 줄은 무시
                if (line.isBlank()) {
                    continue;
                }


                List<String> values =
                        ParseCsvLine(line);


                if (values.size() != 6) {

                    throw new IllegalArgumentException(
                            lineNumber
                                    + "행: CSV 컬럼 개수가 올바르지 않습니다."
                    );
                }


                Product product =
                        ParseProduct(
                                values,
                                lineNumber
                        );


                products.add(product);
            }
        }


        return products;
    }


    // ============================================================
    // CSV 헤더 검사
    // ============================================================

    private void ValidateHeader(
            String header
    ) {

        List<String> actualHeader =
                ParseCsvLine(header);


        if (!actualHeader.equals(
                EXPECTED_HEADER
        )) {

            throw new IllegalArgumentException(
                    "CSV 헤더 형식이 올바르지 않습니다.\n"
                            + "필요한 헤더: "
                            + String.join(
                            ",",
                            EXPECTED_HEADER
                    )
            );
        }
    }


    // ============================================================
    // CSV 한 행 → Product 변환
    // ============================================================

    private Product ParseProduct(
            List<String> values,
            int lineNumber
    ) {

        try {

            Product product =
                    new Product();


            product.setProductCode(
                    values.get(0).trim()
            );


            product.setCategoryId(
                    Long.parseLong(
                            values.get(1).trim()
                    )
            );


            product.setProductName(
                    values.get(2).trim()
            );


            product.setPrice(
                    new BigDecimal(
                            values.get(3).trim()
                    )
            );


            product.setReorderLevel(
                    Integer.parseInt(
                            values.get(4).trim()
                    )
            );


            product.setRequiresSerial(
                    ParseBoolean(
                            values.get(5),
                            lineNumber
                    )
            );


            // CSV 불러오기는 신규 상품 등록용
            product.setStockQuantity(0);
            product.setSaleStatus("SELLING");


            return product;


        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    lineNumber
                            + "행: 숫자 형식이 올바르지 않습니다."
            );
        }
    }


    // ============================================================
    // Boolean 변환
    // ============================================================

    private Boolean ParseBoolean(
            String value,
            int lineNumber
    ) {

        String text =
                value.trim()
                        .toUpperCase();


        if (text.equals("TRUE") ||
                text.equals("Y")) {

            return true;
        }


        if (text.equals("FALSE") ||
                text.equals("N")) {

            return false;
        }


        throw new IllegalArgumentException(
                lineNumber
                        + "행: requires_serial은 "
                        + "true/false 또는 Y/N만 가능합니다."
        );
    }


    // ============================================================
    // CSV 한 줄 파싱
    //
    // 예:
    // P001,1,"볼펜, 검정",1000,5,false
    // ============================================================

    private List<String> ParseCsvLine(
            String line
    ) {

        List<String> values =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        boolean inQuotes = false;


        for (int i = 0;
             i < line.length();
             i++) {

            char ch =
                    line.charAt(i);


            if (ch == '"') {

                // "" 는 실제 " 문자
                if (inQuotes &&
                        i + 1 < line.length() &&
                        line.charAt(i + 1) == '"') {

                    current.append('"');
                    i++;

                } else {

                    inQuotes =
                            !inQuotes;
                }

            } else if (ch == ',' &&
                    !inQuotes) {

                values.add(
                        current
                                .toString()
                                .trim()
                );

                current.setLength(0);

            } else {

                current.append(ch);
            }
        }


        if (inQuotes) {

            throw new IllegalArgumentException(
                    "CSV 큰따옴표 형식이 올바르지 않습니다."
            );
        }


        values.add(
                current
                        .toString()
                        .trim()
        );


        return values;
    }
}