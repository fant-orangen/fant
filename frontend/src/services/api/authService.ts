import axiosInstance from '@/axiosConfig';

export async function fetchToken({ username, password }: { username: string, password: string }) {
  console.log('Sending auth request with:', { email: username, password });

  // Make sure axiosInstance properly handles the request
  return await axiosInstance.post('/auth/login',
    JSON.stringify({
      email: username,
      password: password
    }),
    {
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );
}
