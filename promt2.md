The Task Manager feature already has its core task cards, data layer, categories, priority system, stat cards, streak banner, quick add bar, filter chips, productivity dashboard, and the home screen redesigned with vibrant gradients matching the Expense Tracker's aesthetic. All of that is working. Now build Prompt 2 — the complete Views and Visualization Engine. This means 7 fully built views: List View, Calendar View, Kanban Board, Time Block Planner, Focus Mode, Dashboard Home, and Analytics. Go through the entire Task Manager codebase extremely thoroughly before touching anything — understand every model, every service, every existing screen, every navigation route, and every design token already established. Do not break anything already working. This is a large prompt — build every single item listed here completely and correctly.

---

## DESIGN FOUNDATION — Read Before Building Anything:

Every view built in this prompt must follow this exact visual language already established in the app: deep navy background (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0A0F1E` or closest match used in the app), glassmorphism-style cards with subtle frosted borders and translucency, color-coded priority system (Low = slate/grey, Normal = blue, High = amber/orange, Urgent = crimson red with glow), generous padding and breathing room between elements, 300ms smooth transitions as the standard, display-weight font for titles and clean sans for metadata, every interactive element has a scale + glow visual feedback on tap, and the same vibrant gradient stat cards established in Prompt 1. Never deviate from this aesthetic in any new screen.

---

## VIEW 1 — LIST VIEW (Enhanced Default):

The List View is already partially built — the home screen shows tasks in a list. Enhance it to be the most powerful list view available in any task manager.

**Grouping System:**
A "Group By" selector accessible from the sort button in the header. Options: No Grouping (flat list), Group by Priority (Urgent / High / Normal / Low sections), Group by Category (each category as a collapsible section), Group by Due Date (Overdue / Today / Tomorrow / This Week / Next Week / Later / No Due Date), Group by Energy Level (Deep Work / Light / Low Energy sections), Group by Tag (one section per tag — tasks with multiple tags appear in multiple sections), Group by Status (Active / In Progress / Completed). Each grouping mode is persisted — the user's last choice is remembered.

**Section Headers:**
Each group section has a premium styled header: the section name in semibold white, a task count badge as a colored pill on the right, a collapse/expand chevron that animates with spring physics when tapped. Collapsed sections show only the header — all task cards inside are hidden with a smooth height collapse animation. Section headers are sticky — they stick to the top of the screen as the user scrolls through the tasks in that section, then unstick when the next section header reaches the top. As a section header approaches the sticky position it has a subtle fade-in blur background so it reads cleanly over the content behind it.

**Sorting System:**
Independent of grouping — within each group (or the full list if ungrouped) tasks are sorted by: Priority (Urgent first descending to None), Due Date (earliest first), Date Created (newest first), Date Created (oldest first), Alphabetical A-Z, Alphabetical Z-A, Estimated Duration (shortest first or longest first), Manual (drag to reorder — handle appears on right side of each card when manual sort is active). The current sort shown as a label on the sort button: "Sort: Due Date ▾".

**Manual Drag Reorder:**
When Manual sort is active every task card shows a drag handle (three horizontal lines icon) on the right side. Long pressing the drag handle lifts the card — it scales to 1.03x with an increased shadow and a subtle glow matching its priority color. Other cards smoothly shift to show the insertion position as the card is dragged. Releasing drops the card with a spring settle animation and saves the new order. Within grouped mode manual reorder works within each group independently.

**Quick Filters Bar:**
Below the main filter chips row add a secondary "Quick Filters" bar that appears when the filter icon is tapped. A horizontal scrollable row of additional filter chips that combine with the main filter: Has Reminder, Has Attachments, Has Subtasks, Is Starred, Has Location, Overdue, Due Today, Due This Week, High+ Priority, Deep Work Energy, No Category, No Due Date, Recurring, In Progress Timer. These are additive — selecting "Has Reminder" + "Due Today" shows only tasks due today that also have reminders. Applied quick filters show as dismissable chips below the search bar. A "Clear Filters" button appears when any quick filter is active.

**Advanced Search in List View:**
The search bar at the top searches across: task titles, descriptions, tags, category names, note content, subtask titles, and attachment names. Results highlight the matching text in each card. A "Search within this filter" mode where search only finds tasks that match the current active filter (e.g., search for "Java" within the "Study" category filter). Search history shows the last 5 searches as tappable chips before typing.

**Empty States — Illustrated for Every Filter:**
All Tasks empty: an illustrated rocket launching, "No tasks yet — add your first task and get moving!" Today empty: sun illustration, "Nothing due today — enjoy your day or plan ahead!" Overdue empty: celebration confetti, "Nothing overdue — you're absolutely crushing it!" Starred empty: constellation of stars, "No starred tasks — star the ones that matter most." Completed empty: trophy illustration, "No completed tasks yet — finish one to see it here." Search no results: magnifying glass illustration, "No tasks match '[query]'" with a Clear Search button. Every empty state has an action button — never a dead end.

---

## VIEW 2 — CALENDAR VIEW (Full Premium — Rivals Fantastical):

A completely new Calendar view for the Task Manager. This is not the existing Calendar feature in the app — this is a Task-specific calendar that shows tasks on a calendar based on their due dates. Accessible from the bottom navigation or a calendar icon in the header.

**View Modes:**
Four calendar modes switchable via a segmented control at the top: Month, Week, Day, Agenda. Smooth animated transition between modes — not a hard swap. The selected mode is remembered per session.

**MONTH VIEW:**

The month grid fills most of the screen. Each day cell shows: the date number top-left, task count dots below the number (up to 3 dots, each the color of the category of that task — if more than 3 tasks exist show "..." in the accent color), a subtle background fill for today's cell (the accent color at 15% opacity), and a past-date dimming (days before today shown at 60% opacity).

Tapping any date opens a bottom sheet — spring animation sliding up from the bottom with a blur backdrop. The bottom sheet shows that day's tasks as a scrollable list of task cards (compact mode — title, priority badge, category, time). The bottom sheet's drag handle is visible and it can be dragged to full screen to see the full Agenda View for that day. The bottom sheet has a "Add Task for [Date]" button at the top.

Heat map overlay toggle: a toggle button in the header switches the month grid to heat map mode. Each day cell fills with a color indicating productivity intensity — no tasks completed = darkest navy, 1-2 = slight blue tint, 3-5 = medium blue, 6+ = bright accent blue. This shows productive vs unproductive days at a glance. The color scale legend is shown as a small bar below the calendar.

Navigation: swipe left to go to the next month, swipe right for the previous. Header shows "February 2026" with left/right arrow buttons. A "Today" button snaps back to the current month with a smooth scroll and highlights today.

**WEEK VIEW:**

A 7-column grid showing the current week. Each column is a day. Time slots run vertically from the earliest task time to the latest (or 6 AM to 11 PM by default). Tasks are shown as colored blocks in their column at the correct time position. Block height is proportional to the task's estimated duration — a 1-hour task is twice as tall as a 30-minute task. If a task has no estimated duration it shows as a fixed minimum height block. Block content: task title (truncated if block is small), category color as the block background gradient, priority indicator as a left border color. Overlapping tasks (same time slot) are shown side by side — each taking a fraction of the column width, stacked horizontally. A glowing animated horizontal line shows the current time across all columns. Tapping a task block opens the task detail. Tapping an empty time slot opens quick task creation pre-filled with that date and time. Long pressing a task block enters drag mode — drag it to a different day column or a different time slot to reschedule it. Release drops the task and updates its due date/time with a confirmation toast "Task rescheduled to Tuesday at 2 PM." Swipe left/right to navigate between weeks.

A mini month calendar sits at the top of the week view — a compact 5-row month grid showing the current month. The current week is highlighted. Tapping any date in the mini calendar jumps to that week.

**DAY VIEW:**

A single-day hour-by-hour timeline. Hours run vertically from midnight or from the earliest task (configurable). Each hour is a labeled row with a subtle dividing line. Tasks appear as blocks at their scheduled time with full width (minus padding). Block content: task title bold, time range if duration is set ("9:00 AM – 10:00 AM"), category chip, priority left border glow. The current time shown as a glowing red/accent horizontal line with a small dot on the left edge. Scrollable — the view auto-scrolls to the current time on open. All-day tasks (no specific time) shown in a pinned strip at the top before the timeline. Tapping empty slot creates a task at that time. Long press to drag and reschedule. "Morning", "Afternoon", "Evening" soft section backgrounds — very subtle color shifts making the day sections visually distinct. Swipe left/right to navigate between days.

**AGENDA VIEW:**

A clean chronological list of all upcoming tasks grouped by date. Section headers for each date: "Today — Saturday, Feb 28", "Tomorrow — Sunday, Mar 1", "Monday, Mar 2", etc. Past dates not shown (or collapsed behind a "Show Past" toggle). Each task shown as a compact row: time on the left (or "All Day"), task title, category color dot, priority badge. Completed tasks shown in a muted style. An "Overdue" section at the very top in red before Today. Infinitely scrollable into the future. Tapping any task opens its detail. A "Jump to Date" button opens a date picker to navigate to a specific date. Swipe to complete or delete tasks inline.

**DRAG AND DROP ACROSS CALENDAR:**
In both Week and Day views long pressing any task block enables drag mode. The block lifts (scale up, shadow, glow) and can be dragged to any other position on the calendar. In Week View dragging left or right between columns changes the day. Dragging up or down changes the time. The time snaps to 15-minute intervals while dragging. Other tasks in the destination slot shift slightly to show where the dropped task will land. Releasing triggers a smooth spring settle and shows a "Task rescheduled" snack bar with an "Undo" button that works for 5 seconds. This reschedule updates the task's due date and time in the database.

**CONFLICT DETECTION:**
When two or more tasks overlap in the same time slot in Week or Day view show a visual conflict indicator — a subtle amber warning stripe between them. A "Conflicts" badge in the header shows the count of time conflicts today. Tapping the badge shows a list of all conflicts with "Resolve" options: move one task earlier, move it later, or remove the time from one task.

**LONG-PRESS TO CREATE:**
Long pressing any empty area in Week or Day view opens the quick task creation bottom sheet pre-filled with the date and time of the long-pressed slot. The time is calculated from the vertical position of the press — pressing at the 2 PM slot pre-fills "2:00 PM today." This makes task creation from the calendar feel natural and fast.

**RECURRING TASK DISPLAY:**
Recurring tasks show on every applicable date in the calendar. They have a dashed border around their block (instead of solid) to visually indicate repetition. In Month View recurring task dots have a small circular arrow overlay. Tapping a recurring task in the calendar opens the standard task detail — editing from here shows the "Edit this occurrence / Edit all / Edit this and future" scope selector.

---

## VIEW 3 — KANBAN BOARD VIEW:

A full Kanban board with drag-and-drop cards between columns. Accessible from the Views menu.

**Columns:**
Default columns: To Do, In Progress, Completed. Plus an optional Cancelled column (hidden by default). Each column is a vertical scrollable lane. The user can add custom columns, rename existing ones, reorder columns by drag, and delete empty columns. Column configuration accessible from a "Manage Columns" button in the board header. Column colors: To Do uses the accent color, In Progress uses amber, Completed uses green, Cancelled uses grey. Column header: column name bold, task count badge, total estimated time for all tasks in that column ("3h 15m").

**Task Cards in Kanban:**
Each task card shows: task title bold, priority badge as a colored pill, due date chip (red if overdue, amber if today), category color as a left border accent, subtask progress bar if subtasks exist ("2/4"), attachment count badge, a timer button if the task is in In Progress column. Cards are compact — enough to see essential info at a glance.

**Drag Between Columns:**
Long pressing any card lifts it — scale to 1.04x, shadow increases, glow matches priority color. The card can be dragged horizontally between columns and vertically within a column. The destination column highlights subtly as the card hovers over it. Other cards in the destination column slide to make room showing the insertion position. Releasing drops the card with a spring animation. Moving a card to "In Progress" column automatically sets its status to In Progress. Moving to "Completed" triggers the completion animation — checkmark draws, card glows green, then settles into the Completed column. Moving to "To Do" resets status to active. The status change is reflected everywhere in the app instantly.

**Within Column Ordering:**
Within each column cards can be sorted by: Priority, Due Date, Manual (drag). The current sort shown in the column header as a small label. Manual drag within a column works by long pressing and dragging vertically — same lift and settle physics as between-column drag.

**Filtering in Kanban:**
The same filter chips from List View work in Kanban — the board shows only cards matching the active filter. When a filter is active cards that don't match fade out (opacity 0.3) rather than being removed — keeps the board layout stable and allows the user to see the full picture while focusing on filtered items.

**Add Task in Column:**
Each column has an "Add Task" button at the bottom — tapping opens quick task creation pre-filled with the status matching that column. A task added in "In Progress" column automatically gets "In Progress" status.

**Column Collapse:**
Tapping the column header collapses the column to a thin vertical strip showing only the column name rotated 90 degrees and the task count. Collapsed columns take minimal horizontal space. Useful when a column has many completed tasks that aren't currently relevant.

**Swimlanes (Advanced):**
An optional "Swimlane" mode toggle in the board settings. When enabled tasks are grouped by category into horizontal rows — each category is a swimlane across all columns. This gives a matrix view: category on the Y axis, status on the X axis. Very useful for seeing work by project area and status simultaneously.

---

## VIEW 4 — TIME BLOCK PLANNER:

A unique view that helps the user plan their day by dragging tasks into time blocks. Accessible from the Views menu as "Time Planner."

**Layout:**
A day timeline on the right side showing hours from 6 AM to midnight as a vertical ruler. On the left side an "Unscheduled" panel showing all tasks without a specific scheduled time — shown as compact draggable cards. In the center the timeline accepts dropped tasks as time blocks.

**Placing Tasks:**
Dragging a task card from the Unscheduled panel onto the timeline places it at the dropped time. The block's height represents the task's estimated duration — a 45-minute task creates a 45-minute block. If the task has no estimated duration a default 30-minute block is created. After placing the block the user can drag its bottom edge to resize it — extending the duration. The estimated duration field on the task updates to match the new block size.

**Visual Design of Time Blocks:**
Each placed task block: the task's category color as the block's gradient background, task title bold and white inside the block, time range shown below the title ("2:00 PM – 2:45 PM"), priority left border glow. Blocks snap to 15-minute grid lines while dragging. Conflict warning: when two blocks overlap the overlap area shows a red striped pattern. A conflict count badge appears in the header.

**Free Time Visualization:**
Gaps between blocks are labeled as "Free time · 45 min" in a muted style. This immediately shows the user how much free time they have between commitments and helps them decide where to fit new tasks.

**"Schedule All" Smart Feature:**
A "Schedule All" button in the header. Tapping it analyzes all unscheduled tasks and automatically distributes them across the day based on: task priority (urgent tasks placed earlier), estimated duration (longer tasks placed in longer free slots), energy level tags (Deep Work tasks placed in morning hours if the user has set Morning as their peak energy in settings, Light tasks placed in afternoon), working hours preference from settings (don't schedule before 9 AM or after 6 PM unless tasks are overdue). Shows the proposed schedule as a preview — each task placed in a time block with a "proposed" visual style (slightly transparent, dashed border). The user can accept the full schedule, drag-adjust individual blocks, or reject specific placements before confirming. Confirming sets the due time on all affected tasks.

**Section Backgrounds:**
Morning (6 AM – 12 PM): very subtle warm gradient overlay. Afternoon (12 PM – 5 PM): neutral. Evening (5 PM – midnight): very subtle cool gradient overlay. These section backgrounds are barely perceptible — just enough to give visual rhythm to the day.

**Day Navigation:**
Swipe left/right to navigate between days. Header shows the current date with forward/backward arrows. A "Today" button returns to today.

---

## VIEW 5 — FOCUS MODE VIEW:

A full-screen distraction-free view that shows one task at a time with an integrated Pomodoro timer. Activated from a "Focus" button in the header or from any task card's context menu.

**Entry Animation:**
When Focus Mode is activated the entire screen transitions — the app UI fades out and a deep focused dark screen fades in. The selected task card expands from its position to fill the center of the screen — a shared element transition.

**Screen Layout:**
The background is a deep dark gradient that slowly and subtly shifts color over time — during focus phase a very deep blue-to-navy pulse, during break phase a very deep green-to-teal pulse. The color shift is extremely slow (2-3 minute cycle) and nearly imperceptible — subliminal rather than distracting.

Top of screen: an "Exit Focus Mode" X button top left, "Task N of M Today" progress indicator top right ("Task 3 of 8 Today").

Center: the current task displayed large and prominently. Task title in a large display font — 24-28sp bold. Priority badge below the title. Category badge. Due date. If subtasks exist show them as a checklist that the user can check off directly from Focus Mode. Tapping a subtask checkbox checks it with the animated checkmark and updates the database in real time.

Pomodoro Timer: a large circular progress ring below the task content. The ring fills clockwise as the timer counts down. Inside the ring: the remaining time in MM:SS format — large and readable. The ring color: accent blue during focus phase, soft green during break phase. Below the ring: "Focus Session 1 of 4" showing which Pomodoro in the current cycle. The ring has a subtle glow that pulses once every 10 seconds — a gentle visual heartbeat keeping the user aware of the session.

Timer controls below the ring: Play/Pause button (large, the most prominent button on the screen), Skip (skip current phase — skip a break or skip to next Pomodoro), Reset (restart current phase). All three as clean circular icon buttons.

Session history strip: at the very bottom a row of small dots — filled circles for completed Pomodoros in this session, hollow for remaining. After 4 Pomodoros (or the configured number) the long break is triggered automatically.

**Pomodoro Phase Transitions:**
When a focus session ends: the timer ring completes with a satisfying fill animation, a brief celebratory glow pulse fills the screen in the accent color, a haptic success pattern fires, a chime sound plays (respecting silent mode), a notification fires ("Focus session complete — take a 5 minute break"), and the screen transitions smoothly to the break phase with the color shift from blue to green. When the break ends: the screen transitions back to focus phase. After the configured number of Pomodoros a long break is triggered with a different green tone and a "Long Break" label.

**Session Logging:**
Every completed Pomodoro session is logged as a TimerSession on the current task — start time, end time, duration, phase type (focus/break). This data feeds into the productivity dashboard and task detail time tracking display.

**"Done" to Next Task:**
A large "Done" button below the timer controls. Tapping marks the current task complete with the full completion animation (checkmark, strikethrough, confetti if urgent), then slides to the next highest priority due-today task — a horizontal slide transition as if flipping to the next card. If no more due-today tasks exist show a completion screen: "All done for today!" with a celebration animation — confetti or particle burst, a large trophy or flame illustration, the number of tasks completed, total focus time, and a motivational message.

**Focus Mode Exit:**
Tapping the X exits Focus Mode. The screen fades back to the normal Task Manager view. If a timer was running show a "Timer paused — resume?" toast. The timer state is preserved — reopening Focus Mode resumes the session.

---

## VIEW 6 — DASHBOARD / HOME VIEW (Enhanced):

The existing home screen has stat cards, streak banner, quick add bar, and filter chips. These are already built. Enhance the dashboard with additional premium sections that make it genuinely data-rich and motivating.

**Today's Focus Section:**
Below the stat cards and streak banner add a "Today's Focus" section — a smart-picked selection of the top 3 most important tasks due today. Selection algorithm: Urgent tasks first, then High priority, then by earliest due time, then by estimated duration (shorter preferred to encourage starting). Show these 3 tasks as larger featured cards — slightly wider than normal list cards, with a subtle gradient border glow. A "Start Focus Session" button below the 3 tasks that launches Focus Mode starting with the first task. If fewer than 3 tasks are due today show however many exist plus an "Add a task for today" card with a dashed border.

**Upcoming Deadlines Timeline:**
A horizontal scrollable timeline strip showing the next 7 days with their task counts. Each day shown as a small pill: day name ("Mon"), date ("Mar 3"), task count badge below. Days with overdue tasks show a red dot. Days with many tasks show a fuller colored pill. Tapping any day pill jumps to the Day View in the Calendar for that date. This gives a quick week-at-a-glance without leaving the home screen.

**Weekly Completion Sparkline:**
A small but beautiful sparkline chart (7-bar mini bar chart) showing tasks completed per day for the last 7 days. Today's bar highlighted in the accent color. Other bars in a muted version of the accent color. A label above: "This Week: N completed." Tapping the sparkline navigates to the full Analytics View.

**Recent Activity Feed:**
A "Recent Activity" section showing the last 6 actions as a compact timeline list. Each item: a small colored dot, action description in muted text ("Completed 'Java Assignment'", "Added 'Team meeting' to Work", "'Submit report' is now overdue"), and relative timestamp ("2h ago"). A "View All Activity" button navigating to a full activity log screen. This gives the user a sense of what's been happening and what needs attention.

**Daily Motivational Quote:**
A subtle card at the bottom of the home screen showing a rotating productivity quote. Changes daily. The quote is shown in italic text with the author in muted text below. The card has a very subtle gradient background. 30 pre-loaded quotes — cycles daily deterministically (same quote shown all day, different one next day). Quotes themed around productivity, focus, and achievement.

**Productivity Score:**
The Focus Score card already in the stat cards strip — enhance it with a tap action: tapping the Focus Score card navigates to the Analytics View with the score breakdown expanded and visible.

---

## VIEW 7 — ANALYTICS VIEW (Full Premium Dashboard):

A dedicated Analytics screen accessible from the productivity dashboard, the streak banner tap, or the drawer. Design it as the most visually stunning data screen in the app — on par with or better than the Expense Tracker analytics.

**Header:**
"Productivity Analytics" as the large bold title. A time range selector showing: Today, This Week, This Month, This Year, All Time. All charts and data update when the range changes — animated transitions between data sets.

**Productivity Score Deep Dive:**
A large featured card at the top. The Focus Score shown as a large animated circular ring — fills from 0 to the actual score on screen load. Inside the ring: the score number large and bold, "/ 100" below it in muted text. Outside the ring: the score category label — "Excellent" / "Good" / "Needs Attention" — in the ring's color. Below the ring: a breakdown of score components shown as horizontal bars with labels and values: On-time Completion Rate (X%), Streak Contribution (X points), Overdue Penalty (-X points), Daily Consistency (X points). A one-line personalized assessment: "You complete tasks on time 78% of the time — excellent! Focus on reducing overdue tasks to improve further."

**7-Day Task Completion Chart:**
A bar chart showing tasks completed per day for the selected period. Bar color: the category's color for that day's most-completed category (or a solid accent color if mixed). Today's bar slightly taller with an accent glow. Tapping any bar shows a tooltip: date, count, list of completed task titles. A trend line overlaid on the bars showing the 7-day moving average — makes the trend direction immediately clear.

**Tasks Created vs Completed Trend:**
A dual line chart: one line for tasks created per day (blue), one line for tasks completed per day (green). The gap between the lines shows whether the user is falling behind (creating more than completing) or catching up. Animate both lines drawing from left to right on load. Period selector: 7 days / 30 days / 90 days.

**On-Time Rate Gauge:**
A semi-circular gauge chart (like a speedometer) showing the percentage of tasks completed before or on their due date for the selected period. Needle animates from 0 to actual value on load. Color zones: red 0-40%, amber 40-70%, green 70-100%. A label below: "78% On-Time Rate — 22% completed late or pending."

**Status Distribution Donut:**
A donut chart showing the current distribution of all tasks: Active (blue), In Progress (amber), Overdue (red), Completed (green), Cancelled (grey). Each segment labeled with count and percentage. Tapping a segment filters the task list to that status — navigates back to the list view with that filter applied.

**Priority Distribution Bar Chart:**
A horizontal bar chart showing task count at each priority level. Bars colored to match priority colors. Shows which priority the user uses most and whether Urgent tasks are a common occurrence (which could indicate poor planning — surface as an insight).

**Category Performance:**
A list of all categories with their own row: category icon and color, category name, task count, completion rate as a mini horizontal bar, and tasks overdue count. The category with the best completion rate gets a small gold trophy emoji. The category with the most overdue tasks gets a subtle red highlight — draws attention to where improvement is needed. Tapping a category row navigates to the list view filtered to that category.

**Most Productive Day of Week:**
A 7-bar chart showing average tasks completed per day of the week (Mon through Sun). The tallest bar (most productive day) highlighted with the accent color and a "🏆 Best Day" label above it. This is calculated over the entire history — not just the selected period, since day-of-week patterns need more data to be meaningful.

**Most Productive Time of Day:**
A 24-column heat map grid showing hourly productivity — each column is an hour of the day (midnight to midnight), the color intensity shows average tasks completed in that hour across history. Lightest for hours with no completions, brightest accent color for peak hours. A label below the most active hour: "Most productive at 10 AM." This is the single most actionable insight — tells the user when to do their most important work.

**Streak History Calendar:**
A GitHub-style contribution heatmap for the last 90 days — each day is a small square. Grey for no tasks completed, increasingly bright green for more completions. The current streak days at the right end of the heatmap are highlighted with a slightly different treatment — a glowing border showing the active streak. Tapping any square shows a tooltip with the date and tasks completed. A "Longest Streak: 12 days" label above the heatmap.

**Focus Time Tracking:**
A section showing total Pomodoro focus time logged: total for the selected period as a large number ("14h 32m"), a daily bar chart showing focus minutes per day, a "Best Focus Day" record, and average focus time per day. Only shows if focus sessions have been logged — empty state if none yet.

**Time Estimation Accuracy:**
For tasks that have both an estimated duration and actual tracked time show an accuracy score: "You estimate task durations accurately 68% of the time — tasks take an average of 1.3x your estimates." A bar chart showing estimated vs actual time for the last 10 completed timed tasks. This is a uniquely useful insight that helps the user plan better.

**Personal Records Section:**
A row of record cards: Most tasks completed in one day (with the date), Longest streak ever, Best on-time rate week (with date), Total tasks completed all time, Total focus time all time. Each shown as a small card with a trophy or fire icon. When a record is broken during the current session show a "New Record!" celebration card at the top of the Analytics screen.

**Export as PDF:**
An "Export Report" button in the Analytics header. Generates a PDF containing all the charts and insights for the selected time period. The PDF is formatted with the app's visual aesthetic — dark background, colored charts, the user's stats prominently displayed. Opens the system share sheet after generation. Useful for productivity tracking or professional reporting.

**Insights Feed:**
At the very bottom of the Analytics screen a "Insights" section showing 4-6 automatically generated insights based on the actual data. Rotate 3 fresh insights each time the screen is opened. Examples: "You've completed 43% more tasks this week than last week — great momentum!", "Study tasks have a 91% completion rate — your best category!", "Tuesday is your most productive day — consider scheduling important tasks then.", "You have 3 tasks that have been pending for over 2 weeks — consider revisiting their priority.", "Your average focus session is 24 minutes — try completing full 25-minute Pomodoros for maximum benefit." Each insight shown as a card with a relevant emoji, a headline, a one-line detail, and an optional action button ("Review Pending Tasks", "View Focus Sessions").

---

## NAVIGATION & VIEW SWITCHING:

**Bottom Tab Navigation Upgrade:**
The existing bottom navigation has Home, Devices, and History tabs. For the Task Manager specifically — within the Tasks card feature — build an internal navigation structure: a tab bar at the top of the Task Manager screen (not the app bottom nav) with these tabs: Home (dashboard), List, Calendar, Views (opens a view picker bottom sheet for Kanban, Time Block, Focus, Analytics). This keeps navigation internal to the Task Manager without conflicting with the app's bottom nav.

**View Picker Bottom Sheet:**
Tapping "Views" tab opens a bottom sheet with large illustrated option cards for each view: Kanban Board (grid icon, "Visual board view"), Time Block (calendar icon, "Plan your day"), Focus Mode (target icon, "One task at a time"), Analytics (chart icon, "Your productivity insights"). Each card has a gradient background matching the view's color identity. Smooth spring slide-up animation on open.

**Shared Element Transitions:**
When navigating from the List View to a task's detail screen the task card expands into the detail screen — a shared element transition where the card background and title smoothly animate to their positions in the detail screen. When returning the reverse animation plays. When entering Focus Mode the selected task card expands from its list position to fill the Focus Mode screen.

**Tab Switching Animation:**
Switching between the Home, List, and Calendar tabs uses a smooth fade-and-slide animation — the outgoing tab slides and fades in the direction of the switch (tap Calendar which is to the right → current content slides left out, calendar slides right in). This gives a sense of spatial orientation within the app.

---

## PERFORMANCE REQUIREMENTS — Non-Negotiable:

The List View with 1000+ tasks must scroll at a consistent 60fps — implement proper lazy loading and widget recycling. The Calendar Month View must render instantly from cached data — no noticeable lag when switching months. The Kanban Board with 200+ cards across columns must scroll smoothly in each column independently. The Time Block planner must render all blocks within 200ms of opening. The Analytics charts must calculate from cached data — all chart data pre-computed in a background thread on app open and updated incrementally when tasks change. Switching between the 4 calendar modes (Month/Week/Day/Agenda) must be instant — data already loaded, only view changes. Search results in List View must appear within 150ms. Drag and drop in Kanban and Calendar must run at 60fps with no frame drops — use platform-level drag APIs, not custom gesture recognizers.

---

**Build every single view completely. List View with grouping, sorting, quick filters, and illustrated empty states. Calendar View with all 4 modes (Month, Week, Day, Agenda), drag-to-reschedule, conflict detection, and heat map overlay. Kanban Board with drag-between-columns, swimlanes, and within-column sorting. Time Block Planner with the Schedule All feature and section backgrounds. Focus Mode with the Pomodoro timer, phase transitions, session logging, and completion celebration. Dashboard Home with Today's Focus, deadline timeline, sparkline, activity feed, and motivational quote. Analytics View with all 12 data visualizations, the insights feed, and PDF export. Navigation with tab switching, shared element transitions, and the view picker. Performance requirements must all be met. Do not leave any view partially implemented — every item listed here must be complete and working.**
