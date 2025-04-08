import axiosInstance from '@/axiosConfig.ts'


export async function register(userData: {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  birthDate: string;
}) {
  return await axiosInstance.post('/auth/register', userData)
}
