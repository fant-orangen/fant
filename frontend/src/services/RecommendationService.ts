import api from '@/services/api/axiosInstance'
import type { CategoryRecommendation } from '@/models/Recommendation'

/**
 * Fetches personalized category recommendations for the current user.
 * The API call relies on the authorization token being included automatically
 * by the axios interceptor in axiosInstance.
 *
 * @returns A Promise resolving to category recommendations with probability distribution
 */
export async function fetchCategoryRecommendations(): Promise<CategoryRecommendation> {
  try {
    const response = await api.get<CategoryRecommendation>('/recommendations/categories')
    return response.data
  } catch (error) {
    console.error('Error fetching category recommendations:', error)
    throw error
  }
}

/**
 * Fetches the total number of item views for the currently authenticated user.
 *
 * @returns A promise that resolves to the user's total view count
 */
export async function fetchUserViewCount(): Promise<number> {
  try {
    const response = await api.get<{ totalViews: number }>('/recommendations/views/count')
    return response.data.totalViews
  } catch (error) {
    console.error('Error fetching user view count:', error)
    throw error
  }
}
