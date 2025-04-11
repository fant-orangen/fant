/**
 * Recommendation service module.
 *
 * This service provides methods for retrieving personalized recommendations
 * and related statistics for the current user, leveraging the backend's
 * recommendation engine.
 *
 * @module RecommendationService
 */
import api from '@/services/api/axiosInstance'
import type { CategoryRecommendation } from '@/models/Recommendation'

/**
 * Fetches personalized category recommendations for the current user.
 *
 * Makes a GET request to retrieve category recommendations with probability
 * distribution based on the user's browsing behavior. Requires user authentication
 * via the JWT token that's automatically included by axios interceptor.
 *
 * @returns {Promise<CategoryRecommendation>} Promise resolving to category recommendations with probability distribution
 * @throws {Error} When the request fails due to authentication issues or network errors
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
 * Makes a GET request to retrieve the aggregate count of item views recorded
 * for the user. This data helps understand the user's engagement level
 * with the marketplace.
 *
 * @returns {Promise<number>} Promise resolving to the user's total item view count
 * @throws {Error} When the request fails due to authentication issues or network errors
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
