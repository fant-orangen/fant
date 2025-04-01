import api from '@/services/api/axiosInstance.ts';
import type { Category } from '@/models/Category.ts';

export async function fetchCategories(): Promise<Category[]> {
  const response = await api.get<Category[]>('http://localhost:3000/categories');
  return response.data;
}

export async function addCategory(category: Category): Promise<Category> {
  const response = await api.post<Category>('http://localhost:3000/categories', category);
  return response.data;
}

export async function updateCategory(id: string, category: { label: string; icon: string }): Promise<Category> {
  const response = await api.put<Category>(`http://localhost:3000/categories/${id}`, category);
  return response.data;
}

export async function deleteCategory(id: string): Promise<void> {
  await api.delete(`http://localhost:3000/categories/${id}`);
}
