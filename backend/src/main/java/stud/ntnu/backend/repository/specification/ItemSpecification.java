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
        Join<Item, Category> categoryJoin = root.join("category", JoinType.LEFT); // Use LEFT JOIN if category might be optional or to avoid errors if join fails unexpectedly
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(categoryJoin.get("name")),
            searchDto.getCategoryName().toLowerCase()));
      }

      // --- REMOVED Location-based search using ST_Distance_Sphere ---
      // The distance filtering will now be handled in the service layer.
      // Optionally, add a basic bounding box filter here if performance becomes an issue:
      // if (searchDto.getUserLatitude() != null && searchDto.getUserLongitude() != null && searchDto.getMaxDistance() != null) {
      //    // Basic Bounding Box Predicates (less precise, but faster DB query)
      //    // Calculate min/max lat/lon based on maxDistance and add predicates here
      // }


      // Ensure the query does not generate duplicates if multiple joins are used
      query.distinct(true);


      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}