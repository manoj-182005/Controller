# 🏆 4 Grand Prompts — Premium Task Manager (Claude Opus 4.6)

> These prompts are designed for Claude Opus 4.6 to be integrated into your mobile app's **card feature**. Each prompt covers a distinct pillar of a world-class task manager. Use them sequentially or independently.

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## 🟣 PROMPT 1 — CORE TASK ENGINE + SMART UI CARDS
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

```
You are building the CORE TASK ENGINE for a world-class premium mobile task manager — the most beautifully designed and feature-rich task manager ever built for Android/iOS. This is Prompt 1 of 4, focusing on the Task Card UI and Core Engine.

DESIGN PHILOSOPHY:
- Dark-first premium aesthetic (deep navy #0A0F1E base, not pure black)
- Glassmorphism cards with subtle frosted borders
- Color-coded priority system: Low (slate), Normal (blue), High (amber), Urgent (crimson red)
- Every card must breathe — generous padding, micro-shadows, smooth 300ms transitions
- Typography: Display font for task titles, clean sans for metadata
- Every interactive element must have a satisfying haptic-like visual feedback (scale + glow on tap)

TASK CARD must contain and beautifully display ALL of these fields:
1. Task Title (bold, prominent, with strikethrough animation on completion)
2. Description / Notes (collapsible, supports markdown, links, rich text)
3. Priority Badge (Low / Normal / High / Urgent) — color-coded pill
4. Category with Icon + Color accent bar on left edge (Personal, Work, Study, Health, Shopping, Finance, Others + custom)
5. Tags (horizontal scrollable chips, each with unique pastel color)
6. Due Date & Time (shown as "Today at 9:00 AM", "Tomorrow", "Mar 5", relative when close)
7. Reminder indicator (bell icon with count badge)
8. Recurrence indicator (repeat icon: Daily, Weekly, Monthly, Yearly, Custom)
9. Estimated Duration (clock icon: "~45 min")
10. Actual Time Tracked (stopwatch icon showing time logged)
11. Progress Bar (for tasks with subtasks — e.g., "3/5 subtasks done")
12. Subtasks mini-preview (show first 2 subtasks with checkboxes inline on card)
13. Attachments count badge (paperclip icon)
14. Location/Geofence indicator (pin icon when location reminder is set)
15. Energy Level tag (⚡ Deep Work / 🌿 Light / 😴 Low Energy)
16. Task Dependencies indicator (link icon showing "Blocked by 1 task")
17. Star/Favorite toggle (animated fill)
18. Completion checkbox (satisfying animated checkmark)
19. Context menu (⋯ button): Edit, Duplicate, Move, Share, Delete, Add to Focus, Start Timer

CARD STATES to handle:
- Default (clean, full info)
- Compact (collapsed, title + priority + due date only)
- Focused (expanded with all subtasks visible)
- Completed (dimmed, strikethrough, moved to bottom)
- Overdue (red left border glow, urgent visual treatment)
- In Progress (pulsing timer indicator, active glow)

SWIPE GESTURES:
- Swipe Right: Complete task (green checkmark sweep animation)
- Swipe Left: Delete / Archive (red trash sweep)
- Long Press: Multi-select mode with batch actions toolbar

SMART FEATURES on the card:
- Auto-detect URLs in notes and show as rich link previews
- Smart due date chip turns red when overdue, amber when due today
- If task has location reminder, show a subtle map thumbnail on expand
- If a recurring task, show next occurrence on card footer
- Subtask progress ring on the card avatar/icon area

ANIMATIONS:
- Card entrance: staggered slide-up with fade (50ms delay between cards)
- Completion: checkmark draws itself → card collapses with satisfying spring → flies to "Done" section
- Priority change: smooth color morph transition
- Expand/collapse: spring physics, not linear easing

OUTPUT: Provide the complete React Native (or Flutter) component code for this premium TaskCard, with all states, gestures, animations, and all 19 fields beautifully integrated. Include the design tokens/theme file. Make it production-ready.
```

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## 🔵 PROMPT 2 — VIEWS, CALENDAR & VISUALIZATION ENGINE
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

