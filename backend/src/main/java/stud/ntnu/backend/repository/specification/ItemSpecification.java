package stud.ntnu.backend.repository.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import stud.ntnu.backend.data.item.ItemSearchDto;
import stud.ntnu.backend.model.Item;
import stud.ntnu.backend.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>ItemSpecification</h2>
 * <p>Provides specifications for querying {@link Item} entities based on various search criteria.</p>
 * <p>This class utilizes Spring Data JPA's {@link Specification} to build dynamic queries based on the
 * fields present in the {@link ItemSearchDto}.</p>
 */
public class ItemSpecification {

  /**
   * <h3>searchByCriteria</h3>
   * <p>Constructs a {@link Specification} to filter {@link Item} entities based on the provided
   * {@link ItemSearchDto}.</p>
   * <p>The specification includes predicates for searching by term in descriptions, price range,
   * item status, and category name. Only the criteria provided in the {@code searchDto} will be
   * applied to the query.</p>
   *
   * @param searchDto the {@link ItemSearchDto} containing the search criteria. Fields that are null
   *                  or empty will be ignored.
   * @return a {@link Specification} that can be used in a JPA query to filter {@link Item} entities
   * based on the criteria in the {@code searchDto}. Returns a specification that always returns
   * true if the {@code searchDto} is null or contains no criteria.
   */
  public static Specification<Item> searchByCriteria(ItemSearchDto searchDto) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Search term (in brief or full description)
      if (StringUtils.hasText(searchDto.getSearchTerm())) {
        String searchPattern = "%" + searchDto.getSearchTerm().toLowerCase() + "%";
        predicates.add(criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("briefDescription")),
                searchPattern),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("fullDescription")),
                searchPattern)));
      }

      // Price range
      if (searchDto.getMinPrice() != null) {
        predicates.add(
            criteriaBuilder.greaterThanOrEqualTo(root.get("price"), searchDto.getMinPrice()));
      }

      if (searchDto.getMaxPrice() != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(root.get("price"), searchDto.getMaxPrice()));
      }

      // Item status
      if (searchDto.getStatus() != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), searchDto.getStatus()));
      }

      // Category name
      if (StringUtils.hasText(searchDto.getCategoryName())) {
        Join<Item, Category> categoryJoin = root.join("category",
            JoinType.LEFT); // Use LEFT JOIN if category might be optional or to avoid errors if join fails unexpectedly
        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(categoryJoin.get("name")),
            searchDto.getCategoryName().toLowerCase()));
      }
      query.distinct(true);


      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}