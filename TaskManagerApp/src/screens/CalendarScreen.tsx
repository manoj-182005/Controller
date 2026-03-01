import React, { useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  Modal,
} from 'react-native';
import { Calendar } from 'react-native-calendars';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';
import { TaskCard } from '../components/TaskCard';
import { Task } from '../types/task';

type CalendarTab = 'Month' | 'Week' | 'Day' | 'Agenda';

export default function CalendarScreen() {
  const { tasks, toggleComplete, deleteTask } = useTaskStore();
  const [tab, setTab] = useState<CalendarTab>('Month');
  const [selectedDate, setSelectedDate] = useState(
    new Date().toISOString().split('T')[0]
  );
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTasks, setModalTasks] = useState<Task[]>([]);
  const [modalDate, setModalDate] = useState('');

  const today = new Date().toISOString().split('T')[0];

  // Build marked dates for calendar
  const markedDates: Record<string, { dots: { key: string; color: string }[]; selected?: boolean; selectedColor?: string }> = {};
  tasks.forEach((t) => {
    if (!t.dueDate) return;
    if (!markedDates[t.dueDate]) {
      markedDates[t.dueDate] = { dots: [] };
    }
    const dot = { key: t.id, color: THEME.colors.category[t.category] };
    if (markedDates[t.dueDate].dots.length < 3) {
      markedDates[t.dueDate].dots.push(dot);
    }
  });
  if (markedDates[selectedDate]) {
    markedDates[selectedDate] = { ...markedDates[selectedDate], selected: true, selectedColor: THEME.colors.accent };
  } else {
    markedDates[selectedDate] = { dots: [], selected: true, selectedColor: THEME.colors.accent };
  }

  const handleDayPress = (day: { dateString: string }) => {
    setSelectedDate(day.dateString);
    const dayTasks = tasks.filter((t) => t.dueDate === day.dateString);
    setModalTasks(dayTasks);
    setModalDate(day.dateString);
    setModalVisible(true);
  };

  // Week view: current week days
  const getWeekDays = () => {
    const now = new Date(selectedDate + 'T00:00:00');
    const day = now.getDay();
    return Array.from({ length: 7 }, (_, i) => {
      const d = new Date(now);
      d.setDate(now.getDate() - day + i);
      return d.toISOString().split('T')[0];
    });
  };

  // Agenda: next 30 days
  const getAgendaDays = () => {
    return Array.from({ length: 30 }, (_, i) => {
      const d = new Date(Date.now() + i * 86400000);
      const dStr = d.toISOString().split('T')[0];
      const dayTasks = tasks.filter((t) => t.dueDate === dStr);
      return { date: dStr, tasks: dayTasks };
    }).filter((d) => d.tasks.length > 0);
  };

  const TABS: CalendarTab[] = ['Month', 'Week', 'Day', 'Agenda'];

  const formatDateDisplay = (dateStr: string) =>
    new Date(dateStr + 'T00:00:00').toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
    });

  const selectedTasks = tasks.filter((t) => t.dueDate === selectedDate);

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Calendar</Text>
        {/* Tab Buttons */}
        <View style={styles.tabRow}>
          {TABS.map((t) => (
            <TouchableOpacity
              key={t}
              style={[styles.tabBtn, tab === t && styles.tabBtnActive]}
              onPress={() => setTab(t)}
            >
              <Text style={[styles.tabBtnText, tab === t && styles.tabBtnTextActive]}>
                {t}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
        {/* Month View */}
        {tab === 'Month' && (
          <View>
            <Calendar
              current={selectedDate}
              onDayPress={handleDayPress}
              markingType="multi-dot"
              markedDates={markedDates}
              theme={{
                backgroundColor: THEME.colors.bg,
                calendarBackground: THEME.colors.surface,
                textSectionTitleColor: THEME.colors.text.secondary,
                selectedDayBackgroundColor: THEME.colors.accent,
                selectedDayTextColor: '#ffffff',
                todayTextColor: THEME.colors.accent,
                dayTextColor: THEME.colors.text.primary,
                textDisabledColor: THEME.colors.text.muted,
                dotColor: THEME.colors.accent,
                selectedDotColor: '#ffffff',
                arrowColor: THEME.colors.accent,
                monthTextColor: THEME.colors.text.primary,
                indicatorColor: THEME.colors.accent,
                textDayFontSize: 14,
                textMonthFontSize: 16,
                textDayHeaderFontSize: 12,
              }}
              style={styles.calendar}
            />
            {selectedTasks.length > 0 && (
              <View style={styles.dayTasksPanel}>
                <Text style={styles.dayTasksTitle}>
                  {formatDateDisplay(selectedDate)} — {selectedTasks.length} task{selectedTasks.length !== 1 ? 's' : ''}
                </Text>
                {selectedTasks.map((task) => (
                  <TaskCard
                    key={task.id}
                    task={task}
                    onComplete={toggleComplete}
                    onDelete={deleteTask}
                  />
                ))}
              </View>
            )}
          </View>
        )}

        {/* Week View */}
        {tab === 'Week' && (
          <View style={styles.weekContainer}>
            <View style={styles.weekHeader}>
              {getWeekDays().map((d) => {
                const isToday = d === today;
                const isSelected = d === selectedDate;
                const dayTasks = tasks.filter((t) => t.dueDate === d);
                const dayName = new Date(d + 'T00:00:00').toLocaleDateString('en-US', { weekday: 'short' });
                const dayNum = new Date(d + 'T00:00:00').getDate();
                return (
                  <TouchableOpacity
                    key={d}
                    style={[
                      styles.weekDay,
                      isSelected && styles.weekDaySelected,
                      isToday && styles.weekDayToday,
                    ]}
                    onPress={() => setSelectedDate(d)}
                  >
                    <Text style={[styles.weekDayName, isSelected && styles.weekDayNameSelected]}>
                      {dayName}
                    </Text>
                    <Text style={[styles.weekDayNum, isSelected && styles.weekDayNumSelected]}>
                      {dayNum}
                    </Text>
                    {dayTasks.length > 0 && (
                      <View style={[styles.weekDot, { backgroundColor: isSelected ? '#fff' : THEME.colors.accent }]} />
                    )}
                  </TouchableOpacity>
                );
              })}
            </View>
            <View style={styles.weekTasks}>
              <Text style={styles.dayTasksTitle}>
                {formatDateDisplay(selectedDate)} — {selectedTasks.length} tasks
              </Text>
              {selectedTasks.map((task) => (
                  <TaskCard key={task.id} task={task} onComplete={toggleComplete} onDelete={deleteTask} />
                ))}
            </View>
          </View>
        )}

        {/* Day View */}
        {tab === 'Day' && (
          <View style={styles.dayContainer}>
            <View style={styles.dayNav}>
              <TouchableOpacity
                style={styles.dayNavBtn}
                onPress={() => {
                  const d = new Date(selectedDate + 'T00:00:00');
                  d.setDate(d.getDate() - 1);
                  setSelectedDate(d.toISOString().split('T')[0]);
                }}
              >
                <Text style={styles.dayNavText}>‹ Prev</Text>
              </TouchableOpacity>
              <Text style={styles.dayNavTitle}>{formatDateDisplay(selectedDate)}</Text>
              <TouchableOpacity
                style={styles.dayNavBtn}
                onPress={() => {
                  const d = new Date(selectedDate + 'T00:00:00');
                  d.setDate(d.getDate() + 1);
                  setSelectedDate(d.toISOString().split('T')[0]);
                }}
              >
                <Text style={styles.dayNavText}>Next ›</Text>
              </TouchableOpacity>
            </View>
            {/* 24hr Timeline */}
            {Array.from({ length: 24 }, (_, hr) => {
              const hrTasks = tasks.filter(
                (t) => {
                  if (t.dueDate !== selectedDate || !t.dueTime) return false;
                  // Normalize dueTime: '9:00' → '09:00'
                  const normalized = t.dueTime.includes(':')
                    ? t.dueTime.split(':')[0].padStart(2, '0') + ':' + t.dueTime.split(':')[1]
                    : t.dueTime;
                  return normalized.startsWith(hr.toString().padStart(2, '0'));
                }
              );
              const h = hr % 12 === 0 ? 12 : hr % 12;
              const ampm = hr < 12 ? 'AM' : 'PM';
              return (
                <View key={hr} style={styles.timeSlot}>
                  <Text style={styles.timeLabel}>{`${h}${ampm}`}</Text>
                  <View style={styles.timeSlotContent}>
                    {hrTasks.map((t) => (
                      <View
                        key={t.id}
                        style={[styles.timeBlock, { backgroundColor: THEME.colors.category[t.category] + '33', borderLeftColor: THEME.colors.category[t.category] }]}
                      >
                        <Text style={styles.timeBlockTitle} numberOfLines={1}>{t.title}</Text>
                        <Text style={styles.timeBlockMeta}>
                          {t.estimatedDuration ? `${t.estimatedDuration}m` : ''}
                        </Text>
                      </View>
                    ))}
                    <View style={styles.timeSlotLine} />
                  </View>
                </View>
              );
            })}
          </View>
        )}

        {/* Agenda View */}
        {tab === 'Agenda' && (
          <View style={styles.agendaContainer}>
            {getAgendaDays().length === 0 ? (
              <View style={styles.emptyState}>
                <Text style={styles.emptyEmoji}>📭</Text>
                <Text style={styles.emptyText}>No upcoming tasks</Text>
              </View>
            ) : (
              getAgendaDays().map(({ date, tasks: dayTasks }) => (
                <View key={date} style={styles.agendaSection}>
                  <View style={styles.agendaDateHeader}>
                    <View style={[styles.agendaDateDot, date === today && { backgroundColor: THEME.colors.accent }]} />
                    <Text style={[styles.agendaDate, date === today && { color: THEME.colors.accent }]}>
                      {date === today ? 'Today' : formatDateDisplay(date)}
                    </Text>
                    <Text style={styles.agendaCount}>{dayTasks.length}</Text>
                  </View>
                  {dayTasks.map((task) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      compact
                      onComplete={toggleComplete}
                      onDelete={deleteTask}
                    />
                  ))}
                </View>
              ))
            )}
          </View>
        )}

        <View style={{ height: 100 }} />
      </ScrollView>

      {/* Day Tasks Modal */}
      <Modal
        visible={modalVisible}
        transparent
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}
      >
        <TouchableOpacity
          style={styles.modalOverlay}
          activeOpacity={1}
          onPress={() => setModalVisible(false)}
        />
        <View style={styles.modalSheet}>
          <View style={styles.modalHandle} />
          <Text style={styles.modalTitle}>
            {formatDateDisplay(modalDate)} — {modalTasks.length} tasks
          </Text>
          <ScrollView>
            {modalTasks.length === 0 ? (
              <View style={styles.emptyState}>
                <Text style={styles.emptyEmoji}>🗓</Text>
                <Text style={styles.emptyText}>No tasks on this day</Text>
              </View>
            ) : (
              modalTasks.map((task) => (
                <TaskCard
                  key={task.id}
                  task={task}
                  onComplete={toggleComplete}
                  onDelete={deleteTask}
                />
              ))
            )}
          </ScrollView>
        </View>
      </Modal>
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
    marginBottom: THEME.spacing.md,
  },
  tabRow: {
    flexDirection: 'row',
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.radius.md,
    padding: 4,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  tabBtn: {
    flex: 1,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.sm,
    alignItems: 'center',
  },
  tabBtnActive: {
    backgroundColor: THEME.colors.accent,
  },
  tabBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    fontWeight: THEME.typography.weights.medium,
  },
  tabBtnTextActive: {
    color: '#fff',
    fontWeight: THEME.typography.weights.bold,
  },
  scroll: { flex: 1 },
  calendar: {
    borderRadius: THEME.radius.md,
    margin: THEME.spacing.lg,
    overflow: 'hidden',
  },
  dayTasksPanel: {
    padding: THEME.spacing.lg,
  },
  dayTasksTitle: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.semibold,
    color: THEME.colors.text.secondary,
    marginBottom: THEME.spacing.md,
  },
  weekContainer: { padding: THEME.spacing.lg },
  weekHeader: { flexDirection: 'row', gap: 4, marginBottom: THEME.spacing.lg },
  weekDay: {
    flex: 1,
    alignItems: 'center',
    padding: THEME.spacing.sm,
    borderRadius: THEME.radius.md,
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  weekDaySelected: { backgroundColor: THEME.colors.accent, borderColor: THEME.colors.accent },
  weekDayToday: { borderColor: THEME.colors.accent },
  weekDayName: { fontSize: 10, color: THEME.colors.text.muted },
  weekDayNameSelected: { color: '#fff' },
  weekDayNum: { fontSize: 14, fontWeight: '700', color: THEME.colors.text.primary },
  weekDayNumSelected: { color: '#fff' },
  weekDot: { width: 4, height: 4, borderRadius: 2, marginTop: 2 },
  weekTasks: {},
  dayContainer: { padding: THEME.spacing.lg },
  dayNav: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: THEME.spacing.lg,
  },
  dayNavBtn: {
    backgroundColor: THEME.colors.surface,
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.sm,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  dayNavText: { color: THEME.colors.accent, fontSize: THEME.typography.sizes.sm },
  dayNavTitle: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  timeSlot: { flexDirection: 'row', minHeight: 44 },
  timeLabel: {
    width: 44,
    fontSize: 10,
    color: THEME.colors.text.muted,
    paddingTop: 4,
  },
  timeSlotContent: { flex: 1, borderTopWidth: 1, borderTopColor: THEME.colors.border + '80', paddingBottom: 4, paddingLeft: 4 },
  timeSlotLine: {},
  timeBlock: {
    borderLeftWidth: 3,
    borderRadius: THEME.radius.sm,
    padding: THEME.spacing.sm,
    marginBottom: 2,
  },
  timeBlockTitle: { fontSize: THEME.typography.sizes.sm, color: THEME.colors.text.primary },
  timeBlockMeta: { fontSize: 10, color: THEME.colors.text.muted },
  agendaContainer: { padding: THEME.spacing.lg },
  agendaSection: { marginBottom: THEME.spacing.xl },
  agendaDateHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: THEME.spacing.md,
  },
  agendaDateDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: THEME.colors.text.muted,
    marginRight: THEME.spacing.sm,
  },
  agendaDate: {
    fontSize: THEME.typography.sizes.md,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    flex: 1,
  },
  agendaCount: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    backgroundColor: THEME.colors.surface,
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 2,
    borderRadius: THEME.radius.full,
  },
  emptyState: { alignItems: 'center', padding: THEME.spacing.xxl },
  emptyEmoji: { fontSize: 48, marginBottom: THEME.spacing.md },
  emptyText: { fontSize: THEME.typography.sizes.md, color: THEME.colors.text.secondary },
  modalOverlay: { ...StyleSheet.absoluteFillObject, backgroundColor: 'rgba(0,0,0,0.6)' },
  modalSheet: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#131929',
    borderTopLeftRadius: THEME.radius.xl,
    borderTopRightRadius: THEME.radius.xl,
    padding: THEME.spacing.lg,
    maxHeight: '70%',
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  modalHandle: {
    width: 40,
    height: 4,
    backgroundColor: THEME.colors.border,
    borderRadius: 2,
    alignSelf: 'center',
    marginBottom: THEME.spacing.md,
  },
  modalTitle: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.md,
  },
});