```
You are building the VIEWS & VISUALIZATION ENGINE for a world-class premium mobile task manager. This is Prompt 2 of 4. The app already has a dark premium aesthetic (deep navy base, glassmorphism cards, color-coded categories). Now build every view and the full Calendar integration.

EXISTING CONTEXT:
- The app has: All Tasks, Today, Upcoming, Overdue, Starred, Completed, Trash views
- Categories: Personal, Work, Study, Health, Shopping, Finance, Others
- Task fields include: title, priority, category, tags, due date/time, reminders, recurrence, estimated duration, time tracked, subtasks, attachments, location, energy level, dependencies

BUILD ALL THESE VIEWS — each must be stunning and functional:

━━━ 1. LIST VIEW (Default) ━━━
- Grouped by: None / Priority / Category / Due Date / Energy Level / Tag
- Sort by: Priority / Due Date / Created / Alphabetical / Manual drag-reorder
- Section headers with task count badges and collapse/expand
- Sticky headers that fade as you scroll past them
- Empty states with illustrated motivational art (not generic "no tasks" text)

━━━ 2. CALENDAR VIEW (FULL PREMIUM) ━━━
This must rival or exceed Google Calendar and Fantastical:
- Month view: task dots under dates (color = category color), tap date to see tasks in bottom sheet
- Week view: horizontal time-blocked layout with tasks as colored blocks showing duration
- Day view: hour-by-hour timeline with tasks placed at their scheduled time
- Agenda view: clean scrollable list grouped by date
- Heat map overlay: show productivity intensity (darker = more tasks completed)
- Drag-and-drop tasks directly on calendar to reschedule
- Long-press empty slot to create task at that time instantly
- Today indicator: glowing animated line across the current time
- Month calendar mini-widget at top of Week/Day view for quick navigation
- Recurring task indicators (dashed border on repeating event blocks)
- Conflict detection: visually stack overlapping tasks side-by-side
- Swipe left/right to navigate between days/weeks/months
- Bottom sheet when tapping a date: smooth spring animation showing that day's tasks

━━━ 3. KANBAN VIEW ━━━
- Columns: To Do / In Progress / Done (customizable)
- Cards show: title, priority badge, due date, assignee avatar, subtask progress
- Drag cards between columns with smooth physics
- Column task count + total estimated time shown in header
- Add task directly in any column
- Collapse columns to save space

━━━ 4. TIME BLOCK VIEW ━━━
- Visual daily planner: drag tasks into time slots
- Shows free vs. busy time at a glance
- Estimated duration blocks render as proportional height
- Conflict warnings when overlapping
- Morning / Afternoon / Evening sections with subtle color shifts
- "Schedule All" AI button that auto-distributes tasks based on priority + energy level + estimated time

━━━ 5. FOCUS MODE VIEW ━━━
- Full screen, distraction-free
- Shows ONE task at a time — large, centered
- Built-in Pomodoro timer: 25 min work / 5 min break (customizable)
- Timer ring animation around task card
- Session history: how many pomodoros completed today
- "Done" swipe to move to next task
- Ambient background that shifts color based on timer phase (focus=deep blue, break=soft green)
- Progress: "Task 3 of 8 Today"

━━━ 6. DASHBOARD / HOME VIEW ━━━
- Stats cards row: Today / Done / Overdue (already exists — enhance it)
- Streak tracker with flame animation (days in a row completing tasks)
- Weekly completion sparkline chart
- Category distribution donut chart (tap to filter by category)
- "Today's Focus" smart section (AI-picked top 3 priority tasks)
- Upcoming deadlines timeline scroll
- Recent activity feed (completed, added, overdue events)
- Motivational quote that changes daily
- Productivity score (0-100) based on on-time completion rate

━━━ 7. ANALYTICS VIEW ━━━
- Weekly / Monthly / Yearly toggle
- Tasks completed vs. created trend line chart
- On-time completion rate percentage
- Average completion time vs. estimated time
- Most productive day of week (bar chart)
- Most productive time of day (heat map grid)
- Category breakdown (pie/donut)
- Tag usage frequency
- Streak history calendar (GitHub-style contribution graph)
- Exportable as PDF report

NAVIGATION:
- Bottom tab bar: Home | Calendar | Add Task (+) | Views | Profile
- Tab switching: smooth shared element transitions
- Floating action button (+) opens a quick-add bottom sheet with smart parsing:
  "Call dentist tomorrow at 3pm urgent" → auto-fills title, date, time, priority

OUTPUT: Provide complete implementation code for all 7 views with navigation, animations, charts (use your preferred chart library), and all interactions described. Include the calendar integration fully. Make every empty state, loading state, and transition pixel-perfect.
```

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## 🟠 PROMPT 3 — AI INTELLIGENCE + PRODUCTIVITY SYSTEM
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

