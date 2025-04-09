import { mount } from '@vue/test-utils';
import MapComponent from "@/components/map/MapComponent.vue"; // Component being tested
import { describe, it, expect, vi, beforeEach } from "vitest";
import type { PaginatedItemPreviewResponse } from '@/models/Item'; // Import the response type

// --- Corrected Mock Setup ---
// Mock both functions used by the component
vi.mock('@/services/ItemService', () => ({
  fetchPagedPreviewItems: vi.fn(), // Mock the function actually called
  fetchPagedPreviewItemsByCategory: vi.fn() // Mock this one too
}));
// --- End Corrected Mock Setup ---

describe('MapComponent.vue', () => {
  // Declare itemService variable at a higher scope
  let itemService: {
    fetchPagedPreviewItems: ReturnType<typeof vi.fn>;
    fetchPagedPreviewItemsByCategory: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    // Dynamically import the mocked service
    itemService = await import('@/services/ItemService');
    // Reset mocks before each test
    itemService.fetchPagedPreviewItems.mockClear();
    itemService.fetchPagedPreviewItemsByCategory.mockClear();

    // Default mock implementation (can be overridden in specific tests)
    // Simulate a single page response for simplicity in beforeEach
    const mockResponse: PaginatedItemPreviewResponse = {
      content: [],
      totalPages: 1,
      totalElements: 0,
      size: 50,
      number: 0,
      first: true,
      last: true,
      empty: true
    };
    itemService.fetchPagedPreviewItems.mockResolvedValue(mockResponse);
    itemService.fetchPagedPreviewItemsByCategory.mockResolvedValue(mockResponse);
  });

  it('renders map container', () => {
    const wrapper = mount(MapComponent);
    expect(wrapper.find('.map-container').exists()).toBe(true);
  });

  // --- Corrected 'initializes map on mount' test ---
  it('initializes map on mount and calls fetchPagedPreviewItems', async () => {
    // No specific mock data needed here, just check the call
    const wrapper = mount(MapComponent);
    // Wait for component mounting and async operations
    await wrapper.vm.$nextTick(); // Wait for initial render
    await new Promise(resolve => setTimeout(resolve, 0)); // Allow promises microtask to resolve
    await wrapper.vm.$nextTick(); // Wait for potential updates after promises

    // Expect the correct function to have been called
    expect(itemService.fetchPagedPreviewItems).toHaveBeenCalled();
    // Optionally check arguments if needed:
    expect(itemService.fetchPagedPreviewItems).toHaveBeenCalledWith(0, 50); // page 0, size 50
  });
  // --- End Corrected Test ---


  // --- Corrected 'fetches items' test ---
  it('fetches items and updates component state', async () => {
    const mockItems = [
      { id:1, title: 'item1', price: 100, imageUrl: 'img1.jpg', latitude: 60.4, longitude: 5.3, categoryId: '1'},
      // Item 2 has no coordinates, MapComponent should filter it out before rendering markers, but it should be in items.value
      { id:2, title: 'item2', price: 200, imageUrl: 'img2.jpg', latitude: null, longitude: null, categoryId: '1'},
      { id:3, title: 'item3', price: 300, imageUrl: 'img3.jpg', latitude: 61, longitude: 6, categoryId: '1'}
    ];

    // Mock the response structure expected by PaginatedItemPreviewResponse
    const mockResponsePage1: PaginatedItemPreviewResponse = {
      content: mockItems, // Put mock items in content
      totalPages: 1,      // Assuming only one page for this test
      totalElements: mockItems.length,
      size: 50,
      number: 0,          // Page number (0-based)
      first: true,
      last: true,         // Indicate this is the last page
      empty: mockItems.length === 0
    };
    // Setup the mock for fetchPagedPreviewItems to return the structured response
    itemService.fetchPagedPreviewItems.mockResolvedValue(mockResponsePage1);

    const wrapper = mount(MapComponent);

    // Wait for mounting and async operations
    await wrapper.vm.$nextTick();
    await new Promise(resolve => setTimeout(resolve, 0)); // Allow promises to resolve
    await wrapper.vm.$nextTick(); // Wait for state updates

    // Check if the component's items ref was updated correctly
    // Note: MapComponent fetches ALL items, even those without coordinates initially
    expect(wrapper.vm.items).toEqual(mockItems);

    // Optionally, you could check if markers were created only for items with coordinates,
    // but that requires inspecting the Leaflet map instance or marker refs, which is more complex.
  });

});
