import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  SectionList,
  StyleSheet,
  TouchableOpacity,
  TextInput,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { TaskCard } from '../components/TaskCard';
import { Task, Priority, Category } from '../types/task';

type Filter = 'all' | 'today' | 'starred' | 'overdue';
type GroupBy = 'none' | 'priority' | 'category' | 'dueDate';
type SortBy = 'priority' | 'dueDate' | 'created' | 'az';

const PRIORITY_ORDER: Record<Priority, number> = { urgent: 0, high: 1, normal: 2, low: 3 };

export default function ListViewScreen() {
  const { tasks, toggleComplete, deleteTask } = useTaskStore();
  const [filter, setFilter] = useState<Filter>('all');
  const [groupBy, setGroupBy] = useState<GroupBy>('none');
  const [sortBy, setSortBy] = useState<SortBy>('priority');
  const [search, setSearch] = useState('');
  const [showGroupMenu, setShowGroupMenu] = useState(false);
  const [showSortMenu, setShowSortMenu] = useState(false);

  const today = new Date().toISOString().split('T')[0];

  const filtered = tasks.filter((t) => {
    if (search && !t.title.toLowerCase().includes(search.toLowerCase())) return false;
    if (filter === 'today') return t.dueDate === today && !t.isCompleted;
    if (filter === 'starred') return t.isStarred;
    if (filter === 'overdue') return t.dueDate && t.dueDate < today && !t.isCompleted;
    return true;
  });

  const sorted = [...filtered].sort((a, b) => {
    if (sortBy === 'priority') return PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority];
    if (sortBy === 'dueDate') return (a.dueDate ?? '').localeCompare(b.dueDate ?? '');
    if (sortBy === 'created') return b.createdAt.localeCompare(a.createdAt);
    if (sortBy === 'az') return a.title.localeCompare(b.title);
    return 0;
  });

  const buildSections = () => {
    if (groupBy === 'none') return [{ title: `All Tasks (${sorted.length})`, data: sorted }];

    const groups: Record<string, Task[]> = {};
    sorted.forEach((t) => {
      let key = '';
      if (groupBy === 'priority') key = t.priority.charAt(0).toUpperCase() + t.priority.slice(1);
      else if (groupBy === 'category') key = t.category.charAt(0).toUpperCase() + t.category.slice(1);
      else if (groupBy === 'dueDate') key = t.dueDate ?? 'No Date';
      if (!groups[key]) groups[key] = [];
      groups[key].push(t);
    });

    return Object.entries(groups).map(([title, data]) => ({
      title: `${title} (${data.length})`,
      data,
    }));
  };

  const sections = buildSections();

  const FILTERS: { key: Filter; label: string }[] = [
    { key: 'all', label: '📋 All' },
    { key: 'today', label: '📅 Today' },
    { key: 'starred', label: '⭐ Starred' },
    { key: 'overdue', label: '🔴 Overdue' },
  ];

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Tasks</Text>
        <View style={styles.headerControls}>
          <TouchableOpacity
            style={styles.controlBtn}
            onPress={() => { setShowGroupMenu(!showGroupMenu); setShowSortMenu(false); }}
          >
            <Text style={styles.controlBtnText}>Group: {groupBy}</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.controlBtn}
            onPress={() => { setShowSortMenu(!showSortMenu); setShowGroupMenu(false); }}
          >
            <Text style={styles.controlBtnText}>Sort: {sortBy}</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Dropdown Menus */}
      {showGroupMenu && (
        <View style={styles.dropdown}>
          {(['none', 'priority', 'category', 'dueDate'] as GroupBy[]).map((g) => (
            <TouchableOpacity
              key={g}
              style={[styles.dropdownItem, groupBy === g && styles.dropdownItemActive]}
              onPress={() => { setGroupBy(g); setShowGroupMenu(false); }}
            >
              <Text style={styles.dropdownItemText}>{g.charAt(0).toUpperCase() + g.slice(1)}</Text>
            </TouchableOpacity>
          ))}
        </View>
      )}
      {showSortMenu && (
        <View style={styles.dropdown}>
          {(['priority', 'dueDate', 'created', 'az'] as SortBy[]).map((s) => (
            <TouchableOpacity
              key={s}
              style={[styles.dropdownItem, sortBy === s && styles.dropdownItemActive]}
              onPress={() => { setSortBy(s); setShowSortMenu(false); }}
            >
              <Text style={styles.dropdownItemText}>{s}</Text>
            </TouchableOpacity>
          ))}
        </View>
      )}

      {/* Search */}
      <View style={styles.searchContainer}>
        <Text style={styles.searchIcon}>🔍</Text>
        <TextInput
          style={styles.searchInput}
          placeholder="Search tasks..."
          placeholderTextColor={THEME.colors.text.muted}
          value={search}
          onChangeText={setSearch}
        />
        {search.length > 0 && (
          <TouchableOpacity onPress={() => setSearch('')}>
            <Text style={styles.clearBtn}>✕</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Filter Chips */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.filterScroll}
        contentContainerStyle={styles.filterContent}
      >
        {FILTERS.map(({ key, label }) => (
          <TouchableOpacity
            key={key}
            style={[styles.filterChip, filter === key && styles.filterChipActive]}
            onPress={() => setFilter(key)}
          >
            <Text style={[styles.filterChipText, filter === key && styles.filterChipTextActive]}>
              {label}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      {/* List */}
      {sorted.length === 0 ? (
        <View style={styles.emptyState}>
          <Text style={styles.emptyEmoji}>🎉</Text>
          <Text style={styles.emptyTitle}>No tasks found</Text>
          <Text style={styles.emptySubtitle}>
            {filter === 'today'
              ? "You're all caught up for today!"
              : filter === 'overdue'
              ? 'No overdue tasks. Great job!'
              : 'Add a task to get started.'}
          </Text>
        </View>
      ) : (
        <SectionList
          sections={sections}
          keyExtractor={(item) => item.id}
          renderItem={({ item }) => (
            <View style={styles.cardWrapper}>
              <TaskCard
                task={item}
                onComplete={toggleComplete}
                onDelete={deleteTask}
              />
            </View>
          )}
          renderSectionHeader={({ section }) => (
            <View style={styles.sectionHeader}>
              <Text style={styles.sectionHeaderText}>{section.title}</Text>
            </View>
          )}
          contentContainerStyle={styles.listContent}
          stickySectionHeadersEnabled={false}
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: THEME.colors.bg,
  },
  header: {
    paddingTop: 56,
    paddingHorizontal: THEME.spacing.lg,
    paddingBottom: THEME.spacing.md,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.sm,
  },
  headerControls: {
    flexDirection: 'row',
    gap: THEME.spacing.sm,
  },
  controlBtn: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  controlBtnText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
    textTransform: 'capitalize',
  },
  dropdown: {
    position: 'absolute',
    top: 120,
    left: THEME.spacing.lg,
    backgroundColor: '#1E293B',
    borderRadius: THEME.radius.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    zIndex: 100,
    minWidth: 140,
    overflow: 'hidden',
  },
  dropdownItem: {
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.md,
  },
  dropdownItemActive: {
    backgroundColor: THEME.colors.accent + '33',
  },
  dropdownItemText: {
    color: THEME.colors.text.primary,
    fontSize: THEME.typography.sizes.sm,
    textTransform: 'capitalize',
  },
  searchContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    marginHorizontal: THEME.spacing.lg,
    marginBottom: THEME.spacing.md,
    paddingHorizontal: THEME.spacing.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  searchIcon: {
    fontSize: 16,
    marginRight: THEME.spacing.sm,
  },
  searchInput: {
    flex: 1,
    paddingVertical: THEME.spacing.md,
    fontSize: THEME.typography.sizes.md,
    color: THEME.colors.text.primary,
  },
  clearBtn: {
    color: THEME.colors.text.muted,
    fontSize: 16,
    paddingHorizontal: THEME.spacing.xs,
  },
  filterScroll: {
    marginBottom: THEME.spacing.md,
  },
  filterContent: {
    paddingHorizontal: THEME.spacing.lg,
    gap: THEME.spacing.sm,
  },
  filterChip: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.full,
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  filterChipActive: {
    backgroundColor: THEME.colors.accent + '33',
    borderColor: THEME.colors.accent,
  },
  filterChipText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
  filterChipTextActive: {
    color: THEME.colors.accent,
    fontWeight: THEME.typography.weights.semibold,
  },
  sectionHeader: {
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.sm,
    marginBottom: THEME.spacing.xs,
  },
  sectionHeaderText: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.secondary,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  cardWrapper: {
    paddingHorizontal: THEME.spacing.lg,
  },
  listContent: {
    paddingBottom: 100,
  },
  emptyState: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: THEME.spacing.xxl,
  },
  emptyEmoji: {
    fontSize: 48,
    marginBottom: THEME.spacing.md,
  },
  emptyTitle: {
    fontSize: THEME.typography.sizes.xl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.sm,
  },
  emptySubtitle: {
    fontSize: THEME.typography.sizes.md,
    color: THEME.colors.text.secondary,
    textAlign: 'center',
  },
});
