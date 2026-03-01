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

type KanbanCol = 'todo' | 'inprogress' | 'done';

const COLUMNS: { key: KanbanCol; label: string; emoji: string; bg: string }[] = [
  { key: 'todo', label: 'To Do', emoji: '📋', bg: '#1E293B' },
  { key: 'inprogress', label: 'In Progress', emoji: '⚡', bg: '#1E3A5F' },
  { key: 'done', label: 'Done', emoji: '✅', bg: '#1A2E1A' },
];

interface KanbanCardProps {
  task: Task;
  onMove: (id: string, col: KanbanCol) => void;
  currentCol: KanbanCol;
}

function KanbanCard({ task, onMove, currentCol }: KanbanCardProps) {
  const [showMenu, setShowMenu] = useState(false);
  const priorityColor = THEME.colors.priority[task.priority];
  const today = new Date().toISOString().split('T')[0];
  const isOverdue = task.dueDate && task.dueDate < today && !task.isCompleted;

  const formatDate = (d?: string) => {
    if (!d) return null;
    return new Date(d + 'T00:00:00').toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  };

  return (
    <View style={[styles.kanbanCard, isOverdue && styles.kanbanCardOverdue]}>
      <View style={[styles.kanbanPriorityBar, { backgroundColor: priorityColor }]} />
      <View style={styles.kanbanCardContent}>
        <Text style={styles.kanbanTitle} numberOfLines={2}>{task.title}</Text>
        <View style={styles.kanbanMeta}>
          <View style={[styles.priorityBadge, { backgroundColor: priorityColor + '33' }]}>
            <Text style={[styles.priorityText, { color: priorityColor }]}>
              {task.priority}
            </Text>
          </View>
          {task.dueDate && (
            <Text style={[styles.kanbanDate, isOverdue && { color: THEME.colors.error }]}>
              📅 {formatDate(task.dueDate)}
            </Text>
          )}
        </View>
        {task.subtasks.length > 0 && (
          <Text style={styles.subtaskProgress}>
            📝 {task.subtasks.filter((s) => s.done).length}/{task.subtasks.length} subtasks
          </Text>
        )}
        <TouchableOpacity
          style={styles.moveBtn}
          onPress={() => setShowMenu(!showMenu)}
        >
          <Text style={styles.moveBtnText}>Move to ▾</Text>
        </TouchableOpacity>
        {showMenu && (
          <View style={styles.moveMenu}>
            {COLUMNS.filter((c) => c.key !== currentCol).map((col) => (
              <TouchableOpacity
                key={col.key}
                style={styles.moveMenuItem}
                onPress={() => {
                  onMove(task.id, col.key);
                  setShowMenu(false);
                }}
              >
                <Text style={styles.moveMenuText}>{col.emoji} {col.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        )}
      </View>
    </View>
  );
}

export default function KanbanScreen() {
  const { tasks, moveKanban, addTask } = useTaskStore();
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({});

  const getColTasks = (col: KanbanCol) =>
    tasks.filter((t) => (t.kanbanColumn ?? 'todo') === col);

  const toggleCollapse = (col: KanbanCol) =>
    setCollapsed((prev) => ({ ...prev, [col]: !prev[col] }));

  const handleAddTask = (col: KanbanCol) => {
    addTask({
      id: Date.now().toString(),
      title: `New ${col === 'todo' ? 'To Do' : col === 'inprogress' ? 'In Progress' : 'Done'} Task`,
      priority: 'normal',
      category: 'work',
      tags: [],
      status: col,
      isStarred: false,
      isCompleted: col === 'done',
      createdAt: new Date().toISOString(),
      subtasks: [],
      kanbanColumn: col,
    });
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Kanban Board</Text>
        <Text style={styles.headerSubtitle}>{tasks.length} total tasks</Text>
      </View>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.board}>
        {COLUMNS.map((col) => {
          const colTasks = getColTasks(col.key);
          const isCollapsed = collapsed[col.key];
          return (
            <View key={col.key} style={[styles.column, { backgroundColor: col.bg }]}>
              {/* Column Header */}
              <TouchableOpacity
                style={styles.colHeader}
                onPress={() => toggleCollapse(col.key)}
              >
                <View style={styles.colHeaderLeft}>
                  <Text style={styles.colEmoji}>{col.emoji}</Text>
                  <Text style={styles.colTitle}>{col.label}</Text>
                  <View style={styles.colBadge}>
                    <Text style={styles.colBadgeText}>{colTasks.length}</Text>
                  </View>
                </View>
                <Text style={styles.colToggle}>{isCollapsed ? '▸' : '▾'}</Text>
              </TouchableOpacity>

              {!isCollapsed && (
                <>
                  <ScrollView
                    style={styles.colScroll}
                    showsVerticalScrollIndicator={false}
                    nestedScrollEnabled
                  >
                    {colTasks.map((task) => (
                      <KanbanCard
                        key={task.id}
                        task={task}
                        onMove={moveKanban}
                        currentCol={col.key}
                      />
                    ))}
                  </ScrollView>
                  <TouchableOpacity
                    style={styles.addTaskBtn}
                    onPress={() => handleAddTask(col.key)}
                  >
                    <Text style={styles.addTaskBtnText}>+ Add Task</Text>
                  </TouchableOpacity>
                </>
              )}
            </View>
          );
        })}
      </ScrollView>
    </View>
  );
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
  },
  headerSubtitle: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    marginTop: 2,
  },
  board: { flex: 1, paddingHorizontal: THEME.spacing.md },
  column: {
    width: 260,
    marginRight: THEME.spacing.md,
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.md,
    maxHeight: '85%',
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  colHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.md,
  },
  colHeaderLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.sm,
  },
  colEmoji: { fontSize: 18 },
  colTitle: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  colBadge: {
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: THEME.radius.full,
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 2,
  },
  colBadgeText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.primary,
    fontWeight: THEME.typography.weights.bold,
  },
  colToggle: {
    fontSize: 16,
    color: THEME.colors.text.secondary,
  },
  colScroll: { flex: 1 },
  kanbanCard: {
    flexDirection: 'row',
    backgroundColor: 'rgba(255,255,255,0.07)',
    borderRadius: THEME.radius.md,
    marginBottom: THEME.spacing.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    overflow: 'hidden',
  },
  kanbanCardOverdue: {
    borderColor: THEME.colors.error + '60',
  },
  kanbanPriorityBar: {
    width: 3,
  },
  kanbanCardContent: {
    flex: 1,
    padding: THEME.spacing.md,
    gap: THEME.spacing.xs,
  },
  kanbanTitle: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.primary,
    lineHeight: 20,
  },
  kanbanMeta: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.sm,
    flexWrap: 'wrap',
  },
  priorityBadge: {
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 2,
    borderRadius: THEME.radius.full,
  },
  priorityText: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    textTransform: 'capitalize',
  },
  kanbanDate: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
  },
  subtaskProgress: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
  },
  moveBtn: {
    marginTop: THEME.spacing.xs,
    backgroundColor: 'rgba(255,255,255,0.1)',
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 4,
    alignSelf: 'flex-start',
  },
  moveBtnText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  moveMenu: {
    backgroundColor: '#1E293B',
    borderRadius: THEME.radius.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    marginTop: 4,
    overflow: 'hidden',
  },
  moveMenuItem: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
  },
  moveMenuText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.primary,
  },
  addTaskBtn: {
    marginTop: THEME.spacing.sm,
    backgroundColor: 'rgba(255,255,255,0.06)',
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.md,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderStyle: 'dashed',
  },
  addTaskBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
});
