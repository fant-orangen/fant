import { defineStore } from 'pinia';
import { fetchToken } from '@/services/api/AuthService.ts';



export const useUserStore = defineStore('user', {
  state: () => ({
    token: null as string | null,
    username: null as string | null,
  }),
  persist: {
    storage: sessionStorage,
  },
  actions: {
    login(status: number, token: string, username: string) {
      if (status === 200) {
        this.token = token;
        this.username = username;
      } else {
        throw new Error ("Login Info Erro")
      }
    },
    async verifyLogin(username: string, password: string) {
      const response = await fetchToken( { username, password } )
      this.login(response.status, reponse.data, username)
    },
  }
})
