import api from './api';

export const authService = {
  getAuthorizationUrl: async (): Promise<string> => {
    const response = await api.get<{ authorizationUrl: string }>('/auth/authorize');
    return response.data.authorizationUrl;
  },

  revokeAccess: async (companyId: number): Promise<void> => {
    await api.post(`/auth/revoke/${companyId}`);
  }
};