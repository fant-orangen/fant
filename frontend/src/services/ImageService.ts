/**
 * Image management service module.
 *
 * This service provides methods for uploading and deleting item images
 * in the marketplace system, with integration to Cloudinary for cloud storage.
 *
 * @module ImageService
 */
import api from '@/services/api/axiosInstance'

/**
 * Uploads image files for an item to the backend and stores them in Cloudinary.
 *
 * Creates a FormData object containing the image files and item ID, then makes
 * a POST request to the image upload endpoint. Handles error reporting for
 * failed uploads.
 *
 * @param {File[]} files - Array of image files to upload
 * @param {number} itemId - The ID of the item the images belong to
 * @returns {Promise<void>} Promise that resolves when the upload is complete
 * @throws {Error} If any upload fails or server returns non-200 status
 */
export async function uploadImages(files: File[], itemId: number): Promise<void> {
  try {
    const formData = new FormData();

    files.forEach((file) => {
      formData.append('files', file); // or 'file' if backend only accepts one
    });

    formData.append('itemId', itemId.toString());

    const response = await api.post('/images/add', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.status !== 200) {
      throw new Error(`Unexpected response status: ${response.status}`);
    }
  } catch (error) {
    throw error;
  }
}

/**
 * Deletes an image for an item from the backend and Cloudinary.
 *
 * Makes a DELETE request with query parameters identifying the item and image URL.
 * Validates the response status to ensure successful deletion.
 *
 * @param {number} itemId - The ID of the item the image belongs to
 * @param {string} imageUrl - The URL of the image to delete
 * @returns {Promise<void>} Promise that resolves when the deletion is complete
 * @throws {Error} If deletion fails or server returns non-200 status
 */
export async function deleteImage(itemId: number, imageUrl: string): Promise<void> {
  try {
    const response = await api.delete('/images/delete', {
      params: {
        itemId,
        imageUrl
      }
    });

    if (response.status !== 200) {
      throw new Error(`Unexpected response status: ${response.status}`);
    }
  } catch (error) {
    throw error;
  }
}

export default {
  uploadImages,
  deleteImage,
};
