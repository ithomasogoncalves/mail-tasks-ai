export type Urgency = 'URGENTE' | 'MEDIANO' | 'ROTINEIRA';
export type TaskStatus = 'PENDING' | 'COMPLETED' | 'ARCHIVED';

export interface Task {
  id: string;
  resumoTarefa: string;
  urgencia: Urgency;
  categoriaSugerida: string;
  fromEmail: string;
  receivedAt: string;
  status: TaskStatus;
  emailSubject?: string;
  emailBody?: string;
}

export interface TaskStats {
  urgent_count: number;
  pending_count: number;
  completed_count: number;
}

export interface DashboardResponse {
  tasks: Task[];
  stats?: TaskStats;
  pagination?: {
    currentPage: number;
    totalPages: number;
    totalItems: number;
    itemsPerPage: number;
  };
}

export interface CreateTaskRequest {
  title: string;
  recipient: string;
  category: string; 
  urgencia: Urgency;
  subject?: string; 
  body?: string;    
}

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  company: string;
  role: string;
  microsoftConnected: boolean;
}