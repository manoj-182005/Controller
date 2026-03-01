Prompts 1, 2, and 3 are complete. The Task Manager has its full core engine with all 19 task card fields, all 7 views including the full Calendar, Kanban, Time Block, Focus Mode, Dashboard, and Analytics, and the complete AI Intelligence Layer with Natural Language Parsing, Smart Scheduling, all 6 reminder types, Task Intelligence Engine, Productivity Intelligence, Time Tracking, Enhanced Focus System, Templates, Import/Export, and Activity Logging. All of that is working perfectly. Now build Prompt 4 — the complete Settings and Personalization Engine, the premium Onboarding Experience, the Widget System, the Micro-Interactions and Polish layer, full Accessibility compliance, and the final performance audit that makes this the most polished task manager ever built on mobile. Go through the entire codebase extremely thoroughly before touching anything. This is the final prompt — make everything production ready, visually stunning, and completely complete.if any of the mentioned feature is already implemented just try to bring it according to the mentioned prompt.

---

## DESIGN FOUNDATION — Enforce Everywhere in Prompt 4:

Every screen built in this prompt must follow the exact established visual language without a single deviation: deep navy background (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0A0F1E`), glassmorphism cards with frosted borders and translucency effects, priority color system (Low = slate `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#64748B`, Normal = blue `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#3B82F6`, High = amber `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#F59E0B`, Urgent = crimson `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#EF4444` with glow), 300ms as the standard transition duration, display-weight font for all titles and headings, clean sans-serif for all metadata and body text, every single interactive element must have a scale-down to 0.97x + accent glow visual feedback on tap, and the same vibrant gradient aesthetic as the Expense Tracker in the existing app. The Settings screen in particular must not look like a plain system settings list — it must feel like a premium feature screen with the same visual richness as the rest of the app.

---

## SETTINGS SCREEN — Complete Architecture:

The Settings screen is accessible from the drawer, from the profile icon if one exists in navigation, and from a gear icon in the Task Manager header. It must be organized into clearly separated sections with visual section headers — not a flat wall of options. Each section has a gradient icon in the header matching its theme. Each setting item has a clear label, a subtitle explaining what it does, and the appropriate control (toggle, selector, slider, picker, or navigation arrow). Every setting change takes effect immediately — no "Save" button needed. Settings are persisted to local storage and survive app restarts.

---

## SETTINGS SECTION 1 — APPEARANCE:

**Theme System — Complete:**

Four theme options shown as large illustrated preview cards in a 2x2 grid — not just text options: Dark (the current deep navy `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0A0F1E` — label "Dark" with a moon icon), Light (white background with dark text — label "Light" with a sun icon), AMOLED Black (pure `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#000000` black optimized for OLED screens — label "AMOLED" with a star icon — saves maximum battery on OLED phones), Auto System (automatically switches between Dark and Light based on device system theme — label "Auto" with a half-sun half-moon icon). The currently active theme shows a colored checkmark badge. Tapping any theme card immediately applies it — the entire app transitions to the new theme with a smooth 400ms cross-fade animation. No restart required.

AMOLED Black theme specifics: all backgrounds pure black, card backgrounds `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#111111`, glassmorphism uses slightly lighter `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#1A1A1A`, text pure white, no gradients on backgrounds — flat dark. This maximizes OLED battery savings. The AMOLED option shows a small "Battery Saver" green pill badge on its card.

Light theme specifics: background `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#F8FAFC`, card backgrounds white with subtle shadow, text `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0F172A` (near black), accent colors remain vibrant — blue, amber, red priority colors still used. Glassmorphism becomes light frosted glass — white with opacity.

**Accent Color System:**

A "Accent Color" setting showing 12 color swatches in a 4x3 grid. Each swatch is a gradient circle — not a flat color. The selected swatch has a white checkmark and a subtle glow ring. The 12 options: Electric Blue (default, `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#3B82F6` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#2563EB`), Violet (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#8B5CF6` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#7C3AED`), Rose (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#F43F5E` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#E11D48`), Amber (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#F59E0B` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#D97706`), Emerald (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#10B981` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#059669`), Cyan (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#06B6D4` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0891B2`), Orange (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#F97316` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#EA580C`), Pink (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#EC4899` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#DB2777`), Indigo (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#6366F1` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#4F46E5`), Teal (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#14B8A6` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#0D9488`), Lime (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#84CC16` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#65A30D`), Gold (`<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#EAB308` → `<span class="inline-block w-3 h-3 border-[0.5px] border-border-200 rounded flex-shrink-0 shadow-sm mr-1 align-middle"></span>#CA8A04`). Selecting any color immediately repaints: all active state indicators, the FAB, the active filter chip, the streak banner, stat card accents, focus mode timer ring, all progress rings and bars, all chart accent colors, all primary action buttons. The color change propagates instantly via a theme provider — no screen rebuilds needed. A "Custom Color" option below the grid opens a hex color input field for power users.

**App Icon Variants:**

Eight app icon designs shown as large rounded-square previews in a 2x4 grid: Default (dark navy with a check icon gradient), Midnight (pure black with a minimal white check), Neon (dark with a glowing green check), Cosmic (dark with a space gradient), Minimal (white with a tiny dark check — for light mode users), Gradient (vibrant rainbow gradient), Gold (dark with gold metallic check), Monochrome (grey tones only). Selecting an icon applies it immediately on supported platforms (iOS 10.3+ via alternate app icons, Android via launcher icon update with user permission dialog). On unsupported platforms show a message: "Icon changes require reinstalling the app from settings." Each icon card shows the icon large with its name below and a "Selected" badge on the active one.

**Card Density:**

Three options shown as illustrated cards with actual task card previews at each density: Comfortable (current default — generous padding, 3-4 cards visible), Compact (reduced padding — 5-6 cards visible), Cozy (medium — 4-5 cards visible, slightly larger typography than Compact). Tapping any density card immediately applies it — the list view transitions to the new density with a smooth resize animation.

**Card Style:**

Four card style options, each shown as a preview card: Glassmorphism (current — frosted dark background with translucent border), Flat (solid dark card, no border, subtle shadow), Outlined (transparent background, colored border 1-2px matching priority), Elevated Shadow (solid dark card with a larger drop shadow creating a more dimensional feel). Selecting immediately applies to all task cards throughout the app.

**Font Size:**

A slider with 4 steps and live preview labels: Small (12sp body), Medium (14sp body — default), Large (16sp body), Extra Large (18sp body). As the slider moves show a live preview of a sample task card with the text at the selected size — the preview updates in real time as the slider is dragged. The font size change applies to body text, subtitles, and metadata — title sizes scale proportionally. Titles at Extra Large are noticeably larger than at Small.

**Animation Speed:**

Three options as a segmented selector: Full (all spring physics, particle effects, and complex transitions — default), Reduced (simpler transitions, no particle effects, shorter durations — for users who find animations distracting or get motion sickness), Off (instant transitions only, no animations whatsoever — for maximum performance or accessibility). Selecting Reduced or Off automatically enables iOS/Android Reduce Motion compatibility.

---

## SETTINGS SECTION 2 — TASK DEFAULTS:

**Default Priority:**

A priority selector showing all 5 options (None, Low, Normal, High, Urgent) as colored pills in a horizontal row. Tapping any pill selects it as the default priority for all newly created tasks. The selected pill shows a checkmark and is visually elevated. Subtitle: "New tasks will default to this priority."

**Default Category:**

A category picker dropdown showing all existing categories with their color icons. The selected category is shown with a checkmark. Also includes "No Category" as an option. Subtitle: "New tasks will be assigned to this category by default."

**Default Estimated Duration:**

A duration picker with common options as chips: None, 15 min, 30 min, 45 min, 1 hour, 2 hours, Custom. Custom opens a time picker. The selected option is highlighted. Subtitle: "Pre-fill estimated duration for new tasks."

**Default Reminder Offset:**

A selector showing: None, 5 min before, 15 min before, 30 min before, 1 hour before, 1 day before, Custom. The selected option highlighted. Subtitle: "Automatically add this reminder to every new task with a due date."

**First Day of Week:**

A horizontal selector: Sunday, Monday, Saturday. Affects: Calendar views (which day starts each week column), Weekly stats calculations, Weekly Review grouping. Selecting Monday makes Monday the leftmost column in Week View.

**Date Format:**

Three options with examples: Relative ("Today", "Tomorrow", "Mar 5"), MM/DD ("03/05/2026"), DD/MM ("05/03/2026"). Affects all date displays throughout the app. Selecting immediately updates all visible dates.

**Time Format:**

Toggle between 12-hour ("3:30 PM") and 24-hour ("15:30"). Affects all time displays.

**Auto-Archive Completed Tasks:**

A toggle with a sub-option visible when enabled: Archive after 7 days / 14 days / 30 days / 90 days. Completed tasks older than the threshold are automatically moved to an Archive view and removed from the main task list. A badge in the drawer shows the archive count.

**Show Completed Tasks:**

A selector: Always Visible (completed tasks always visible at the bottom of each filter), Collapsed Section (completed tasks grouped under a collapsible "Completed (N)" section collapsed by default), Hidden (completed tasks never shown in the list — only accessible via the Completed filter chip). Affects all list views.

---

## SETTINGS SECTION 3 — NOTIFICATIONS & REMINDERS:

**Master Notification Toggle:**

A large prominent toggle at the top of this section. When disabled all Task Manager notifications are silenced — reminder notifications, overdue alerts, smart suggestions, weekly review, daily briefing. When toggled off show a confirmation: "Turning off notifications means you won't receive any task reminders. Are you sure?" with a "Turn Off" and "Keep On" button. If device-level notification permission hasn't been granted show a "Enable Notifications" button instead that opens the device permission dialog.

**Per-Category Notification Settings:**

A "Category Notifications" sub-section. A list of all categories each with a toggle and a "Customize" arrow. Toggling a category off silences all notifications from tasks in that category — useful for silencing Work notifications on weekends. Tapping "Customize" opens a category-specific notification settings screen: notification tone for this category (from the sound selector), vibration pattern for this category, reminder offset override (override the default for this category — e.g., Study tasks get 1 day before reminder by default), badge behavior (show/hide badge for this category's tasks).

**Reminder Sound Selector:**

Tapping opens a full-screen sound picker with 12+ sounds. Each sound shown as a card with its name and a play preview icon. Tapping the play button plays a short sample of the sound. Available sounds: Default System, Gentle Chime, Crystal Bell, Soft Ping, Subtle Pop, Digital Alert, Morning Tone, Evening Calm, Zen Bowl, Urgent Pulse, Soft Vibration (vibrate only), Silent (no sound — vibrate only). A "Use Device Default" option at the top. The selected sound shown with a checkmark. An "Import Custom Sound" option on supported platforms.

**Vibration Pattern Selector:**

Options: Default System, Single Short, Double Tap, Triple Pulse, Long Buzz, SOS Pattern (three short three long three short), Custom (opens a pattern builder with drag-able pulses). Each pattern can be previewed by tapping a "Feel it" button that triggers the pattern via haptic engine.

**Badge Count:**

Four options as a segmented selector: All Pending (badge = total active task count), Overdue Only (badge = count of overdue tasks — most urgent), Today Only (badge = tasks due today), Off (no badge). The badge on the app icon updates immediately when changed.

**Daily Briefing Notification:**

A toggle with a time picker visible when enabled. When enabled fire a notification every morning at the configured time (default 8:00 AM): "Good morning! You have N tasks due today and N overdue." Tapping navigates to Today filter. The notification includes a brief weather note if weather integration is enabled ("It's sunny and 28°C today.").

**Weekly Review Prompt:**

A toggle with a day selector (default Sunday) and time picker (default 6:00 PM). When the scheduled time arrives on the selected day fire the Weekly Review notification opening the full Weekly Review flow built in Prompt 3.

**Escalating Reminder Settings:**

A toggle "Enable Escalating Reminders" that applies to all High and Urgent priority tasks. When enabled sub-options appear: First Escalation After (10 min / 20 min / 30 min after the original reminder fires with no action), Second Escalation After (another 15 min / 30 min / 45 min after the first escalation), Maximum Escalations (2 or 3 — after which no more fire), Escalate During DND (toggle — default off).

**Smart Reminder Learning:**

A toggle "Learn from my snooze patterns." When enabled the app tracks snooze behavior and adjusts reminder suggestions. A "Reset Learning Data" button below it that clears all stored snooze patterns. A brief explanation: "When enabled, the app learns your typical snooze duration and suggests it automatically."

---

## SETTINGS SECTION 4 — PRODUCTIVITY SETTINGS:

**Pomodoro Configuration:**

Four numeric pickers in a 2x2 grid: Focus Duration (default 25 min, range 5-90 min), Short Break (default 5 min, range 1-30 min), Long Break (default 15 min, range 5-60 min), Long Break After Every N Pomodoros (default 4, range 2-8). Each picker uses a drum-roll style scroll selector — the currently selected value is centered and larger. A "Reset to Defaults" button. A live preview below showing "Today's focus plan: [N] pomodoros = [X hours] focus time with breaks."

**Auto-Start Sessions:**

A toggle "Auto-start next Pomodoro" — when enabled the next session (work or break) begins automatically after the current one ends without any user input. A sub-toggle "Auto-start breaks only" for users who want breaks to auto-start but want to manually start each work session.

**Focus Mode Ambient Sounds:**

A toggle "Enable ambient sounds in Focus Mode." When enabled a sound selector appears showing the 8 ambient sounds from Prompt 3. The selected sound becomes the default when entering Focus Mode. A "Always ask" option that prompts the user to choose a sound each time they enter Focus Mode rather than using the default.

**Do Not Disturb Integration:**

A toggle "Enable DND during Focus sessions." When enabled sub-options: DND Mode (Priority Mode — allows calls from starred contacts / Silent Mode — blocks everything), Show DND Status in Focus Mode (toggle — shows the "🔕 DND Active" pill in Focus Mode UI).

**Working Hours:**

Two time pickers: Work Start Time (default 9:00 AM) and Work End Time (default 6:00 PM). These are used by the Smart Scheduling algorithm to avoid scheduling tasks outside working hours. A toggle "Schedule tasks outside working hours if needed" — when enabled the algorithm can use non-working hours if the task load can't fit within them.

**Energy Peak Hours:**

A selector with three base options shown as illustrated cards: Morning Person (peak 7 AM – 11 AM — sun rising illustration), Night Owl (peak 8 PM – midnight — moon illustration), Afternoon (peak 1 PM – 5 PM — sun illustration). Each card shows example scheduling ("Deep Work tasks scheduled in [peak hours]"). A "Custom" option that opens two time pickers for custom start and end of peak hours. The selected option used by the Smart Scheduling algorithm to place Deep Work energy tasks in peak hours.

**Daily Task Limit Warning:**

A toggle with a number picker (range 5-25, default 10). When the number of tasks due today exceeds the set limit show a gentle in-app warning card on the dashboard: "You have N tasks due today — that's a lot. Consider rescheduling some to tomorrow." Not a blocking dialog — just an informational card.

**Streak Reminder:**

A toggle with a time picker. When enabled if the user hasn't completed any tasks by the configured time (default 8:00 PM) fire a notification: "🔥 Don't break your streak! Complete at least one task today to keep your N-day streak going." A motivating push — not annoying — just once per day maximum.

**Burnout Detection:**

A toggle "Enable burnout detection." When enabled the system monitors the indicators from Prompt 3 (overloaded task list, declining completion rate, falling productivity score) and shows the gentle warning card when triggered. A "Sensitivity" slider: Low (only warn when severely overloaded), Medium (default), High (warn at first signs of overload).

---

## SETTINGS SECTION 5 — CALENDAR SETTINGS:

**Default Calendar View:**

Four options as a segmented selector with icons: Month (grid icon), Week (columns icon), Day (single column icon), Agenda (list icon). The selected view is what opens when the Calendar tab is tapped.

**Show Weekends:**

A toggle. When disabled Saturdays and Sundays are hidden from the Week View — the week shows only Mon-Fri, making the columns wider. The Month View still shows all 7 days but weekend columns are subtly dimmed.

**Calendar Accounts Sync:**

A section listing available calendar integrations: Google Calendar (shows "Connect" button if not connected, "Connected — [account email]" if connected with a disconnect option), Apple Calendar (iOS only — toggle to show/hide Apple Calendar events in the Task Calendar). When Google Calendar is connected show sync options: Sync Direction (One-Way: Tasks → Google Calendar, Two-Way: sync changes both directions), Sync Frequency (Every 15 min / Every hour / Manual only), Which Tasks to Sync (All tasks / Tasks with due dates only / High+ priority only). Connected calendar events shown in the Task Calendar as grey blocks — visually distinct from tasks (different opacity or a calendar icon overlay) so the user can distinguish their tasks from their calendar events.

**Event Color Source:**

A selector affecting how task blocks appear in calendar views: Use Priority Color (Urgent = red block, High = amber, Normal = blue, Low = grey — default), Use Category Color (each category's gradient color as the block color), Use Task's Assigned Color (each task's custom color if set).

**Show Completed Tasks on Calendar:**

A toggle. When enabled completed tasks still show in the calendar on their due date — rendered in a muted strikethrough style. When disabled completed tasks disappear from the calendar. Most users prefer disabled (cleaner calendar) but some want the historical record.

**Time Zone Display:**

A dropdown to select the display time zone. By default uses the device's system time zone. Power users who travel or work across time zones can set a fixed display zone. A "Use Device Time Zone" toggle that overrides the manual selection when enabled.

**Calendar Start Time:**

A time picker for the earliest time shown in Day and Week Views (default 6:00 AM). Tasks before this time are still shown — the calendar just scrolls to the start time on open rather than showing from midnight.

---

## SETTINGS SECTION 6 — CATEGORIES MANAGEMENT:

A full category management screen. Not just a list — a rich organized view.

**Category List:**

Each category shown as a premium card: the category gradient as the card background accent on the left border, the category icon large, the category name bold, task count shown as a badge ("12 tasks"), a drag handle on the right for reordering. Swipe left on any category to reveal: Edit and Archive options. Tapping a category card opens the Category Edit screen.

**Category Edit Screen:**

Name field (autofocused), color picker (12 gradient colors — same palette as the task card), icon picker (30+ icons organized in a searchable grid: work icons, study icons, health icons, shopping icons, finance icons, personal icons, custom), Default Priority for this category (selector — tasks created in this category default to this priority), Default Tags (pre-fill these tags on every new task in this category), Notification Tone override (use a different sound for this category's reminders), Default Estimated Duration (pre-fill duration for tasks in this category). Save changes immediately — no separate save button needed.

**Category Reordering:**

Drag handles are visible on all category cards. Long pressing a card activates drag mode — the card lifts with increased shadow and can be dragged to a new position. Other cards smooth-shift as it's dragged. The order is reflected in: category filter chips row, category picker in task creation, category breakdown charts.

**Archive Category:**

Archiving a category: moves it out of the active categories list, all tasks in it are still accessible via the "Archived" filter, the category chip disappears from the main filter row. An "Archived Categories" collapsible section at the bottom of the categories list shows archived categories with a "Restore" button each.

**Add New Category:**

A "+ New Category" button at the bottom of the list that opens the Category Edit screen in create mode with all fields blank and autofocus on the name field.

---

## SETTINGS SECTION 7 — TAGS MANAGEMENT:

A dedicated Tags Management screen showing all tags used across all tasks.

**Tags Grid:**

All tags shown as a masonry grid of colored chips — each chip shows the tag name and a count badge ("12 uses"). The chip color is the tag's assigned color. Sort by: Most Used (default), Alphabetical, Recently Used, Color.

**Tag Actions:**

Tapping any tag opens a tag detail bottom sheet: tag name (editable inline), color picker (12 colors), task list showing all tasks with this tag (tappable), usage count, a "Rename" button (updates the tag name on all tasks that use it), a "Merge with..." button (opens a tag picker — selecting another tag merges both into one, all tasks with either tag get the merged tag, the other tag is deleted), a "Delete" button (removes the tag from all tasks and deletes it — confirmation required showing how many tasks will be affected).

**Pin Frequently Used Tags:**

Each tag detail sheet has a "Pin to Quick Access" toggle. Pinned tags show at the very beginning of the tags chip row in task creation — the most useful tags are always immediately visible without scrolling.

**Unused Tags:**

A "Clean Up" button at the top of the Tags screen that finds all tags with 0 uses and shows them in a list with a "Delete All Unused" button. Helps keep the tag library clean.

---

## SETTINGS SECTION 8 — DATA & BACKUP:

**Cloud Backup:**

Three cloud storage options shown as cards with their logos: iCloud (iOS only), Google Drive (Android and iOS), Dropbox (both platforms). Each card shows: connection status, last backup timestamp if connected ("Last backup: Feb 28 at 11:45 PM"), a "Backup Now" button, a "Restore from Backup" button. Connecting any service opens the OAuth flow in an in-app browser. When connected auto-backup runs on the configured schedule.

**Auto-Backup Frequency:**

A selector: Daily (at 2:00 AM by default — time configurable), Weekly (Sunday at 2:00 AM), Manual Only. A "Backup Now" button that immediately triggers a backup to the connected cloud service with a progress indicator.

**Last Backup Status:**

A card showing: last backup time, backup size, which service it went to, status (Success ✓ / Failed ✗ with error message). If the last backup failed show a red warning and a "Retry" button.

**Restore from Backup:**

Tapping "Restore from Backup" shows a list of available backup files from the connected cloud service — each with its timestamp and file size. Selecting one shows a confirmation: "This will replace ALL current tasks, categories, settings, and templates with the backup from [date]. This cannot be undone. Continue?" with a text confirmation input requiring the user to type "RESTORE" before the button activates. After restoration a success screen shows what was restored.

**Export All Data:**

Three export format buttons: JSON (complete backup including all task data, settings, templates, activity logs — suitable for full restore), CSV (tasks only in spreadsheet format — suitable for analysis in Excel/Sheets), PDF Report (formatted productivity report for the current month — same format as the Analytics PDF export). Each button shows a brief description of what's included. After generating the file opens the system share sheet.

**Clear Completed Tasks:**

A "Clear Old Completed Tasks" option with a selector for the age threshold: 7 days / 30 days / 90 days / All completed tasks. Shows a preview count before confirming: "This will permanently delete 47 completed tasks older than 30 days." Confirmation required.

**Full Data Wipe:**

In a red "Danger Zone" sub-section at the very bottom. A "Delete All Data" button. Tapping shows a multi-step confirmation: Step 1 — "Are you absolutely sure? This will permanently delete ALL tasks, categories, templates, settings, and history. This cannot be undone." with Cancel and "I'm Sure" buttons. Step 2 — A text input requiring the user to type "DELETE ALL DATA" exactly. Step 3 — A final countdown: "Deleting all data in 5... 4... 3... 2... 1..." with a Cancel button that works during the countdown. Only after the countdown completes does deletion happen. After deletion restart the app fresh as if newly installed.

---

## SETTINGS SECTION 9 — SECURITY & PRIVACY:

**App Lock:**

A toggle with biometric/PIN sub-options. When enabled require authentication before the Task Manager can be opened. Lock method selector: Face ID (iOS) / Face Unlock (Android) first, Fingerprint fallback, PIN entry last. Lock After selector: Immediately (lock as soon as app goes to background), 1 minute, 5 minutes, 15 minutes, Never lock while app is in foreground. A "Change PIN" option (requires current PIN verification, then new PIN entry twice with strength indicator).

**Lock Screen Design:**

When the Task Manager is locked and opened show a premium full-screen lock screen: a blurred screenshot of the last viewed Task Manager screen as the background (gives a preview while protecting content), a large lock icon with a subtle glow animation, "Task Manager Locked" title, biometric prompt auto-triggers, "Use PIN Instead" button below. PIN entry pad: same premium circular button design as the Notes vault PIN pad established in the Notes prompts. Consistent across the app.

**Hide Content in App Switcher:**

A toggle. When enabled the app switcher (recent apps) shows a branded black screen with the app logo instead of the actual task content. Prevents privacy leakage when showing the screen to others. On iOS uses the UIScreen.main.isCaptured flag approach. On Android applies the FLAG_SECURE window flag.

**Data Stored Locally Toggle:**

A toggle "Offline-Only Mode." When enabled all data is stored exclusively on device — no cloud backup, no sync, no network requests. Disabling all network features. Useful for maximum privacy users. When enabling show a warning: "Offline mode disables cloud backup. Ensure you export your data regularly."

**Privacy Policy & Data Info:**

A navigation item opening a full-screen rich text view of the privacy policy. Specifically detail: what data is stored locally, what (if anything) is sent to external services, that the Claude API calls from the AI features use only the task content the user explicitly processes, that location data for geofencing stays on device and is never uploaded. A "Data Usage Summary" card at the top with a simple visual breakdown using icons — more readable than a legal document alone.

---

## SETTINGS SECTION 10 — ABOUT & SUPPORT:

App name and version number prominently displayed. Build number in smaller muted text. A "Check for Updates" button. Changelog / What's New — shows the release notes for the current version as a scrollable card.

Rate the App — opens the App Store / Play Store rating page.

Send Feedback — opens a feedback form: a text field "What would you like to tell us?", an optional screenshot attachment, a submit button. Stores feedback locally and opens the email client with the feedback pre-composed if the user wants to send it directly.

Open Source Licenses — a list of all open source packages used with their licenses. Each expandable.

A "Tip Jar" section (optional, shown only if relevant) — three tip options as gradient cards: Small ☕ ($0.99), Medium 🍕 ($2.99), Large 🎉 ($4.99). A thank you message when a tip is made. Uses in-app purchases — no subscription, just one-time optional support.

---

## PREMIUM ONBOARDING EXPERIENCE — 5 Screens:

The onboarding is shown only on first launch after installation. It must be premium, fast, and genuinely useful — not just decorative. Skip button always visible in the top right. Progress dots at the bottom. Swiping forward or tapping "Next" advances. Swiping back goes back.

**Screen 1 — Welcome:**

Full screen animated splash. A premium 3D-style illustrated logo animates in — the app icon scales from 0 to full size with a spring bounce, then a subtle shimmer passes over it. Below: "Task Manager" in a large display font fading in. Below it: the tagline in a smaller italic font — "The last task manager you'll ever need." fading in 200ms after the title. A brief looping animation: small task cards float up from the bottom of the screen, each checking itself off with an animated checkmark as they ascend — a whimsical illustration of completing tasks. The background is the app's deep navy with very subtle animated particles drifting upward slowly. A "Get Started" button at the bottom with the accent gradient — tapping it advances to Screen 2 with a horizontal slide transition. The entire screen takes under 3 seconds to fully animate in and then loops gently.

**Screen 2 — Choose Your Style:**

Title: "Make it yours." Subtitle: "Choose the look that fits you best."

Three theme cards in a vertical stack — larger and more illustrated than the Settings theme cards: Dark (the standard dark navy — "Most popular" badge), AMOLED Black ("Battery saving" badge), Light ("Clean and bright" badge). Each card shows a full mini app preview — a tiny rendered version of the home dashboard in that theme — so the user can see exactly what they're choosing. Tapping a card immediately applies that theme to the onboarding screen itself — the background transitions to that theme's colors so the user can feel the difference before committing. Below the theme cards an accent color picker showing 6 of the 12 accent colors as circles. Tapping selects that accent — the "Next" button and all highlights immediately change to the selected color. A "See all colors" link that expands to show all 12.

**Screen 3 — Import Existing Tasks:**

Title: "Already have tasks elsewhere?" Subtitle: "Import them in seconds — or start fresh."

Four import source cards in a 2x2 grid: Todoist (red logo illustration), TickTick (blue teal logo), Google Tasks (colorful Google logo), Apple Reminders (iOS only — white and orange logo). Each card has a brief description "Import your existing tasks and lists." A "Skip" link below the grid prominently visible — "I'll start fresh." Tapping any import source starts the import flow: shows a brief explanation, requests necessary permissions (OAuth for Todoist/Google, system permission for Apple), then imports with a progress animation ("Importing 34 tasks..."). After import show a success card: "✓ 34 tasks imported successfully from Todoist." The "Next" button advances to Screen 4. If no import is needed the "Skip" link advances directly.

**Screen 4 — Set Your Preferences:**

Title: "When do you work best?" Subtitle: "Help us schedule tasks at the right time for you."

Two settings on this screen — kept minimal to avoid overwhelming the user:

Working Hours: Two time pickers side by side with labels "Start" and "End." Default 9 AM and 6 PM. Large, satisfying drum-roll time pickers. A visual timeline bar below them showing the working window as a colored block — the block adjusts in real time as the user changes the times.

Energy Peak: The three illustrated cards (Morning Person, Afternoon, Night Owl) from Settings Section 4 — pick your productivity peak. Tapping a card selects it with a spring scale animation.

A brief explanation below: "We'll use these preferences to suggest the best schedule for your tasks." No overwhelming options — just these two. Everything else can be configured in Settings later.

**Screen 5 — Quick Tutorial:**

Title: "Three gestures to master." Subtitle: "That's all you need to be a power user."

Three gesture tutorials shown as animated cards that play in sequence — each demonstrates a key gesture:

Card 1 — Swipe Right to Complete: an animated task card with a finger icon swiping right. The card fills with green, a checkmark animates, the card checks off. Text: "Swipe right on any task to complete it instantly."

Card 2 — Long Press for Multi-Select: a finger icon long-pressing a card. Multiple checkboxes appear on all cards. Text: "Long press to select multiple tasks for bulk actions."

Card 3 — Quick Add with Natural Language: the quick add bar with text appearing character by character — "Submit report Friday urgent work." Below it the Parse Preview card appears showing the extracted fields as chips. Text: "Type naturally — we understand dates, priorities, and categories automatically."

Each card is shown for 3 seconds then automatically advances to the next with a smooth horizontal slide. Dots below the cards indicate position. The user can also swipe manually. After all 3 cards have been shown the "Get Started" button appears at the bottom with a pulsing glow. Tapping "Get Started" dismisses onboarding with a zoom-out transition — the home screen zooms in from the background as onboarding zooms away. First launch shows 5 sample pre-populated tasks as gentle examples: "Try completing this task →" (urgent), "This task is due today" (high), "Low priority — whenever you get to it" (low), "Recurring daily task" (recurring), "This task has subtasks" (with 2 subtasks). These help the user immediately understand how tasks look and interact with them before creating their own.

---

## WIDGET SYSTEM:

Build home screen widgets for both Android (Glance/AppWidgets API) and iOS (WidgetKit). All widgets: match app theme automatically (dark/light based on device setting), update every 15 minutes, tap any task in a widget to open the app directly to that task, tap the widget header/title to open the app to the relevant view.

**SMALL WIDGET (2x2):**

Shows: the app icon in the top-left corner (small), "Task Manager" label in muted text next to it, the next due task's title in bold below (truncated to 2 lines), the task's due time in the accent color ("Due Today at 3 PM"), a priority indicator dot in the top-right corner (colored circle matching priority), a task count pill at the bottom ("N tasks due today"). Dark gradient background matching the app. When tapped opens the app to Today filter. When no tasks due today show: a green checkmark and "All caught up today!" message.

**MEDIUM WIDGET (4x2):**

Shows: "Today's Focus" header with today's date. Three task rows — the top 3 priority tasks due today. Each row: a small checkbox (tappable — tapping the checkbox in the widget marks the task complete directly from the home screen without opening the app), the task title (truncated), the due time, and a priority colored dot. A "+" button at the bottom-right opens the app's quick add screen. If fewer than 3 tasks exist today fill empty rows with "Add a task for today" in muted italic. A thin progress bar at the very bottom fills as tasks are completed — provides a satisfying visual of today's progress.

**LARGE WIDGET (4x4):**

Shows: a compact version of the stat cards row at the top — Today count, Done count, Streak count in a horizontal strip. Below it: "Today" section with up to 5 tasks (same row format as medium widget with tappable checkboxes). Below the tasks: a mini 5-day upcoming strip — each day as a small column showing the day name, date, and task count dot. A "View All" button at the bottom opens the app. If the streak is active show a flame icon next to the streak count with the number of days.

**EXTRA LARGE WIDGET (4x6) — Android only:**

Combines: the full stat card strip (Today, Done, Overdue, Streak, Focus Score — all 5), a 7-task "Today" list with tappable checkboxes, the 7-day upcoming timeline strip, and a motivational quote at the very bottom in muted italic text. The most comprehensive glanceable view of the task manager without opening the app.

**Widget Configuration:**

Each widget size has a configurable option screen (long press widget → Edit Widget): Filter to show (All / Today / Starred / Specific Category), Theme override (Auto / Always Dark / Always Light), Show completed tasks toggle (hide tasks marked complete), Number of tasks shown (for medium and large widgets — 3/4/5 tasks).

---

## MICRO-INTERACTIONS & POLISH — Every Single One:

This section is non-negotiable. Every interaction listed here must be implemented completely and correctly. These are the details that make an app feel premium.

**Task Completion Celebration:**

When a task is marked complete (checkbox tap or swipe right) the sequence is: the checkbox fills with a spring animation — the checkmark draws itself from start to end of the stroke over 200ms with a slight overshoot. Simultaneously the task title begins a strikethrough that draws from left to right over 150ms. The card background flashes very briefly (50ms) in the priority color then fades. For tasks that were High or Urgent priority: a confetti burst fires from the checkbox position — 12-15 small colored particles (matching the priority color + complementary colors) burst outward and fall with gravity physics over 800ms. The card then collapses with a spring animation (height reduces to 0 over 300ms with spring overshoot) and flies to the "Completed" section with a quick upward or downward translation depending on where that section is. A subtle success haptic pattern fires (double tap pattern). A brief "✓ Completed" toast appears at the bottom of the screen and auto-dismisses after 2 seconds with a slide-down animation. The toast has an "Undo" button — tapping it within 5 seconds reverses the completion with a reverse animation (card expands back from the Completed section, strikethrough erases, checkbox unchecks).

**Streak Milestone Celebration:**

When the streak count increases (at least one task completed after a day without) the streak banner on the home screen activates: the flame icon animates — a particle fire effect where small orange particles rise from the flame in a realistic upward drift. The streak counter flips from the old number to the new number using a drum-roll flip animation — the digit spins briefly then settles on the new number. If it's a milestone streak (7 days, 14 days, 21 days, 30 days, 60 days, 100 days) a full-screen celebration overlay appears for 2 seconds: a centered achievement card with the milestone flame icon animated large, "🔥 [N]-Day Streak!" in large bold white text, a brief particle burst fills the screen edges, and a distinctive achievement haptic pattern fires (three strong pulses). The overlay auto-dismisses or can be tapped to dismiss immediately.

**New Task Added:**

When a task is created from the quick add bar: the task card slides in from below the input field with a spring bounce — it overshoots slightly then settles into its position in the list. Other cards below it smoothly shift down to make room. A brief green glow pulses once around the new card's border. The input field clears with a smooth fade. A "Task added ✓" micro-toast appears briefly in the accent color below the input bar.

**Task Card Delete:**

When a task is deleted (swipe left and confirm, or context menu delete): the card crumples — a CSS/animation transform that compresses the card's height and shifts it slightly as if being physically scrunched — over 200ms. Then it shrinks to zero width from right to left over 150ms. Then disappears with a final fade. The surrounding cards close the gap with a smooth spring animation. A warning haptic fires before the deletion animation starts. A "Deleted" toast with an "Undo" button allows restoration within 5 seconds — tapping Undo reverses the animation: the card expands back from nothing into its original position.

**Priority Change Animation:**

When a task's priority is changed (from the priority badge tap or context menu) the task card's left border, priority badge, and any glow effects smoothly morph to the new priority color over 300ms — a color cross-fade that's clearly visible and satisfying. The priority badge text also changes. If the priority is elevated to Urgent the card border begins its subtle pulsing glow animation. If lowered from Urgent the pulsing stops with a fade-out.

**Pull to Refresh:**

Replace the default spinner with a custom branded animation. When the user pulls down on the task list: a custom animation appears in the pull space showing the app's stylized logo or a task-related animation (a small task card with a circular arrow indicating refresh). As the user pulls further the animation grows. On release the animation plays a completion spin and the list refreshes. The animation runs for a minimum of 600ms (even if the refresh is instant) so it feels deliberate and polished — not a flash.

**Long Press Haptic and Multi-Select:**

When a long press is detected: a ripple animation expands from the press point across the card surface (a lighter transparent circle expanding outward over 200ms). Simultaneously a medium impact haptic fires. All task cards instantly show a circular checkbox in their top-left corner — the checkboxes appear with a spring scale-up animation staggered 20ms between cards (so they appear to cascade across the screen). The pressed card's checkbox is pre-selected (filled). A bottom action bar slides up from below with spring physics: "N selected" counter on the left, action icons in the center and right (Complete, Move, Tag, Delete, Share). Selecting additional cards: the checkbox fills with a bounce animation, the counter in the bottom bar increments. Deselecting: the checkbox empties, counter decrements. Selecting all: a "Select All" button in the header fills all checkboxes with a satisfying cascade animation.

**Bottom Sheet Open:**

Every bottom sheet in the app — quick add, task detail, filter panel, sort panel, template picker, color picker — opens with the same physics: it springs up from the bottom with a cubic-bezier easing that overshoots very slightly and settles. The backdrop behind it fades from 0 to a dark semi-transparent blur simultaneously. The spring feels natural — like a physical object being released. Closing is the reverse — it slides down with a ease-in and the backdrop fades out. Drag-to-dismiss is always available — dragging the sheet down past 40% of its height triggers the close animation.

**Empty State Animated Characters:**

Every empty state has a small illustrated character or scene that reacts to the context:

All Tasks empty: a small illustrated character sitting at a desk looking relaxed with a thumbs up. When the screen is first shown the character waves. "No tasks yet — add your first task and get moving!"

Today empty: a sun illustrated scene with a character lounging. "Nothing due today — enjoy your free day!"

Overdue empty: a character doing a victory dance, confetti around them. "Nothing overdue — you're absolutely crushing it!"

All Done for the day (after completing everything due today): the character throws their hands up in celebration, a burst of stars around them. A unique celebration that only appears when the last due-today task is completed.

The character illustrations are consistent across all empty states — the same character in different poses and situations creates a sense of personality and continuity.

**Success States — Particle Effects:**

For significant achievements (completing the last task due today, hitting a goal, reaching a milestone streak) a tasteful particle effect fires — not overdone. Particles: 15-20 small circles in the accent color + complementary colors, burst from a central point, follow realistic gravity (initial upward velocity, then fall), fade out as they fall. Duration: 1.2 seconds total. Fires once and stops — never loops. Used sparingly: only for genuinely significant moments, not every task completion.

**Error States — Gentle Shake:**

When a validation error occurs (trying to save a task with no title, entering an invalid date, failing to connect to Google Calendar) the relevant field or card shakes horizontally — a brief left-right-left oscillation over 300ms with decreasing amplitude (like a physical object settling after being bumped). An error haptic fires simultaneously (a single heavy buzz). An error message appears below the field in red with a fade-in animation. The shake + haptic combination is immediately understandable without needing to read the error message — the user knows something is wrong and looks for the message.

**Skeleton Loading Screens:**

Every list view shows skeleton screens while data is loading (on cold start or after a pull-to-refresh). The skeleton: task card-shaped rectangles in the correct card dimensions, filled with a shimmer animation — a lighter grey/blue wave that moves from left to right across all skeletons simultaneously in a loop. Three skeleton cards shown by default. The skeletons fade out and real cards fade in when data is ready. The transition from skeletons to real cards is smooth — opacity cross-fade rather than a sudden swap. Never show a spinner in the center of a task list — always use skeletons.

**Sound Design:**

All sounds are subtle and respect system volume and silent mode. If the device is on silent no sounds play. Implementation approach: use platform audio APIs with the media stream set to NOT_MUSIC (doesn't interrupt music playing). Sounds included: Task Completion Chime (a soft single-note bell tone, 200ms — plays on every task completion), Urgent Task Complete (a slightly richer 2-note chime — plays only when an Urgent task is completed), Swipe Whoosh (a soft whoosh sound, 100ms, plays when swipe-to-complete or swipe-to-delete gesture completes), Streak Milestone (a short ascending 3-note chime, plays on milestone streaks), Focus Session Complete (a gentle ambient completion tone, 500ms, plays when a Pomodoro session ends), Error Sound (a very soft low "thud," 100ms, plays on validation errors). All sounds bundled locally — no internet required. A "Sound Effects" toggle in Settings Section 9 (Appearance) that disables all task manager sounds globally.

---

## ACCESSIBILITY — Full Compliance:

**VoiceOver / TalkBack Support:**

Every interactive element has a semantic accessibility label: task cards labeled "Task: [title], [priority] priority, due [date], [completion status]", checkboxes labeled "Mark [task title] as complete", priority badges labeled "[Priority] priority — double tap to change", swipe actions announced ("Swipe right to complete, swipe left to delete"), the FAB labeled "Add new task — double tap to activate", filter chips labeled "[Filter name] filter — double tap to select", stat cards labeled "[Stat name]: [value]". Focus order is logical — goes left to right, top to bottom through the screen. Dynamic content updates are announced: "Task marked complete", "3 tasks selected", "Filter applied: Today", "No tasks match current filter."

**Reduced Motion Mode:**

When the device's Reduce Motion accessibility setting is enabled (or when "Animation Speed: Off" is selected in app settings) replace all spring physics animations with simple instant transitions, disable particle effects entirely, replace the shimmer loading with a static skeleton, replace the floating confetti with a simple green flash, replace the streak flame particles with a static flame icon, replace all complex transitions with simple opacity fades at 200ms duration. The app must be fully usable and visually clear without any animations. Test every screen in reduced motion mode.

**High Contrast Mode:**

A toggle in Settings Appearance section "High Contrast Mode." When enabled: all card backgrounds increase their contrast ratio to meet WCAG AAA standards (7:1 minimum), borders become more visible (increased opacity), text becomes pure white (never muted), priority colors use their full saturation versions without any opacity reduction, disabled states use a pattern (diagonal stripes) in addition to reduced opacity so color-blind users can distinguish them. High Contrast also activates when the device's "Increase Contrast" accessibility setting is enabled.

**Dynamic Type:**

All text in the app scales with the system Dynamic Type setting (iOS) or system font size (Android). Small text scales up (never stays fixed), titles scale proportionally. Test that at the largest accessibility text size the app remains usable — no text is clipped, no layout overflows, cards expand to accommodate larger text. Long task titles wrap gracefully — never truncated in a way that hides important information.

**Color-Blind Accessibility:**

Priority levels must be distinguishable without relying solely on color. In addition to the priority color each priority has a unique icon: Low priority = chevron-down icon, Normal = dash icon (—), High = chevron-up icon, Urgent = exclamation mark icon (!). These icons appear on the priority badge alongside the color so users with any type of color blindness can distinguish priorities by shape. The icon sizes are accessible at minimum — 16sp. A "Color-Blind Mode" toggle in Settings Appearance section that applies Deuteranopia-safe colors: replaces green with blue for success states, replaces red/green combinations with red/blue combinations, uses patterns and shapes more prominently.

**Minimum Touch Targets:**

Every tappable element is minimum 44x44 points in both dimensions. This includes: the task card checkbox (the tappable area extends beyond the visual checkbox into the surrounding padding), the priority badge (the tap area is padded even if the visual badge is small), all icon buttons in toolbars, filter chips, and the bottom navigation tabs. Verify every element meets this standard — no exceptions.

**Keyboard Navigation:**

On Android devices with physical keyboards and on iOS with connected Bluetooth keyboards the app must be fully navigable: Tab to move between interactive elements, Enter to activate, Escape to dismiss sheets and dialogs, Arrow keys to navigate within lists and pickers. All keyboard shortcuts announced to screen readers.

---

## FINAL PERFORMANCE AUDIT — Non-Negotiable Targets:

Go through every screen and operation in the entire Task Manager feature and verify these targets are met. Fix any that fall short.

**App Cold Start:**

From tapping the app icon to the Task Manager home screen being fully interactive (not just visible — actually responding to input): under 1.5 seconds on a mid-range device (3GB RAM, 2020-era processor). Home screen data must be pre-loaded from the local database before the UI appears — show skeletons during loading, not a blank screen. The splash screen (app icon) shows during loading and transitions to the home screen smoothly.

**List Scrolling:**

A list of 1000+ tasks must scroll at a sustained 60fps with zero dropped frames. Achieve this through: proper virtualization — only render the task cards visible on screen plus 5 above and below (the window), pre-calculate card heights and cache them, use the platform's built-in RecyclerView (Android) or UICollectionView (iOS) with proper cell reuse, lazy-load any images or attachments in cards (show placeholder until the card is visible), avoid any database queries during scrolling (all data pre-loaded into memory for the current view).

**Optimistic UI Updates:**

Every user action (check a task, change priority, add a tag, create a task, delete a task) must give instant visual feedback before the database write completes. The UI updates immediately assuming success. The database write happens asynchronously in the background. If the write fails (which should be extremely rare for local SQLite operations) roll back the UI change with an error animation and show an error toast. This makes every interaction feel instantaneous regardless of device storage speed.

**Database Queries:**

No database query should ever run on the main UI thread. All reads and writes use async operations. Frequently accessed data (today's tasks, overdue count, streak count, productivity score) is cached in memory and updated incrementally when tasks change — never fully reloaded from the database on every screen open. The cache invalidates only when a relevant change occurs (a task's due date changes → invalidate the "today" cache). Query plan optimization: all commonly filtered fields (dueDate, priority, categoryId, isCompleted, isStarred) have database indexes.

**Search Response Time:**

Full-text search results must appear within 150ms of the user stopping typing (300ms debounce + 150ms search = 450ms total from last keystroke to results visible). Achieve through: in-memory search index built from all tasks on app open and updated incrementally on task changes, indexed search across title, description, tags, notes — never a raw SQL LIKE query on the entire database for every keystroke.

**Animation Frame Rate:**

Every animation in the app must run at 60fps minimum (120fps on ProMotion displays if the platform supports it). Verify by: profiling the completion animation with Perfetto (Android) or Instruments (iOS), profiling the list scroll with 1000 tasks, profiling the Kanban drag and drop, profiling the Calendar swipe between months. Any animation consistently dropping below 55fps must be optimized — move work off the main thread, reduce the number of animated properties simultaneously, use hardware acceleration correctly.

**Memory Usage:**

The app must not exceed 150MB RAM on a mid-range device with 1000 tasks loaded. Achieve through: thumbnail lazy loading with an LRU cache, proper widget disposal when screens are removed from navigation, no memory leaks in timer services or background services, proper cleanup of listeners and subscriptions on screen dispose.

**Battery Impact:**

Background services (timer, geofencing, intelligence analysis) must not drain the battery noticeably. Verify: geofence monitoring uses significant location change events (not continuous GPS polling), intelligence analysis runs at most every 4 hours and completes within 30 seconds, timer background notification updates every second but uses minimal CPU, no wakelocks held unnecessarily. Use Android Battery Historian and iOS Energy Log to verify impact is minimal.

**Offline Operation:**

Every single feature must work without any internet connection: task creation, editing, deletion, all views, focus mode, time tracking, templates, search, analytics, settings, onboarding. The only features requiring internet are: cloud backup sync, Google Calendar sync, weather-aware reminders (silently skipped when offline), and Google Tasks import. All other features must work identically online and offline. Test by: enabling airplane mode and using every feature. No crashes, no error toasts (except explicitly network-dependent features which show a clear "Requires internet" message).

---

## FINAL INTEGRATION VERIFICATION:

Go through every integration with other features in the app and verify they work end-to-end correctly:

Calendar Integration: tasks with due dates appear in the Calendar feature as events. Changes to due dates in the Task Manager reflect in the Calendar immediately. Calendar events linked to tasks show "View Task" in the Calendar event detail. Meeting tasks created from the Meeting feature appear in the Task Manager correctly.

Notes Integration: notes linked to tasks appear in the task detail. Tasks linked to notes appear in the note detail. The "Extract Tasks" feature from Notes correctly creates tasks in the Task Manager.

Smart File Hub Integration: files attached to tasks via "Attach from File Hub" display correctly in the task detail. Files linked from the File Hub show their task linkages correctly.

Expense Tracker Integration: "Log Expense" from task detail opens the Expense Tracker pre-filled correctly.

History Tab Integration: all Task Manager activity (task created, completed, overdue events, priority changes) feeds into the app's global History tab correctly and shows with the correct task icons and descriptions.

Home Screen Integration: the Tasks card on the main app home screen shows the correct live count of today's tasks in its subtitle. Tapping the Tasks card navigates to the Task Manager home correctly.

---

**THE ABSOLUTE FINAL STANDARD:**

After all 4 prompts are complete the Task Manager must be the most premium, feature-complete, visually stunning task management application ever built as part of a personal productivity suite. The Settings screen is comprehensive and richly designed — not a plain list. The onboarding converts new users instantly — they understand the app's power within 60 seconds. The widgets give genuine daily utility without opening the app. Every micro-interaction is deliberate and satisfying — the completion checkmark, the streak flame, the skeleton screens, the shake on errors, the particle celebrations. Accessibility ensures the app works for every user regardless of visual ability, motor ability, or preferred input method. Performance targets are all met — 60fps everywhere, 1.5 second cold start, instant optimistic UI updates. A user paying for the most expensive productivity app on the market should feel this is better than what they paid for. That is the standard. Build it completely. Make it extraordinary.

if any
