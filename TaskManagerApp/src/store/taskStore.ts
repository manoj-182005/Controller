import { create } from 'zustand';
import { Task, Priority, Category, TaskStatus } from '../types/task';

const todayStr = new Date().toISOString().split('T')[0];
const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
const twoDaysAgo = new Date(Date.now() - 2 * 86400000).toISOString().split('T')[0];
const threeDaysAgo = new Date(Date.now() - 3 * 86400000).toISOString().split('T')[0];
const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
const in2Days = new Date(Date.now() + 2 * 86400000).toISOString().split('T')[0];
const in3Days = new Date(Date.now() + 3 * 86400000).toISOString().split('T')[0];
const in5Days = new Date(Date.now() + 5 * 86400000).toISOString().split('T')[0];
const in7Days = new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0];

const SAMPLE_TASKS: Task[] = [
  {
    id: '1',
    title: 'Prepare Q3 quarterly report',
    description: 'Compile all financial data and create presentation slides',
    priority: 'urgent',
    category: 'work',
    tags: ['finance', 'report'],
    dueDate: todayStr,
    dueTime: '10:00',
    status: 'inprogress',
    isStarred: true,
    isCompleted: false,
    createdAt: threeDaysAgo + 'T08:00:00Z',
    estimatedDuration: 120,
    timeTracked: 45,
    subtasks: [
      { id: 's1', title: 'Gather sales data', done: true },
      { id: 's2', title: 'Create charts', done: false },
      { id: 's3', title: 'Write executive summary', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'inprogress',
    timeBlockStart: '09:00',
  },
  {
    id: '2',
    title: 'Morning workout — chest & triceps',
    description: '45-minute gym session',
    priority: 'high',
    category: 'health',
    tags: ['fitness', 'gym'],
    dueDate: todayStr,
    dueTime: '07:00',
    status: 'done',
    isStarred: false,
    isCompleted: true,
    completedAt: todayStr + 'T07:45:00Z',
    createdAt: todayStr + 'T06:00:00Z',
    estimatedDuration: 45,
    timeTracked: 50,
    subtasks: [],
    energyLevel: 'light',
    kanbanColumn: 'done',
    timeBlockStart: '07:00',
  },
  {
    id: '3',
    title: 'Review pull requests on GitHub',
    priority: 'normal',
    category: 'work',
    tags: ['code', 'review'],
    dueDate: todayStr,
    dueTime: '14:00',
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T09:00:00Z',
    estimatedDuration: 60,
    subtasks: [
      { id: 's4', title: 'Review auth PR', done: false },
      { id: 's5', title: 'Review dashboard PR', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'todo',
    timeBlockStart: '14:00',
  },
  {
    id: '4',
    title: 'Read "Atomic Habits" — Chapter 12',
    priority: 'low',
    category: 'study',
    tags: ['books', 'habits'],
    dueDate: todayStr,
    status: 'todo',
    isStarred: true,
    isCompleted: false,
    createdAt: yesterday + 'T20:00:00Z',
    estimatedDuration: 30,
    subtasks: [],
    energyLevel: 'light',
    kanbanColumn: 'todo',
    timeBlockStart: '20:00',
  },
  {
    id: '5',
    title: 'Pay credit card bill',
    priority: 'urgent',
    category: 'finance',
    tags: ['bills', 'payment'],
    dueDate: yesterday,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: twoDaysAgo + 'T10:00:00Z',
    estimatedDuration: 10,
    subtasks: [],
    energyLevel: 'low',
    kanbanColumn: 'todo',
  },
  {
    id: '6',
    title: 'Buy groceries for the week',
    priority: 'normal',
    category: 'shopping',
    tags: ['food', 'errands'],
    dueDate: yesterday,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: twoDaysAgo + 'T11:00:00Z',
    estimatedDuration: 45,
    subtasks: [
      { id: 's6', title: 'Vegetables', done: false },
      { id: 's7', title: 'Dairy products', done: false },
      { id: 's8', title: 'Snacks', done: false },
    ],
    energyLevel: 'low',
    kanbanColumn: 'todo',
  },
  {
    id: '7',
    title: 'Plan weekend trip to mountains',
    priority: 'low',
    category: 'personal',
    tags: ['travel', 'weekend'],
    dueDate: in3Days,
    status: 'todo',
    isStarred: true,
    isCompleted: false,
    createdAt: yesterday + 'T15:00:00Z',
    estimatedDuration: 60,
    subtasks: [
      { id: 's9', title: 'Book accommodation', done: false },
      { id: 's10', title: 'Pack gear', done: false },
    ],
    energyLevel: 'light',
    kanbanColumn: 'todo',
  },
  {
    id: '8',
    title: 'Complete online JavaScript course — Module 5',
    priority: 'normal',
    category: 'study',
    tags: ['coding', 'javascript'],
    dueDate: in2Days,
    status: 'inprogress',
    isStarred: false,
    isCompleted: false,
    createdAt: threeDaysAgo + 'T14:00:00Z',
    estimatedDuration: 90,
    timeTracked: 30,
    subtasks: [
      { id: 's11', title: 'Watch lecture videos', done: true },
      { id: 's12', title: 'Complete exercises', done: false },
      { id: 's13', title: 'Take quiz', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'inprogress',
  },
  {
    id: '9',
    title: 'Doctor appointment — annual checkup',
    priority: 'high',
    category: 'health',
    tags: ['medical', 'checkup'],
    dueDate: in5Days,
    dueTime: '11:30',
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T09:00:00Z',
    estimatedDuration: 60,
    subtasks: [],
    energyLevel: 'low',
    kanbanColumn: 'todo',
    timeBlockStart: '11:30',
  },
  {
    id: '10',
    title: 'Update portfolio website',
    priority: 'normal',
    category: 'personal',
    tags: ['portfolio', 'design'],
    dueDate: in7Days,
    status: 'todo',
    isStarred: true,
    isCompleted: false,
    createdAt: twoDaysAgo + 'T16:00:00Z',
    estimatedDuration: 180,
    subtasks: [
      { id: 's14', title: 'Update project showcase', done: false },
      { id: 's15', title: 'Rewrite bio section', done: false },
      { id: 's16', title: 'Add new case studies', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'todo',
  },
  {
    id: '11',
    title: 'Team standup meeting',
    description: 'Daily sync with engineering team',
    priority: 'normal',
    category: 'work',
    tags: ['meeting', 'team'],
    dueDate: todayStr,
    dueTime: '09:30',
    status: 'done',
    isStarred: false,
    isCompleted: true,
    completedAt: todayStr + 'T09:45:00Z',
    createdAt: todayStr + 'T08:00:00Z',
    estimatedDuration: 15,
    timeTracked: 15,
    subtasks: [],
    energyLevel: 'low',
    kanbanColumn: 'done',
    timeBlockStart: '09:30',
  },
  {
    id: '12',
    title: 'Meditate for 10 minutes',
    priority: 'low',
    category: 'health',
    tags: ['mindfulness', 'wellness'],
    dueDate: yesterday,
    status: 'done',
    isStarred: false,
    isCompleted: true,
    completedAt: yesterday + 'T08:00:00Z',
    createdAt: yesterday + 'T07:00:00Z',
    estimatedDuration: 10,
    timeTracked: 10,
    subtasks: [],
    recurrence: 'daily',
    energyLevel: 'low',
    kanbanColumn: 'done',
  },
  {
    id: '13',
    title: 'Write blog post on React performance',
    priority: 'high',
    category: 'work',
    tags: ['writing', 'react', 'blog'],
    dueDate: in2Days,
    status: 'todo',
    isStarred: true,
    isCompleted: false,
    createdAt: twoDaysAgo + 'T13:00:00Z',
    estimatedDuration: 150,
    subtasks: [
      { id: 's17', title: 'Draft outline', done: true },
      { id: 's18', title: 'Write first draft', done: false },
      { id: 's19', title: 'Add code examples', done: false },
      { id: 's20', title: 'Proofread and publish', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'todo',
  },
  {
    id: '14',
    title: 'Set up investment portfolio review',
    priority: 'high',
    category: 'finance',
    tags: ['investing', 'finance'],
    dueDate: in3Days,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T12:00:00Z',
    estimatedDuration: 45,
    subtasks: [
      { id: 's21', title: 'Check stock performance', done: false },
      { id: 's22', title: 'Rebalance allocations', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'todo',
  },
  {
    id: '15',
    title: 'Call parents',
    priority: 'normal',
    category: 'personal',
    tags: ['family', 'call'],
    dueDate: todayStr,
    dueTime: '19:00',
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: todayStr + 'T08:00:00Z',
    estimatedDuration: 30,
    subtasks: [],
    recurrence: 'weekly',
    energyLevel: 'light',
    kanbanColumn: 'todo',
    timeBlockStart: '19:00',
  },
  {
    id: '16',
    title: 'Finish UI design for mobile app',
    priority: 'high',
    category: 'work',
    tags: ['design', 'mobile', 'ui'],
    dueDate: tomorrow,
    status: 'inprogress',
    isStarred: true,
    isCompleted: false,
    createdAt: threeDaysAgo + 'T09:00:00Z',
    estimatedDuration: 240,
    timeTracked: 120,
    subtasks: [
      { id: 's23', title: 'Onboarding screens', done: true },
      { id: 's24', title: 'Dashboard screens', done: true },
      { id: 's25', title: 'Settings screens', done: false },
      { id: 's26', title: 'Prototype testing', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'inprogress',
    timeBlockStart: '10:00',
  },
  {
    id: '17',
    title: 'Renew gym membership',
    priority: 'normal',
    category: 'health',
    tags: ['gym', 'membership'],
    dueDate: in5Days,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T10:00:00Z',
    estimatedDuration: 15,
    subtasks: [],
    energyLevel: 'low',
    kanbanColumn: 'todo',
  },
  {
    id: '18',
    title: 'Practice Spanish on Duolingo',
    priority: 'low',
    category: 'study',
    tags: ['language', 'spanish'],
    dueDate: todayStr,
    status: 'done',
    isStarred: false,
    isCompleted: true,
    completedAt: todayStr + 'T08:30:00Z',
    createdAt: todayStr + 'T08:00:00Z',
    estimatedDuration: 20,
    timeTracked: 20,
    subtasks: [],
    recurrence: 'daily',
    energyLevel: 'light',
    kanbanColumn: 'done',
  },
  {
    id: '19',
    title: 'Order birthday gift for Sarah',
    priority: 'high',
    category: 'shopping',
    tags: ['gift', 'birthday'],
    dueDate: tomorrow,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T16:00:00Z',
    estimatedDuration: 20,
    subtasks: [],
    energyLevel: 'light',
    kanbanColumn: 'todo',
  },
  {
    id: '20',
    title: 'Refactor authentication module',
    priority: 'normal',
    category: 'work',
    tags: ['code', 'refactor', 'auth'],
    dueDate: in7Days,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: twoDaysAgo + 'T11:00:00Z',
    estimatedDuration: 180,
    subtasks: [
      { id: 's27', title: 'Audit existing code', done: false },
      { id: 's28', title: 'Write new auth service', done: false },
      { id: 's29', title: 'Update tests', done: false },
    ],
    energyLevel: 'deep',
    kanbanColumn: 'todo',
  },
  {
    id: '21',
    title: 'Organize home office desk',
    priority: 'low',
    category: 'personal',
    tags: ['home', 'organize'],
    dueDate: in2Days,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: yesterday + 'T17:00:00Z',
    estimatedDuration: 30,
    subtasks: [],
    energyLevel: 'low',
    kanbanColumn: 'todo',
  },
  {
    id: '22',
    title: 'Submit expense report',
    priority: 'urgent',
    category: 'finance',
    tags: ['expenses', 'work'],
    dueDate: twoDaysAgo,
    status: 'todo',
    isStarred: false,
    isCompleted: false,
    createdAt: threeDaysAgo + 'T14:00:00Z',
    estimatedDuration: 30,
    subtasks: [
      { id: 's30', title: 'Collect receipts', done: true },
      { id: 's31', title: 'Fill expense form', done: false },
    ],
    energyLevel: 'low',
    kanbanColumn: 'todo',
  },
];

interface TaskStore {
  tasks: Task[];
  addTask: (task: Task) => void;
  updateTask: (id: string, updates: Partial<Task>) => void;
  deleteTask: (id: string) => void;
  toggleComplete: (id: string) => void;
  toggleStar: (id: string) => void;
  moveKanban: (id: string, column: 'todo' | 'inprogress' | 'done') => void;
  setTimeBlock: (id: string, start: string) => void;
  getTodayTasks: () => Task[];
  getOverdueTasks: () => Task[];
  getCompletedTasks: () => Task[];
  getStarredTasks: () => Task[];
  getTasksByDate: (date: string) => Task[];
  getTasksByCategory: (category: string) => Task[];
}

export const useTaskStore = create<TaskStore>((set, get) => ({
  tasks: SAMPLE_TASKS,

  addTask: (task) => set((state) => ({ tasks: [...state.tasks, task] })),

  updateTask: (id, updates) =>
    set((state) => ({
      tasks: state.tasks.map((t) => (t.id === id ? { ...t, ...updates } : t)),
    })),

  deleteTask: (id) =>
    set((state) => ({ tasks: state.tasks.filter((t) => t.id !== id) })),

  toggleComplete: (id) =>
    set((state) => ({
      tasks: state.tasks.map((t) =>
        t.id === id
          ? {
              ...t,
              isCompleted: !t.isCompleted,
              status: !t.isCompleted ? 'done' : 'todo',
              completedAt: !t.isCompleted ? new Date().toISOString() : undefined,
              kanbanColumn: !t.isCompleted ? 'done' : 'todo',
            }
          : t
      ),
    })),

  toggleStar: (id) =>
    set((state) => ({
      tasks: state.tasks.map((t) =>
        t.id === id ? { ...t, isStarred: !t.isStarred } : t
      ),
    })),

  moveKanban: (id, column) =>
    set((state) => ({
      tasks: state.tasks.map((t) => {
        if (t.id !== id) return t;
        const statusMap: Record<string, TaskStatus> = {
          todo: 'todo',
          inprogress: 'inprogress',
          done: 'done',
        };
        return {
          ...t,
          kanbanColumn: column,
          status: statusMap[column],
          isCompleted: column === 'done',
          completedAt: column === 'done' ? new Date().toISOString() : t.completedAt,
        };
      }),
    })),

  setTimeBlock: (id, start) =>
    set((state) => ({
      tasks: state.tasks.map((t) =>
        t.id === id ? { ...t, timeBlockStart: start } : t
      ),
    })),

  getTodayTasks: () => {
    const today = new Date().toISOString().split('T')[0];
    return get().tasks.filter((t) => t.dueDate === today && !t.isCompleted);
  },

  getOverdueTasks: () => {
    const today = new Date().toISOString().split('T')[0];
    return get().tasks.filter(
      (t) => t.dueDate && t.dueDate < today && !t.isCompleted
    );
  },

  getCompletedTasks: () => get().tasks.filter((t) => t.isCompleted),

  getStarredTasks: () => get().tasks.filter((t) => t.isStarred),

  getTasksByDate: (date) => get().tasks.filter((t) => t.dueDate === date),

  getTasksByCategory: (category) =>
    get().tasks.filter((t) => t.category === category),
}));
