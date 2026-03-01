export type Priority = 'low' | 'normal' | 'high' | 'urgent';
export type Category = 'personal' | 'work' | 'study' | 'health' | 'shopping' | 'finance' | 'others';
export type TaskStatus = 'todo' | 'inprogress' | 'done';
export type EnergyLevel = 'deep' | 'light' | 'low';

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
  timeTracked?: number; // minutes
  subtasks: { id: string; title: string; done: boolean }[];
  recurrence?: 'daily' | 'weekly' | 'monthly' | 'yearly';
  energyLevel?: EnergyLevel;
  kanbanColumn?: 'todo' | 'inprogress' | 'done';
  timeBlockStart?: string; // HH:MM
}
