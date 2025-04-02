import api from '@/services/api/axiosInstance.ts';
import type { ItemPreviewType } from '@/models/Item.ts';
import type {ItemDetailsType} from "@/models/Item.ts";

export async function fetchPreviewItems(): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>('http://localhost:3000/items');
    return response.data;
  } catch (error) {
    console.error('Error fetching items:', error);
    throw error;
  }
}

export async function fetchItem(itemId: string | number): Promise<ItemDetailsType> {
  try {
    const response = await api.get<ItemDetailsType>(`http://localhost:3000/item-detail?id=${itemId}`);
    console.log(response.data, "here is data")
    return response.data;
  } catch (error) {
    console.error(`Error fetching item with ID ${itemId}:`, error);
    throw error;
  }
}

export async function fetchPreviewItemsByCategoryId(categoryId: string): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>(`http://localhost:3000/items?categoryId=${categoryId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching items for category ID ${categoryId}:`, error);
    throw error;
  }
}
