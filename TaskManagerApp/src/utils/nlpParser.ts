import { Priority, Category, EnergyLevel } from '../types/task';

export interface ParsedTask {
  title: string;
  priority?: Priority;
  category?: Category;
  dueDate?: string; // YYYY-MM-DD
  dueTime?: string; // HH:MM
  estimatedDuration?: number; // minutes
  recurrence?: 'daily' | 'weekly' | 'monthly' | 'yearly';
  energyLevel?: EnergyLevel;
  tags: string[];
  confidence: Record<string, number>; // field -> 0-1 confidence
}

// ─── Helpers ────────────────────────────────────────────────────────────────

function nextWeekday(name: string): Date {
  const days: Record<string, number> = {
    sunday: 0, sun: 0,
    monday: 1, mon: 1,
    tuesday: 2, tue: 2,
    wednesday: 3, wed: 3,
    thursday: 4, thu: 4,
    friday: 5, fri: 5,
    saturday: 6, sat: 6,
  };
  const target = days[name.toLowerCase()];
  if (target === undefined) return new Date();
  const today = new Date();
  const diff = (target - today.getDay() + 7) % 7;
  const result = new Date(today);
  result.setDate(today.getDate() + (diff === 0 ? 7 : diff));
  return result;
}

function toDateStr(d: Date): string {
  return d.toISOString().split('T')[0];
}

function parseTime(raw: string): string | undefined {
  // Accepts: "3pm", "3:30pm", "15:00", "9am", "9:30 am"
  const m = raw.replace(/\s/g, '').toLowerCase().match(/^(\d{1,2})(?::(\d{2}))?(am|pm)?$/);
  if (!m) return undefined;
  let h = parseInt(m[1], 10);
  const min = parseInt(m[2] ?? '0', 10);
  const period = m[3];
  if (period === 'pm' && h !== 12) h += 12;
  if (period === 'am' && h === 12) h = 0;
  return `${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}`;
}

// ─── Main Parser ─────────────────────────────────────────────────────────────

