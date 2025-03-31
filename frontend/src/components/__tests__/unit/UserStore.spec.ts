import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../../../stores/UserStore'
import * as authService from '../../../services/api/authService'
import { beforeEach, describe, it, expect, vi } from 'vitest'
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios'

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should log in a user and store token and username', async () => {
    // Arrange: Create a proper mock AxiosResponse
    const mockResponse: AxiosResponse = {
      status: 200,
      statusText: 'OK',
      headers: {},
      data: 'fake-jwt-token',
      config: {
        headers: {}  // Include required headers property
      } as InternalAxiosRequestConfig
    }

    vi.spyOn(authService, 'fetchToken').mockResolvedValue(mockResponse)

    const store = useUserStore()

    // Act: Call verifyLogin with test credentials
    await store.verifyLogin('testuser', 'password123')

    // Assert: Verify that the store's state is updated correctly
    expect(store.token).toEqual('fake-jwt-token')
    expect(store.username).toEqual('testuser')
  })
})
