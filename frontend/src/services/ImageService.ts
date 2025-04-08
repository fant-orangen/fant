import api from '@/services/api/axiosInstance'

/**
 * Uploads image files for an item to the backend and stores them in Cloudinary.
 *
 * @param files Array of image files to upload
 * @param itemId The ID of the item the images belong to
 * @returns A promise that resolves when the upload is complete
 * @throws Error if any upload fails
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
    console.error('Image upload failed:', error);
    throw error;
  }
}

export default {
  uploadImages,
};
