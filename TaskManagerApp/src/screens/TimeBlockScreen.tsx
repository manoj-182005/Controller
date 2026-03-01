import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { Task } from '../types/task';

const HOURS = Array.from({ length: 24 }, (_, i) => i);

const fmt12 = (hr: number, min: number = 0) => {
  const h = hr % 12 === 0 ? 12 : hr % 12;
  const ampm = hr < 12 ? 'AM' : 'PM';
  const mm = min.toString().padStart(2, '0');
  return `${h}:${mm} ${ampm}`;
};

const BLOCK_HEIGHT_PER_MIN = 1.6; // px per minute

function getSectionLabel(hr: number) {
  if (hr >= 5 && hr < 12) return 'Morning 🌅';
  if (hr >= 12 && hr < 17) return 'Afternoon ☀️';
  if (hr >= 17 && hr < 21) return 'Evening 🌆';
  return 'Night 🌙';
}

export default function TimeBlockScreen() {
  const { tasks, setTimeBlock, addTask } = useTaskStore();
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().split('T')[0]
  );

  const changeDate = (delta: number) => {
    const d = new Date(selectedDate + 'T00:00:00');
    d.setDate(d.getDate() + delta);
    setSelectedDate(d.toISOString().split('T')[0]);
  };

  const scheduledTasks = tasks.filter(
    (t) => t.dueDate === selectedDate && t.timeBlockStart
  );
  const unscheduledTasks = tasks.filter(
    (t) => t.dueDate === selectedDate && !t.timeBlockStart && !t.isCompleted
  );

  const getTasksAtHour = (hr: number): Task[] =>
    scheduledTasks.filter((t) => {
      const [h] = (t.timeBlockStart ?? '').split(':').map(Number);
      return h === hr;
    });

  const START_OF_WORKDAY = 9;
  const END_OF_WORKDAY = 18;

  const handleAutoSchedule = () => {
    let currentHr = START_OF_WORKDAY;
    let currentMin = 0;
    unscheduledTasks.forEach((task) => {
      const startStr = `${currentHr.toString().padStart(2, '0')}:${currentMin.toString().padStart(2, '0')}`;
      setTimeBlock(task.id, startStr);
      const dur = task.estimatedDuration ?? 30;
      currentMin += dur;
      while (currentMin >= 60) {
        currentMin -= 60;
        currentHr += 1;
      }
      if (currentHr >= END_OF_WORKDAY) currentHr = START_OF_WORKDAY; // wrap
    });
  };

  const formatDate = (d: string) =>
    new Date(d + 'T00:00:00').toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
    });

  let lastSection = '';

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Time Blocks</Text>
        <View style={styles.dateNav}>
          <TouchableOpacity style={styles.navBtn} onPress={() => changeDate(-1)}>
            <Text style={styles.navBtnText}>‹</Text>
          </TouchableOpacity>
          <Text style={styles.dateLabel}>{formatDate(selectedDate)}</Text>
          <TouchableOpacity style={styles.navBtn} onPress={() => changeDate(1)}>
            <Text style={styles.navBtnText}>›</Text>
          </TouchableOpacity>
        </View>
      </View>

      <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
        {/* Timeline */}
        <View style={styles.timeline}>
          {HOURS.map((hr) => {
            const section = getSectionLabel(hr);
            const showSection = section !== lastSection;
            if (showSection) lastSection = section;
            const hrTasks = getTasksAtHour(hr);

            return (
              <View key={hr}>
                {showSection && (
                  <View style={[styles.sectionHeader, { backgroundColor: getSectionBg(hr) }]}>
                    <Text style={styles.sectionLabel}>{section}</Text>
                  </View>
                )}
                <View style={styles.timeRow}>
                  <View style={styles.timeCol}>
                    <Text style={styles.timeText}>{fmt12(hr)}</Text>
                    <View style={styles.timeDot} />
                  </View>
                  <View style={styles.slotCol}>
                    <View style={styles.slotLine} />
                    {hrTasks.map((task) => {
                      const [, mm = '0'] = (task.timeBlockStart ?? '').split(':');
                      const topOffset = parseInt(mm) * BLOCK_HEIGHT_PER_MIN;
                      const height = Math.max((task.estimatedDuration ?? 30) * BLOCK_HEIGHT_PER_MIN, 24);
                      return (
                        <View
                          key={task.id}
                          style={[
                            styles.block,
                            {
                              top: topOffset,
                              height,
                              backgroundColor: THEME.colors.category[task.category] + '33',
                              borderLeftColor: THEME.colors.category[task.category],
                            },
                          ]}
                        >
                          <Text style={styles.blockTitle} numberOfLines={1}>{task.title}</Text>
                          <Text style={styles.blockTime}>
                            {fmt12(hr, parseInt(mm))}
                            {task.estimatedDuration ? ` · ${task.estimatedDuration}m` : ''}
                          </Text>
                        </View>
                      );
                    })}
                    {/* Half-hour line */}
                    <View style={[styles.halfLine, { top: 30 * BLOCK_HEIGHT_PER_MIN }]} />
                  </View>
                </View>
              </View>
            );
          })}
        </View>

        {/* Unscheduled Panel */}
        <View style={styles.unscheduledPanel}>
          <View style={styles.unscheduledHeader}>
            <Text style={styles.unscheduledTitle}>
              Unscheduled ({unscheduledTasks.length})
            </Text>
            {unscheduledTasks.length > 0 && (
              <TouchableOpacity style={styles.autoBtn} onPress={handleAutoSchedule}>
                <Text style={styles.autoBtnText}>⚡ Auto-Schedule</Text>
              </TouchableOpacity>
            )}
          </View>
          {unscheduledTasks.length === 0 ? (
            <View style={styles.emptyUnscheduled}>
              <Text style={styles.emptyText}>All tasks are scheduled! 🎉</Text>
            </View>
          ) : (
            unscheduledTasks.map((task) => (
              <View key={task.id} style={styles.unscheduledTask}>
                <View style={[styles.unscheduledDot, { backgroundColor: THEME.colors.category[task.category] }]} />
                <View style={styles.unscheduledInfo}>
                  <Text style={styles.unscheduledTaskTitle} numberOfLines={1}>
                    {task.title}
                  </Text>
                  <Text style={styles.unscheduledMeta}>
                    {task.estimatedDuration ? `⏱ ${task.estimatedDuration}m` : 'No duration'}
                    {' · '}
                    <Text style={{ color: THEME.colors.priority[task.priority] }}>
                      {task.priority}
                    </Text>
                  </Text>
                </View>
              </View>
            ))
          )}
        </View>

        <View style={{ height: 100 }} />
      </ScrollView>
    </View>
  );
}

