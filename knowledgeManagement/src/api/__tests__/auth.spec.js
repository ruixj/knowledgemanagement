import { login, logout } from '../auth';
import httpService from '../../utils/request';
import { useAuthStore } from '../../utils/auth';
import { vi } from 'vitest';

// Mock httpService
vi.mock('../../utils/request', () => ({
  post: vi.fn(),
}));

// Mock authStore
vi.mock('../../utils/auth', () => ({
  useAuthStore: vi.fn(() => ({
    setUser: vi.fn(),
    clearUser: vi.fn(),
  })),
}));

describe('auth API', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('login', () => {
    it('should call login API and update authStore on success', async () => {
      const mockResponse = { data: { token: 'test-token' } };
      httpService.post.mockResolvedValue(mockResponse);
      
      const credentials = { username: 'test', password: '123456' };
      const result = await login(credentials);
      
      expect(httpService.post).toHaveBeenCalledWith('/api/login', credentials);
      expect(useAuthStore().setUser).toHaveBeenCalledWith({
        token: 'test-token',
        username: 'test',
      });
      expect(result).toEqual(mockResponse.data);
    });

    it('should throw error on login failure', async () => {
      const mockError = { response: { data: { message: 'Invalid credentials' } } };
      httpService.post.mockRejectedValue(mockError);
      
      const credentials = { username: 'test', password: 'wrong' };
      await expect(login(credentials)).rejects.toThrow('Invalid credentials');
    });
  });

  describe('logout', () => {
    it('should clear authStore', async () => {
      await logout();
      expect(useAuthStore().clearUser).toHaveBeenCalled();
    });
  });
});