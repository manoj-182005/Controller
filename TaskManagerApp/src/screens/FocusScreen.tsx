import React, { useState, useRef, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import Svg, { Circle } from 'react-native-svg';
import { THEME } from '../theme/tokens';
import { useTaskStore } from '../store/taskStore';

const RADIUS = 100;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

const FOCUS_PRESETS = [
  { label: '25 min', focus: 25, shortBreak: 5 },
  { label: '50 min', focus: 50, shortBreak: 10 },
  { label: '90 min', focus: 90, shortBreak: 15 },
];

export default function FocusScreen() {
  const { tasks, logFocusSession, focusSessions } = useTaskStore();
  const incompleteTasks = tasks.filter((t) => !t.isCompleted);

  const [taskIndex, setTaskIndex] = useState(0);
  const [phase, setPhase] = useState<'focus' | 'break'>('focus');
  const [running, setRunning] = useState(false);
  const [presetIdx, setPresetIdx] = useState(0);
  const [session, setSession] = useState(1);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const sessionStartRef = useRef<number | null>(null);

  const preset = FOCUS_PRESETS[presetIdx];
  const FOCUS_DURATION = preset.focus * 60;
  const BREAK_DURATION = preset.shortBreak * 60;

  const [seconds, setSeconds] = useState(FOCUS_DURATION);

  const total = phase === 'focus' ? FOCUS_DURATION : BREAK_DURATION;
  const progress = seconds / total;
  const strokeDash = CIRCUMFERENCE * progress;

  const currentTask = incompleteTasks[taskIndex];

  // Weekly focus data (last 7 days)
  const weekFocus = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(Date.now() - (6 - i) * 86400000).toISOString().split('T')[0];
    const s = focusSessions.find((fs) => fs.date === d);
    return { d, minutes: s?.focusMinutes ?? 0 };
  });
  const maxFocusMin = Math.max(...weekFocus.map((w) => w.minutes), 1);
  const totalFocusToday = focusSessions.find(
    (s) => s.date === new Date().toISOString().split('T')[0]
  )?.focusMinutes ?? 0;

  useEffect(() => {
    if (running) {
      if (phase === 'focus' && sessionStartRef.current === null) {
        sessionStartRef.current = Date.now();
      }
      intervalRef.current = setInterval(() => {
        setSeconds((prev) => {
          if (prev <= 1) {
            clearInterval(intervalRef.current!);
            setRunning(false);
            if (phase === 'focus') {
              if (sessionStartRef.current !== null) {
                const elapsed = Math.round((Date.now() - sessionStartRef.current) / 60000);
                logFocusSession(Math.max(1, elapsed));
                sessionStartRef.current = null;
              }
              setPhase('break');
              setSeconds(BREAK_DURATION);
              setSession((s) => s + 1);
            } else {
              setPhase('focus');
              setSeconds(FOCUS_DURATION);
            }
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      if (intervalRef.current) clearInterval(intervalRef.current);
    }
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [running, phase, FOCUS_DURATION, BREAK_DURATION]);

  const formatTime = (s: number) => {
    const m = Math.floor(s / 60).toString().padStart(2, '0');
    const sec = (s % 60).toString().padStart(2, '0');
    return `${m}:${sec}`;
  };

  const handleReset = () => {
    setRunning(false);
    sessionStartRef.current = null;
    setSeconds(phase === 'focus' ? FOCUS_DURATION : BREAK_DURATION);
  };

  const handleSkip = () => {
    setRunning(false);
    sessionStartRef.current = null;
    if (phase === 'focus') {
      setPhase('break');
      setSeconds(BREAK_DURATION);
      setSession((s) => s + 1);
    } else {
      setPhase('focus');
      setSeconds(FOCUS_DURATION);
    }
  };

  const handlePresetChange = (idx: number) => {
    setPresetIdx(idx);
    setRunning(false);
    sessionStartRef.current = null;
    setSeconds(FOCUS_PRESETS[idx].focus * 60);
    setPhase('focus');
  };

  const bgColor = phase === 'focus' ? '#0D1B3E' : '#0B2A1C';
  const ringColor = phase === 'focus' ? THEME.colors.accent : THEME.colors.success;

  return (
    <ScrollView
      style={[styles.container, { backgroundColor: bgColor }]}
      contentContainerStyle={styles.scrollContent}
      showsVerticalScrollIndicator={false}
    >
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Focus Mode</Text>
        <View style={[styles.phaseBadge, { backgroundColor: phase === 'focus' ? THEME.colors.accent + '33' : THEME.colors.success + '33' }]}>
          <Text style={[styles.phaseText, { color: phase === 'focus' ? THEME.colors.accent : THEME.colors.success }]}>
            {phase === 'focus' ? '🎯 Focus' : '☕ Break'}
          </Text>
        </View>
      </View>

      {/* Session info */}
      <Text style={styles.sessionInfo}>
        Session {session} · Task {taskIndex + 1} of {incompleteTasks.length}
        {totalFocusToday > 0 && ` · ${totalFocusToday}m focused today`}
      </Text>

      {/* Duration presets */}
      <View style={styles.presetRow}>
        {FOCUS_PRESETS.map((p, i) => (
          <TouchableOpacity
            key={p.label}
            style={[styles.presetBtn, presetIdx === i && styles.presetBtnActive]}
            onPress={() => handlePresetChange(i)}
          >
            <Text style={[styles.presetBtnText, presetIdx === i && styles.presetBtnTextActive]}>
              {p.label}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Current Task */}
      {currentTask && (
        <View style={styles.currentTaskCard}>
          <Text style={styles.currentTaskLabel}>Current Task</Text>
          <Text style={styles.currentTaskTitle}>{currentTask.title}</Text>
          <View style={[styles.priorityPill, { backgroundColor: THEME.colors.priority[currentTask.priority] + '33' }]}>
            <Text style={[styles.priorityPillText, { color: THEME.colors.priority[currentTask.priority] }]}>
              {currentTask.priority}
            </Text>
          </View>
        </View>
      )}

      {/* SVG Timer Ring */}
      <View style={styles.timerContainer}>
        <Svg width={240} height={240} viewBox="0 0 240 240">
          <Circle
            cx={120}
            cy={120}
            r={RADIUS}
            stroke="rgba(255,255,255,0.08)"
            strokeWidth={12}
            fill="none"
          />
          <Circle
            cx={120}
            cy={120}
            r={RADIUS}
            stroke={ringColor}
            strokeWidth={12}
            fill="none"
            strokeDasharray={`${strokeDash} ${CIRCUMFERENCE}`}
            strokeLinecap="round"
            transform="rotate(-90 120 120)"
          />
        </Svg>
        <View style={styles.timerTextContainer}>
          <Text style={styles.timerText}>{formatTime(seconds)}</Text>
          <Text style={styles.timerSubtext}>
            {phase === 'focus' ? 'Stay focused' : 'Take a breather'}
          </Text>
        </View>
      </View>

      {/* Controls */}
      <View style={styles.controls}>
        <TouchableOpacity style={styles.secondaryBtn} onPress={handleReset}>
          <Text style={styles.secondaryBtnText}>↺ Reset</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.primaryBtn, { backgroundColor: running ? THEME.colors.warning : ringColor }]}
          onPress={() => setRunning(!running)}
        >
          <Text style={styles.primaryBtnText}>{running ? '⏸ Pause' : '▶ Start'}</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.secondaryBtn} onPress={handleSkip}>
          <Text style={styles.secondaryBtnText}>Skip ⏭</Text>
        </TouchableOpacity>
      </View>

      {/* Next Task */}
      {incompleteTasks.length > 1 && (
        <TouchableOpacity
          style={styles.nextTaskBtn}
          onPress={() => setTaskIndex((i) => (i + 1) % incompleteTasks.length)}
        >
          <Text style={styles.nextTaskBtnText}>
            Next Task: {incompleteTasks[(taskIndex + 1) % incompleteTasks.length]?.title ?? ''} →
          </Text>
        </TouchableOpacity>
      )}

      {/* Weekly Focus Chart */}
      {weekFocus.some((w) => w.minutes > 0) && (
        <View style={styles.focusChart}>
          <Text style={styles.focusChartTitle}>⚡ Weekly Focus Hours</Text>
          <View style={styles.focusBars}>
            {weekFocus.map((w, i) => {
              const dayNames = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
              const dayIdx = new Date(w.d + 'T00:00:00').getDay();
              const isToday = i === 6;
              return (
                <View key={i} style={styles.focusBarCol}>
                  <View style={styles.focusBarTrack}>
                    <View
                      style={[
                        styles.focusBarFill,
                        {
                          height: `${(w.minutes / maxFocusMin) * 100}%`,
                          backgroundColor: isToday ? THEME.colors.accent : THEME.colors.accent + '55',
                        },
                      ]}
                    />
                  </View>
                  <Text style={styles.focusBarLabel}>{dayNames[dayIdx]}</Text>
                  {w.minutes > 0 && (
                    <Text style={styles.focusBarValue}>{Math.round((w.minutes / 60) * 10) / 10}h</Text>
                  )}
                </View>
              );
            })}
          </View>
        </View>
      )}

      <View style={{ height: 80 }} />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {
    alignItems: 'center',
    paddingTop: 56,
    paddingHorizontal: THEME.spacing.lg,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    width: '100%',
    marginBottom: THEME.spacing.sm,
  },
  headerTitle: {
    fontSize: THEME.typography.sizes.xxl,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  phaseBadge: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.full,
  },
  phaseText: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.bold,
  },
  sessionInfo: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    marginBottom: THEME.spacing.md,
    alignSelf: 'flex-start',
  },
  presetRow: {
    flexDirection: 'row',
    gap: THEME.spacing.sm,
    alignSelf: 'flex-start',
    marginBottom: THEME.spacing.lg,
  },
  presetBtn: {
    paddingHorizontal: THEME.spacing.md,
    paddingVertical: THEME.spacing.sm,
    borderRadius: THEME.radius.full,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    backgroundColor: 'rgba(255,255,255,0.05)',
  },
  presetBtnActive: {
    backgroundColor: THEME.colors.accent + '33',
    borderColor: THEME.colors.accent,
  },
  presetBtnText: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.secondary,
    fontWeight: THEME.typography.weights.medium,
  },
  presetBtnTextActive: {
    color: THEME.colors.accent,
  },
  currentTaskCard: {
    backgroundColor: 'rgba(255,255,255,0.07)',
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.lg,
    width: '100%',
    marginBottom: THEME.spacing.xl,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    gap: THEME.spacing.sm,
  },
  currentTaskLabel: {
    fontSize: THEME.typography.sizes.xs,
    color: THEME.colors.text.muted,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  currentTaskTitle: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
  },
  priorityPill: {
    alignSelf: 'flex-start',
    paddingHorizontal: THEME.spacing.sm,
    paddingVertical: 3,
    borderRadius: THEME.radius.full,
  },
  priorityPillText: {
    fontSize: THEME.typography.sizes.xs,
    fontWeight: THEME.typography.weights.semibold,
    textTransform: 'capitalize',
  },
  timerContainer: {
    width: 240,
    height: 240,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: THEME.spacing.xxl,
  },
  timerTextContainer: {
    position: 'absolute',
    alignItems: 'center',
  },
  timerText: {
    fontSize: THEME.typography.sizes.xxxl + 8,
    fontWeight: THEME.typography.weights.extrabold,
    color: THEME.colors.text.primary,
    fontVariant: ['tabular-nums'],
  },
  timerSubtext: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    marginTop: THEME.spacing.xs,
  },
  controls: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: THEME.spacing.lg,
    marginBottom: THEME.spacing.xl,
  },
  primaryBtn: {
    paddingHorizontal: THEME.spacing.xxl,
    paddingVertical: THEME.spacing.lg,
    borderRadius: THEME.radius.full,
    minWidth: 120,
    alignItems: 'center',
  },
  primaryBtnText: {
    fontSize: THEME.typography.sizes.lg,
    fontWeight: THEME.typography.weights.bold,
    color: '#fff',
  },
  secondaryBtn: {
    backgroundColor: 'rgba(255,255,255,0.08)',
    borderRadius: THEME.radius.full,
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.md,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  secondaryBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
  },
  nextTaskBtn: {
    backgroundColor: 'rgba(255,255,255,0.05)',
    borderRadius: THEME.radius.lg,
    paddingHorizontal: THEME.spacing.xl,
    paddingVertical: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    width: '100%',
    marginBottom: THEME.spacing.xl,
  },
  nextTaskBtnText: {
    fontSize: THEME.typography.sizes.sm,
    color: THEME.colors.text.secondary,
    textAlign: 'center',
  },
  focusChart: {
    width: '100%',
    backgroundColor: 'rgba(255,255,255,0.05)',
    borderRadius: THEME.radius.lg,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
  },
  focusChartTitle: {
    fontSize: THEME.typography.sizes.sm,
    fontWeight: THEME.typography.weights.bold,
    color: THEME.colors.text.primary,
    marginBottom: THEME.spacing.md,
  },
  focusBars: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    height: 60,
    alignItems: 'flex-end',
    gap: 4,
  },
  focusBarCol: {
    flex: 1,
    alignItems: 'center',
    height: '100%',
    justifyContent: 'flex-end',
  },
  focusBarTrack: {
    width: '100%',
    flex: 1,
    justifyContent: 'flex-end',
    borderRadius: THEME.radius.sm,
    overflow: 'hidden',
    backgroundColor: 'rgba(255,255,255,0.04)',
  },
  focusBarFill: {
    width: '100%',
    borderRadius: THEME.radius.sm,
    minHeight: 3,
  },
  focusBarLabel: {
    fontSize: 9,
    color: THEME.colors.text.muted,
    marginTop: 3,
  },
  focusBarValue: {
    fontSize: 9,
    color: THEME.colors.text.secondary,
  },
});
