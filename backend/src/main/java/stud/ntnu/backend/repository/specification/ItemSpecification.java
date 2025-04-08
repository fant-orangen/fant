package stud.ntnu.backend.repository.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import stud.ntnu.backend.data.item.ItemSearchDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ItemSpecification {

  public static Specification<Item> searchByCriteria(ItemSearchDto searchDto) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Search term (in brief or full description)
      if (StringUtils.hasText(searchDto.getSearchTerm())) {
        String searchPattern = "%" + searchDto.getSearchTerm().toLowerCase() + "%";
        predicates.add(criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("briefDescription")), searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("fullDescription")), searchPattern)
        ));
      }

      // Price range
      if (searchDto.getMinPrice() != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), searchDto.getMinPrice()));
      }

      if (searchDto.getMaxPrice() != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), searchDto.getMaxPrice()));
      }

      // Item status
      if (searchDto.getStatus() != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), searchDto.getStatus()));
      }

      // Category name
      if (StringUtils.hasText(searchDto.getCategoryName())) {
        Join<Item, Category> categoryJoin = root.join("category");
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(categoryJoin.get("name")),
            searchDto.getCategoryName().toLowerCase()));
      }

      // Location-based search (using Haversine formula for distance calculation)
      if (searchDto.getUserLatitude() != null && searchDto.getUserLongitude() != null && searchDto.getMaxDistance() != null) {
        // MySQL's ST_Distance_Sphere function calculates distance in meters between two points
        // Convert km to meters
        double maxDistanceMeters = searchDto.getMaxDistance() * 1000;

        Expression<Double> distance = criteriaBuilder.function(
            "ST_Distance_Sphere",
            Double.class,
            criteriaBuilder.function("POINT", Object.class, root.get("longitude"), root.get("latitude")),
            criteriaBuilder.function("POINT", Object.class, criteriaBuilder.literal(searchDto.getUserLongitude()), criteriaBuilder.literal(searchDto.getUserLatitude()))
        );

        predicates.add(criteriaBuilder.lessThanOrEqualTo(distance, maxDistanceMeters));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}