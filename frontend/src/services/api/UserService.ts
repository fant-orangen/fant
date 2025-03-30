import axios from 'axios'

export async function register(userData: any) {
  return await axios.post('/api/users/register', userData)
}
