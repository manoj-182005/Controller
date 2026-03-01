import React, { useState, useRef, useEffect } from 'react';
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
import { parseNaturalLanguage, ParsedTask } from '../utils/nlpParser';

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

function formatDateLabel(dateStr?: string): string | null {
  if (!dateStr) return null;
  const today = new Date().toISOString().split('T')[0];
  const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
  if (dateStr === today) return 'Today';
  if (dateStr === tomorrow) return 'Tomorrow';
  return new Date(dateStr + 'T00:00:00').toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
}

export function QuickAddModal({ visible, onClose }: QuickAddModalProps) {
  const slideAnim = useRef(new Animated.Value(300)).current;
  const [rawInput, setRawInput] = useState('');
  const [nlpMode, setNlpMode] = useState(true);
  const [parsed, setParsed] = useState<ParsedTask | null>(null);

  // Manual fields
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState<Priority>('normal');
  const [category, setCategory] = useState<Category>('personal');
  const [dueOption, setDueOption] = useState<'none' | 'today' | 'tomorrow'>('today');
  const addTask = useTaskStore((s) => s.addTask);

  useEffect(() => {
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

  // Debounced NLP parse
  useEffect(() => {
    if (!nlpMode || rawInput.trim().length < 4) {
      setParsed(null);
      return;
    }
    const timer = setTimeout(() => {
      const result = parseNaturalLanguage(rawInput);
      setParsed(result);
    }, 350);
    return () => clearTimeout(timer);
  }, [rawInput, nlpMode]);

  const reset = () => {
    setRawInput('');
    setTitle('');
    setPriority('normal');
    setCategory('personal');
    setDueOption('today');
    setParsed(null);
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  const handleAdd = () => {
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

    if (nlpMode && parsed) {
      const dueDate = parsed.dueDate ?? today;
      addTask({
        id: Date.now().toString(),
        title: parsed.title || rawInput.trim(),
        priority: parsed.priority ?? 'normal',
        category: parsed.category ?? 'personal',
        tags: parsed.tags,
        dueDate,
        dueTime: parsed.dueTime,
        status: 'todo',
        isStarred: false,
        isCompleted: false,
        createdAt: new Date().toISOString(),
        subtasks: [],
        estimatedDuration: parsed.estimatedDuration,
        recurrence: parsed.recurrence,
        energyLevel: parsed.energyLevel,
        kanbanColumn: 'todo',
        timeSessions: [],
      });
    } else {
      if (!title.trim()) return;
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
        timeSessions: [],
      });
    }
    reset();
    onClose();
  };

  const canAdd = nlpMode ? rawInput.trim().length > 0 : title.trim().length > 0;

  return (
    <Modal
      visible={visible}
      transparent
      animationType="none"
      onRequestClose={handleClose}
      statusBarTranslucent
    >
      <TouchableOpacity style={styles.overlay} activeOpacity={1} onPress={handleClose} />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.avoidView}
        pointerEvents="box-none"
      >
        <Animated.View
          style={[styles.sheet, { transform: [{ translateY: slideAnim }] }]}
        >
          <View style={styles.handle} />

          {/* Title row with mode toggle */}
          <View style={styles.titleRow}>
            <Text style={styles.sheetTitle}>
              {nlpMode ? '✨ Smart Add' : '📝 Quick Add'}
            </Text>
            <TouchableOpacity
              style={styles.modeToggle}
              onPress={() => setNlpMode(!nlpMode)}
            >
              <Text style={styles.modeToggleText}>
                {nlpMode ? 'Manual' : 'Smart'}
              </Text>
            </TouchableOpacity>
          </View>

          {nlpMode ? (
            <>
              <TextInput
                style={styles.input}
                placeholder='Try: "Call mom every Sunday at 6pm" or "Buy groceries tomorrow shopping 30 min"'
                placeholderTextColor={THEME.colors.text.muted}
                value={rawInput}
                onChangeText={setRawInput}
                autoFocus
                returnKeyType="done"
                onSubmitEditing={handleAdd}
                multiline={false}
              />

              {/* Parsed preview */}
              {parsed && rawInput.trim().length > 3 && (
                <View style={styles.parsedCard}>
                  <Text style={styles.parsedTitle}>📋 Parsed Preview</Text>
                  <View style={styles.parsedGrid}>
                    <ParsedField icon="📌" label="Title" value={parsed.title} confidence={1} />
                    {parsed.priority && (
                      <ParsedField icon={PRIORITY_ICONS[parsed.priority]} label="Priority" value={parsed.priority} confidence={parsed.confidence.priority} />
                    )}
                    {parsed.category && (
                      <ParsedField icon={CATEGORY_ICONS[parsed.category]} label="Category" value={parsed.category} confidence={parsed.confidence.category} />
                    )}
                    {parsed.dueDate && (
                      <ParsedField icon="📅" label="Due" value={formatDateLabel(parsed.dueDate) ?? parsed.dueDate} confidence={parsed.confidence.dueDate} />
                    )}
                    {parsed.dueTime && (
                      <ParsedField icon="🕐" label="Time" value={parsed.dueTime} confidence={parsed.confidence.dueTime} />
                    )}
                    {parsed.estimatedDuration && (
                      <ParsedField icon="⏱" label="Duration" value={`${parsed.estimatedDuration} min`} confidence={parsed.confidence.estimatedDuration} />
                    )}
                    {parsed.recurrence && (
                      <ParsedField icon="🔁" label="Repeat" value={parsed.recurrence} confidence={parsed.confidence.recurrence} />
                    )}
                    {parsed.energyLevel && (
                      <ParsedField icon="⚡" label="Energy" value={parsed.energyLevel} confidence={parsed.confidence.energyLevel} />
                    )}
                  </View>
                </View>
              )}
            </>
          ) : (
            <>
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
            </>
          )}

          <TouchableOpacity
            style={[styles.addBtn, !canAdd && styles.addBtnDisabled]}
            onPress={handleAdd}
            disabled={!canAdd}
          >
            <Text style={styles.addBtnText}>+ Add Task</Text>
          </TouchableOpacity>
        </Animated.View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

// ─── Sub-component: Parsed Field Chip ────────────────────────────────────────
interface ParsedFieldProps {
  icon: string;
  label: string;
  value: string;
  confidence?: number;
}
function ParsedField({ icon, label, value, confidence }: ParsedFieldProps) {
  const conf = confidence ?? 1;
  const borderColor =
    conf >= 0.9 ? THEME.colors.success : conf >= 0.7 ? THEME.colors.warning : THEME.colors.error;
  return (
    <View style={[styles.parsedField, { borderColor }]}>
      <Text style={styles.parsedFieldIcon}>{icon}</Text>
      <View>
        <Text style={styles.parsedFieldLabel}>{label}</Text>
        <Text style={styles.parsedFieldValue}>{value}</Text>
      </View>
    </View>
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
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.lg,
  },
  sheetTitle: {
    fontSize: THEME.typography.sizes.xl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  modeToggle: {
    backgroundColor: THEME.colors.accent + '33',
    borderWidth: 1,
    borderColor: THEME.colors.accent,
    borderRadius: THEME.radius.full,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.xs,
  },
  modeToggleText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.accent,
    fontWeight: THEME.typography.weights.semibold,
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
  parsedCard: {
    backgroundColor: 'rgba(99,102,241,0.08)',
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.md,
    borderWidth: 1,
    borderColor: THEME.colors.accent + '40',
    marginBottom: THEME.spacing.lg,
  },
  parsedTitle: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.accent,
    marginBottom: THEME.spacing.sm,
  },
  parsedGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: THEME.spacing.sm,
  },
  parsedField: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.sm,
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 5,
    borderWidth: 1,
  },
  parsedFieldIcon: {
    fontSize: 14,
  },
  parsedFieldLabel: {
    fontSize: 9,
    color: THEME.colors.text.muted,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  parsedFieldValue: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.primary,
    fontWeight: THEME.typography.weights.medium,
    textTransform: 'capitalize',
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
