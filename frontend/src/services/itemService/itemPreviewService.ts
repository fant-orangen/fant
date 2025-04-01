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
export async function fetchFavoriteItems(): Promise<ItemPreviewType[]> {
  try {
    // The actual endpoint might be different, adjust as needed.
    // Uses the base URL and auth token from the shared axios instance 'api'.
    const response = await api.get<ItemPreviewType[]>('http://localhost:3000/items');
    return response.data;
  } catch (error) {
    console.error('Error fetching favorite items:', error);
    // You might want more specific error handling here depending on backend responses
    throw error;
  }
}
