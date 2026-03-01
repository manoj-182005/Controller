import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
} from 'react-native';
import { THEME } from '../theme/tokens';

interface ViewsScreenProps {
  navigation: {
    navigate: (screen: string) => void;
  };
}

const VIEW_OPTIONS = [
  {
    id: 'list',
    title: 'List View',
    subtitle: 'Manage tasks with grouping & sorting',
    emoji: '📋',
    color: THEME.colors.accent,
    screen: 'ListView',
  },
  {
    id: 'kanban',
    title: 'Kanban Board',
    subtitle: 'Visual columns: To Do → In Progress → Done',
    emoji: '🗂',
    color: '#3B82F6',
    screen: 'Kanban',
  },
  {
    id: 'timeblock',
    title: 'Time Blocks',
    subtitle: '24-hour timeline planner with auto-schedule',
    emoji: '⏱',
    color: '#10B981',
    screen: 'TimeBlock',
  },
  {
    id: 'focus',
    title: 'Focus Mode',
    subtitle: 'Pomodoro timer with customizable sessions & session logging',
    emoji: '🎯',
    color: '#F59E0B',
    screen: 'Focus',
  },
  {
    id: 'templates',
    title: 'Templates',
    subtitle: 'Apply pre-built task sets for common workflows',
    emoji: '📄',
    color: '#8B5CF6',
    screen: 'Templates',
  },
];

export default function ViewsScreen({ navigation }: ViewsScreenProps) {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Views</Text>
        <Text style={styles.headerSubtitle}>Choose your preferred task view</Text>
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {VIEW_OPTIONS.map((opt) => (
          <TouchableOpacity
            key={opt.id}
            style={[styles.card, { borderLeftColor: opt.color }]}
            onPress={() => navigation.navigate(opt.screen)}
            activeOpacity={0.8}
          >
            <View style={[styles.iconBox, { backgroundColor: opt.color + '22' }]}>
              <Text style={styles.icon}>{opt.emoji}</Text>
            </View>
            <View style={styles.cardContent}>
              <Text style={styles.cardTitle}>{opt.title}</Text>
              <Text style={styles.cardSubtitle}>{opt.subtitle}</Text>
            </View>
            <Text style={[styles.arrow, { color: opt.color }]}>›</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: THEME.colors.bg },
  header: {
    paddingTop: 56,
    paddingHorizontal: THEME.spacing.lg,
    paddingBottom: THEME.spacing.xl,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  headerSubtitle: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    marginTop: 4,
  },
  scroll: { flex: 1 },
  scrollContent: {
    paddingHorizontal: THEME.spacing.lg,
    gap: THEME.spacing.md,
    paddingBottom: 100,
  },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderLeftWidth: 4,
    gap: THEME.spacing.md,
  },
  iconBox: {
    width: 52,
    height: 52,
    borderRadius: THEME.radius.md,
    justifyContent: 'center',
    alignItems: 'center',
  },
  icon: { fontSize: 26 },
  cardContent: { flex: 1 },
  cardTitle: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: 4,
  },
  cardSubtitle: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    lineHeight: 18,
  },
  arrow: {
    fontSize: 24,
    fontWeight: THEME.typography.weights.bold,
  },
});
