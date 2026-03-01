import { Task, Priority } from '../types/task';

export interface AIInsight {
  id: string;
  type: 'priority_upgrade' | 'duplicate' | 'overdue_risk' | 'habit' | 'burnout' | 'peak_hours';
  icon: string;
  title: string;
  message: string;
  taskId?: string;
  actionLabel?: string;
}

// ─── Productivity Score ───────────────────────────────────────────────────────
export function calcProductivityScore(tasks: Task[]): number {
  if (tasks.length === 0) return 0;
  const total = tasks.length;
  const completed = tasks.filter((t) => t.isCompleted).length;
  const onTime = tasks.filter(
    (t) =>
      t.isCompleted &&
      (!t.dueDate ||
        (t.completedAt?.split('T')[0] ?? '') <= (t.dueDate ?? ''))
  ).length;

  const completionRate = completed / total;
  const onTimeRate = total > 0 ? onTime / total : 0;

  // Streak bonus
  let streak = 0;
  for (let i = 0; i < 30; i++) {
    const d = new Date(Date.now() - i * 86400000).toISOString().split('T')[0];
    if (tasks.some((t) => t.completedAt?.startsWith(d))) streak++;
    else break;
  }
  const streakBonus = Math.min(streak / 14, 1) * 0.2;

  const score = completionRate * 0.4 + onTimeRate * 0.4 + streakBonus;
  return Math.round(score * 100);
}

// ─── Peak Hour Detection ──────────────────────────────────────────────────────
export function getPeakHour(tasks: Task[]): number | null {
  const hourCounts: Record<number, number> = {};
  for (const t of tasks) {
    if (!t.completedAt) continue;
    const h = new Date(t.completedAt).getHours();
    hourCounts[h] = (hourCounts[h] ?? 0) + 1;
  }
  const entries = Object.entries(hourCounts);
  if (entries.length === 0) return null;
  const best = entries.sort((a, b) => Number(b[1]) - Number(a[1]))[0];
  return parseInt(best[0], 10);
}

