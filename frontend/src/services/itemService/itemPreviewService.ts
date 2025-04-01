import api from '@/services/api/axiosInstance';
import type { ItemPreviewType } from '@/models/Item';

export async function fetchPreviewItems(): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>('http://localhost:3000/items');
    return response.data;
  } catch (error) {
    console.error('Error fetching items:', error);
    throw error;
  }
}
