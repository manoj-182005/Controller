import React, { useState, useRef } from 'react';
import {
  Modal,
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  Animated,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { Priority, Category } from '../types/task';

interface QuickAddModalProps {
  visible: boolean;
  onClose: () => void;
}

const PRIORITIES: Priority[] = ['low', 'normal', 'high', 'urgent'];
const CATEGORIES: Category[] = [
  'personal', 'work', 'study', 'health', 'shopping', 'finance', 'others',
];

const PRIORITY_ICONS: Record<Priority, string> = {
  low: '🔵',
  normal: '🟢',
  high: '🟡',
  urgent: '🔴',
};

const CATEGORY_ICONS: Record<Category, string> = {
  personal: '👤',
  work: '💼',
  study: '📚',
  health: '❤️',
  shopping: '🛒',
  finance: '💰',
  others: '📌',
};

export function QuickAddModal({ visible, onClose }: QuickAddModalProps) {
  const slideAnim = useRef(new Animated.Value(300)).current;
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState<Priority>('normal');
  const [category, setCategory] = useState<Category>('personal');
  const [dueOption, setDueOption] = useState<'none' | 'today' | 'tomorrow'>('today');
  const addTask = useTaskStore((s) => s.addTask);

  React.useEffect(() => {
    if (visible) {
      Animated.spring(slideAnim, {
        toValue: 0,
        useNativeDriver: true,
        tension: 60,
        friction: 12,
      }).start();
    } else {
      Animated.timing(slideAnim, {
        toValue: 300,
        duration: 200,
        useNativeDriver: true,
      }).start();
    }
  }, [visible]);

  const handleAdd = () => {
    if (!title.trim()) return;
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
    const dueDate =
      dueOption === 'today' ? today : dueOption === 'tomorrow' ? tomorrow : undefined;

    addTask({
      id: Date.now().toString(),
      title: title.trim(),
      priority,
      category,
      tags: [],
      dueDate,
      status: 'todo',
      isStarred: false,
      isCompleted: false,
      createdAt: new Date().toISOString(),
      subtasks: [],
      kanbanColumn: 'todo',
    });
    setTitle('');
    setPriority('normal');
    setCategory('personal');
    setDueOption('today');
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
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.avoidView}
        pointerEvents="box-none"
      >
        <Animated.View
          style={[styles.sheet, { transform: [{ translateY: slideAnim }] }]}
        >
          <View style={styles.handle} />
          <Text style={styles.sheetTitle}>Quick Add Task</Text>

          <TextInput
            style={styles.input}
            placeholder="What needs to be done?"
            placeholderTextColor={THEME.colors.text.muted}
            value={title}
            onChangeText={setTitle}
            autoFocus
            returnKeyType="done"
            onSubmitEditing={handleAdd}
          />

          {/* Priority */}
          <Text style={styles.label}>Priority</Text>
          <View style={styles.chipRow}>
            {PRIORITIES.map((p) => (
              <TouchableOpacity
                key={p}
                style={[
                  styles.chip,
                  priority === p && {
                    backgroundColor: THEME.colors.priority[p] + '33',
                    borderColor: THEME.colors.priority[p],
                  },
                ]}
                onPress={() => setPriority(p)}
              >
                <Text style={styles.chipText}>
                  {PRIORITY_ICONS[p]} {p.charAt(0).toUpperCase() + p.slice(1)}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          {/* Category */}
          <Text style={styles.label}>Category</Text>
          <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            <View style={styles.chipRow}>
              {CATEGORIES.map((c) => (
                <TouchableOpacity
                  key={c}
                  style={[
                    styles.chip,
                    category === c && {
                      backgroundColor: THEME.colors.category[c] + '33',
                      borderColor: THEME.colors.category[c],
                    },
                  ]}
                  onPress={() => setCategory(c)}
                >
                  <Text style={styles.chipText}>
                    {CATEGORY_ICONS[c]} {c.charAt(0).toUpperCase() + c.slice(1)}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </ScrollView>

          {/* Due Date */}
          <Text style={styles.label}>Due Date</Text>
          <View style={styles.chipRow}>
            {(['none', 'today', 'tomorrow'] as const).map((opt) => (
              <TouchableOpacity
                key={opt}
                style={[
                  styles.chip,
                  dueOption === opt && {
                    backgroundColor: THEME.colors.accent + '33',
                    borderColor: THEME.colors.accent,
                  },
                ]}
                onPress={() => setDueOption(opt)}
              >
                <Text style={styles.chipText}>
                  {opt === 'none' ? '📭 No date' : opt === 'today' ? '📅 Today' : '📅 Tomorrow'}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          <TouchableOpacity
            style={[styles.addBtn, !title.trim() && styles.addBtnDisabled]}
            onPress={handleAdd}
            disabled={!title.trim()}
          >
            <Text style={styles.addBtnText}>+ Add Task</Text>
          </TouchableOpacity>
        </Animated.View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.6)',
  },
  avoidView: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: '#131929',
    borderTopLeftRadius: THEME.radius.xl,
    borderTopRightRadius: THEME.radius.xl,
    padding: THEME.spacing.xxl,
    paddingBottom: 40,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderBottomWidth: 0,
  },
  handle: {
    width: 40,
    height: 4,
    backgroundColor: THEME.colors.border,
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: THEME.spacing.lg,
  },
  sheetTitle: {
    fontSize: THEME.typography.sizes.xl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.lg,
  },
  input: {
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    borderRadius: THEME.radius.md,
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.md,
    fontSize: THEME.typography.sizes.md,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.lg,
  },
  label: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.secondary,
    marginBottom: THEME.spacing.sm,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  chipRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: THEME.spacing.sm,
    marginBottom: THEME.spacing.lg,
  },
  chip: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.full,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    backgroundColor: THEME.colors.surface,
  },
  chipText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.primary,
    fontWeight: THEME.typography.weights.medium,
  },
  addBtn: {
    backgroundColor: THEME.colors.accent,
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.lg,
    alignItems: 'center',
    marginTop: THEME.spacing.sm,
  },
  addBtnDisabled: {
    opacity: 0.4,
  },
  addBtnText: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: '#FFFFFF',
  },
});
