package com.shopsphere.product.service.impl;

import com.shopsphere.product.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filter(
            String name,
            String status,
            Long categoryId,
            Long brandId
    ) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Search by product name OR description
            if (name != null && !name.isBlank()) {

                String searchValue = "%" + name.toLowerCase() + "%";

                Predicate namePredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")
                                ),
                                searchValue
                        );

                Predicate descriptionPredicate =
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("description")
                                ),
                                searchValue
                        );

                predicates.add(
                        criteriaBuilder.or(
                                namePredicate,
                                descriptionPredicate
                        )
                );
            }

            // Filter by status
            if (status != null && !status.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            // Filter by category
            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                categoryId
                        )
                );
            }

            // Filter by brand
            if (brandId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("brand").get("id"),
                                brandId
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
