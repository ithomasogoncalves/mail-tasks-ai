import api from './api';
import { UserProfile } from '../types';

export const userService = {
  getProfile: async (): Promise<UserProfile> => {
    const response = await api.get<UserProfile>('/user/profile');
    return response.data;
  },

  disconnectOutlook: async (): Promise<void> => {
    await api.post('/auth/logout');
  }
};