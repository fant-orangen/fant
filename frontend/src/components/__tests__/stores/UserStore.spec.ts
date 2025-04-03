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



})
