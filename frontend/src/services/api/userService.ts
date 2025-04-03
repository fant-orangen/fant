import axios from 'axios'

export async function register(userData: {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  birthDate: string;
}) {
  return await axios.post('/api/auth/register', userData)
}
