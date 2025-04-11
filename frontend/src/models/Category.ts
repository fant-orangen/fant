/**
 * @fileoverview Category model for item classification functionality.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Category tree structure with hierarchical relationships</li>
 *   <li>Category metadata including names and images</li>
 *   <li>Internationalization support via translation keys</li>
 * </ul>
 */

/**
 * Represents a product/item category in the application.
 * Categories can form a hierarchical structure through parent relationships.
 * @interface Category
 */
export interface Category {
  /**
   * The unique identifier for the category.
   * May be null for new categories not yet persisted.
   * @type {string|number|null}
   */
  id: string | number | null;

  /**
   * The display name of the category.
   * Used as a fallback when translations are unavailable.
   * @type {string}
   */
  name: string;

  /**
   * URL to the category's representative image.
   * Used in category selection UI and navigation components.
   * @type {string}
   */
  imageUrl: string;

  /**
   * Optional reference to parent category.
   * Creates hierarchical category structure for breadcrumbs and filtering.
   * @type {Category|null|undefined}
   */
  parent?: Category | null;

  /**
   * Optional key for i18n translations.
   * When provided, enables localized category names.
   * @type {string|undefined}
   */
  translationKey?: string;
}
