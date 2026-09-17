package com.team.orderapp.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

    private Long categoryId;
    private Long parentCategoryId;
    private String categoryCode;
    private String categoryName;

    public Category() {
    }
}