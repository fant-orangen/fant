// src/services/FavoriteService.ts
import api from '@/services/api/axiosInstance.ts'
import type { ItemPreviewType, ItemFavoritesType } from '@/models/Item.ts';
import {fetchFavoriteItems} from "@/services/ItemService.ts";

export interface PaginatedItemsResponse {
  items: ItemPreviewType[]
  totalItems: number
}

/**
 * Add an item to user's favorites
 * @param itemId ID of the item to add to favorites
 * @returns Promise that resolves when the item is added
 */
export async function addFavorite(itemId: string | number): Promise<void> {
  console.log("added fav:", itemId);
  await api.post(`/favorite/${itemId}`);
}

/**
 * Remove an item from user's favorites
 * @param itemId ID of the item to remove from favorites
 * @returns Promise that resolves when the item is removed
 */
export async function removeFavorite(itemId: string | number): Promise<void> {
  await api.delete(`/favorite/${itemId}`);
}

export async function fetchFavoriteItemsShort(): Promise<ItemFavoritesType[]> {
  const { data } = await api.get<ItemFavoritesType[]>('/favorite');
  return data;
}

export async function checkIsFavorite(itemId: string | number): Promise<boolean> {
  if (!itemId) return false;

  try {
    const favorites = await fetchFavoriteItemsShort();
    return favorites.some(fav => fav?.itemId?.toString() === itemId.toString());
  } catch {
    return false;
  }
}


/**
 * Get favorite count for an item
 * @param itemId ID of the item to get count for
 * @returns Promise that resolves to count of favorites
 */
export async function getFavoriteCount(itemId: string | number): Promise<number> {
  const response = await api.get(`/favorite/count/${itemId}`);
  return response.data;
}
