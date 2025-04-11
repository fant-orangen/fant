/**
 * @fileoverview Map component model for interactive location mapping.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Map component configuration props</li>
 *   <li>Map state tracking (position, zoom)</li>
 *   <li>Flexible styling via CSS dimensions</li>
 *   <li>Default configuration targeting Bergen, Norway</li>
 * </ul>
 */

/**
 * Props for the Map Component.
 * Configuration options for the interactive map display.
 * @interface MapComponentProps
 */
export interface MapComponentProps {
  /**
   * CSS height for the map container.
   * Determines the vertical size of the map element.
   * @default '800px'
   * @type {string|undefined}
   */
  height?: string;

  /**
   * CSS width for the map container.
   * Determines the horizontal size of the map element.
   * @default '100%'
   * @type {string|undefined}
   */
  width?: string;

  /**
   * Starting latitude for the map center.
   * Used when initializing the map view.
   * @default 60.39 (Bergen, Norway)
   * @type {number|undefined}
   */
  initialLatitude?: number;

  /**
   * Starting longitude for the map center.
   * Used when initializing the map view.
   * @default 5.32 (Bergen, Norway)
   * @type {number|undefined}
   */
  initialLongitude?: number;

  /**
   * Initial zoom level for the map.
   * Controls the starting magnification level.
   * @default 7
   * @type {number|undefined}
   */
  initialZoom?: number;
}
