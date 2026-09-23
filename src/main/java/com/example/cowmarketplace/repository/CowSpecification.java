package com.example.cowmarketplace.repository;

import com.example.cowmarketplace.entity.Breed;
import com.example.cowmarketplace.entity.Cow;
import com.example.cowmarketplace.entity.CowStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CowSpecification {

    public static Specification<Cow> hasStatus(CowStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Cow> hasBreed(Breed breed) {
        return (root, query, cb) ->
                breed == null ? null : cb.equal(root.get("breed"), breed);
    }

    public static Specification<Cow> hasCity(String city) {
        return (root, query, cb) ->
                (city == null || city.isBlank())
                        ? null
                        : cb.equal(cb.lower(root.get("location")), city.toLowerCase());
    }

    public static Specification<Cow> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Cow> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Cow> ageGreaterThanOrEqual(Integer minAge) {
        return (root, query, cb) ->
                minAge == null ? null : cb.greaterThanOrEqualTo(root.get("age"), minAge);
    }

    public static Specification<Cow> ageLessThanOrEqual(Integer maxAge) {
        return (root, query, cb) ->
                maxAge == null ? null : cb.lessThanOrEqualTo(root.get("age"), maxAge);
    }

    public static Specification<Cow> milkPerDayGreaterThanOrEqual(Double minMilkPerDay) {
        return (root, query, cb) ->
                minMilkPerDay == null ? null : cb.greaterThanOrEqualTo(root.get("milkPerDay"), minMilkPerDay);
    }
}