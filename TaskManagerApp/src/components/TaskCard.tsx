import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Animated,
} from 'react-native';
import { Task } from '../types/task';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';

interface TaskCardProps {
  task: Task;
  compact?: boolean;
  onComplete?: (id: string) => void;
  onDelete?: (id: string) => void;
  onPress?: (task: Task) => void;
}

const PRIORITY_LABELS: Record<string, string> = {
  low: 'Low',
  normal: 'Normal',
  high: 'High',
  urgent: 'Urgent',
};

export function TaskCard({
  task,
  compact = false,
  onComplete,
  onDelete,
  onPress,
}: TaskCardProps) {
  const scaleAnim = React.useRef(new Animated.Value(1)).current;
  const { activeTimerTaskId, startTimer, stopTimer } = useTaskStore();
  const isTimerActive = activeTimerTaskId === task.id;

  const priorityColor = THEME.colors.priority[task.priority];
  const categoryColor = THEME.colors.category[task.category];
  const today = new Date().toISOString().split('T')[0];
  const isOverdue = task.dueDate && task.dueDate < today && !task.isCompleted;

  const handlePressIn = () => {
    Animated.spring(scaleAnim, {
      toValue: 0.97,
      useNativeDriver: true,
    }).start();
  };
  const handlePressOut = () => {
    Animated.spring(scaleAnim, {
      toValue: 1,
      useNativeDriver: true,
    }).start();
  };

  const formatDate = (date?: string) => {
    if (!date) return null;
    const d = new Date(date + 'T00:00:00');
    const now = new Date();
    const diffDays = Math.round(
      (d.getTime() - now.setHours(0, 0, 0, 0)) / 86400000
    );
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Tomorrow';
    if (diffDays === -1) return 'Yesterday';
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  const formatTracked = (minutes?: number) => {
    if (!minutes) return null;
    if (minutes < 60) return `${minutes}m`;
    return `${Math.floor(minutes / 60)}h ${minutes % 60}m`;
  };

  const handleTimerToggle = () => {
    if (isTimerActive) {
      stopTimer(task.id);
    } else {
      startTimer(task.id);
    }
  };

  return (
    <Animated.View style={{ transform: [{ scale: scaleAnim }] }}>
      <TouchableOpacity
        activeOpacity={0.85}
        onPress={() => onPress?.(task)}
        onPressIn={handlePressIn}
        onPressOut={handlePressOut}
        style={[
          styles.card,
          isOverdue && styles.overdueCard,
          task.isCompleted && styles.completedCard,
          isTimerActive && styles.timerActiveCard,
          { borderLeftColor: isOverdue ? THEME.colors.error : categoryColor },
        ]}
      >
        {/* Left color bar */}
        <View style={[styles.colorBar, { backgroundColor: isTimerActive ? THEME.colors.warning : categoryColor }]} />

        <View style={styles.content}>
          <View style={styles.topRow}>
            {/* Title */}
            <Text
              style={[
                styles.title,
                compact && styles.titleCompact,
                task.isCompleted && styles.titleCompleted,
              ]}
              numberOfLines={compact ? 1 : 2}
            >
              {task.title}
            </Text>

            {/* Star + Timer */}
            <View style={styles.topIcons}>
              {!compact && !task.isCompleted && (
                <TouchableOpacity
                  style={[styles.timerBtn, isTimerActive && styles.timerBtnActive]}
                  onPress={handleTimerToggle}
                  hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}
                >
                  <Text style={styles.timerBtnText}>{isTimerActive ? '⏹' : '▶'}</Text>
                </TouchableOpacity>
              )}
              {task.isStarred && (
                <Text style={styles.star}>⭐</Text>
              )}
            </View>
          </View>

          <View style={styles.bottomRow}>
            {/* Priority badge */}
            <View
              style={[styles.priorityBadge, { backgroundColor: priorityColor + '33' }]}
            >
              <View style={[styles.priorityDot, { backgroundColor: priorityColor }]} />
              <Text style={[styles.priorityText, { color: priorityColor }]}>
                {PRIORITY_LABELS[task.priority]}
              </Text>
            </View>

            {/* Due date */}
            {task.dueDate && (
              <Text style={[styles.dueDate, isOverdue && styles.dueDateOverdue]}>
                📅 {formatDate(task.dueDate)}
              </Text>
            )}

            {/* Time tracked */}
            {task.timeTracked && task.timeTracked > 0 && (
              <Text style={[styles.timeTracked, isTimerActive && styles.timeTrackedActive]}>
                ⏱ {formatTracked(task.timeTracked)}
              </Text>
            )}

            {/* Tags */}
            {!compact && task.tags.slice(0, 2).map((tag) => (
              <View key={tag} style={styles.tag}>
                <Text style={styles.tagText}>#{tag}</Text>
              </View>
            ))}
          </View>

          {/* Active timer indicator */}
          {isTimerActive && (
            <View style={styles.timerIndicator}>
              <Text style={styles.timerIndicatorText}>● Timer running — tap ⏹ to stop</Text>
            </View>
          )}

          {/* Actions */}
          {(onComplete || onDelete) && !compact && (
            <View style={styles.actions}>
              {onComplete && (
                <TouchableOpacity
                  style={[styles.actionBtn, styles.completeBtn]}
                  onPress={() => onComplete(task.id)}
                >
                  <Text style={styles.actionBtnText}>
                    {task.isCompleted ? '↩ Undo' : '✓ Done'}
                  </Text>
                </TouchableOpacity>
              )}
              {onDelete && (
                <TouchableOpacity
                  style={[styles.actionBtn, styles.deleteBtn]}
                  onPress={() => onDelete(task.id)}
                >
                  <Text style={styles.actionBtnText}>🗑</Text>
                </TouchableOpacity>
              )}
            </View>
          )}
        </View>
      </TouchableOpacity>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    marginBottom: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderLeftWidth: 3,
    overflow: 'hidden',
  },
  overdueCard: {
    borderColor: THEME.colors.error + '40',
  },
  completedCard: {
    opacity: 0.6,
  },
  timerActiveCard: {
    borderColor: THEME.colors.warning + '60',
    backgroundColor: 'rgba(245,158,11,0.06)',
  },
  colorBar: {
    width: 3,
  },
  content: {
    flex: 1,
    padding: THEME.spacing.md,
    gap: THEME.spacing.sm,
  },
  topRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
  },
  title: {
    flex: 1,
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
    lineHeight: 22,
  },
  titleCompact: {
    fontSize: THEME.typography.sizes.sm,
  },
  titleCompleted: {
    textDecorationLine: 'line-through',
    color: THEME.colors.text.muted,
  },
  topIcons: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.xs,
    marginLeft: THEME.spacing.xs,
  },
  timerBtn: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  timerBtnActive: {
    backgroundColor: THEME.colors.warning + '33',
    borderColor: THEME.colors.warning,
  },
  timerBtnText: {
    fontSize: 11,
  },
  star: {
    fontSize: 14,
  },
  bottomRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: THEME.spacing.xs,
  },
  priorityBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 3,
    borderRadius: THEME.radius.full,
    gap: 4,
  },
  priorityDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
  },
  priorityText: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
  },
  dueDate: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  dueDateOverdue: {
    color: THEME.colors.error,
  },
  timeTracked: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
  },
  timeTrackedActive: {
    color: THEME.colors.warning,
  },
  timerIndicator: {
    backgroundColor: THEME.colors.warning + '22',
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 3,
  },
  timerIndicatorText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.warning,
  },
  tag: {
    backgroundColor: 'rgba(255,255,255,0.06)',
    paddingHorizontal: THEME.spacing.xs,
    paddingVertical: 2,
    borderRadius: THEME.radius.sm,
  },
  tagText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
  },
  actions: {
    flexDirection: 'row',
    gap: THEME.spacing.sm,
    marginTop: THEME.spacing.xs,
  },
  actionBtn: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.xs,
    borderRadius: THEME.radius.sm,
  },
  completeBtn: {
    backgroundColor: THEME.colors.success + '33',
  },
  deleteBtn: {
    backgroundColor: THEME.colors.error + '33',
  },
  actionBtnText: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
  },
});
