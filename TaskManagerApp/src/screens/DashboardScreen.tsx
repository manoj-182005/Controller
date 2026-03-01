import React, { useRef } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  Animated,
  TouchableOpacity,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { TaskCard } from '../components/TaskCard';
import { Category } from '../types/task';

const QUOTES = [
  '"The secret of getting ahead is getting started." — Mark Twain',
  '"It always seems impossible until it\'s done." — Nelson Mandela',
  '"Focus on being productive instead of busy." — Tim Ferriss',
  '"You don\'t have to be great to start, but you have to start to be great." — Zig Ziglar',
  '"The way to get started is to quit talking and begin doing." — Walt Disney',
  '"Action is the foundational key to all success." — Pablo Picasso',
  '"Don\'t watch the clock; do what it does. Keep going." — Sam Levenson',
  '"Productivity is never an accident. It is always the result of a commitment." — Paul J. Meyer',
  '"If you spend too much time thinking about a thing, you\'ll never get it done." — Bruce Lee',
  '"Either you run the day or the day runs you." — Jim Rohn',
];

const CATEGORY_ORDER: Category[] = [
  'work', 'personal', 'study', 'health', 'shopping', 'finance', 'others',
];

export default function DashboardScreen() {
  const { tasks, toggleComplete, deleteTask } = useTaskStore();
  const pulseAnim = useRef(new Animated.Value(1)).current;

  React.useEffect(() => {
    const pulse = Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, { toValue: 1.25, duration: 700, useNativeDriver: true }),
        Animated.timing(pulseAnim, { toValue: 1, duration: 700, useNativeDriver: true }),
      ])
    );
    pulse.start();
    return () => pulse.stop();
  }, []);

  const now = new Date();
  const hour = now.getHours();
  const greeting =
    hour < 12 ? 'Good Morning' : hour < 18 ? 'Good Afternoon' : 'Good Evening';
  const dateStr = now.toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
  });
  const todayStr = now.toISOString().split('T')[0];

  const todayTasks = tasks.filter((t) => t.dueDate === todayStr && !t.isCompleted);
  const completedToday = tasks.filter(
    (t) => t.completedAt?.startsWith(todayStr)
  );
  const overdueTasks = tasks.filter(
    (t) => t.dueDate && t.dueDate < todayStr && !t.isCompleted
  );
  const topPriorityTasks = [...todayTasks]
    .sort((a, b) => {
      const order = { urgent: 0, high: 1, normal: 2, low: 3 };
      return order[a.priority] - order[b.priority];
    })
    .slice(0, 3);

  const quote = QUOTES[now.getDate() % QUOTES.length];

  // Weekly sparkline: last 7 days completed
  const weekBars = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(Date.now() - (6 - i) * 86400000).toISOString().split('T')[0];
    return tasks.filter((t) => t.completedAt?.startsWith(d)).length;
  });
  const maxBar = Math.max(...weekBars, 1);

  // Upcoming dates
  const upcomingDates = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(Date.now() + i * 86400000);
    const dStr = d.toISOString().split('T')[0];
    const count = tasks.filter((t) => t.dueDate === dStr && !t.isCompleted).length;
    return { date: dStr, label: d.toLocaleDateString('en-US', { weekday: 'short', day: 'numeric' }), count };
  });

  // Category distribution
  const catCounts = CATEGORY_ORDER.map((c) => ({
    cat: c,
    count: tasks.filter((t) => t.category === c).length,
    color: THEME.colors.category[c],
  }));
  const totalCatTasks = catCounts.reduce((s, c) => s + c.count, 0) || 1;

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}
    >
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.greeting}>{greeting} 👋</Text>
        <Text style={styles.date}>{dateStr}</Text>
      </View>

      {/* Stats Row */}
      <View style={styles.statsRow}>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.accent }]}>
          <Text style={styles.statValue}>{todayTasks.length}</Text>
          <Text style={styles.statLabel}>Today</Text>
        </View>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.success }]}>
          <Text style={styles.statValue}>{completedToday.length}</Text>
          <Text style={styles.statLabel}>Done</Text>
        </View>
        <View style={[styles.statCard, { borderTopColor: THEME.colors.error }]}>
          <Text style={styles.statValue}>{overdueTasks.length}</Text>
          <Text style={styles.statLabel}>Overdue</Text>
        </View>
      </View>

      {/* Streak */}
      <View style={styles.streakCard}>
        <Animated.Text style={[styles.flame, { transform: [{ scale: pulseAnim }] }]}>
          🔥
        </Animated.Text>
        <View>
          <Text style={styles.streakTitle}>5 Day Streak!</Text>
          <Text style={styles.streakSub}>Keep up the momentum</Text>
        </View>
      </View>

      {/* Weekly Sparkline */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Weekly Progress</Text>
        <View style={styles.sparkline}>
          {weekBars.map((val, i) => {
            const days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
            const dayIdx = (new Date(Date.now() - (6 - i) * 86400000).getDay());
            return (
              <View key={i} style={styles.sparklineCol}>
                <View style={styles.barContainer}>
                  <View
                    style={[
                      styles.bar,
                      {
                        height: Math.max((val / maxBar) * 48, 4),
                        backgroundColor:
                          i === 6 ? THEME.colors.accent : THEME.colors.accent + '66',
                      },
                    ]}
                  />
                </View>
                <Text style={styles.barLabel}>{days[dayIdx]}</Text>
                <Text style={styles.barValue}>{val}</Text>
              </View>
            );
          })}
        </View>
      </View>

      {/* Today's Focus */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Today's Focus 🎯</Text>
        {topPriorityTasks.length > 0 ? (
          topPriorityTasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              compact
              onComplete={toggleComplete}
              onDelete={deleteTask}
            />
          ))
        ) : (
          <View style={styles.emptyState}>
            <Text style={styles.emptyEmoji}>✅</Text>
            <Text style={styles.emptyText}>All tasks done for today!</Text>
          </View>
        )}
      </View>

      {/* Category Distribution */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Category Distribution</Text>
        <View style={styles.catBar}>
          {catCounts.map(({ cat, count, color }) => (
            <View
              key={cat}
              style={[
                styles.catSegment,
                { flex: count / totalCatTasks, backgroundColor: color },
              ]}
            />
          ))}
        </View>
        <View style={styles.catLegend}>
          {catCounts.map(({ cat, color }) => (
            <View key={cat} style={styles.catLegendItem}>
              <View style={[styles.catDot, { backgroundColor: color }]} />
              <Text style={styles.catLegendText}>
                {cat.charAt(0).toUpperCase() + cat.slice(1)}
              </Text>
            </View>
          ))}
        </View>
      </View>

      {/* Upcoming Deadlines */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Upcoming Deadlines</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          {upcomingDates.map(({ date, label, count }, i) => (
            <View
              key={date}
              style={[
                styles.dateCard,
                i === 0 && styles.dateCardToday,
              ]}
            >
              <Text style={[styles.dateLabel, i === 0 && styles.dateLabelToday]}>
                {i === 0 ? 'Today' : label}
              </Text>
              <Text style={[styles.dateCount, { color: count > 0 ? THEME.colors.accent : THEME.colors.text.muted }]}>
                {count}
              </Text>
              <Text style={styles.dateSub}>tasks</Text>
            </View>
          ))}
        </ScrollView>
      </View>

      {/* Quote */}
      <View style={styles.quoteCard}>
        <Text style={styles.quoteText}>{quote}</Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: THEME.colors.bg,
  },
  content: {
    padding: THEME.spacing.lg,
    paddingTop: 60,
    paddingBottom: 100,
  },
  header: {
    marginBottom: THEME.spacing.xl,
  },
  greeting: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  date: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    marginTop: 4,
  },
  statsRow: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
    marginBottom: THEME.spacing.lg,
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
  },
  statValue: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  statLabel: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
    marginTop: 2,
  },
  streakCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: 'rgba(249,115,22,0.12)',
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.lg,
    gap: THEME.spacing.md,
    borderWidth: 1,
    borderColor: 'rgba(249,115,22,0.3)',
    marginBottom: THEME.spacing.lg,
  },
  flame: {
    fontSize: 32,
  },
  streakTitle: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: '#F97316',
  },
  streakSub: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
  section: {
    marginBottom: THEME.spacing.xl,
  },
  sectionTitle: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.md,
  },
  sparkline: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  sparklineCol: {
    alignItems: 'center',
    flex: 1,
  },
  barContainer: {
    height: 48,
    justifyContent: 'flex-end',
  },
  bar: {
    width: 16,
    borderRadius: THEME.radius.sm,
  },
  barLabel: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    marginTop: 4,
  },
  barValue: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  emptyState: {
    alignItems: 'center',
    padding: THEME.spacing.xl,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  emptyEmoji: {
    fontSize: 32,
    marginBottom: THEME.spacing.sm,
  },
  emptyText: {
    color: THEME.colors.text.secondary,
    fontSize: THEME.typography.sizes.md,
  },
  catBar: {
    flexDirection: 'row',
    height: 12,
    borderRadius: THEME.radius.full,
    overflow: 'hidden',
    marginBottom: THEME.spacing.md,
  },
  catSegment: {
    height: '100%',
  },
  catLegend: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: THEME.spacing.sm,
  },
  catLegendItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  catDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  catLegendText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  dateCard: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.md,
    alignItems: 'center',
    marginRight: THEME.spacing.sm,
    minWidth: 64,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  dateCardToday: {
    borderColor: THEME.colors.accent,
    backgroundColor: THEME.colors.accent + '22',
  },
  dateLabel: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
    marginBottom: 4,
  },
  dateLabelToday: {
    color: THEME.colors.accent,
    fontWeight: THEME.typography.weights.bold,
  },
  dateCount: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
  },
  dateSub: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
  },
  quoteCard: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.xl,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderLeftWidth: 4,
    borderLeftColor: THEME.colors.accent,
  },
  quoteText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    fontStyle: 'italic',
    lineHeight: 20,
  },
});
