/**
 * Represents a probability distribution for category recommendations.
 * Each category has a probability value indicating its relevance for the user.
 */
export interface CategoryRecommendation {
  /**
   * Maps category IDs to their recommendation probabilities.
   * Values are between 0 and 1, with the sum of all probabilities equal to 1.
   */
  distribution: Record<string, number>;
}
