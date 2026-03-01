export type Priority = 'low' | 'normal' | 'high' | 'urgent';
export type Category = 'personal' | 'work' | 'study' | 'health' | 'shopping' | 'finance' | 'others';
export type TaskStatus = 'todo' | 'inprogress' | 'done';
export type EnergyLevel = 'deep' | 'light' | 'low';

export interface TimeSession {
  startedAt: string; // ISO timestamp
  endedAt?: string;  // ISO timestamp (undefined if running)
  durationMinutes?: number;
}

export interface Task {
  id: string;
  title: string;
  description?: string;
  priority: Priority;
  category: Category;
  tags: string[];
  dueDate?: string; // ISO date YYYY-MM-DD
  dueTime?: string; // HH:MM
  status: TaskStatus;
  isStarred: boolean;
  isCompleted: boolean;
  completedAt?: string;
  createdAt: string;
  estimatedDuration?: number; // minutes
  timeTracked?: number; // minutes (total accumulated)
  timeSessions?: TimeSession[]; // individual session log
  subtasks: { id: string; title: string; done: boolean }[];
  recurrence?: 'daily' | 'weekly' | 'monthly' | 'yearly';
  energyLevel?: EnergyLevel;
  kanbanColumn?: 'todo' | 'inprogress' | 'done';
  timeBlockStart?: string; // HH:MM
  templateId?: string; // if created from a template
}

export interface TaskTemplate {
  id: string;
  name: string;
  description: string;
  icon: string;
  tasks: Omit<Task, 'id' | 'createdAt' | 'isCompleted' | 'completedAt' | 'timeSessions' | 'templateId'>[];
}

export interface FocusSession {
  date: string; // YYYY-MM-DD
  focusMinutes: number;
  sessions: number;
}