function getSectionBg(hr: number) {
  if (hr >= 5 && hr < 12) return 'rgba(251,191,36,0.08)';
  if (hr >= 12 && hr < 17) return 'rgba(59,130,246,0.08)';
  if (hr >= 17 && hr < 21) return 'rgba(249,115,22,0.08)';
  return 'rgba(99,102,241,0.08)';
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: THEME.colors.bg },
  header: {
    paddingTop: 56,
    paddingHorizontal: THEME.spacing.lg,
    paddingBottom: THEME.spacing.md,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.md,
  },
  dateNav: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.md,
  },
  navBtn: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  navBtnText: {
    fontSize: THEME.typography.sizes.xl,
    color: THEME.colors.accent,
    lineHeight: 22,
  },
  dateLabel: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
    flex: 1,
    textAlign: 'center',
  },
  scroll: { flex: 1 },
  timeline: { paddingHorizontal: THEME.spacing.lg },
  sectionHeader: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: 4,
    borderRadius: THEME.radius.sm,
    marginVertical: 4,
  },
  sectionLabel: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.secondary,
  },
  timeRow: {
    flexDirection: 'row',
    minHeight: 48,
  },
  timeCol: {
    width: 56,
    paddingRight: THEME.spacing.sm,
    alignItems: 'flex-end',
    paddingTop: 4,
  },
  timeText: {
    fontSize: 10,
    color: THEME.colors.text.muted,
  },
  timeDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: THEME.colors.border,
    marginTop: 4,
    alignSelf: 'center',
  },
  slotCol: {
    flex: 1,
    minHeight: 48,
    position: 'relative',
    paddingBottom: 4,
  },
  slotLine: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 1,
    backgroundColor: THEME.colors.border + '60',
  },
  halfLine: {
    position: 'absolute',
    left: 0,
    right: 0,
    height: 1,
    backgroundColor: THEME.colors.border + '30',
  },
  block: {
    position: 'absolute',
    left: 4,
    right: 4,
    borderLeftWidth: 3,
    borderRadius: THEME.radius.sm,
    padding: THEME.spacing.xs,
    overflow: 'hidden',
  },
  blockTitle: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
  },
  blockTime: {
    fontSize: 9,
    color: THEME.colors.text.muted,
  },
  unscheduledPanel: {
    margin: THEME.spacing.lg,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  unscheduledHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.md,
  },
  unscheduledTitle: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  autoBtn: {
    backgroundColor: THEME.colors.accent + '33',
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.accent + '66',
  },
  autoBtnText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.accent,
    fontWeight: THEME.typography.weights.semibold,
  },
  emptyUnscheduled: { alignItems: 'center', paddingVertical: THEME.spacing.lg },
  emptyText: { color: THEME.colors.text.secondary, fontSize: THEME.typography.sizes.sm },
  unscheduledTask: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderBottomWidth: 1,
    borderBottomColor: THEME.colors.border + '40',
  },
  unscheduledDot: {
    width: 10,
    height: 10,
    borderRadius: 5,
  },
  unscheduledInfo: { flex: 1 },
  unscheduledTaskTitle: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.primary,
    fontWeight: THEME.typography.weights.medium,
  },
  unscheduledMeta: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    marginTop: 2,
  },
});
