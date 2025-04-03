import { setActivePinia, createPinia } from "pinia";
import { useUserStore } from '@/stores/UserStore';
import { describe,it,expect,beforeEach, vi } from "vitest";

// Mock API services used by the store

vi.mock('@/services/api/authService');
vi.mock('@/services/api/userService');
vi.mock('@/services/api/axiosInstance');

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());

  });

  it('initialize with default values', () => {
    const store = useUserStore();
    expect(store.token).toBeNull();
    expect(store.username).toBeNull();
    expect(store.profile.email).toBe('');
    expect(store.loggedIn).toBe(false);
  })

  it('login action does correct update', () => {
    const store = useUserStore();
    const testToken = 'fake_jwt_token';
    const testUser = 'testuser';

    store.login(200, testToken, testUser);

    expect(store.token).toBe(testToken);
    expect(store.username).toBe(testUser);
    expect(store.loggedIn).toBe(true);
  })

  it('login action throws error on non-200 status', () => {
    const store = useUserStore();
    expect(() => store.login(401,'','')).toThrowError("Login Info Error");
  });

  it('logout clears user state', () => {
    const store = useUserStore();

    store.token = 'fake_jwt_token';
    store.username = 'testuser';
    store.profile = { email: 'test@example.com', firstName: 'TestFirstName', lastName: 'TestSurname', phoneNumber: '12345678'};

    store.logout();

    expect(store.token).toBeNull();
    expect(store.username).toBeNull();
    expect(store.profile.email).toBe('');
    expect(store.loggedIn).toBe(false);
  })

  it('verifyLogin successfully updates state on valid credentials', async ()=> {
    const store = useUserStore();
    const authService = await import('@/services/api/authService');
    const mockToken = 'mock_jwt_token';

    // Setup mock for fetchToken
    authService.fetchToken = vi.fn().mockResolvedValue({
      status: 200,
      data: { token: mockToken }
    });

    await store.verifyLogin('testuser@example.com', 'password123');

    expect(authService.fetchToken).toHaveBeenCalledWith({ username: 'testuser@example.com', password: 'password123'});
    expect(store.token).toBe(mockToken);
    expect(store.username).toBe('testuser@example.com');
    expect(store.loggedIn).toBe(true);
  });

  it('verifyLogin handles errors', async () => {
    const store = useUserStore();
    const authService = await import('@/services/api/authService');

    authService.fetchToken = vi.fn().mockRejectedValue(new Error("Invalid credentials"));

    await expect(store.verifyLogin('wronguser@example.com','incorrectpassword')).rejects.toThrow("Invalid credentials");

    expect(store.token).toBeNull();
    expect(store.username).toBeNull();

  })

})
