package com.team.orderapp.product;

import com.team.orderapp.common.DbConnectionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 관리 Service
 */
public class CategoryService {


    // ============================================================
    // 전체 조회
    // ============================================================

    public List<Category> FindAll() {

        try (SqlSession session = OpenSession()) {

            CategoryDao dao =
                    session.getMapper(CategoryDao.class);

            return dao.FindAll();
        }
    }


    // ============================================================
    // 카테고리 등록
    // ============================================================

    public boolean RegisterCategory(Category category) {

        ValidateCategory(category);

        try (SqlSession session = OpenSession()) {

            CategoryDao dao =
                    session.getMapper(CategoryDao.class);

            try {

                // 코드 중복 확인
                if (dao.ExistsByCode(
                        category.getCategoryCode()
                )) {
                    throw new IllegalArgumentException(
                            "이미 사용 중인 카테고리 코드입니다."
                    );
                }

                // 이름 중복 확인
                if (dao.ExistsByName(
                        category.getCategoryName()
                )) {
                    throw new IllegalArgumentException(
                            "이미 사용 중인 카테고리 이름입니다."
                    );
                }


                // 하위 카테고리라면 상위 카테고리 확인
                ValidateParentCategory(
                        dao,
                        category.getParentCategoryId(),
                        null
                );


                boolean result =
                        dao.Insert(category);

                if (!result) {
                    session.rollback();
                    return false;
                }

                session.commit();
                return true;

            } catch (Exception e) {

                session.rollback();
                throw e;
            }
        }
    }


    // ============================================================
    // 카테고리 수정
    // ============================================================

    public boolean UpdateCategory(Category category) {

        ValidateCategory(category);

        if (category.getCategoryId() == null ||
                category.getCategoryId() <= 0) {

            throw new IllegalArgumentException(
                    "올바른 카테고리 ID를 입력해 주세요."
            );
        }


        try (SqlSession session = OpenSession()) {

            CategoryDao dao =
                    session.getMapper(CategoryDao.class);

            try {

                // 수정 대상 존재 여부
                Optional<Category> existing =
                        dao.FindById(
                                category.getCategoryId()
                        );

                if (existing.isEmpty()) {
                    throw new IllegalArgumentException(
                            "존재하지 않는 카테고리입니다."
                    );
                }


                // 자기 자신 제외 코드 중복 검사
                if (dao.ExistsByCodeExceptId(
                        category.getCategoryCode(),
                        category.getCategoryId()
                )) {

                    throw new IllegalArgumentException(
                            "이미 사용 중인 카테고리 코드입니다."
                    );
                }


                // 자기 자신 제외 이름 중복 검사
                if (dao.ExistsByNameExceptId(
                        category.getCategoryName(),
                        category.getCategoryId()
                )) {

                    throw new IllegalArgumentException(
                            "이미 사용 중인 카테고리 이름입니다."
                    );
                }


                ValidateParentCategory(
                        dao,
                        category.getParentCategoryId(),
                        category.getCategoryId()
                );


                boolean result =
                        dao.Update(category);

                if (!result) {
                    session.rollback();
                    return false;
                }

                session.commit();
                return true;

            } catch (Exception e) {

                session.rollback();
                throw e;
            }
        }
    }


    // ============================================================
    // 카테고리 삭제
    // ============================================================

    public boolean DeleteCategory(Long categoryId) {

        if (categoryId == null || categoryId <= 0) {

            throw new IllegalArgumentException(
                    "올바른 카테고리 ID를 입력해 주세요."
            );
        }


        try (SqlSession session = OpenSession()) {

            CategoryDao dao =
                    session.getMapper(CategoryDao.class);

            try {

                Optional<Category> category =
                        dao.FindById(categoryId);

                if (category.isEmpty()) {

                    throw new IllegalArgumentException(
                            "존재하지 않는 카테고리입니다."
                    );
                }


                // 하위 카테고리가 있으면 삭제 금지
                if (dao.HasChildren(categoryId)) {

                    throw new IllegalStateException(
                            "하위 카테고리가 존재하여 삭제할 수 없습니다."
                    );
                }


                // 사용 중인 상품이 있으면 삭제 금지
                if (dao.HasProducts(categoryId)) {

                    throw new IllegalStateException(
                            "해당 카테고리를 사용하는 상품이 있어 삭제할 수 없습니다."
                    );
                }


                boolean result =
                        dao.DeleteById(categoryId);

                if (!result) {
                    session.rollback();
                    return false;
                }

                session.commit();
                return true;

            } catch (Exception e) {

                session.rollback();
                throw e;
            }
        }
    }


    // ============================================================
    // 입력 검증
    // ============================================================

    private void ValidateCategory(Category category) {

        if (category == null) {

            throw new IllegalArgumentException(
                    "카테고리 정보가 없습니다."
            );
        }


        if (category.getCategoryCode() == null ||
                category.getCategoryCode().isBlank()) {

            throw new IllegalArgumentException(
                    "카테고리 코드를 입력해 주세요."
            );
        }


        if (category.getCategoryName() == null ||
                category.getCategoryName().isBlank()) {

            throw new IllegalArgumentException(
                    "카테고리 이름을 입력해 주세요."
            );
        }
    }


    /**
     * 상위 카테고리 검증
     *
     * parentCategoryId == null이면 상위 카테고리이므로 허용
     */
    private void ValidateParentCategory(
            CategoryDao dao,
            Long parentCategoryId,
            Long currentCategoryId
    ) {

        if (parentCategoryId == null) {
            return;
        }


        // 자기 자신을 부모로 설정 금지
        if (currentCategoryId != null &&
                parentCategoryId.equals(currentCategoryId)) {

            throw new IllegalArgumentException(
                    "자기 자신을 상위 카테고리로 지정할 수 없습니다."
            );
        }


        Optional<Category> parent =
                dao.FindById(parentCategoryId);

        if (parent.isEmpty()) {

            throw new IllegalArgumentException(
                    "존재하지 않는 상위 카테고리입니다."
            );
        }


        // 현재 설계는 상위 → 하위의 2단계 구조로 사용
        if (parent.get().getParentCategoryId() != null) {

            throw new IllegalArgumentException(
                    "하위 카테고리를 다시 상위 카테고리로 지정할 수 없습니다."
            );
        }
    }


    // ============================================================
    // 공통
    // ============================================================

    private SqlSession OpenSession() {

        SqlSession session =
                DbConnectionFactory.OpenSession();

        if (session == null) {

            throw new IllegalStateException(
                    "DB 연결 설정이 초기화되지 않았습니다."
            );
        }

        return session;
    }
}