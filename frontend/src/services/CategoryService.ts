import api from '@/services/api/axiosInstance.ts';
import type { Category } from '@/models/Category.ts';

export async function fetchCategories(): Promise<Category[]> {
  const response = await api.get<Category[]>('/category');
  console.log("inside the fetch categories:", response.data);
  return response.data;
}

export async function addCategory(category: Category): Promise<Category> {
  const response = await api.post<Category>('/admin/category', category);
  return response.data;
}

export async function updateCategory(category: Category): Promise<Category> {
  const response = await api.put<Category>(`/admin/category`, category);
  return response.data;
}

export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/admin/category/${id}`);
}
