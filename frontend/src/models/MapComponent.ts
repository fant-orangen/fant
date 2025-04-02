/**
 * Props for the Map Component
 *
 * Configuration options for the interactive map display
 */
export interface MapComponentProps {
  /**
   * CSS height for the map container
   * @default '800px'
   */
  height?: string;

  /**
   * CSS width for the map container
   * @default '100%'
   */
  width?: string;

  /**
   * Starting latitude for the map center
   * @default 60.39 (Bergen, Norway)
   */
  initialLatitude?: number;

  /**
   * Starting longitude for the map center
   * @default 5.32 (Bergen, Norway)
   */
  initialLongitude?: number;

  /**
   * Initial zoom level for the map
   * @default 7
   */
  initialZoom?: number;
}

/**
 * Map state interface representing the current map configuration
 */
export interface MapState {
  latitude: number;
  longitude: number;
  zoom: number;
}
