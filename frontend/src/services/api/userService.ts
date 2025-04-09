import axiosInstance from '@/axiosConfig'


class RegistrationData {}

export async function register(userData: RegistrationData) {
  return await axiosInstance.post('/auth/register', userData)
}
