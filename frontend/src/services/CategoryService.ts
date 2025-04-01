import api from '@/services/api/axiosInstance.ts';
import Category from '@/models/Category.ts';

export async function fetchCategories(): Promise<Category[]> {
  const response = await api.get<Category[]>('/api/categories');
  return response.data;
}

export async function addCategory(category: Category): Promise<Category> {
  const response = await api.post<Category>('/api/categories', category);
  return response.data;
}

export async function updateCategory(category: Category): Promise<Category> {
  const response = await api.put<Category>(`/api/categories/${category.id}`, category);
  return response.data;
}

export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/api/categories/${id}`);
}
