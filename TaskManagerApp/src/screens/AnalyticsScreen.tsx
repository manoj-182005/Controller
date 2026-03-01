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
import { Category } from '../types/task';

type Range = 'Week' | 'Month' | 'Year';

const CATEGORY_LIST: Category[] = [
  'work', 'personal', 'study', 'health', 'shopping', 'finance', 'others',
];

const DAYS_OF_WEEK = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export default function AnalyticsScreen() {
  const { tasks } = useTaskStore();
  const [range, setRange] = useState<Range>('Week');

  const completed = tasks.filter((t) => t.isCompleted);
  const total = tasks.length;

  // Completion bars — last 7 days
  const getWeekData = () =>
    Array.from({ length: 7 }, (_, i) => {
      const d = new Date(Date.now() - (6 - i) * 86400000);
      const dStr = d.toISOString().split('T')[0];
      return {
        label: DAYS_OF_WEEK[d.getDay()],
        count: tasks.filter((t) => t.completedAt?.startsWith(dStr)).length,
      };
    });

  const getMonthData = () =>
    Array.from({ length: 4 }, (_, i) => {
      const start = new Date(Date.now() - (3 - i) * 7 * 86400000);
      const end = new Date(Date.now() - (2 - i) * 7 * 86400000);
      const count = tasks.filter((t) => {
        if (!t.completedAt) return false;
        const d = new Date(t.completedAt);
        return d >= start && d < end;
      }).length;
      return { label: `W${i + 1}`, count };
    });

  const getYearData = () =>
    MONTHS.map((label, i) => ({
      label,
      count: tasks.filter((t) => {
        if (!t.completedAt) return false;
        return new Date(t.completedAt).getMonth() === i;
      }).length,
    }));

  const chartData =
    range === 'Week' ? getWeekData() : range === 'Month' ? getMonthData() : getYearData();
  const maxCount = Math.max(...chartData.map((d) => d.count), 1);

  // Stats
  const onTimeRate = total > 0
    ? Math.round((completed.filter((t) => !t.dueDate || (t.completedAt?.split('T')[0] ?? '') <= (t.dueDate ?? '')).length / total) * 100)
    : 0;
  const totalTracked = tasks.reduce((s, t) => s + (t.timeTracked ?? 0), 0);
  const avgCompletionTime = completed.length > 0
    ? Math.round(completed.reduce((s, t) => s + (t.estimatedDuration ?? 30), 0) / completed.length)
    : 0;

  // Most productive day
  const dayProductive = DAYS_OF_WEEK.map((label, i) => ({
    label,
    count: tasks.filter((t) => t.completedAt && new Date(t.completedAt).getDay() === i).length,
  }));
  const maxDayCount = Math.max(...dayProductive.map((d) => d.count), 1);

  // Category breakdown
  const catBreakdown = CATEGORY_LIST.map((c) => ({
    cat: c,
    count: tasks.filter((t) => t.category === c).length,
    color: THEME.colors.category[c],
  }));
  const catTotal = catBreakdown.reduce((s, c) => s + c.count, 0) || 1;

  // Streak calendar: last 28 days (4x7)
  const streakDays = Array.from({ length: 28 }, (_, i) => {
    const d = new Date(Date.now() - (27 - i) * 86400000).toISOString().split('T')[0];
    const count = tasks.filter((t) => t.completedAt?.startsWith(d)).length;
    return { date: d, count };
  });
  const maxStreak = Math.max(...streakDays.map((d) => d.count), 1);

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}
    >
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Analytics</Text>
        <View style={styles.rangeToggle}>
          {(['Week', 'Month', 'Year'] as Range[]).map((r) => (
            <TouchableOpacity
              key={r}
              style={[styles.rangeBtn, range === r && styles.rangeBtnActive]}
              onPress={() => setRange(r)}
            >
              <Text style={[styles.rangeBtnText, range === r && styles.rangeBtnTextActive]}>
                {r}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Stats Cards */}
      <View style={styles.statsGrid}>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.success }]}>
          <Text style={styles.statEmoji}>✅</Text>
          <Text style={styles.statValue}>{onTimeRate}%</Text>
          <Text style={styles.statLabel}>On-time Rate</Text>
        </View>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.accent }]}>
          <Text style={styles.statEmoji}>⏱</Text>
          <Text style={styles.statValue}>{avgCompletionTime}m</Text>
          <Text style={styles.statLabel}>Avg Duration</Text>
        </View>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.warning }]}>
          <Text style={styles.statEmoji}>🎯</Text>
          <Text style={styles.statValue}>{Math.floor(totalTracked / 60)}h</Text>
          <Text style={styles.statLabel}>Focus Time</Text>
        </View>
      </View>

      {/* Completion Trend */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Completion Trend</Text>
        <View style={styles.barChart}>
          {chartData.map(({ label, count }, i) => (
            <View key={i} style={styles.barCol}>
              <Text style={styles.barCountText}>{count > 0 ? count : ''}</Text>
              <View style={styles.barTrack}>
                <View
                  style={[
                    styles.barFill,
                    {
                      height: `${(count / maxCount) * 100}%`,
                      backgroundColor: i === chartData.length - 1 ? THEME.colors.accent : THEME.colors.accent + '66',
                    },
                  ]}
                />
              </View>
              <Text style={styles.barAxisLabel}>{label}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* Most Productive Day */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Most Productive Day</Text>
        <View style={styles.barChart}>
          {dayProductive.map(({ label, count }, i) => (
            <View key={i} style={styles.barCol}>
              <View style={styles.barTrack}>
                <View
                  style={[
                    styles.barFill,
                    {
                      height: `${(count / maxDayCount) * 100}%`,
                      backgroundColor: THEME.colors.success + '99',
                    },
                  ]}
                />
              </View>
              <Text style={styles.barAxisLabel}>{label}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* Category Breakdown */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Category Breakdown</Text>
        <View style={styles.catBar}>
          {catBreakdown.map(({ cat, count, color }) => (
            <View
              key={cat}
              style={[styles.catSegment, { flex: count / catTotal, backgroundColor: color }]}
            />
          ))}
        </View>
        {catBreakdown.map(({ cat, count, color }) => (
          <View key={cat} style={styles.catRow}>
            <View style={styles.catRowLeft}>
              <View style={[styles.catDot, { backgroundColor: color }]} />
              <Text style={styles.catName}>{cat.charAt(0).toUpperCase() + cat.slice(1)}</Text>
            </View>
            <View style={styles.catBarInline}>
              <View
                style={[
                  styles.catBarFill,
                  { width: `${(count / catTotal) * 100}%`, backgroundColor: color },
                ]}
              />
            </View>
            <Text style={styles.catCount}>{count}</Text>
          </View>
        ))}
      </View>

      {/* Streak Calendar */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Activity Calendar 🔥</Text>
        {[0, 1, 2, 3].map((row) => (
          <View key={row} style={styles.streakRow}>
            {streakDays.slice(row * 7, row * 7 + 7).map(({ date, count }) => {
              const opacity = count === 0 ? 0.08 : 0.2 + (count / maxStreak) * 0.8;
              return (
                <View
                  key={date}
                  style={[
                    styles.streakCell,
                    {
                      backgroundColor: count === 0 ? THEME.colors.surface : THEME.colors.accent,
                      opacity: count === 0 ? 1 : opacity,
                      borderColor: count > 0 ? THEME.colors.accent + '40' : THEME.colors.border,
                    },
                  ]}
                />
              );
            })}
          </View>
        ))}
        <View style={styles.streakLegend}>
          <Text style={styles.streakLegendText}>Less</Text>
          {[0, 0.3, 0.6, 1].map((op, i) => (
            <View
              key={i}
              style={[
                styles.streakLegendCell,
                {
                  backgroundColor: op === 0 ? THEME.colors.surface : THEME.colors.accent,
                  opacity: op === 0 ? 1 : 0.2 + op * 0.8,
                },
              ]}
            />
          ))}
          <Text style={styles.streakLegendText}>More</Text>
        </View>
      </View>

      <View style={{ height: 100 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: THEME.colors.bg },
  content: { padding: THEME.spacing.lg, paddingTop: 56 },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.xl,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  rangeToggle: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: 3,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  rangeBtn: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.xs,
    borderRadius: THEME.radius.sm,
  },
  rangeBtnActive: { backgroundColor: THEME.colors.accent },
  rangeBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
  rangeBtnTextActive: {
    color: '#fff',
    fontWeight: THEME.typography.weights.semibold,
  },
  statsGrid: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
    marginBottom: THEME.spacing.xl,
  },
  statCard: {
    flex: 1,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.md,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderTopWidth: 3,
    gap: 4,
  },
  statEmoji: { fontSize: 20 },
  statValue: {
    fontSize: THEME.typography.sizes.xl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  statLabel: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
    textAlign: 'center',
  },
  section: {
    marginBottom: THEME.spacing.xxl,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  sectionTitle: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.lg,
  },
  barChart: {
    flexDirection: 'row',
    height: 100,
    alignItems: 'flex-end',
    gap: 4,
  },
  barCol: {
    flex: 1,
    alignItems: 'center',
    height: '100%',
    justifyContent: 'flex-end',
  },
  barCountText: {
    fontSize: 9,
    color: THEME.colors.text.muted,
    marginBottom: 2,
  },
  barTrack: {
    width: '100%',
    flex: 1,
    justifyContent: 'flex-end',
    borderRadius: THEME.radius.sm,
    overflow: 'hidden',
    backgroundColor: 'rgba(255,255,255,0.04)',
  },
  barFill: {
    width: '100%',
    borderRadius: THEME.radius.sm,
    minHeight: 4,
  },
  barAxisLabel: {
    fontSize: 9,
    color: THEME.colors.text.muted,
    marginTop: 4,
    textAlign: 'center',
  },
  catBar: {
    flexDirection: 'row',
    height: 10,
    borderRadius: THEME.radius.full,
    overflow: 'hidden',
    marginBottom: THEME.spacing.md,
  },
  catSegment: { height: '100%' },
  catRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: THEME.spacing.sm,
    gap: THEME.spacing.sm,
  },
  catRowLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    width: 80,
  },
  catDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  catName: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  catBarInline: {
    flex: 1,
    height: 6,
    backgroundColor: 'rgba(255,255,255,0.06)',
    borderRadius: THEME.radius.full,
    overflow: 'hidden',
  },
  catBarFill: {
    height: '100%',
    borderRadius: THEME.radius.full,
    minWidth: 4,
  },
  catCount: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    width: 20,
    textAlign: 'right',
  },
  streakRow: {
    flexDirection: 'row',
    gap: 3,
    marginBottom: 3,
  },
  streakCell: {
    flex: 1,
    aspectRatio: 1,
    borderRadius: 3,
    borderWidth: 1,
  },
  streakLegend: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
    marginTop: THEME.spacing.sm,
    gap: 3,
  },
  streakLegendText: {
    fontSize: 10,
    color: THEME.colors.text.muted,
  },
  streakLegendCell: {
    width: 12,
    height: 12,
    borderRadius: 2,
    backgroundColor: THEME.colors.accent,
  },
});
