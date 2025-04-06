import { setActivePinia, createPinia } from "pinia";
import { useUserStore } from '@/stores/UserStore';
import { describe, it, expect, beforeEach, vi } from "vitest";

vi.mock('@/services/api/authService');
vi.mock('@/services/api/userService');
vi.mock('@/services/api/axiosInstance', () => {
  return {

    default: {
      get: vi.fn(),
      put: vi.fn(),

    }
  };
});

describe('UserStore', () => {
  let authService;
  let userService;
  let api;


  beforeEach(async () => {
    setActivePinia(createPinia());

    vi.resetAllMocks();


    authService = await import('@/services/api/authService');
    userService = await import('@/services/api/userService');
    api = (await import('@/services/api/axiosInstance')).default; // Get the mocked default export
  });


  it('initialize with default values', () => {
    const store = useUserStore();
    expect(store.token).toBeNull();
    expect(store.username).toBeNull();
    expect(store.profile.email).toBe('');
    expect(store.loggedIn).toBe(false);
  });

  it('login action does correct update', () => {
    const store = useUserStore();
    const testToken = 'fake_jwt_token';
    const testUser = 'testuser';

    store.login(200, testToken, testUser);

    expect(store.token).toBe(testToken);
    expect(store.username).toBe(testUser);
    expect(store.loggedIn).toBe(true);
  });

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
  });

  it('verifyLogin successfully updates state on valid credentials', async ()=> {
    const store = useUserStore();
    const mockToken = 'mock_jwt_token';

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

    authService.fetchToken = vi.fn().mockRejectedValue(new Error("Invalid credentials"));

    await expect(store.verifyLogin('wronguser@example.com','incorrectpassword')).rejects.toThrow("Invalid credentials");

    expect(store.token).toBeNull();
    expect(store.username).toBeNull();
  });

  describe('registerUser function', () => {
    const userData = {
      username: 'newtestuser@example.com',
      password: 'password123',
      email: 'newtestuser@example.com',
      firstName: 'Alice',
      lastName: 'Test',
      birthDate: '2000-01-01',
    };
    const mockToken = 'registered_user_token';

    it('registers and logs in user successfully', async () => {
      const store = useUserStore();

      userService.register = vi.fn().mockResolvedValue({ status: 200, data: {} });

      authService.fetchToken = vi.fn().mockResolvedValue({
        status: 200,
        data: { token: mockToken }
      });

      await store.registerUser(userData);

      expect(userService.register).toHaveBeenCalledWith(userData);
      expect(authService.fetchToken).toHaveBeenCalledWith({ username: userData.username, password: userData.password });
      expect(store.token).toBe(mockToken);
      expect(store.username).toBe(userData.username);
      expect(store.loggedIn).toBe(true);
    });

    it('handles registration failure', async () => {
      const store = useUserStore();
      const registrationError = new Error("Registration Failed - Email Already Exists");
      userService.register = vi.fn().mockRejectedValue(registrationError);
      authService.fetchToken = vi.fn();

      await expect(store.registerUser(userData)).rejects.toThrow("Registration Failed - Email Already Exists");

      expect(userService.register).toHaveBeenCalledWith(userData);
      expect(authService.fetchToken).not.toHaveBeenCalled();
      expect(store.token).toBeNull();
      expect(store.username).toBeNull();
    });


  it('handles login failure after after successful registration', async () => {
    const store = useUserStore();
    const loginError = new Error("Login Failure After Registration");
    userService.register = vi.fn().mockResolvedValue({ status: 200, data: {} });
    authService.fetchToken = vi.fn().mockRejectedValue(loginError);

    await expect(store.registerUser(userData)).rejects.toThrow("Login Failure After Registration");

    expect(userService.register).toHaveBeenCalledWith(userData);
    expect(authService.fetchToken).toHaveBeenCalledWith({ username: userData.username, password: userData.password });
    expect(store.username).toBeNull();
  });
  });

  describe('fetchProfile function', () => {
    it('fetches and updates profile successfully', async () => {
      const store = useUserStore();
      const mockProfileData = {
        email: 'fetcheduser@example.com',
        firstName: 'Alice',
        lastName: 'Test',
        phoneNumber: '12345678'
      }
      api.get.mockResolvedValue({ data: mockProfileData });

      await store.fetchProfile();

      expect(api.get).toHaveBeenCalledWith('/users/profile');
      expect(store.profile).toEqual(mockProfileData);
    });

    it('handles fetch profile failure', async () => {
      const store = useUserStore();
      const fetchError = new Error("Network Error");

      api.get.mockRejectedValue(fetchError);

      const initialProfile = { ...store.profile };

      await expect(store.fetchProfile()).rejects.toThrow("Network Error");

      expect(api.get).toHaveBeenCalledWith('/users/profile');
      expect(store.profile).toEqual(initialProfile);
    });
  });

  describe('updateProfile action', () => {
    const updatedProfileData = {
      email: 'updateduser@example.com',
      firstName: 'Alice',
      lastName: 'Test',
      phoneNumber: '12345678'
    };

    const responseProfileData = {
      email: 'updateduser@example.com',
      firstName: 'Alice',
      lastName: 'Test',
      phoneNumber: '12345678'
    };

    it('updates profile successfully', async () => {
      const store = useUserStore();
      store.profile = {
        email: 'unupdated@example.com',
        firstName: 'notAlice',
        lastName: 'notTest',
        phoneNumber: '87654321'
      };
      api.put.mockResolvedValue({ data: responseProfileData });

      await store.updateProfile(updatedProfileData);

      expect(api.put).toHaveBeenCalledWith('/users/profile', updatedProfileData);
      expect(store.profile).toEqual(responseProfileData);
    });

    it('handles profile update failures', async () => {
      const store = useUserStore();
      const initalProfile = {
        email: 'unupdated@example.com',
        firstName: 'notAlice',
        lastName: 'notTest',
        phoneNumber: '87654321'
      };
      store.profile = { ...initalProfile };

      const updateError = new Error("Update Failed");

      api.put.mockRejectedValue(updateError);

      await expect(store.updateProfile(updatedProfileData)).rejects.toThrow("Update Failed");

      expect(api.put).toHaveBeenCalledWith('/users/profile', updatedProfileData);
      expect(store.profile).toEqual(initalProfile);

    });

    describe('Getters', () => {
      it('returns current username with getUsername', () => {
        const store = useUserStore();
        store.username = 'Alice-GetterTest';
        expect(store.getUsername).toBe('Alice-GetterTest');
        store.username = null;
        expect(store.getUsername).toBeNull();
      });
      it('returns the current token with getToken', () => {
        const store = useUserStore();
        store.token = 'Token-GetterTest123';
        expect(store.getToken).toBe('Token-GetterTest123');
        store.token = null;
        expect(store.getToken).toBeNull();
      });
    });

  });
});