```
You are building the AI INTELLIGENCE LAYER and PRODUCTIVITY SYSTEM for a world-class premium mobile task manager. This is Prompt 3 of 4. The app has beautiful dark premium UI, full task cards, calendar view, kanban, focus mode, and analytics. Now add the intelligent brain that makes it the smartest task manager ever built.

SMART TASK CREATION — Natural Language Parsing:
Build a natural language input engine that understands:
- "Call mom every Sunday at 6pm" → recurring, Sunday, 6PM, call mom
- "Submit report by Friday high priority work" → due Friday, High priority, Work category
- "Buy groceries tomorrow shopping 30 min" → Tomorrow, Shopping category, 30min duration
- "Team meeting next Monday 10am to 11am zoom" → date, time, duration, note=zoom
- Show a parsed preview card below input before confirming
- Confidence indicators for each parsed field (tap to correct)
- Learn from corrections over time

AI SMART SCHEDULING:
- "Schedule My Day" button: analyzes all pending tasks, your calendar availability, estimated durations, energy levels, and priority → auto-creates an optimal daily plan
- Respects: working hours preference, break times, energy levels (deep work in morning, light tasks in afternoon)
- Shows a proposed schedule as a beautiful time-block visual
- User can drag-adjust before confirming
- Conflict warnings with suggestions

SMART REMINDERS SYSTEM (beyond basic alarms):
- Location-based reminders: "Remind me when I arrive at the gym" (geofencing)
- Context-aware reminders: "Remind me when I open WhatsApp to reply to John"
- Smart snooze: learns your snooze patterns and suggests better reminder times
- Pre-task reminders: "You have a 1hr task at 3pm — start preparing now" (sends at 2:30pm)
- Weather-aware: "Your outdoor task is scheduled — rain expected, want to reschedule?"
- Escalating reminders: sends reminder → if no action in 10min → sends urgent follow-up

TASK INTELLIGENCE FEATURES:
- Overdue prediction: flags tasks likely to be missed based on your patterns
- Priority suggestions: "This task has been pending 5 days — consider upgrading priority"
- Auto-categorization: suggests category based on task title
- Duplicate detection: warns when adding similar task to existing one
- Dependency auto-detection: "This task seems related to [Project X] — add dependency?"
- Estimated duration suggestions based on similar past tasks
- Smart tags: auto-suggests tags based on task content

PRODUCTIVITY INTELLIGENCE:
- Productivity Score (0-100): calculated from on-time rate, streak, daily completion
- Peak Performance Insights: "You complete 80% more tasks before 11am — schedule important work then"
- Weekly Review prompt every Sunday: summarizes week, asks reflection questions
- Goal tracking: set weekly task completion goals, track progress with visual ring
- Burnout detection: if task load is too high, gently warns and suggests prioritization
- Habit suggestions: "You've been inconsistent with Health tasks — want to set a daily habit?"

FOCUS & DEEP WORK SYSTEM:
- Pomodoro timer fully integrated (customizable: work/short break/long break durations)
- Focus session logging: track total deep work hours per day/week
- "Do Not Disturb" mode during focus: mutes all non-urgent notifications
- Focus music/ambient sound selector (lo-fi, white noise, rain, café) — link to external app or built-in
- After focus session: show micro celebration + task auto-marked progress
- Weekly focus hours chart

TIME TRACKING SYSTEM:
- One-tap start/stop timer on any task card
- Timer runs in background, shows persistent notification
- Multiple sessions per task accumulate
- Time log per task: see all sessions with timestamps
- Compare estimated vs. actual time with efficiency score
- Export time logs (CSV) for billing/reporting
- Weekly time distribution chart by category

TEMPLATES SYSTEM:
- Pre-built templates: Morning Routine, Weekly Review, Project Launch, Trip Planning, etc.
- Create custom templates from any task or group of tasks
- Apply template: instantly creates all tasks with relative dates
- Template variables: "[Project Name]", "[Deadline]" filled on apply
- Community template library (browse/import popular templates)

IMPORT/EXPORT & INTEGRATIONS:
- Import from: Todoist, TickTick, Google Tasks, Apple Reminders, Notion, CSV
- Export to: CSV, PDF report, JSON backup
- Calendar sync: Google Calendar, Apple Calendar (two-way sync)
- Zapier/webhook integration for automation
- Share task as: link, text summary, image card (Instagram/WhatsApp-friendly)

COLLABORATION FEATURES:
- Share individual tasks with anyone via link
- Assign tasks to contacts (they get notified, can update status)
- Task comments thread (each task has a discussion section)
- Activity log per task: "You completed this subtask at 2:30pm"
- Shared category/project lists with team members
- Permission levels: viewer / editor / admin

OUTPUT: Build the complete code architecture and UI for all systems above. For the AI features, use the Claude API (model: claude-opus-4-6) for natural language parsing, smart scheduling suggestions, and intelligent insights. Show the API integration pattern. Include all UI screens for: smart task creation, schedule view, focus mode with timer, time tracking panel, templates browser, collaboration share sheet, and analytics insights cards. Everything must match the premium dark aesthetic already established.
```

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## 🟢 PROMPT 4 — SETTINGS, PERSONALIZATION & PREMIUM POLISH
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

