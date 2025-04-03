import { mount } from '@vue/test-utils';
import MapComponent from "@/components/map/MapComponent.vue";
import { describe, it, expect, vi, beforeEach } from "vitest";
import L from 'leaflet';

vi.mock('@/services/ItemService', () => ({
  fetchPreviewItems: vi.fn()
}));

describe('MapComponent.vue', () => {
  let itemService;

  beforeEach(async () => {
    itemService = await import('@/services/ItemService');
    itemService.fetchPreviewItems.mockClear();
  });

  it('renders map container', () => {
    const wrapper = mount(MapComponent);
    expect(wrapper.find('.map-container').exists()).toBe(true);
  });

  it('initializes map on mount', async () => {
    itemService.fetchPreviewItems.mockResolvedValue([]);
    const wrapper = mount(MapComponent);
    await wrapper.vm.$nextTick();
    expect(itemService.fetchPreviewItems).toHaveBeenCalled();
  });

  it('fetches items', async () => {
    const mockItems = [
      { id:1, title: 'item1', price: 100, imageUrl: 'img1.jpg', latitude: 60.4, longitude: 5.3},
      { id:2, title: 'item2', price: 200, imageUrl: 'img2.jpg', latitude: null, longitude: null},
      { id:3, title: 'item3', price: 300, imageUrl: 'img3.jpg', latitude: 61, longitude: 6}

    ];

    itemService.fetchPreviewItems.mockResolvedValue(mockItems);
    const wrapper = mount(MapComponent);

    await new Promise(resolve => setTimeout(resolve, 0));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.items).toEqual(mockItems);
  });
});
