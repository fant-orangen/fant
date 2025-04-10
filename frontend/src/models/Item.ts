/**
 * @fileoverview Item model definitions for marketplace listings.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Item preview data for listing cards</li>
 *   <li>Detailed item information for product pages</li>
 *   <li>Pagination support for item listings</li>
 *   <li>Item creation payload structure</li>
 *   <li>Favorites tracking and image management</li>
 * </ul>
 */

/**
 * Represents the minimal item data needed for listings and search results.
 * Used in grid views and item cards.
 * @interface ItemPreviewType
 */
export interface ItemPreviewType {
  /**
   * The unique identifier for the item.
   * @type {string|number}
   */
  id: string | number;

  /**
   * The display title of the item.
   * @type {string}
   */
  title: string;

  /**
   * URL to the primary image for the item.
   * @type {string}
   */
  imageUrl: string;

  /**
   * The listed price of the item.
   * @type {number}
   */
  price: number;

  /**
   * The identifier of the category this item belongs to.
   * @type {string}
   */
  categoryId: string;

  /**
   * Optional latitude coordinate for location-based filtering.
   * @type {number|undefined}
   */
  latitude?: number;

  /**
   * Optional longitude coordinate for location-based filtering.
   * @type {number|undefined}
   */
  longitude?: number;
}

/**
 * Represents a paginated response of item previews from the API.
 * Used for list views where multiple items are returned.
 * @interface PaginatedItemPreviewResponse
 */
export interface PaginatedItemPreviewResponse {
  /**
   * The array of item preview objects for the current page.
   * @type {ItemPreviewType[]}
   */
  content: ItemPreviewType[];

  /**
   * Total number of pages available.
   * @type {number}
   */
  totalPages: number;

  /**
   * Total number of item elements across all pages.
   * @type {number}
   */
  totalElements: number;

  /**
   * Number of elements per page.
   * @type {number}
   */
  size: number;

  /**
   * Current page number (0-based).
   * @type {number}
   */
  number: number;

  /**
   * Whether this is the first page.
   * @type {boolean}
   */
  first: boolean;

  /**
   * Whether this is the last page.
   * @type {boolean}
   */
  last: boolean;

  /**
   * Whether the current page is empty.
   * @type {boolean}
   */
  empty: boolean;
}

/**
 * Represents the complete detailed information for an item.
 * Used in item detail views and product pages.
 * @interface ItemDetailsType
 */
export interface ItemDetailsType {
  /**
   * The unique identifier for the item.
   * @type {string|number}
   */
  id: string | number;

  /**
   * The display title of the item.
   * @type {string}
   */
  title: string;

  /**
   * The full description of the item with details.
   * @type {string}
   */
  description: string;

  /**
   * The category name this item belongs to.
   * @type {string}
   */
  category: string;

  /**
   * The listed price of the item.
   * @type {number}
   */
  price: number;

  /**
   * Contact information for the seller.
   * @type {string}
   */
  contact: string;

  /**
   * Array of image URLs for item gallery.
   * @type {string[]}
   */
  imageUrls: string[];

  /**
   * Optional latitude coordinate for map display.
   * @type {number|undefined}
   */
  latitude?: number;

  /**
   * Optional longitude coordinate for map display.
   * @type {number|undefined}
   */
  longitude?: number;

  /**
   * The user ID of the seller who posted this item.
   * @type {number|string}
   */
  sellerId: number | string;
}

/**
 * Represents favorite item reference data.
 * Used for tracking user's favorited items.
 * @interface ItemFavoritesType
 */
export interface ItemFavoritesType {
  /**
   * The ID of the favorited item.
   * @type {string|number}
   */
  itemId: string | number;

  /**
   * Timestamp when the item was added to favorites.
   * Often received as ISO 8601 string from the backend.
   * @type {string}
   */
  createdAt: string;
}

/**
 * Represents the data structure for creating a new item listing.
 * Matches the structure expected by the createItem service.
 * @interface CreateItemType
 */
export interface CreateItemType {
  /**
   * The ID of the category to which this item belongs.
   * @type {number}
   */
  categoryId: number;

  /**
   * Short description used in listings and cards.
   * @type {string}
   */
  briefDescription: string;

  /**
   * Full detailed description for the item page.
   * @type {string}
   */
  fullDescription: string;

  /**
   * The asking price for the item.
   * @type {number}
   */
  price: number;

  /**
   * Optional latitude coordinate for item location.
   * @type {number|undefined}
   */
  latitude?: number;

  /**
   * Optional longitude coordinate for item location.
   * @type {number|undefined}
   */
  longitude?: number;

  /**
   * Array of image URLs for the item.
   * @type {string[]}
   */
  images: string[];
}
