import { setActivePinia, createPinia } from 'pinia'
  import { useUserStore } from '@/stores/UserStore'
import * as authService from '@/services/api/authService'
import { beforeEach, describe, it, expect, vi } from 'vitest'
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios'

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should log in a user and store token and username', async () => {

    const mockResponse: AxiosResponse = {
      status: 200,
      statusText: 'OK',
      headers: {},
      data: 'fake-jwt-token',
      config: {
        headers: {}
      } as InternalAxiosRequestConfig
    }

    vi.spyOn(authService, 'fetchToken').mockResolvedValue(mockResponse)

    const store = useUserStore()
    await store.verifyLogin('testuser', 'password123')

    expect(store.token).toEqual('fake-jwt-token')
    expect(store.username).toEqual('testuser')
  })
})
