import axios from 'axios';

export async function fetchToken({ username, password }: { username: string, password: string }) {
  return await axios.post('/api/auth/token', { username, password });
}
