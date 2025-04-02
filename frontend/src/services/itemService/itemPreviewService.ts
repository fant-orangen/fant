import api from '@/services/api/axiosInstance';
import type { ItemPreviewType } from '@/models/Item';

export async function fetchPreviewItems(): Promise<ItemPreviewType[]> {
  try {
    const response = await api.get<ItemPreviewType[]>('http://localhost:8080/items/all'); // TODO: Set this to use the URL configured by axios
    return response.data;
  } catch (error) {
    console.error('Error fetching items:', error);
    throw error;
  }
}
