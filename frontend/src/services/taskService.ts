import api from './api';
import { DashboardResponse, CreateTaskRequest } from '../types';


export const taskService = {
  getTasks: async (): Promise<DashboardResponse> => {
    const response = await api.get<DashboardResponse>('/dashboard/tasks', {
      params: { 
        status: 'pending'
      }
    });
    return response.data;
  },

  createTask: async (data: CreateTaskRequest): Promise<void> => {
    await api.post('/tasks/send', data);
  },

  sendReply: async (id: string, message: string): Promise<void> => {
    await api.patch(`/tasks/${id}/reply`, message, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
  },

  completeTask: async (id: string): Promise<void> => {
    await api.patch(`/tasks/${id}/complete`);
  },

  markAsViewed: async (id: string): Promise<void> => {
    await api.patch(`/tasks/${id}/viewed`);
  }
};