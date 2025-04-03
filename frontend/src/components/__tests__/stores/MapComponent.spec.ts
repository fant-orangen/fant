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
    await wrapper.vm.$nectTick();
    expect(itemService.fetchPreviewItems).toHaveBeenCalled();
  })
})
