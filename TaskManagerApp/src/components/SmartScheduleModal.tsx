import React, { useState } from 'react';
import {
  Modal,
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Animated,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { buildSmartSchedule, ScheduleBlock } from '../utils/aiInsights';

interface SmartScheduleModalProps {
  visible: boolean;
  onClose: () => void;
}

const PRIORITY_COLORS: Record<string, string> = {
  low: THEME.colors.priority.low,
  normal: THEME.colors.priority.normal,
  high: THEME.colors.priority.high,
  urgent: THEME.colors.priority.urgent,
};

const ENERGY_ICONS: Record<string, string> = {
  deep: '⚡',
  light: '🌿',
  low: '😴',
};

function to12h(time: string): string {
  const [h, m] = time.split(':').map(Number);
  const period = h >= 12 ? 'PM' : 'AM';
  const h12 = h % 12 || 12;
  return `${h12}:${String(m).padStart(2, '0')} ${period}`;
}

export default function SmartScheduleModal({ visible, onClose }: SmartScheduleModalProps) {
  const slideAnim = React.useRef(new Animated.Value(600)).current;
  const { tasks, setTimeBlock } = useTaskStore();
  const [schedule, setSchedule] = useState<ScheduleBlock[]>([]);
  const [generated, setGenerated] = useState(false);

  React.useEffect(() => {
    if (visible) {
      setGenerated(false);
      Animated.spring(slideAnim, {
        toValue: 0,
        useNativeDriver: true,
        tension: 55,
        friction: 11,
      }).start();
    } else {
      Animated.timing(slideAnim, {
        toValue: 600,
        duration: 220,
        useNativeDriver: true,
      }).start();
    }
  }, [visible]);

  const handleGenerate = () => {
    const blocks = buildSmartSchedule(tasks);
    setSchedule(blocks);
    setGenerated(true);
  };

  const handleConfirm = () => {
    schedule.forEach((block) => {
      setTimeBlock(block.taskId, block.startTime);
    });
    onClose();
  };

  return (
    <Modal
      visible={visible}
      transparent
      animationType="none"
      onRequestClose={onClose}
      statusBarTranslucent
    >
      <TouchableOpacity style={styles.overlay} activeOpacity={1} onPress={onClose} />
      <Animated.View style={[styles.sheet, { transform: [{ translateY: slideAnim }] }]}>
        <View style={styles.handle} />
        <Text style={styles.title}>🗓 Schedule My Day</Text>
        <Text style={styles.subtitle}>
          AI analyzes your tasks by priority and energy level to build an optimal daily plan.
        </Text>

        {!generated ? (
          <View style={styles.generateSection}>
            <View style={styles.legendRow}>
              {[
                { icon: '🔴', label: 'Urgent first' },
                { icon: '⚡', label: 'Deep work AM' },
                { icon: '🌿', label: 'Light tasks PM' },
              ].map((item) => (
                <View key={item.label} style={styles.legendItem}>
                  <Text style={styles.legendIcon}>{item.icon}</Text>
                  <Text style={styles.legendText}>{item.label}</Text>
                </View>
              ))}
            </View>

            <TouchableOpacity style={styles.generateBtn} onPress={handleGenerate}>
              <Text style={styles.generateBtnText}>✨ Generate Schedule</Text>
            </TouchableOpacity>
          </View>
        ) : (
          <>
            {schedule.length === 0 ? (
              <View style={styles.emptyState}>
                <Text style={styles.emptyEmoji}>🎉</Text>
                <Text style={styles.emptyText}>No pending tasks to schedule!</Text>
              </View>
            ) : (
              <ScrollView style={styles.scheduleList} showsVerticalScrollIndicator={false}>
                {schedule.map((block, i) => (
                  <View key={block.taskId} style={styles.block}>
                    {/* Time column */}
                    <View style={styles.timeColumn}>
                      <Text style={styles.blockStart}>{to12h(block.startTime)}</Text>
                      <View style={[styles.timeLine, { backgroundColor: PRIORITY_COLORS[block.priority] }]} />
                      <Text style={styles.blockEnd}>{to12h(block.endTime)}</Text>
                    </View>

                    {/* Task info */}
                    <View
                      style={[
                        styles.blockCard,
                        { borderLeftColor: PRIORITY_COLORS[block.priority] },
                      ]}
                    >
                      <View style={styles.blockHeader}>
                        <Text style={styles.blockTitle} numberOfLines={2}>
                          {block.title}
                        </Text>
                        {block.energyLevel && (
                          <Text style={styles.energyIcon}>
                            {ENERGY_ICONS[block.energyLevel] ?? ''}
                          </Text>
                        )}
                      </View>
                      <View
                        style={[
                          styles.priorityPill,
                          { backgroundColor: PRIORITY_COLORS[block.priority] + '33' },
                        ]}
                      >
                        <Text
                          style={[
                            styles.priorityPillText,
                            { color: PRIORITY_COLORS[block.priority] },
                          ]}
                        >
                          {block.priority}
                        </Text>
                      </View>
                    </View>
                  </View>
                ))}
              </ScrollView>
            )}

            <View style={styles.actionRow}>
              <TouchableOpacity style={styles.regenBtn} onPress={handleGenerate}>
                <Text style={styles.regenBtnText}>↺ Regenerate</Text>
              </TouchableOpacity>
              {schedule.length > 0 && (
                <TouchableOpacity style={styles.confirmBtn} onPress={handleConfirm}>
                  <Text style={styles.confirmBtnText}>✓ Apply Schedule</Text>
                </TouchableOpacity>
              )}
            </View>
          </>
        )}
      </Animated.View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.65)',
  },
  sheet: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#131929',
    borderTopLeftRadius: THEME.radius.xl,
    borderTopRightRadius: THEME.radius.xl,
    padding: THEME.spacing.xxl,
    paddingBottom: 48,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderBottomWidth: 0,
    maxHeight: '85%',
  },
  handle: {
    width: 40,
    height: 4,
    backgroundColor: THEME.colors.border,
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: THEME.spacing.lg,
  },
  title: {
    fontSize: THEME.typography.sizes.xl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.sm,
  },
  subtitle: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    lineHeight: 20,
    marginBottom: THEME.spacing.xl,
  },
  generateSection: {
    gap: THEME.spacing.xl,
  },
  legendRow: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
    flexWrap: 'wrap',
  },
  legendItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.full,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  legendIcon: { fontSize: 14 },
  legendText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  generateBtn: {
    backgroundColor: THEME.colors.accent,
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.lg,
    alignItems: 'center',
  },
  generateBtnText: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: '#fff',
  },
  emptyState: {
    alignItems: 'center',
    paddingVertical: THEME.spacing.xxl,
  },
  emptyEmoji: { fontSize: 40, marginBottom: THEME.spacing.md },
  emptyText: {
    fontSize: THEME.typography.sizes.md,
    color: THEME.colors.text.secondary,
  },
  scheduleList: {
    maxHeight: 320,
    marginBottom: THEME.spacing.lg,
  },
  block: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
    marginBottom: THEME.spacing.sm,
    alignItems: 'stretch',
  },
  timeColumn: {
    width: 72,
    alignItems: 'center',
    gap: 3,
  },
  blockStart: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    textAlign: 'center',
  },
  timeLine: {
    flex: 1,
    width: 2,
    borderRadius: 1,
    minHeight: 16,
    opacity: 0.5,
  },
  blockEnd: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    textAlign: 'center',
  },
  blockCard: {
    flex: 1,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderLeftWidth: 3,
    gap: THEME.spacing.sm,
  },
  blockHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: THEME.spacing.sm,
  },
  blockTitle: {
    flex: 1,
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
    lineHeight: 20,
  },
  energyIcon: { fontSize: 14 },
  priorityPill: {
    alignSelf: 'flex-start',
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 2,
    borderRadius: THEME.radius.full,
  },
  priorityPillText: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    textTransform: 'capitalize',
  },
  actionRow: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
  },
  regenBtn: {
    flex: 1,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.md,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  regenBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    fontWeight: THEME.typography.weights.medium,
  },
  confirmBtn: {
    flex: 2,
    backgroundColor: THEME.colors.success,
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.md,
    alignItems: 'center',
  },
  confirmBtnText: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.bold,
    color: '#fff',
  },
});
