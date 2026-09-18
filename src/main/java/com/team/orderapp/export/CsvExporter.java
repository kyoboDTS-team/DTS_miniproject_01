package com.team.orderapp.export;

import com.team.orderapp.product.Product;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 상품 정보를 UTF-8 CSV 파일로 저장합니다.
 */
public class CsvExporter {

    private static final String HEADER =
            "product_code,category_id,product_name,price,reorder_level,requires_serial";


    public void Export(
            List<Product> products,
            Path filePath
    ) throws IOException {

        // csv 폴더가 없으면 자동 생성
        if (filePath.getParent() != null) {
            Files.createDirectories(
                    filePath.getParent()
            );
        }


        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             filePath,
                             StandardCharsets.UTF_8
                     )) {

            // Excel에서 한글 UTF-8 인식이 잘 되도록 BOM 추가
            writer.write("\uFEFF");

            writer.write(HEADER);
            writer.newLine();


            for (Product product : products) {

                String line =
                        Escape(product.getProductCode())
                                + ","
                                + Escape(product.getCategoryId())
                                + ","
                                + Escape(product.getProductName())
                                + ","
                                + Escape(product.getPrice())
                                + ","
                                + Escape(product.getReorderLevel())
                                + ","
                                + Escape(product.getRequiresSerial());


                writer.write(line);
                writer.newLine();
            }
        }
    }


    /**
     * 쉼표나 큰따옴표가 포함된 값을
     * CSV 형식에 맞게 안전하게 변환합니다.
     */
    private String Escape(Object value) {

        if (value == null) {
            return "";
        }


        String text =
                String.valueOf(value);


        // 큰따옴표는 "" 형태로 변환
        text = text.replace(
                "\"",
                "\"\""
        );


        // 쉼표 / 큰따옴표 / 줄바꿈이 있으면
        // 전체 값을 큰따옴표로 감쌈
        if (text.contains(",") ||
                text.contains("\"") ||
                text.contains("\n") ||
                text.contains("\r")) {

            return "\""
                    + text
                    + "\"";
        }


        return text;
    }
}