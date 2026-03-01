import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  Alert,
} from 'react-native';
import { THEME } from '../theme/tokens';
import { useTaskStore, BUILT_IN_TEMPLATES } from '../store/taskStore';
import { TaskTemplate } from '../types/task';

const CATEGORY_ICONS: Record<string, string> = {
  personal: '👤',
  work: '💼',
  study: '📚',
  health: '❤️',
  shopping: '🛒',
  finance: '💰',
  others: '📌',
};

const PRIORITY_COLORS: Record<string, string> = {
  low: THEME.colors.priority.low,
  normal: THEME.colors.priority.normal,
  high: THEME.colors.priority.high,
  urgent: THEME.colors.priority.urgent,
};

export default function TemplatesScreen() {
  const { applyTemplate } = useTaskStore();
  const [expanded, setExpanded] = useState<string | null>(null);

  const handleApply = (template: TaskTemplate) => {
    Alert.alert(
      `Apply "${template.name}"?`,
      `This will create ${template.tasks.length} task${template.tasks.length > 1 ? 's' : ''} due today based on this template.`,
      [
        { text: 'Cancel', style: 'cancel' },
        {
          text: 'Apply',
          style: 'default',
          onPress: () => {
            applyTemplate(template);
          },
        },
      ]
    );
  };

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      showsVerticalScrollIndicator={false}
    >
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>📄 Templates</Text>
        <Text style={styles.headerSub}>
          Instantly create task sets from pre-built templates
        </Text>
      </View>

      {/* Template cards */}
      {BUILT_IN_TEMPLATES.map((template) => {
        const isOpen = expanded === template.id;
        return (
          <View key={template.id} style={styles.card}>
            {/* Card header */}
            <TouchableOpacity
              style={styles.cardHeader}
              onPress={() => setExpanded(isOpen ? null : template.id)}
              activeOpacity={0.8}
            >
              <View style={styles.cardIconWrap}>
                <Text style={styles.cardIcon}>{template.icon}</Text>
              </View>
              <View style={styles.cardInfo}>
                <Text style={styles.cardName}>{template.name}</Text>
                <Text style={styles.cardDesc}>{template.description}</Text>
                <Text style={styles.cardCount}>
                  {template.tasks.length} task{template.tasks.length > 1 ? 's' : ''}
                </Text>
              </View>
              <Text style={[styles.chevron, isOpen && styles.chevronOpen]}>›</Text>
            </TouchableOpacity>

            {/* Expanded task preview */}
            {isOpen && (
              <View style={styles.taskList}>
                {template.tasks.map((t, i) => (
                  <View key={i} style={styles.taskRow}>
                    <View
                      style={[
                        styles.taskDot,
                        { backgroundColor: PRIORITY_COLORS[t.priority] },
                      ]}
                    />
                    <View style={styles.taskRowInfo}>
                      <Text style={styles.taskTitle}>{t.title}</Text>
                      <View style={styles.taskMeta}>
                        <Text style={styles.taskMetaText}>
                          {CATEGORY_ICONS[t.category]} {t.category}
                        </Text>
                        {t.estimatedDuration && (
                          <Text style={styles.taskMetaText}>
                            ⏱ {t.estimatedDuration} min
                          </Text>
                        )}
                      </View>
                    </View>
                  </View>
                ))}

                <TouchableOpacity
                  style={styles.applyBtn}
                  onPress={() => handleApply(template)}
                  activeOpacity={0.8}
                >
                  <Text style={styles.applyBtnText}>
                    ✨ Apply Template
                  </Text>
                </TouchableOpacity>
              </View>
            )}
          </View>
        );
      })}

      {/* Info section */}
      <View style={styles.infoCard}>
        <Text style={styles.infoIcon}>💡</Text>
        <Text style={styles.infoText}>
          Templates create tasks due <Text style={{ color: THEME.colors.accent }}>today</Text>. You can edit any task after applying.
        </Text>
      </View>

      <View style={{ height: 100 }} />
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
    paddingTop: 56,
  },
  header: {
    marginBottom: THEME.spacing.xl,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: 4,
  },
  headerSub: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
  card: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    marginBottom: THEME.spacing.md,
    overflow: 'hidden',
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: THEME.spacing.lg,
    gap: THEME.spacing.md,
  },
  cardIconWrap: {
    width: 48,
    height: 48,
    borderRadius: THEME.radius.md,
    backgroundColor: THEME.colors.accent + '22',
    alignItems: 'center',
    justifyContent: 'center',
  },
  cardIcon: {
    fontSize: 24,
  },
  cardInfo: {
    flex: 1,
    gap: 2,
  },
  cardName: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  cardDesc: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
  },
  cardCount: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.accent,
    fontWeight: THEME.typography.weights.medium,
    marginTop: 2,
  },
  chevron: {
    fontSize: 22,
    color: THEME.colors.text.muted,
    transform: [{ rotate: '0deg' }],
  },
  chevronOpen: {
    transform: [{ rotate: '90deg' }],
  },
  taskList: {
    paddingHorizontal: THEME.spacing.lg,
    paddingBottom: THEME.spacing.lg,
    borderTopWidth: 1,
    borderTopColor: THEME.colors.border,
    gap: THEME.spacing.md,
  },
  taskRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: THEME.spacing.sm,
    paddingTop: THEME.spacing.md,
  },
  taskDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginTop: 5,
  },
  taskRowInfo: {
    flex: 1,
    gap: 2,
  },
  taskTitle: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.medium,
    color: THEME.colors.text.primary,
  },
  taskMeta: {
    flexDirection: 'row',
    gap: THEME.spacing.md,
  },
  taskMetaText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    textTransform: 'capitalize',
  },
  applyBtn: {
    marginTop: THEME.spacing.sm,
    backgroundColor: THEME.colors.accent,
    borderRadius: THEME.radius.md,
    paddingVertical: THEME.spacing.md,
    alignItems: 'center',
  },
  applyBtnText: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: '#fff',
  },
  infoCard: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: THEME.spacing.sm,
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    marginTop: THEME.spacing.sm,
  },
  infoIcon: {
    fontSize: 18,
  },
  infoText: {
    flex: 1,
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    lineHeight: 20,
  },
});