// ─── Insights ────────────────────────────────────────────────────────────────
export function generateInsights(tasks: Task[]): AIInsight[] {
  const insights: AIInsight[] = [];
  const today = new Date().toISOString().split('T')[0];

  // 1. Priority upgrade suggestions (pending > 5 days)
  tasks
    .filter((t) => !t.isCompleted && t.priority === 'normal')
    .forEach((t) => {
      const age = Math.floor(
        (Date.now() - new Date(t.createdAt).getTime()) / 86400000
      );
      if (age >= 5) {
        insights.push({
          id: `priority_${t.id}`,
          type: 'priority_upgrade',
          icon: '⬆️',
          title: 'Priority Upgrade?',
          message: `"${t.title}" has been pending for ${age} days. Consider upgrading to High priority.`,
          taskId: t.id,
          actionLabel: 'Upgrade',
        });
      }
    });

  // 2. Duplicate detection (same first 4 words)
  const seen: Record<string, string> = {};
  tasks
    .filter((t) => !t.isCompleted)
    .forEach((t) => {
      const key = t.title.toLowerCase().split(' ').slice(0, 4).join(' ');
      if (seen[key]) {
        insights.push({
          id: `dup_${t.id}`,
          type: 'duplicate',
          icon: '⚠️',
          title: 'Possible Duplicate',
          message: `"${t.title}" looks similar to another task. Review and merge if needed.`,
          taskId: t.id,
          actionLabel: 'Review',
        });
      } else {
        seen[key] = t.id;
      }
    });

  // 3. Overdue risk: tasks due today with high/urgent priority not started
  tasks
    .filter(
      (t) =>
        !t.isCompleted &&
        t.dueDate === today &&
        (t.priority === 'high' || t.priority === 'urgent') &&
        t.status === 'todo'
    )
    .forEach((t) => {
      insights.push({
        id: `overdue_risk_${t.id}`,
        type: 'overdue_risk',
        icon: '🚨',
        title: 'At Risk Today',
        message: `"${t.title}" is due today and hasn't been started yet.`,
        taskId: t.id,
        actionLabel: 'Start Now',
      });
    });

  // 4. Burnout warning: > 8 tasks due today
  const todayTasks = tasks.filter((t) => t.dueDate === today && !t.isCompleted);
  if (todayTasks.length > 8) {
    insights.push({
      id: 'burnout_warning',
      type: 'burnout',
      icon: '😓',
      title: 'Heavy Load Today',
      message: `You have ${todayTasks.length} tasks due today. Consider rescheduling some to avoid burnout.`,
      actionLabel: 'Schedule Day',
    });
  }

  // 5. Habit suggestion: inconsistent health/study tasks
  const categoryDays: Record<string, Set<string>> = {};
  tasks
    .filter((t) => t.isCompleted && (t.category === 'health' || t.category === 'study'))
    .forEach((t) => {
      const d = t.completedAt?.split('T')[0];
      if (!d) return;
      if (!categoryDays[t.category]) categoryDays[t.category] = new Set();
      categoryDays[t.category].add(d);
    });

  ['health', 'study'].forEach((cat) => {
    const days = categoryDays[cat]?.size ?? 0;
    if (days > 0 && days < 3) {
      insights.push({
        id: `habit_${cat}`,
        type: 'habit',
        icon: '💪',
        title: 'Build a Habit',
        message: `You've only completed ${cat} tasks on ${days} day(s) this period. Want to set a daily habit?`,
        actionLabel: 'Set Habit',
      });
    }
  });

  // 6. Peak performance insight
  const peakHour = getPeakHour(tasks);
  if (peakHour !== null) {
    const period = peakHour < 12 ? 'morning' : peakHour < 17 ? 'afternoon' : 'evening';
    const timeStr = peakHour <= 12 ? `${peakHour}am` : `${peakHour - 12}pm`;
    insights.push({
      id: 'peak_hours',
      type: 'peak_hours',
      icon: '⚡',
      title: 'Your Peak Time',
      message: `You complete the most tasks in the ${period} around ${timeStr}. Schedule deep work then for best results.`,
    });
  }

  return insights.slice(0, 5); // cap at 5
}

// ─── Smart Schedule ───────────────────────────────────────────────────────────
export interface ScheduleBlock {
  taskId: string;
  title: string;
  startTime: string; // HH:MM
  endTime: string;   // HH:MM
  priority: Priority;
  energyLevel?: string;
}

export function buildSmartSchedule(
  tasks: Task[],
  workStart = 9,
  workEnd = 18
): ScheduleBlock[] {
  const pending = tasks
    .filter((t) => !t.isCompleted && t.dueDate)
    .sort((a, b) => {
      const pOrder: Record<string, number> = { urgent: 0, high: 1, normal: 2, low: 3 };
      const eOrder: Record<string, number> = { deep: 0, light: 1, low: 2 };
      const pDiff = (pOrder[a.priority] ?? 2) - (pOrder[b.priority] ?? 2);
      if (pDiff !== 0) return pDiff;
      return (eOrder[a.energyLevel ?? 'light'] ?? 1) - (eOrder[b.energyLevel ?? 'light'] ?? 1);
    })
    .slice(0, 8);

  const blocks: ScheduleBlock[] = [];
  let cursor = workStart * 60; // minutes from midnight
  const endMinutes = workEnd * 60;

  for (const task of pending) {
    const duration = task.estimatedDuration ?? 30;
    if (cursor + duration > endMinutes) break;

    const startH = Math.floor(cursor / 60);
    const startM = cursor % 60;
    const endCursor = cursor + duration;
    const endH = Math.floor(endCursor / 60);
    const endM = endCursor % 60;

    blocks.push({
      taskId: task.id,
      title: task.title,
      startTime: `${String(startH).padStart(2, '0')}:${String(startM).padStart(2, '0')}`,
      endTime: `${String(endH).padStart(2, '0')}:${String(endM).padStart(2, '0')}`,
      priority: task.priority,
      energyLevel: task.energyLevel,
    });

    cursor = endCursor + 10; // 10-min buffer between tasks
  }

  return blocks;
}