```
You are building the SETTINGS, PERSONALIZATION ENGINE and FINAL PREMIUM POLISH for a world-class mobile task manager. This is Prompt 4 of 4. The app now has: premium dark UI task cards, 7 views including full calendar, AI intelligence layer, Pomodoro focus mode, time tracking, templates, and collaboration. Now make it feel truly premium — personalized, polished, and delightful in every interaction.

━━━ SETTINGS ARCHITECTURE ━━━
Build a beautiful Settings screen organized as:

APPEARANCE:
- Theme: Dark (default) / Light / Auto (system) / AMOLED Black
- Accent color picker: 12 preset colors + custom hex input
- App icon variants: 8 icon designs to choose from
- Card density: Comfortable / Compact / Cozy
- Font size: Small / Medium / Large / Extra Large
- Animation speed: Full / Reduced / Off (accessibility)
- Card style: Glassmorphism / Flat / Outlined / Elevated shadow

TASK DEFAULTS:
- Default priority for new tasks
- Default category for new tasks
- Default reminder offset (e.g., 30 min before)
- Default estimated duration
- First day of week (Sunday / Monday)
- Date format (MM/DD, DD/MM, relative)
- Time format (12h / 24h)

NOTIFICATIONS & REMINDERS:
- Master notification toggle
- Per-category notification settings
- Reminder sound selector (10+ sounds + custom)
- Vibration pattern selector
- Badge count: show pending / show overdue / show today only / off
- Daily summary notification time ("Send me a daily briefing at 8am")
- Weekly review prompt (day + time)
- Escalating reminder settings

PRODUCTIVITY SETTINGS:
- Pomodoro: Work duration / Short break / Long break / Long break interval
- Auto-start next Pomodoro toggle
- Focus mode: enable/disable sounds, auto-DND
- Working hours: Start time / End time (used for smart scheduling)
- Energy peak hours: Morning person / Night owl / Custom
- Daily task limit warning (alert if >10 tasks due today)
- Streak reminder time

CALENDAR SETTINGS:
- Default calendar view (Month / Week / Day / Agenda)
- Show/hide weekends
- Calendar accounts to sync (Google / Apple)
- Event color source: use task priority color / category color
- Show completed tasks on calendar toggle
- Time zone display

CATEGORIES MANAGEMENT:
- Full CRUD for categories (already shown in screenshots — enhance it)
- Reorder categories via drag
- Archive unused categories
- Category-level settings: default priority, default tags, notification tone

TAGS MANAGEMENT:
- View all tags with usage count
- Merge tags, rename, delete
- Color-assign tags
- Pin frequent tags to quick-access

DATA & BACKUP:
- Cloud backup: iCloud / Google Drive / Dropbox
- Auto-backup frequency: Daily / Weekly / Manual
- Last backup timestamp
- Restore from backup
- Export all data (JSON / CSV / PDF)
- Clear completed tasks older than: 7 days / 30 days / 90 days / Never
- Full data wipe with confirmation

SECURITY & PRIVACY:
- App lock: Face ID / Fingerprint / PIN
- Lock after: Immediately / 1 min / 5 min / 15 min
- Hide task content in app switcher
- Data stored locally toggle (offline-only mode)
- Privacy policy / Data usage info

PREMIUM SUBSCRIPTION SCREEN:
Build a gorgeous paywall/premium screen:
- Three tiers: Free / Pro ($4.99/mo) / Lifetime ($49.99)
- Feature comparison table (animated reveal)
- Pro features: AI scheduling, time tracking, collaboration, advanced analytics, calendar sync, unlimited categories/tags, custom themes, priority support
- "Most Popular" badge on Pro
- 7-day free trial CTA button
- Testimonials carousel
- Restore purchase option

━━━ ONBOARDING EXPERIENCE ━━━
5-screen premium onboarding (shown only on first launch):
1. Welcome screen: animated logo, tagline "The last task manager you'll ever need"
2. Choose your style: Dark / Light / AMOLED theme selector
3. Import existing tasks: Todoist / TickTick / Google Tasks / Skip
4. Set your working hours + energy peak time
5. Quick tutorial: animated walkthrough of 3 core gestures (swipe complete, long press, quick add)
- Skip available at all times
- Progress dots at bottom
- "Get Started" → goes to home with sample tasks pre-populated

━━━ WIDGET SYSTEM ━━━
Build home screen widgets (iOS/Android):
- Small (2x2): Today's task count + next due task
- Medium (4x2): Today's Focus list (top 3 tasks with checkboxes)
- Large (4x4): Full today view with mini calendar strip
- Extra Large (4x6): Today + upcoming + streak info
- All widgets: match app theme, update every 15 minutes
- Tap widget task → opens app directly to that task

━━━ PREMIUM MICRO-INTERACTIONS & POLISH ━━━
Every single one of these must be implemented:
- Task completion: checkmark draws → card glows green → collapses with spring → confetti burst if it was an urgent/important task
- Streak milestone: flame animation with counter flip when streak increases
- New task added: card slides in from bottom with spring bounce
- Delete: card crumples and shrinks away (physics)
- Priority change: background color morphs smoothly across the card
- Empty state animations: illustrated character that reacts (thumbs up when all done, sleeping when no tasks due)
- Pull to refresh: custom branded loading animation (not default spinner)
- Long press haptic: visual ripple + subtle scale
- Bottom sheet open: spring up with blur backdrop
- Success states: subtle particle effects (not overdone — tasteful)
- Error states: card shakes gently
- Loading skeleton screens (shimmer effect) instead of spinners
- Satisfying sound design: soft completion chime, gentle swipe whoosh (respects silent mode)

━━━ ACCESSIBILITY ━━━
- Full VoiceOver / TalkBack support with meaningful labels
- Reduced motion mode (no spring physics, instant transitions)
- High contrast mode
- Dynamic type support (all text scales with system font size)
- Color-blind friendly mode (patterns instead of color alone for priority)
- Minimum tap target: 44x44pt

━━━ PERFORMANCE REQUIREMENTS ━━━
- List of 1000+ tasks must scroll at 60fps
- App cold start < 1.5 seconds
- Optimistic UI updates (instant feedback, sync in background)
- Offline-first: all actions work without internet
- Background sync when connection returns
- Image/attachment lazy loading
- Virtualized lists for all task views

OUTPUT: Provide the complete, production-ready code for: Settings screen with all sections, Premium paywall screen, Onboarding flow (all 5 screens), Widget configurations, and a micro-interactions implementation guide with code for every animation listed. Include the complete app theme system with all variants (Dark/Light/AMOLED/Accent colors). Every screen must match the premium dark glassmorphism aesthetic of the existing app. This should look and feel like it costs $49.99 and is worth every penny.
```

---

## 📋 HOW TO USE THESE PROMPTS

| Prompt | Focus | What You Get |
|--------|-------|--------------|
| **Prompt 1** | Task Card Engine | The perfect task card with all 19 fields, all states, gestures, animations |
| **Prompt 2** | Views & Calendar | 7 complete views: List, Calendar, Kanban, Time Block, Focus, Dashboard, Analytics |
| **Prompt 3** | AI & Productivity | NLP input, smart scheduling, time tracking, templates, collaboration, integrations |
| **Prompt 4** | Settings & Polish | Full settings, onboarding, widgets, micro-interactions, premium paywall |

### Tips for Best Results with Claude Opus 4.6:
- Run each prompt **separately** in a fresh conversation
- After Prompt 1, paste the **theme/design tokens** output into Prompts 2, 3, 4 for consistency
- Tell Opus: *"Continue from the exact dark navy #0A0F1E aesthetic, glassmorphism cards, and component library established in Prompt 1"*
- Request **platform-specific** output: specify React Native / Flutter / SwiftUI / Jetpack Compose

---

*Built for Claude Opus 4.6 — The most powerful model for building the most powerful task manager.*
