Prompts 1 and 2 are complete. The Task Manager has its full core engine with all 19 task card fields, all card states and swipe gestures, the vibrant premium home screen with stat cards and streak banner, and all 7 views — List View with grouping and sorting, Calendar View with Month/Week/Day/Agenda modes and drag-to-reschedule, Kanban Board with drag-between-columns, Time Block Planner with Schedule All, Focus Mode with Pomodoro timer and session logging, Dashboard with Today's Focus and activity feed, and Analytics with all 12 data visualizations. All of that is working. Now build Prompt 3 — the complete AI Intelligence Layer and Productivity System. This is the brain of the Task Manager — the features that make it genuinely smarter than any other task manager available. Go through the entire codebase extremely thoroughly before touching anything. Understand every model, every service, every screen, every database operation, every existing smart feature already built. Do not break anything. Build everything listed here completely, correctly, and to production quality.

---

## DESIGN FOUNDATION — Non-Negotiable:

Every screen, card, sheet, and interaction built in this prompt must follow the exact visual language already established: deep navy background (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0A0F1E` or the exact value used in the existing app), glassmorphism cards with frosted borders and translucency, priority color system (Low = slate, Normal = blue, High = amber, Urgent = crimson with glow), 300ms smooth transitions as the standard, display font for titles, clean sans for metadata, scale + glow feedback on every interactive element tap, and the same vibrant gradient aesthetic as the Expense Tracker. Every new screen must feel like it belongs to the same premium app — never a visual inconsistency.

---

## SYSTEM 1 — NATURAL LANGUAGE TASK CREATION ENGINE:

This is the most visible AI feature. The quick add bar at the top of the home screen already exists. Completely replace its backend with a natural language parsing engine that understands complex human input and extracts structured task data from it.

**Parser Architecture:**

Build a local-first parsing engine that runs entirely on-device without any API call — fast, private, and works offline. The engine uses pattern matching, keyword dictionaries, and contextual analysis to extract the following fields from free-text input:

Task title — everything that isn't a recognized modifier keyword.

Due date — extract: specific dates ("March 15", "15th March", "3/15"), relative dates ("today", "tomorrow", "next Monday", "this Friday", "in 3 days", "next week", "end of month"), time-relative ("in 2 hours", "in 30 minutes"), and event-relative ("before the meeting", "after lunch"). Resolve all relative dates against the actual current date at the moment of parsing.

Due time — extract: 12-hour formats ("3pm", "3:30pm", "3:30 PM"), 24-hour formats ("15:00", "15:30"), natural language times ("noon", "midnight", "morning" → 9 AM, "afternoon" → 2 PM, "evening" → 6 PM, "tonight" → 8 PM).

Duration — extract: "30 min", "30m", "1 hour", "1h", "1.5 hours", "90 minutes", "half an hour", "two hours", "1h 30m".

Priority — extract: "urgent", "ASAP", "critical" → Urgent. "important", "high priority", "!!" → High. "low priority", "whenever", "someday", "eventually" → Low. No modifier → Normal.

Category — extract category names directly: "work", "study", "health", "shopping", "personal", "finance". Also detect category keywords: "gym", "workout", "exercise" → Health. "buy", "purchase", "get" → Shopping. "meeting", "client", "report", "deadline" → Work. "exam", "assignment", "lecture", "homework" → Study. "pay", "bill", "invoice", "budget" → Finance.

Recurrence — extract: "every day" / "daily" → Daily. "every week" / "weekly" → Weekly. "every Monday" / "every Tuesday" etc. → Weekly on that day. "every Monday and Wednesday" → Weekly on Mon + Wed. "every month" / "monthly" → Monthly. "every year" / "annually" → Yearly. "every 3 days" → Custom interval.

Tags — extract hashtags typed directly: "review #important #urgent" → tags: [important, urgent].

Energy Level — extract: "deep work", "focused", "concentrate" → Deep Work. "quick", "easy", "light" → Light. "low energy", "simple" → Low Energy.

Reminders — extract: "remind me 30 minutes before", "remind me at 2pm", "remind me tomorrow morning" → creates a reminder offset or absolute reminder time.

Location reminder — extract: "when I'm at the gym", "when I get home", "when I arrive at work" → creates a location reminder with the detected location keyword.

**Parsing Examples — Must All Work Correctly:**

"Call mom every Sunday at 6pm" → Title: "Call mom", Recurrence: Weekly on Sunday, Time: 6:00 PM, No due date.

"Submit report by Friday high priority work" → Title: "Submit report", Due: this Friday, Priority: High, Category: Work.

"Buy groceries tomorrow shopping 30 min" → Title: "Buy groceries", Due: tomorrow, Category: Shopping, Duration: 30 min.

"Team meeting next Monday 10am to 11am zoom" → Title: "Team meeting", Due: next Monday, Time: 10:00 AM, Duration: 1 hour, Notes: "zoom".

"Pay electricity bill by March 15 finance remind me 2 days before" → Title: "Pay electricity bill", Due: March 15, Category: Finance, Reminder: March 13 (2 days before).

"Workout at gym every weekday morning health deep work" → Title: "Workout at gym", Recurrence: Daily Mon-Fri, Time: 9:00 AM (morning default), Category: Health, Energy: Deep Work.

"Finish Java assignment urgent study tomorrow 2 hours" → Title: "Finish Java assignment", Priority: Urgent, Category: Study, Due: tomorrow, Duration: 2 hours.

"Review project proposal ASAP with Sarah" → Title: "Review project proposal with Sarah", Priority: Urgent.

"Read 10 pages every night before sleep low priority personal" → Title: "Read 10 pages", Recurrence: Daily, Time: 10:00 PM (night default), Priority: Low, Category: Personal.

**Live Parse Preview Card:**

As the user types in the quick add bar — after a brief 400ms debounce — show a "Parse Preview" card that slides up smoothly below the input field. This card shows the extracted fields as formatted chips in real time as parsing happens:

Title field: the cleaned task title in bold.

Each extracted field shown as a colored chip: a calendar chip for due date (blue, calendar icon, "Tomorrow 3 PM"), a priority chip (the priority color, flag icon, "High Priority"), a category chip (category color, category icon, "Work"), a duration chip (clock icon, "~1 hour"), a recurrence chip (repeat icon, "Every Monday"), a reminder chip (bell icon, "2 days before"), an energy chip (lightning icon, "Deep Work").

Fields that could not be extracted are shown as dim placeholder chips — "No priority set", "No category" — indicating what the parser didn't find. This helps the user see what was understood and what wasn't.

**Confidence Indicators:**

Each extracted field chip shows a confidence level via a subtle visual indicator: a thin colored border — green border = high confidence (>85%), amber border = medium confidence (60-85%), red border = low confidence (<60%). High confidence fields are immediately accepted. Low confidence fields have a subtle pulsing indicator suggesting the user should verify them.

Tapping any chip opens an inline editor for that field — a small bottom sheet with the correct picker for that field type (date picker for due date, priority selector for priority, category picker for category, etc.). The user corrects the extraction, taps confirm, and the chip updates with green border and a "fixed" animation.

**Learning from Corrections:**

Store every correction the user makes to parsed fields in a local learning database. Record: what input pattern was used, what the parser extracted, what the user corrected it to. Over time use this data to improve pattern weights — if the user always corrects "morning" to 8 AM instead of the default 9 AM, update the default morning time resolution for this user. If the user often types "call" tasks in Personal category but the parser guesses Work, adjust the weight for "call" toward Personal. This personalization happens entirely on-device.

**Batch Natural Language Import:**

A dedicated screen accessible from the drawer or import menu — "Import from Text." The user pastes or types multiple tasks, one per line. The parser processes each line independently and shows all parsed tasks in a review list — each with their extracted fields shown as chips. The user can accept all, edit individual items, or reject specific ones. A "Create All" button creates all accepted tasks simultaneously. Useful for importing a todo list from a notes app, WhatsApp message, or email.

---

## SYSTEM 2 — AI SMART SCHEDULING ENGINE:

**"Schedule My Day" Feature:**

A prominent "Schedule My Day" button accessible from: the home screen dashboard below the Today's Focus section, the Time Block Planner view header, and the three dot menu in the List View. This is one of the most powerful features in the app.

**Scheduling Algorithm:**

When triggered the engine collects: all active tasks with due dates today or overdue (highest priority), all tasks with estimated durations, all tasks without a specific scheduled time (unblocked tasks), the user's configured working hours (from settings — e.g., 9 AM to 6 PM), the user's configured energy peak time (morning person / night owl / custom from settings), and existing scheduled tasks to avoid conflicts.

The engine then builds an optimal schedule using these rules in priority order: Urgent tasks with today's due date scheduled first and earliest. High priority tasks with today's due date scheduled next. Overdue tasks elevated to near the top regardless of priority. Deep Work energy tasks scheduled during the user's peak energy hours. Light and Low Energy tasks scheduled for off-peak hours (post-lunch, late afternoon). Break buffers of 10-15 minutes inserted between tasks over 45 minutes. Back-to-back tasks avoided where possible — at least 5 minutes gap. Tasks not fitting within working hours flagged as "Could not schedule today — reschedule?" with a suggested date.

**Proposed Schedule Screen:**

After the algorithm runs show a full-screen "Proposed Schedule" view. Design it as a premium experience — not a plain list. A vertical timeline for the day showing each scheduled task as a color-coded time block — same visual style as the Time Block Planner view. Each block shows: task title, time range, estimated duration, category color, and priority border. Gaps between blocks labeled "Free time — 25 min." Unschedulable tasks shown in a section below the timeline: "Could not fit today (3 tasks)" as a scrollable list with suggested reschedule dates for each.

At the top of the proposed schedule screen: a summary card showing "Your day is X% booked — N hours of tasks scheduled, N hours free." The summary card uses a colored background: green if under 70% booked (comfortable), amber if 70-85% (busy), red if over 85% (overloaded) with a gentle warning message.

**User Adjustment Before Confirming:**

The user can adjust the proposed schedule before accepting: drag any time block to a different position — same drag physics as the Time Block Planner. Remove a task from today's schedule by swiping it out. Tap a task to see its full details and change its scheduled time manually. Add an unscheduled task to the day by tapping the "Add from unscheduled" panel.

A "Regenerate" button re-runs the algorithm from scratch. A "Confirm Schedule" button applies all the scheduled times as the task due times. An "Discard" button exits without changes.

**Conflict Warnings During Schedule:**

If two tasks scheduled by the user (not by the algorithm) overlap in the proposed schedule — highlight the conflict with a red striped overlap area and a "Conflict" label. The conflict resolution suggestions: "Move [Task A] to 3:30 PM?" and "Move [Task B] to 4:15 PM?" shown as quick action buttons. The algorithm avoids creating conflicts itself — conflicts only arise when the user manually drags blocks.

**Daily Schedule Notification:**

An optional morning notification (configurable time, default 8:30 AM) that says "Good morning! You have N tasks due today. Tap to see your suggested schedule." Tapping opens the Smart Schedule screen directly with the algorithm already pre-run — the user just confirms or adjusts. Makes starting the day with a plan effortless.

---

## SYSTEM 3 — SMART REMINDERS SYSTEM:

Go far beyond basic "remind me at X time" alarms. Build a multi-layered intelligent reminder system.

**Data Layer:**

Reminder model: id, taskId, type (TIME_BASED / LOCATION_BASED / PRE_TASK / ESCALATING / CONTEXT_AWARE), triggerDateTime (for time-based), locationName (for location-based), locationLatLng (optional), radiusMeters, offsetMinutes (for pre-task reminders), escalationChainId (for escalating reminders), isFired, isActedOn, snoozedUntil, createdAt.

EscalationChain model: id, taskId, intervals (list of minutes — e.g., [0, 10, 30, 60] meaning fire at 0 min, then +10 min if no action, then +30 min if still no action, then +60 min), currentStep, createdAt.

**TYPE 1 — Time-Based Reminders (Enhanced):**

Already partially implemented. Enhance with: multiple reminders per task (already exists — ensure unlimited reminders are supported). Smart time suggestions when adding a reminder: if the task is due at 3 PM suggest "30 minutes before (2:30 PM)", "1 hour before (2:00 PM)", "Day before (3 PM yesterday)", "1 week before." Each suggestion shown as a tappable chip. Custom reminder with a full date-time picker. Reminder offset options: 5 min / 10 min / 15 min / 30 min / 1 hour / 2 hours / 4 hours / 1 day / 2 days / 1 week before.

**TYPE 2 — Pre-Task Preparation Reminders:**

A "Prep Reminder" toggle on tasks with an estimated duration. When enabled the system automatically fires a preparation notification before the scheduled task time — the prep time equals half the estimated duration (capped at 30 minutes). For a task scheduled at 3:00 PM with a 1-hour estimate — fire a prep reminder at 2:30 PM: "Starting soon: [Task Title] in 30 minutes — time to prepare." The notification action buttons: "Start Now" (marks task as In Progress and opens it), "Snooze 15 min", "Dismiss." This feature helps the user transition into focused work rather than being caught off guard.

**TYPE 3 — Escalating Reminders:**

For high-priority or urgent tasks configure an escalation chain. When the first reminder fires and the user doesn't act on it (doesn't open the app, doesn't complete or snooze the task) the system escalates: fire another notification after 10 minutes with higher urgency — "[IMPORTANT] Still pending: [Task Title]." If still no action after 20 more minutes fire a final escalation: "[URGENT] [Task Title] needs your attention NOW." The notification tone and vibration pattern intensify with each escalation. After the final escalation no more escalations fire — to avoid becoming annoying. The escalation chain is configurable: intervals (10 min / 20 min / custom), maximum escalation steps (2 or 3), whether to escalate during Do Not Disturb (default: no).

**TYPE 4 — Location-Based Reminders:**

When a task has a location trigger set ("remind me when I arrive at the gym") use geofencing. The user sets the location either by: typing a location name (stored as text — used for display and notification), or using the device's location to set a geofence radius (requires location permission). When the device enters the geofence radius fire a local notification: "You're at [Location]: [Task Title]." Geofencing works using the platform's background location API (iOS CLLocationManager significant change monitoring, Android geofence API). Show a clear permission explanation before requesting location access — "Location reminders require background location access to notify you when you arrive." If permission is denied show the reminder in time-based mode as a fallback at the task's due time instead. Active location reminders shown with a pin icon on task cards. A "Location Reminders" section in the reminder settings showing all active geofences.

**TYPE 5 — Smart Snooze Intelligence:**

When the user snoozes a reminder track: the original reminder time, how many times they snoozed it, the snooze durations chosen, and what time they finally acted on the task. After 10+ snooze events build a snooze pattern per user: "You tend to snooze morning reminders for 15 minutes — default snooze set to 15 min." Show snooze suggestions in the notification action: instead of a generic "Snooze" button show "Snooze 15 min" pre-filled with the learned duration. In the Snooze Picker (when user taps snooze) show: the learned duration as "My Usual (15 min)" at the top, then standard options (5 min, 10 min, 30 min, 1 hour, until tomorrow, custom). Surface the snooze pattern insight in the Analytics View: "You typically snooze tasks 2.3 times before acting — try setting reminders 30 minutes earlier to reduce snoozes."

**TYPE 6 — Weather-Aware Reminders:**

For tasks that have a location set and are scheduled outdoors (detected by keywords in the task title or notes: "run", "walk", "outdoor", "park", "garden", "cycling", "jog") check the weather forecast on the morning of the task date. If rain, extreme heat (above 38°C), storm, or snow is forecast fire a morning notification: "Weather alert for your task: [Task Title] at [Time] — [Rain/Storm] expected. Want to reschedule?" Notification action buttons: "Reschedule" (opens the date picker pre-focused on the task), "Keep As Is" (dismisses the alert). Weather data fetched from a free weather API (Open-Meteo or similar — no API key required) using the task's location coordinates or the device's current location. If the device has no internet connection skip weather checking silently — no error shown to user.

**Reminder Notification Design:**

All reminder notifications must be rich and actionable — not plain text pushes. Notification layout: app icon, task title as the notification title, category and due time as the body text, a priority indicator in the notification color. Action buttons: for normal reminders — "Done" (marks complete), "Snooze" (snooze with learned duration), "Open." For escalated reminders — "Done" (marks complete), "Remind in 1 hour." For location reminders — "Done", "Open Task." Notifications must work on both Android (all versions from 8.0+) and iOS in all app states — foreground, background, and killed. Test and verify all notification types fire correctly.

---

## SYSTEM 4 — TASK INTELLIGENCE ENGINE:

A background intelligence service that continuously analyzes tasks and surfaces proactive suggestions. Runs on a background isolate — never affects UI performance.

**Overdue Prediction:**

Analyze historical task completion patterns to identify tasks likely to be missed before they become overdue. Signals used: the task has been in the same status for more days than similar past tasks took to complete, the task priority hasn't changed despite being pending for a long time, similar tasks in the same category had a high miss rate, the task's due date is approaching within 48 hours but no progress has been made (no subtasks checked, no timer started, no notes added). When a task is predicted to be at risk show a subtle warning in its card: a small amber "At Risk" chip below the task title. Also surface at-risk tasks in a dedicated "At Risk" section on the Dashboard below "Today's Focus" — "3 tasks may be missed this week." Tapping opens a filtered list of at-risk tasks.

**Priority Escalation Suggestions:**

Monitor tasks that have been pending for longer than a threshold (configurable: 3 days for Urgent, 5 days for High, 7 days for Normal, 14 days for Low). When a task exceeds its threshold show a suggestion notification and an in-app banner on the task card: "This task has been pending for 7 days — consider upgrading its priority." The banner has two action buttons: "Upgrade Priority" (one tap to increase priority one level) and "Dismiss" (removes the suggestion and doesn't show it again for 3 more days). Priority escalations suggested via notification: "3 tasks pending over a week — tap to review." All suggestions are optional — never automatic.

**Smart Auto-Categorization:**

When the user creates a task via the quick add bar or the full task creation screen analyze the task title and notes for category keywords. Show the detected category as a pre-filled suggestion in the category field — highlighted as "Suggested" with a magic wand icon. If the user accepts it (just doesn't change it) increment the confidence for that pattern. If the user overrides it record the correction for learning. Category keyword dictionaries: Work (meeting, report, client, deadline, presentation, email, call, review, project, proposal), Study (assignment, exam, lecture, notes, homework, chapter, quiz, study, read, research), Health (gym, workout, run, doctor, medicine, exercise, yoga, diet, sleep, therapy), Shopping (buy, purchase, get, order, pick up, grocery, store, market), Finance (pay, bill, invoice, budget, tax, bank, transfer, expense, receipt, salary), Personal (family, friend, home, clean, cook, trip, vacation, appointment).

**Duplicate Detection:**

When the user types a task title in any creation field scan existing active tasks for similarity. Use a combination of: exact title match (immediately show a warning), word overlap score (titles sharing more than 60% of significant words), and fuzzy matching (typos and minor variations). If a potential duplicate is found show a non-blocking banner below the title field: "Similar task exists: '[Existing Task Title]' due [Date]. View it?" with a "View" button that opens the existing task and a "Dismiss" button that hides the warning and doesn't show it again for this creation session. If the existing task is already completed don't show the duplicate warning — a completed task and a new similar task are not duplicates.

**Dependency Auto-Detection:**

When viewing or editing a task analyze its title, tags, and category against other tasks to detect potential dependencies. If two tasks appear related — same project keywords, same category and similar names, or one task's title suggests it follows another ("Submit report" and "Write report draft" are clearly sequential) — show a suggestion banner on the task detail screen: "This task may depend on 'Write report draft' — add dependency?" Two action buttons: "Add Dependency" (links the tasks) and "Not Related" (dismisses permanently for this pair). When a dependency is added both tasks show dependency indicators — the dependent task shows "Blocked by: Write report draft" and the blocking task shows "Blocks: Submit report."

**Estimated Duration Intelligence:**

When the user creates a task without setting an estimated duration — or with the duration field empty — analyze historical tasks with similar titles, same category, and same tags to suggest a duration. Show as a pre-filled suggestion in the duration field: "~45 min (based on similar tasks)." If no similar past tasks exist use category averages (calculated from all the user's completed tasks in that category). If no history at all use global defaults (Work tasks: 45 min, Study tasks: 60 min, Health tasks: 45 min, Shopping tasks: 30 min, Personal tasks: 30 min, Finance tasks: 20 min). The suggestion shows a small "Suggested" label — the user can accept or override freely.

**Smart Tags Suggestions:**

When creating or editing a task after the user has typed the title and description analyze the content for tag suggestions. Compare keywords against: all existing tags the user has used (most frequent shown first), common task management tags (priority, urgent, important, review, waiting, blocked, delegated, reference), and category-specific tags (Study: exam, lecture, assignment, notes; Work: meeting, deadline, client; Health: routine, weekly). Show suggested tags as tappable chips below the tag input field — a "Suggested:" row with 3-5 relevant tags. Tapping adds the tag. A "Add all" button adds all suggestions. Suggestions are dismissed if ignored and don't re-appear for the same task session.

---

## SYSTEM 5 — PRODUCTIVITY INTELLIGENCE LAYER:

The intelligence layer that goes beyond individual task management to give the user genuine insights about their productivity patterns and wellbeing.

**Productivity Score (Enhanced):**

The Productivity Score already exists on the home dashboard. Deepen its implementation significantly. The score (0-100) is calculated from these weighted factors: On-Time Completion Rate (35% weight — percentage of tasks completed on or before due date over the last 30 days), Current Streak (20% weight — days in a row with at least one task completed, scaled logarithmically so a 7-day streak gives more points than the difference between a 50-day and 57-day streak), Daily Consistency (20% weight — how many of the last 7 days had at least one task completed), Overdue Penalty (15% negative weight — current overdue task count as a percentage of total active tasks), Focus Time (10% weight — hours of Pomodoro focus time logged this week, capped at contributing 10 points).

Score recalculates every hour in the background and also on every task completion or creation. The score change from the last calculation is shown as a delta indicator: "+3 ▲" in green or "-2 ▼" in red next to the score. A 7-day score trend line shown below the score ring in the Analytics View.

**Peak Performance Insights:**

Generated weekly from the user's actual completion data. Analyze: which hours of the day have the highest task completion rate (when do they actually complete tasks?), which days of the week are most productive, which categories are consistently completed on time vs consistently overdue, and whether focus sessions correlate with more completions on the same day.

Surface these insights as natural language cards in the Analytics View and as weekly notification: "📊 Productivity Insight: You complete 73% more tasks between 9-11 AM than any other time. Schedule your most important work in this window." The insights rotate — a new set every Sunday. Insights stored in a local database so they persist between app opens. A "View All Insights" button shows the full insight history.

**Weekly Review System:**

Every Sunday at a configurable time (default 6 PM) fire a "Weekly Review" notification: "Time for your weekly review — you completed N tasks this week!" Tapping opens a dedicated Weekly Review screen:

The screen flows as a series of swipeable cards — like a story or onboarding flow:

Card 1 — Week Summary: "This Week at a Glance." Stats: tasks completed (with a comparison to last week — "+5 more than last week ✓"), completion rate percentage, total focus time, most productive day, longest task completed. A mini 7-day sparkline at the bottom.

Card 2 — Wins: "Your Top Completions." The 3 most significant tasks completed this week (highest priority completed tasks). Each shown as a card with task title, category, and a celebration icon. A text "You crushed it this week!" if all are high/urgent priority.

Card 3 — Pending Review: "Still On Your List." All tasks that were due this week but weren't completed shown as a scrollable list. For each task: quick action buttons — Reschedule (opens date picker), Mark as Irrelevant and Archive, Keep (leaves it as is). This forces the user to consciously decide about every pending item rather than just ignoring overdue tasks.

Card 4 — Reflection: "Quick Reflection." Two optional text fields: "What went well this week?" and "What will I do differently next week?" Responses stored locally. Viewable from a "Reflections" screen in the drawer.

Card 5 — Next Week Preview: "Week Ahead." Shows all tasks due next week grouped by day. A "Schedule Next Week" button that runs the Smart Scheduling algorithm for the entire next week — generating a day-by-day plan.

Card 6 — Goal Check-In: If the user has set a weekly completion goal show progress: goal ring showing N/M tasks completed, whether the goal was met or missed, and a prompt to set next week's goal.

Navigation: swipe between cards or use dot pagination at the bottom. Can be dismissed at any point. "Complete Review" button at the end marks it as done.

**Goal Tracking System:**

A "Goals" feature accessible from the drawer and from the Analytics View.

Weekly Completion Goal: set a target number of tasks to complete per week. Shown as a ring chart on the home dashboard filling as tasks are completed. A "N/M tasks this week" label inside the ring. Ring color: grey when below 50%, amber at 50-80%, green when goal is met. When the goal is reached a celebration animation fires — confetti, the ring pulses green, a "Goal Met! 🎯" toast appears.

Category Goals: set a minimum number of tasks per category per week. Example: "Complete at least 5 Study tasks per week." Shown as a list of category progress bars in the Goals screen. Overachieved goals shown in green with a checkmark. Underachieved goals shown in amber with a nudge suggestion.

Goal History: a chart showing goal completion over the last 8 weeks — how many weeks the user met their weekly goal. A streak of consecutive goal-met weeks shown prominently with a trophy icon.

**Burnout Detection:**

Monitor the user's task load and behavior patterns for signs of overload. Burnout indicators: more than 15 tasks due today, overdue task count growing week over week (more overdue now than 7 days ago), completion rate dropping significantly (this week's rate is 30%+ lower than the previous week's average), Focus Score declining for 3+ consecutive days, the user has been creating tasks at a faster rate than completing them for 14+ days. When burnout indicators are detected — at least 3 of the above — show a gentle, empathetic in-app card on the dashboard: a warm gradient card (not red — don't be alarming) with a calming illustration: "You have a lot on your plate right now. It's okay to prioritize. Would you like help simplifying your task list?" Action buttons: "Review & Prioritize" (opens a filtered view of the most important tasks), "Archive Old Tasks" (offers to archive tasks older than 2 weeks), "Dismiss" (closes for 3 days). Never shame the user — the tone is supportive and understanding throughout.

**Habit Suggestions:**

After 2+ weeks of data analyze category consistency. A category is considered "inconsistent" if the user has tasks in it but their week-to-week completion rate for that category varies by more than 40%. For inconsistent categories surface a habit suggestion: "You've been inconsistent with Health tasks (38% completion last week, 72% the week before). Would you like to set a daily Health habit?" The suggestion card shows in the Analytics View in the Habit Suggestions section. Accepting opens the task creation screen pre-filled as a recurring daily task in that category. The user customizes and saves. A "Habits" section in the drawer shows all active habit tasks (recurring daily tasks) with their completion streaks — a mini streak counter on each habit.

---

## SYSTEM 6 — TIME TRACKING SYSTEM:

A complete time tracking system that lets users measure actual time spent on tasks versus estimated time.

**Timer on Every Task Card:**

Every task card has a timer button in its context — a play/pause icon that appears in the card's action area. Tapping "Start" immediately begins a timer session for that task. The task card shows an "In Progress" state: a subtle pulsing green glow around the card border, the timer running in real-time on the card showing "0:45:23" counting up, and the task status changes to "In Progress." Only one timer can run at a time — starting a timer on a second task pauses the first with a confirmation: "Pause timer on '[Task A]' and start timing '[Task B]'?"

**Background Timer:**

When the timer is running and the user navigates away from the task list (opens another feature, goes to home screen, locks the phone) the timer continues running in the background. A persistent foreground notification appears: "[Task Title] — Timer running: 0:45:23" with a Pause button and a Stop button in the notification actions. The notification updates every second. Tapping the notification navigates back to the Task Manager with that task in view. When the app returns to foreground the timer resumes from the correct elapsed time — never loses time even if the app is killed and relaunched (calculate elapsed time from the session start timestamp stored in the database, not from an in-memory counter).

**Session Management:**

Each timer start creates a new TimerSession record: id, taskId, startTime (timestamp), endTime (timestamp, null while running), durationSeconds (calculated on stop), notes (optional — user can add a note when stopping a session: "Worked on intro section"), sessionType (MANUAL for user-started, POMODORO for Focus Mode sessions).

Stopping the timer: tap the stop button on the card or in the notification. A "Stop Timer" bottom sheet slides up asking: "Add a note for this session?" with an optional text field. Confirm to save the session. The session is added to the task's time log.

Multiple sessions per task: a task accumulates sessions over multiple work periods. The total tracked time shown on the task card is the sum of all completed sessions. Running session shown separately as "In Progress: 0:45:23."

**Time Log Per Task:**

In the task detail screen a "Time Tracking" section shows: total time tracked as a large number ("2h 34m"), estimated duration below it ("Estimated: 1h 30m"), efficiency indicator comparing the two ("+64 min over estimate — 1.7x estimate" in amber, or "Under estimate by 15 min — 0.8x" in green), and a list of all sessions in reverse chronological order — each session showing: date, start time, end time, duration, and optional notes. A "Start Timer" button if no session is currently running. A "Manual Entry" button to add a session retroactively (useful if the user forgot to start the timer) — opens a date and duration picker. A "Delete Session" swipe action on each session row with confirmation.

**Time Tracking Analytics:**

In the Analytics View a "Time Tracking" section (only shown if time tracking data exists — minimum 5 sessions): Total time tracked this week as a large number, a 7-day bar chart showing hours tracked per day, a category breakdown showing tracked time per category as a donut chart, efficiency score: "On average your tasks take 1.4x your estimates — consider adding 40% buffer to your estimates," top 5 most time-consuming tasks of the week. A "Time Report" export button that generates a CSV with: task name, category, estimated duration, total tracked time, all session timestamps, efficiency ratio. Useful for freelancers and students tracking study hours.

---

## SYSTEM 7 — FOCUS & DEEP WORK SYSTEM (Enhanced):

The Pomodoro timer exists in Focus Mode View (built in Prompt 2). Significantly enhance the entire focus system.

**Do Not Disturb Integration:**

When a Focus Mode session starts offer to enable Do Not Disturb mode on the device. A one-time permission dialog: "Enable Do Not Disturb during focus sessions to avoid interruptions?" Accept → the app requests DND permission (Android: MANAGE_DND permission, iOS: Focus mode integration). When DND is enabled the app shows a small "🔕 DND Active" pill in the Focus Mode UI. When the focus session ends (or the user manually exits) DND is automatically disabled. Allow exceptions: calls from starred contacts are allowed even in DND (uses platform's priority DND mode). This is configurable in Focus settings.

**Ambient Sound System:**

Inside Focus Mode add an ambient sound player. A music note icon in the top right of the Focus Mode screen. Tapping opens a sound picker bottom sheet with 8 sound options shown as illustrated cards: Rain (💧), Forest (🌲), Café (☕), White Noise (〰), Ocean (🌊), Fire Crackling (🔥), Library (📚), Lo-Fi Beats (🎵). Each card shows the name, a play preview button, and a subtle animated icon when active. Selecting a sound starts playing it immediately at 50% volume. A volume slider in the bottom sheet. The selected sound plays on loop until the Focus session ends or the user stops it. Sound respects the device's silent mode and media volume — if media volume is muted no sound plays. Store the last used sound as a preference — auto-start it on the next Focus session. The sounds are locally bundled audio files — no internet required.

**Focus Session Statistics:**

After every Focus Mode session (exit or complete all tasks) show a "Session Summary" screen before returning to the task list. The summary shows: total session duration, number of Pomodoros completed, tasks completed during the session (with a list of their titles), total focus time added to the task's time log, the ambient sound used (if any), and a motivational message appropriate to the session length ("Great 25-minute session!", "Impressive 2-hour focus block!"). A "Share Session" button generates a small image card showing the session stats — shareable to WhatsApp or other apps. The image card has the app's dark premium aesthetic — a clean dark background, the stats in bold white typography, the app name as a subtle watermark.

**Focus Hours Tracking:**

All focus sessions (from Focus Mode, not manual timers) are tracked separately as "Focus Sessions." A "Focus Hours" metric shown in: the Analytics View (weekly focus hours bar chart, total all-time focus hours), the home dashboard as a small "Focus Time This Week: Xh Xm" stat, and the task detail screen showing Pomodoro sessions separately from manual timer sessions. A "Focus Streak" — consecutive days with at least one completed Pomodoro — tracked alongside the task completion streak. Both streaks shown on the home dashboard: the task streak with a flame icon, the focus streak with a target icon.

---

## SYSTEM 8 — TEMPLATES SYSTEM:

A comprehensive template system that makes creating repetitive task structures instant.

**Pre-Built Templates Library:**

Build a templates browser screen accessible from the FAB speed dial ("Use Template") and from the drawer. Pre-built templates organized into categories: Daily Routines, Work & Projects, Study, Health & Wellness, Travel, Finance, Personal. Each template shown as a card: template name, category badge, task count ("8 tasks"), a brief description, and a preview of the first 3 task titles.

Pre-built templates to include with full task structures:

Morning Routine: Wake up and stretch (5 min, Health, Daily, 6:30 AM), Review today's tasks (10 min, Personal, Daily, 7:00 AM), Meditate (10 min, Health, Daily, 7:15 AM), Exercise (45 min, Health, Daily, 7:30 AM), Shower and get ready (20 min, Personal, Daily, 8:30 AM), Healthy breakfast (20 min, Personal, Daily, 9:00 AM).

Weekly Review: Review completed tasks (20 min, Work, Weekly Sunday), Plan next week's priorities (30 min, Work, Weekly Sunday), Update project statuses (15 min, Work, Weekly Sunday), Check upcoming deadlines (10 min, Work, Weekly Sunday), Set weekly goal (5 min, Personal, Weekly Sunday).

Project Launch: Define project scope and goals (2 hours, Work, due in 1 day), Identify key stakeholders (1 hour, Work, due in 2 days), Create project timeline (2 hours, Work, due in 3 days), Set up project folder and documentation (1 hour, Work, due in 3 days), Schedule kickoff meeting (30 min, Work, due in 4 days), Create task breakdown (2 hours, Work, due in 5 days), Define success metrics (1 hour, Work, due in 5 days).

Exam Preparation: Review lecture notes (2 hours, Study, due in 7 days), Complete practice problems (1.5 hours, Study, due in 6 days), Create summary sheets (2 hours, Study, due in 5 days), Study with flashcards (1 hour, Study, daily for 5 days), Take practice exam (2 hours, Study, due in 3 days), Review weak areas (1.5 hours, Study, due in 2 days), Rest and light review (30 min, Study, due in 1 day).

Trip Planning: Book flights/transport (30 min, Personal, due in 14 days), Book accommodation (30 min, Personal, due in 13 days), Research destination activities (1 hour, Personal, due in 10 days), Create packing list (30 min, Personal, due in 7 days), Notify bank of travel (15 min, Finance, due in 7 days), Pack bags (1 hour, Personal, due in 1 day), Check travel documents (15 min, Personal, due in 1 day).

Monthly Finance Review: Download bank statements (15 min, Finance, due on last day of month), Categorize all expenses (30 min, Finance), Check budget vs actuals (20 min, Finance), Review upcoming bills (15 min, Finance), Update savings goals (15 min, Finance), Pay any outstanding bills (20 min, Finance).

Workout Week: Monday Push (chest, shoulders, triceps — 1 hour, Health), Tuesday Pull (back, biceps — 1 hour, Health), Wednesday Legs (1 hour, Health), Thursday Rest or light cardio (30 min, Health), Friday Full body (1 hour, Health), Saturday Cardio and core (45 min, Health), Sunday Rest and stretching (20 min, Health).

**Template Preview Screen:**

Tapping a template opens a full preview screen showing all tasks in the template as a scrollable list. Each task shows all its fields: title, estimated duration, category, priority, energy level, and relative due date ("Due in 3 days" from template application date). A "Use Template" button at the bottom.

**Template Application Flow:**

When "Use Template" is tapped open a configuration bottom sheet: Choose Start Date (the date the relative dates calculate from — default is today, but the user can pick any date), Template Variables (if the template has variable placeholders like "[Project Name]" or "[Exam Subject]" show text fields to fill them in — all placeholders in all task titles and descriptions are replaced with the user's input), Category Override (apply a single category to all tasks in the template or keep template defaults), Preview toggle. A "Create N Tasks" button that creates all tasks instantly.

After creation a success toast: "12 tasks created from 'Project Launch' template." Navigate to the List View filtered to show only the newly created tasks — letting the user review and adjust them immediately.

**Custom Template Creation:**

From any task's context menu: "Save as Template" — saves just that task as a single-task template. From the multi-select mode: "Create Template from Selected" — saves all selected tasks as a multi-task template. Template editor: set the template name, description, category, and optionally convert specific task titles to variables (highlight any word or phrase and tap "Make Variable" to turn it into a [Placeholder]).

**Template Management Screen:**

A "My Templates" section in the templates browser below the pre-built templates. Shows all custom templates as cards. Tap to preview. Long press to edit or delete. Drag to reorder. A usage count shown on each card: "Used 5 times."

---

## SYSTEM 9 — IMPORT & EXPORT SYSTEM:

**Import from External Apps:**

An "Import Tasks" screen accessible from the drawer. Import sources available:

CSV Import: upload a CSV file with columns — title, description, priority, dueDate, category, tags (comma separated), estimatedDuration (minutes), isCompleted. The app shows a column mapping screen where the user maps their CSV columns to task fields. A preview of the first 5 rows shown before confirming. Import progress bar shows "Importing N of M tasks." Error handling: rows that fail to parse are shown in an "Import Errors" list after import completes — the user can fix and retry.

JSON Backup Import: import from the app's own JSON export format — restores all task data, categories, tags, templates, and settings from a backup file.

Google Tasks Import: connect to Google Tasks via OAuth. After authorization show a list of the user's Google Task lists — select which ones to import. Maps Google Tasks fields to the app's task model: title, notes, due date, completed status. Creates corresponding categories for each imported list. A sync option to keep importing on a schedule.

Plain Text Import: paste a bullet list of tasks (one per line, preceded by - or •). The natural language parser processes each line independently. Useful for importing todos from WhatsApp messages, emails, or notes apps.

**Export System:**

Export All Tasks as CSV: generates a CSV with all task fields including all metadata. Time tracking sessions exported as separate rows with a taskId column linking them. Download to device Downloads folder and open system share sheet.

Export as PDF Report: a beautifully formatted PDF report. Cover page with the user's productivity score and week summary. Tasks section organized by category with completion status indicators. Analytics charts embedded as images (completion rate, category distribution). Export options: All Tasks, Completed Tasks Only, Overdue Tasks Only, Specific Category, Date Range. The PDF matches the app's dark aesthetic — dark background, colored text, premium typography.

Export Time Log as CSV: all TimerSession records with: task name, category, session date, start time, end time, duration (hours:minutes), session notes. Useful for billing, academic time logging, or productivity analysis in external tools.

Export Selected Tasks: from multi-select mode in the list view an "Export" action exports only the selected tasks.

**Share Individual Task:**

From any task's context menu a "Share" option. Share formats: Text Summary — a formatted text showing task title, due date, priority, description, and subtasks — formatted for WhatsApp ("📋 Task: [Title]\n📅 Due: [Date]\n⚡ Priority: [Priority]\nNotes: [Description]"). Image Card — generates a premium styled image: dark background, task title large, due date, priority badge, category, and the app watermark. Optimized size for WhatsApp status and Instagram stories (1080x1920). Calendar Event — creates an ICS file that can be added to any calendar app. Opens system share sheet after generation.

---

## SYSTEM 10 — TASK ACTIVITY LOG:

Every task has a complete activity log tracking its full history. This makes tasks feel like living records rather than static items.

**Activity Types to Log:**

Task created (with creation method: manual, NLP, template, import), title changed (old → new), description updated, priority changed (from → to), category changed (from → to), due date set (date), due date changed (old → new), due date removed, reminder added (details), reminder removed, subtask added (title), subtask completed (title), subtask uncompleted (title), subtask deleted (title), tag added (tag name), tag removed (tag name), dependency added (linked task title), dependency removed, time tracking session started, time tracking session ended (duration), note added to session, status changed (active → in progress → completed → archived), task completed (timestamp), task reopened, attachment added, attachment removed, recurrence changed, task duplicated from (source task title), task moved to category, task starred/unstarred.

**Activity Log UI:**

In the task detail screen below all other sections a "History" section with a "Show history" expand button. When expanded shows the full activity log as a clean timeline: each entry shows a small colored icon matching the action type (pencil for edit, calendar for date change, checkmark for completion, clock for time tracking, etc.), the action description in natural language ("Priority changed from Normal to High"), and the timestamp ("Feb 28 at 3:45 PM" or "2 hours ago" for recent items). The timeline goes from oldest at top to newest at bottom. A "Clear History" button at the bottom (removes the log entries — doesn't affect the task itself). Activity log entries are stored with the task and survive app reinstalls if backed up.

**Global Activity Feed (Enhanced):**

The activity feed on the home dashboard already exists from Prompt 2. Enhance it: every logged activity across all tasks feeds into the global feed. Filter the global feed by: All, Completions only, Overdue events, Creations, High/Urgent priority changes. A "Today" / "This Week" tab switch. Search within the activity feed. Export the global activity log as a CSV for journaling or personal records.

---

## PERFORMANCE & ARCHITECTURE REQUIREMENTS:

The NLP parser must respond within 200ms even on low-end devices — use efficient string processing, not complex ML models. Natural language parsing happens synchronously on the main thread since it must be instant and is not CPU-intensive enough to warrant a background thread.

The background intelligence service (overdue prediction, priority escalation detection, habit analysis, burnout detection) runs on a background isolate scheduled every 4 hours — never impacts UI performance.

The Smart Scheduling algorithm runs on a background thread — the "Schedule My Day" button shows a brief loading animation while the algorithm computes, then presents the result. Target: under 2 seconds for up to 50 tasks.

All timer operations (start, stop, update, background persistence) are handled via a dedicated TimerService that maintains state independently of the UI layer — the timer is never lost due to navigation changes or app backgrounding.

Template application with 20+ tasks must complete in under 1 second — all tasks created in a single batch database operation.

Import of 500+ tasks from CSV must show accurate progress and complete without freezing the UI — run on a background thread with progress updates sent to the UI thread via stream.

All suggestion banners (duplicate detection, priority escalation, auto-categorization) must appear within 300ms of the trigger condition being met — pre-compute where possible.

---

**Build every single system in this prompt completely. Natural Language Parsing with all extraction types, live preview card, confidence indicators, and learning from corrections. Smart Scheduling with the algorithm, proposed schedule screen, user adjustment, and morning notification. Smart Reminders with all 6 types — time-based, pre-task prep, escalating, location-based, smart snooze, and weather-aware. Task Intelligence with overdue prediction, priority escalation, auto-categorization, duplicate detection, dependency detection, duration suggestions, and smart tags. Productivity Intelligence with the full Productivity Score calculation, peak performance insights, complete Weekly Review flow, goal tracking, burnout detection, and habit suggestions. Time Tracking with background timer, session management, time log UI, and time tracking analytics. Enhanced Focus System with DND integration, ambient sounds, session summary, and focus hour tracking. Complete Templates System with all 7 pre-built templates, preview, application flow, custom template creation, and template management. Import and Export with CSV, JSON, Google Tasks, plain text import and CSV, PDF, time log, and individual task share export. Full Task Activity Log with all activity types, timeline UI, and global feed enhancement. All performance requirements must be met. Do not leave any system partially implemented.**

Important:

if any mentioned feature is already implemented dont rewrite it. enhance the feature to the level best