export function parseNaturalLanguage(input: string): ParsedTask {
  let text = input.trim();
  const confidence: Record<string, number> = {};
  const tags: string[] = [];
  let priority: Priority | undefined;
  let category: Category | undefined;
  let dueDate: string | undefined;
  let dueTime: string | undefined;
  let estimatedDuration: number | undefined;
  let recurrence: ParsedTask['recurrence'];
  let energyLevel: EnergyLevel | undefined;

  // ── Priority ──────────────────────────────────────────────────────────────
  const priorityMap: [RegExp, Priority][] = [
    [/\b(urgent|asap|critical|immediately)\b/i, 'urgent'],
    [/\b(high[\s-]?priority|important|high)\b/i, 'high'],
    [/\b(low[\s-]?priority|low|minor|someday)\b/i, 'low'],
    [/\b(normal|medium|mid)\b/i, 'normal'],
  ];
  for (const [re, p] of priorityMap) {
    if (re.test(text)) {
      priority = p;
      confidence.priority = 0.9;
      text = text.replace(re, '').trim();
      break;
    }
  }

  // ── Category ─────────────────────────────────────────────────────────────
  const categoryMap: [RegExp, Category][] = [
    [/\b(work|office|meeting|report|project|sprint|standup|client|boss)\b/i, 'work'],
    [/\b(study|learn|course|homework|class|lecture|assignment|exam|quiz)\b/i, 'study'],
    [/\b(health|gym|workout|doctor|fitness|medical|medicine|run|yoga|exercise)\b/i, 'health'],
    [/\b(shop|shopping|groceries|buy|purchase|order|amazon|mall)\b/i, 'shopping'],
    [/\b(finance|bank|bill|tax|invest|expense|budget|payment|rent)\b/i, 'finance'],
    [/\b(personal|family|home|house|mom|dad|parents|friend|birthday|wedding)\b/i, 'personal'],
  ];
  for (const [re, c] of categoryMap) {
    if (re.test(text)) {
      category = c;
      confidence.category = 0.85;
      break;
    }
  }

  // ── Recurrence ────────────────────────────────────────────────────────────
  if (/\bevery\s+day\b|\bdaily\b/i.test(text)) {
    recurrence = 'daily';
    confidence.recurrence = 0.95;
    text = text.replace(/\bevery\s+day\b|\bdaily\b/i, '').trim();
  } else if (/\bevery\s+week\b|\bweekly\b/i.test(text)) {
    recurrence = 'weekly';
    confidence.recurrence = 0.9;
    text = text.replace(/\bevery\s+week\b|\bweekly\b/i, '').trim();
  } else if (/\bevery\s+month\b|\bmonthly\b/i.test(text)) {
    recurrence = 'monthly';
    confidence.recurrence = 0.9;
    text = text.replace(/\bevery\s+month\b|\bmonthly\b/i, '').trim();
  } else if (/\bevery\s+(sunday|monday|tuesday|wednesday|thursday|friday|saturday|sun|mon|tue|wed|thu|fri|sat)\b/i.test(text)) {
    recurrence = 'weekly';
    confidence.recurrence = 0.9;
  }

  // ── Due date ──────────────────────────────────────────────────────────────
  const today = new Date();
  const todayStr = toDateStr(today);

  if (/\btoday\b/i.test(text)) {
    dueDate = todayStr;
    confidence.dueDate = 0.95;
    text = text.replace(/\btoday\b/i, '').trim();
  } else if (/\btomorrow\b/i.test(text)) {
    const d = new Date(today);
    d.setDate(today.getDate() + 1);
    dueDate = toDateStr(d);
    confidence.dueDate = 0.95;
    text = text.replace(/\btomorrow\b/i, '').trim();
  } else if (/\bnext\s+week\b/i.test(text)) {
    const d = new Date(today);
    d.setDate(today.getDate() + 7);
    dueDate = toDateStr(d);
    confidence.dueDate = 0.8;
    text = text.replace(/\bnext\s+week\b/i, '').trim();
  } else {
    // "every Sunday" / "next Monday"
    const weekdayMatch = text.match(/\b(?:every|next)\s+(sunday|monday|tuesday|wednesday|thursday|friday|saturday|sun|mon|tue|wed|thu|fri|sat)\b/i);
    if (weekdayMatch) {
      dueDate = toDateStr(nextWeekday(weekdayMatch[1]));
      confidence.dueDate = 0.85;
      text = text.replace(weekdayMatch[0], '').trim();
    } else {
      const weekdayOnly = text.match(/\b(sunday|monday|tuesday|wednesday|thursday|friday|saturday)\b/i);
      if (weekdayOnly) {
        dueDate = toDateStr(nextWeekday(weekdayOnly[1]));
        confidence.dueDate = 0.75;
        text = text.replace(weekdayOnly[0], '').trim();
      }
    }
  }

  // ── Time ──────────────────────────────────────────────────────────────────
  // "at 3pm", "at 3:30 pm", "@ 9am", "10am to 11am" (range)
  const timeRangeMatch = text.match(/\bat\s+(\d{1,2}(?::\d{2})?\s*(?:am|pm))\s+to\s+(\d{1,2}(?::\d{2})?\s*(?:am|pm))\b/i);
  if (timeRangeMatch) {
    dueTime = parseTime(timeRangeMatch[1]);
    const endTime = parseTime(timeRangeMatch[2]);
    if (dueTime && endTime) {
      const [sh, sm] = dueTime.split(':').map(Number);
      const [eh, em] = endTime.split(':').map(Number);
      const startMinutes = sh * 60 + sm;
      const endMinutes = eh * 60 + em;
      // Only use duration for same-day ranges (end > start, max 12 hours)
      if (endMinutes > startMinutes && endMinutes - startMinutes <= 720) {
        estimatedDuration = endMinutes - startMinutes;
        confidence.estimatedDuration = 0.9;
      }
    }
    confidence.dueTime = 0.95;
    text = text.replace(timeRangeMatch[0], '').trim();
  } else {
    const timeMatch = text.match(/\b(?:at|@)\s*(\d{1,2}(?::\d{2})?\s*(?:am|pm)?)\b/i);
    if (timeMatch) {
      const t = parseTime(timeMatch[1]);
      if (t) {
        dueTime = t;
        confidence.dueTime = 0.9;
        text = text.replace(timeMatch[0], '').trim();
      }
    }
    // Standalone time like "3pm", "9:30am" (without "at")
    if (!dueTime) {
      const standaloneTime = text.match(/\b(\d{1,2}(?::\d{2})?\s*(?:am|pm))\b/i);
      if (standaloneTime) {
        const t = parseTime(standaloneTime[1]);
        if (t) {
          dueTime = t;
          confidence.dueTime = 0.8;
          text = text.replace(standaloneTime[0], '').trim();
        }
      }
    }
  }

  // ── Duration ──────────────────────────────────────────────────────────────
  // "30 min", "1 hour", "45min", "2h"
  const durationMatch = text.match(/\b(\d+)\s*(hour|hr|h)\b/i);
  if (durationMatch) {
    estimatedDuration = parseInt(durationMatch[1], 10) * 60;
    confidence.estimatedDuration = 0.9;
    text = text.replace(durationMatch[0], '').trim();
  }
  const minMatch = text.match(/\b(\d+)\s*(minutes?|mins?|m)\b/i);
  if (minMatch) {
    const mins = parseInt(minMatch[1], 10);
    estimatedDuration = estimatedDuration ? estimatedDuration + mins : mins;
    confidence.estimatedDuration = 0.9;
    text = text.replace(minMatch[0], '').trim();
  }

  // ── Energy Level ──────────────────────────────────────────────────────────
  if (/\b(deep\s*work|concentrate|intense)\b/i.test(text)) {
    energyLevel = 'deep';
    confidence.energyLevel = 0.8;
    text = text.replace(/\b(deep\s*work|concentrate|intense)\b/i, '').trim();
  } else if (/\b(light|easy|simple|quick)\b/i.test(text)) {
    energyLevel = 'light';
    confidence.energyLevel = 0.7;
    text = text.replace(/\b(light|easy|simple|quick)\b/i, '').trim();
  } else if (/\b(low energy|tired)\b/i.test(text)) {
    energyLevel = 'low';
    confidence.energyLevel = 0.7;
    text = text.replace(/\b(low energy|tired)\b/i, '').trim();
  }

  // ── Clean up extra punctuation / spaces ──────────────────────────────────
  const title = text.replace(/\s{2,}/g, ' ').replace(/^[-–,\s]+|[-–,\s]+$/g, '').trim() || input.trim();

  // If no category detected, infer from cleaned title
  if (!category) {
    for (const [re, c] of categoryMap) {
      if (re.test(title)) {
        category = c;
        confidence.category = 0.7;
        break;
      }
    }
  }

  return {
    title,
    priority,
    category,
    dueDate,
    dueTime,
    estimatedDuration,
    recurrence,
    energyLevel,
    tags,
    confidence,
  };
}

export function confidenceLabel(score: number): string {
  if (score >= 0.9) return 'high';
  if (score >= 0.7) return 'medium';
  return 'low';
}
