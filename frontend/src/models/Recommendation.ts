/**
 * @fileoverview Recommendation model for personalized category suggestions.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Category recommendation probability distributions</li>
 *   <li>Personalized content suggestions based on user behavior</li>
 *   <li>Data structures for recommendation engine outputs</li>
 * </ul>
 */

/**
 * Represents a probability distribution for category recommendations.
 * Each category has a probability value indicating its relevance for the user.
 * Used by the recommendation engine to suggest personalized content.
 * @interface CategoryRecommendation
 */
export interface CategoryRecommendation {
  /**
   * Maps category IDs to their recommendation probabilities.
   * Values are between 0 and 1, with the sum of all probabilities equal to 1.
   * Higher probability values indicate stronger recommendations.
   * @type {Record<string, number>}
   * @example
   * {
   *   "4": 0.25,   // Books category (25% relevance)
   *   "9": 0.15,   // Furniture category (15% relevance)
   *   "7": 0.05    // Clothing category (5% relevance)
   * }
   */
  distribution: Record<string, number>;
}
