package com.prajwal.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * NotesSettingsActivity — comprehensive Notes & To-Do settings screen.
 *
 * Sections: Appearance · Editor · Organization · Security
 *           To-Do · Notifications · Data · About
 */
public class NotesSettingsActivity extends Activity {

    private NotesSettings settings;

    // ─── Appearance ───────────────────────────────────────────────
    private TextView itemDefaultNoteColor;
    private TextView itemBackgroundTexture;
    private TextView itemDefaultView;
    private TextView itemGridColumns;
    private TextView itemPreviewLines;
    private Switch   switchShowTags;
    private Switch   switchShowFolderBadge;
    private Switch   switchShowTimestamps;
    private TextView itemAccentColor;

    // ─── Editor ───────────────────────────────────────────────────
    private TextView itemAutosaveInterval;
    private TextView itemFontSize;
    private TextView itemFontFamily;
    private Switch   switchAutoCapitalize;
    private Switch   switchSpellCheck;
    private TextView itemToolbarVisibility;
    private Switch   switchWordCount;
    private Switch   switchReadingTime;
    private TextView itemChecklistBehavior;

    // ─── Organization ─────────────────────────────────────────────
    private TextView itemDefaultSort;
    private TextView itemDefaultCategory;
    private Switch   switchAutoCateg;
    private Switch   switchSmartTags;
    private Switch   switchDuplicateDetect;

    // ─── Security ─────────────────────────────────────────────────
    private Switch   switchAppLock;
    private TextView itemBiometricPref;
    private TextView itemAutoLockAfter;
    private Switch   switchScreenshotProtection;

    // ─── To-Do ────────────────────────────────────────────────────
    private TextView itemDefaultPriority;
    private TextView itemDefaultDueTime;
    private TextView itemOverdueNotifTime;
    private Switch   switchDailyDigest;
    private TextView itemDigestTime;
    private Switch   switchRecurringAutoCreate;
    private Switch   switchCompletedAutoArchive;

    // ─── Notifications ────────────────────────────────────────────
    private Switch   switchNoteReminders;
    private Switch   switchTodoReminders;
    private Switch   switchQuickCapture;
    private TextView itemNotifSound;
    private Switch   switchNotifVibration;

    // ─── Data ─────────────────────────────────────────────────────
    private TextView itemTrashAutoDelete;
    private TextView itemArchiveNotes;
    private TextView itemExportAll;
    private TextView itemImportNotes;
    private TextView itemClearCompletedTodos;
    private TextView itemResetSettings;

    // ─── About ────────────────────────────────────────────────────
    private TextView tvAboutVersion;
    private TextView tvAboutStorage;
    private TextView itemKeyboardShortcuts;

    // ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_settings);

        settings = NotesSettings.getInstance(this);

        applyScreenshotProtection();
        bindViews();
        loadSettings();
        setupListeners();
    }

    private void applyScreenshotProtection() {
        if (settings.isScreenshotProtection()) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    // ─── View binding ─────────────────────────────────────────────

    private void bindViews() {
        findViewById(R.id.btnBackNotesSettings)
                .setOnClickListener(v -> finish());

        // Appearance
        itemDefaultNoteColor   = findViewById(R.id.itemDefaultNoteColor);
        itemBackgroundTexture  = findViewById(R.id.itemBackgroundTexture);
        itemDefaultView        = findViewById(R.id.itemDefaultView);
        itemGridColumns        = findViewById(R.id.itemGridColumns);
        itemPreviewLines       = findViewById(R.id.itemPreviewLines);
        switchShowTags         = findViewById(R.id.switchShowTags);
        switchShowFolderBadge  = findViewById(R.id.switchShowFolderBadge);
        switchShowTimestamps   = findViewById(R.id.switchShowTimestamps);
        itemAccentColor        = findViewById(R.id.itemAccentColor);

        // Editor
        itemAutosaveInterval   = findViewById(R.id.itemAutosaveInterval);
        itemFontSize           = findViewById(R.id.itemFontSize);
        itemFontFamily         = findViewById(R.id.itemFontFamily);
        switchAutoCapitalize   = findViewById(R.id.switchAutoCapitalize);
        switchSpellCheck       = findViewById(R.id.switchSpellCheck);
        itemToolbarVisibility  = findViewById(R.id.itemToolbarVisibility);
        switchWordCount        = findViewById(R.id.switchWordCount);
        switchReadingTime      = findViewById(R.id.switchReadingTime);
        itemChecklistBehavior  = findViewById(R.id.itemChecklistBehavior);

        // Organization
        itemDefaultSort        = findViewById(R.id.itemDefaultSort);
        itemDefaultCategory    = findViewById(R.id.itemDefaultCategory);
        switchAutoCateg        = findViewById(R.id.switchAutoCateg);
        switchSmartTags        = findViewById(R.id.switchSmartTags);
        switchDuplicateDetect  = findViewById(R.id.switchDuplicateDetect);

        // Security
        switchAppLock          = findViewById(R.id.switchAppLock);
        itemBiometricPref      = findViewById(R.id.itemBiometricPref);
        itemAutoLockAfter      = findViewById(R.id.itemAutoLockAfter);
        switchScreenshotProtection = findViewById(R.id.switchScreenshotProtection);

        // To-Do
        itemDefaultPriority    = findViewById(R.id.itemDefaultPriority);
        itemDefaultDueTime     = findViewById(R.id.itemDefaultDueTime);
        itemOverdueNotifTime   = findViewById(R.id.itemOverdueNotifTime);
        switchDailyDigest      = findViewById(R.id.switchDailyDigest);
        itemDigestTime         = findViewById(R.id.itemDigestTime);
        switchRecurringAutoCreate  = findViewById(R.id.switchRecurringAutoCreate);
        switchCompletedAutoArchive = findViewById(R.id.switchCompletedAutoArchive);

        // Notifications
        switchNoteReminders    = findViewById(R.id.switchNoteReminders);
        switchTodoReminders    = findViewById(R.id.switchTodoReminders);
        switchQuickCapture     = findViewById(R.id.switchQuickCapture);
        itemNotifSound         = findViewById(R.id.itemNotifSound);
        switchNotifVibration   = findViewById(R.id.switchNotifVibration);

        // Data
        itemTrashAutoDelete    = findViewById(R.id.itemTrashAutoDelete);
        itemArchiveNotes       = findViewById(R.id.itemArchiveNotes);
        itemExportAll          = findViewById(R.id.itemExportAll);
        itemImportNotes        = findViewById(R.id.itemImportNotes);
        itemClearCompletedTodos = findViewById(R.id.itemClearCompletedTodos);
        itemResetSettings      = findViewById(R.id.itemResetSettings);

        // About
        tvAboutVersion         = findViewById(R.id.tvAboutVersion);
        tvAboutStorage         = findViewById(R.id.tvAboutStorage);
        itemKeyboardShortcuts  = findViewById(R.id.itemKeyboardShortcuts);
    }

    // ─── Load current settings into views ─────────────────────────

    private void loadSettings() {
        // Appearance
        itemDefaultNoteColor.setText("🎨  Default Note Color: " + settings.getDefaultNoteColor());
        itemBackgroundTexture.setText("🖼️  Background Texture: " + capitalize(settings.getBackgroundTexture()));
        itemDefaultView.setText("📐  Default View: " + capitalize(settings.getDefaultView()));
        itemGridColumns.setText("⊞  Grid Columns: " + settings.getGridColumnCount());
        int lines = settings.getCardPreviewLines();
        itemPreviewLines.setText("📄  Preview Lines: " + (lines == 0 ? "None (title only)" : lines + " lines"));
        switchShowTags.setChecked(settings.showTagsOnCards());
        switchShowFolderBadge.setChecked(settings.showFolderBadge());
        switchShowTimestamps.setChecked(settings.showTimestamps());
        itemAccentColor.setText("🎨  App Theme Accent: " + accentColorName(settings.getAccentColor()));

        // Editor
        itemAutosaveInterval.setText("💾  Auto-save: " + formatInterval(settings.getAutosaveInterval()));
        itemFontSize.setText("🔤  Font Size: " + capitalize(settings.getFontSize()));
        itemFontFamily.setText("🔡  Font Family: " + capitalize(settings.getFontFamily()));
        switchAutoCapitalize.setChecked(settings.isAutoCapitalize());
        switchSpellCheck.setChecked(settings.isSpellCheck());
        itemToolbarVisibility.setText("🛠️  Show Toolbar: " +
                ("always".equals(settings.getToolbarVisibility()) ? "Always" : "Only When Keyboard Visible"));
        switchWordCount.setChecked(settings.showWordCount());
        switchReadingTime.setChecked(settings.showReadingTime());
        itemChecklistBehavior.setText("☑️  Completed Items: " +
                (settings.hideCompletedChecklist() ? "Hide completed" : "Show completed"));

        // Organization
        itemDefaultSort.setText("⇅  Default Sort: " + sortLabel(settings.getDefaultSort()));
        itemDefaultCategory.setText("🗂️  Default Category: " + settings.getDefaultCategory());
        switchAutoCateg.setChecked(settings.isAutoCategorization());
        switchSmartTags.setChecked(settings.isSmartTagSuggestions());
        switchDuplicateDetect.setChecked(settings.isDuplicateDetection());

        // Security
        switchAppLock.setChecked(settings.isAppLockEnabled());
        itemBiometricPref.setText("🔐  Biometric: " + capitalize(settings.getBiometricPref()));
        itemAutoLockAfter.setText("⏱  Auto-lock After: " + formatLockAfter(settings.getAutoLockAfter()));
        switchScreenshotProtection.setChecked(settings.isScreenshotProtection());

        // To-Do
        itemDefaultPriority.setText("⚡  Default Priority: " + capitalize(settings.getDefaultTaskPriority()));
        itemDefaultDueTime.setText("🕒  Default Due Time: " + settings.getDefaultDueTime());
        itemOverdueNotifTime.setText("⏰  Overdue Alert Time: " + settings.getOverdueNotifTime());
        switchDailyDigest.setChecked(settings.isDailyDigest());
        itemDigestTime.setText("📋  Digest Time: " + settings.getDigestTime());
        switchRecurringAutoCreate.setChecked(settings.isRecurringAutoCreate());
        switchCompletedAutoArchive.setChecked(settings.isCompletedAutoArchive());

        // Notifications
        switchNoteReminders.setChecked(settings.isNoteReminders());
        switchTodoReminders.setChecked(settings.isTodoReminders());
        switchQuickCapture.setChecked(settings.isQuickCaptureNotif());
        itemNotifSound.setText("🔔  Sound: " + capitalize(settings.getNotifSound()));
        switchNotifVibration.setChecked(settings.isNotifVibration());

        // Data
        int trashDays = settings.getTrashAutoDeleteDays();
        itemTrashAutoDelete.setText("🗑️  Trash Auto-delete: " +
                (trashDays < 0 ? "Never" : "After " + trashDays + " days"));

        // About
        tvAboutVersion.setText("Notes v3.0  ·  Built for Android");
        long notesBytes = estimateNotesStorage();
        tvAboutStorage.setText("💾  Storage: " + formatBytes(notesBytes));
    }

    // ─── Listeners ────────────────────────────────────────────────

    private void setupListeners() {
        // Appearance
        itemDefaultNoteColor.setOnClickListener(v -> pickDefaultNoteColor());
        itemBackgroundTexture.setOnClickListener(v -> pickOption("Background Texture",
                new String[]{"Plain", "Grid", "Dots", "Crosshatch"},
                new String[]{NotesSettings.TEXTURE_PLAIN, NotesSettings.TEXTURE_GRID,
                             NotesSettings.TEXTURE_DOTS, NotesSettings.TEXTURE_CROSSHATCH},
                settings.getBackgroundTexture(),
                (val) -> { settings.setBackgroundTexture(val);
                           itemBackgroundTexture.setText("🖼️  Background Texture: " + capitalize(val)); }));
        itemDefaultView.setOnClickListener(v -> pickOption("Default View",
                new String[]{"Grid", "List"},
                new String[]{"grid", "list"},
                settings.getDefaultView(),
                (val) -> { settings.setDefaultView(val);
                           itemDefaultView.setText("📐  Default View: " + capitalize(val)); }));
        itemGridColumns.setOnClickListener(v -> pickOption("Grid Column Count",
                new String[]{"2 columns", "3 columns"},
                new String[]{"2", "3"},
                String.valueOf(settings.getGridColumnCount()),
                (val) -> { settings.setGridColumnCount(Integer.parseInt(val));
                           itemGridColumns.setText("⊞  Grid Columns: " + val); }));
        itemPreviewLines.setOnClickListener(v -> pickOption("Preview Lines",
                new String[]{"None (title only)", "1 line", "2 lines", "3 lines"},
                new String[]{"0", "1", "2", "3"},
                String.valueOf(settings.getCardPreviewLines()),
                (val) -> { int n = Integer.parseInt(val); settings.setCardPreviewLines(n);
                           itemPreviewLines.setText("📄  Preview Lines: " +
                               (n == 0 ? "None (title only)" : n + " lines")); }));
        switchShowTags.setOnCheckedChangeListener((b, checked) -> {
            settings.setShowTagsOnCards(checked);
        });
        switchShowFolderBadge.setOnCheckedChangeListener((b, checked) -> {
            settings.setShowFolderBadge(checked);
        });
        switchShowTimestamps.setOnCheckedChangeListener((b, checked) -> {
            settings.setShowTimestamps(checked);
        });
        itemAccentColor.setOnClickListener(v -> pickAccentColor());

        // Editor
        itemAutosaveInterval.setOnClickListener(v -> pickOption("Auto-save Interval",
                new String[]{"15 seconds", "30 seconds", "1 minute", "2 minutes"},
                new String[]{"15000", "30000", "60000", "120000"},
                String.valueOf(settings.getAutosaveInterval()),
                (val) -> { long ms = Long.parseLong(val); settings.setAutosaveInterval(ms);
                           itemAutosaveInterval.setText("💾  Auto-save: " + formatInterval(ms)); }));
        itemFontSize.setOnClickListener(v -> pickOption("Font Size",
                new String[]{"Small", "Medium", "Large", "Extra Large"},
                new String[]{"small", "medium", "large", "xlarge"},
                settings.getFontSize(),
                (val) -> { settings.setFontSize(val);
                           itemFontSize.setText("🔤  Font Size: " + capitalize(val)); }));
        itemFontFamily.setOnClickListener(v -> pickOption("Font Family",
                new String[]{"Default (Sans-serif)", "Serif", "Monospace"},
                new String[]{"default", "serif", "monospace"},
                settings.getFontFamily(),
                (val) -> { settings.setFontFamily(val);
                           itemFontFamily.setText("🔡  Font Family: " + capitalize(val)); }));
        switchAutoCapitalize.setOnCheckedChangeListener((b, checked) -> settings.setAutoCapitalize(checked));
        switchSpellCheck.setOnCheckedChangeListener((b, checked) -> settings.setSpellCheck(checked));
        itemToolbarVisibility.setOnClickListener(v -> pickOption("Show Toolbar",
                new String[]{"Always", "Only When Keyboard Visible"},
                new String[]{"always", "keyboard"},
                settings.getToolbarVisibility(),
                (val) -> { settings.setToolbarVisibility(val);
                           itemToolbarVisibility.setText("🛠️  Show Toolbar: " +
                               ("always".equals(val) ? "Always" : "Only When Keyboard Visible")); }));
        switchWordCount.setOnCheckedChangeListener((b, checked) -> settings.setShowWordCount(checked));
        switchReadingTime.setOnCheckedChangeListener((b, checked) -> settings.setShowReadingTime(checked));
        itemChecklistBehavior.setOnClickListener(v -> pickOption("Default Checklist Behavior",
                new String[]{"Show completed items", "Hide completed items"},
                new String[]{"show", "hide"},
                settings.hideCompletedChecklist() ? "hide" : "show",
                (val) -> { boolean hide = "hide".equals(val); settings.setHideCompletedChecklist(hide);
                           itemChecklistBehavior.setText("☑️  Completed Items: " +
                               (hide ? "Hide completed" : "Show completed")); }));

        // Organization
        itemDefaultSort.setOnClickListener(v -> pickOption("Default Sort",
                new String[]{"Date Modified", "Date Created", "Title A–Z", "Manual"},
                new String[]{"modified", "created", "title", "manual"},
                settings.getDefaultSort(),
                (val) -> { settings.setDefaultSort(val);
                           itemDefaultSort.setText("⇅  Default Sort: " + sortLabel(val)); }));
        itemDefaultCategory.setOnClickListener(v -> {
            String[] cats = {"All", "Personal", "Work", "Study", "Ideas"};
            pickOption("Default Category", cats, cats, settings.getDefaultCategory(),
                (val) -> { settings.setDefaultCategory(val);
                           itemDefaultCategory.setText("🗂️  Default Category: " + val); });
        });
        switchAutoCateg.setOnCheckedChangeListener((b, checked) -> settings.setAutoCategorization(checked));
        switchSmartTags.setOnCheckedChangeListener((b, checked) -> settings.setSmartTagSuggestions(checked));
        switchDuplicateDetect.setOnCheckedChangeListener((b, checked) -> settings.setDuplicateDetection(checked));

        // Security
        switchAppLock.setOnCheckedChangeListener((b, checked) -> {
            settings.setAppLockEnabled(checked);
            itemBiometricPref.setEnabled(checked);
            itemAutoLockAfter.setEnabled(checked);
        });
        itemBiometricPref.setOnClickListener(v -> pickOption("Biometric Preference",
                new String[]{"Face ID", "Fingerprint", "PIN"},
                new String[]{"face", "fingerprint", "pin"},
                settings.getBiometricPref(),
                (val) -> { settings.setBiometricPref(val);
                           itemBiometricPref.setText("🔐  Biometric: " + capitalize(val)); }));
        itemAutoLockAfter.setOnClickListener(v -> pickOption("Auto-lock After",
                new String[]{"1 minute", "2 minutes", "5 minutes", "Never"},
                new String[]{"60000", "120000", "300000", "-1"},
                String.valueOf(settings.getAutoLockAfter()),
                (val) -> { long ms = Long.parseLong(val); settings.setAutoLockAfter(ms);
                           itemAutoLockAfter.setText("⏱  Auto-lock After: " + formatLockAfter(ms)); }));
        switchScreenshotProtection.setOnCheckedChangeListener((b, checked) -> {
            settings.setScreenshotProtection(checked);
            if (checked) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                     WindowManager.LayoutParams.FLAG_SECURE);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        });

        // To-Do
        itemDefaultPriority.setOnClickListener(v -> pickOption("Default Priority",
                new String[]{"None", "Low", "Medium", "High"},
                new String[]{"none", "low", "medium", "high"},
                settings.getDefaultTaskPriority(),
                (val) -> { settings.setDefaultTaskPriority(val);
                           itemDefaultPriority.setText("⚡  Default Priority: " + capitalize(val)); }));
        itemDefaultDueTime.setOnClickListener(v ->
                showTimePicker(settings.getDefaultDueTime(), (time) -> {
                    settings.setDefaultDueTime(time);
                    itemDefaultDueTime.setText("🕒  Default Due Time: " + time);
                }));
        itemOverdueNotifTime.setOnClickListener(v ->
                showTimePicker(settings.getOverdueNotifTime(), (time) -> {
                    settings.setOverdueNotifTime(time);
                    itemOverdueNotifTime.setText("⏰  Overdue Alert Time: " + time);
                }));
        switchDailyDigest.setOnCheckedChangeListener((b, checked) -> {
            settings.setDailyDigest(checked);
            itemDigestTime.setEnabled(checked);
        });
        itemDigestTime.setOnClickListener(v ->
                showTimePicker(settings.getDigestTime(), (time) -> {
                    settings.setDigestTime(time);
                    itemDigestTime.setText("📋  Digest Time: " + time);
                }));
        switchRecurringAutoCreate.setOnCheckedChangeListener((b, checked) -> settings.setRecurringAutoCreate(checked));
        switchCompletedAutoArchive.setOnCheckedChangeListener((b, checked) -> settings.setCompletedAutoArchive(checked));

        // Notifications
        switchNoteReminders.setOnCheckedChangeListener((b, checked) -> settings.setNoteReminders(checked));
        switchTodoReminders.setOnCheckedChangeListener((b, checked) -> settings.setTodoReminders(checked));
        switchQuickCapture.setOnCheckedChangeListener((b, checked) -> settings.setQuickCaptureNotif(checked));
        itemNotifSound.setOnClickListener(v -> pickOption("Notification Sound",
                new String[]{"Default", "Chime", "Bell", "None"},
                new String[]{"default", "chime", "bell", "none"},
                settings.getNotifSound(),
                (val) -> { settings.setNotifSound(val);
                           itemNotifSound.setText("🔔  Sound: " + capitalize(val)); }));
        switchNotifVibration.setOnCheckedChangeListener((b, checked) -> settings.setNotifVibration(checked));

        // Data
        itemTrashAutoDelete.setOnClickListener(v -> pickOption("Trash Auto-delete",
                new String[]{"7 days", "15 days", "30 days", "Never"},
                new String[]{"7", "15", "30", "-1"},
                String.valueOf(settings.getTrashAutoDeleteDays()),
                (val) -> { int days = Integer.parseInt(val); settings.setTrashAutoDeleteDays(days);
                           itemTrashAutoDelete.setText("🗑️  Trash Auto-delete: " +
                               (days < 0 ? "Never" : "After " + days + " days")); }));
        itemArchiveNotes.setOnClickListener(v ->
                startActivity(new Intent(this, NotesArchiveActivity.class)));
        itemExportAll.setOnClickListener(v -> {
            Toast.makeText(this, "Export: share individual notes from the editor", Toast.LENGTH_SHORT).show();
        });
        itemImportNotes.setOnClickListener(v ->
                Toast.makeText(this, "Import: select a ZIP or CSV from Files", Toast.LENGTH_SHORT).show());
        itemClearCompletedTodos.setOnClickListener(v -> confirmClearCompletedTodos());
        itemResetSettings.setOnClickListener(v -> confirmResetSettings());

        // About
        itemKeyboardShortcuts.setOnClickListener(v -> showKeyboardShortcuts());
    }

    // ─── Dialogs ──────────────────────────────────────────────────

    /** Generic single-choice picker. */
    private void pickOption(String title, String[] labels, String[] values,
                            String current, OnPickedListener listener) {
        int selected = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) { selected = i; break; }
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setSingleChoiceItems(labels, selected, (dialog, which) -> {
                    listener.onPicked(values[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void pickDefaultNoteColor() {
        String[] colorNames = Note.NOTE_COLOR_NAMES;
        String[] colorHexes = Note.NOTE_COLORS;
        String current = settings.getDefaultNoteColor();
        pickOption("Default Note Color", colorNames, colorHexes, current,
            (val) -> { settings.setDefaultNoteColor(val);
                       itemDefaultNoteColor.setText("🎨  Default Note Color: " + val); });
    }

    private void pickAccentColor() {
        pickOption("App Theme Accent",
                NotesSettings.ACCENT_COLOR_NAMES,
                NotesSettings.ACCENT_COLORS,
                settings.getAccentColor(),
                (val) -> { settings.setAccentColor(val);
                           itemAccentColor.setText("🎨  App Theme Accent: " + accentColorName(val)); });
    }

    private void showTimePicker(String currentTime, OnPickedListener listener) {
        int hour = 9, minute = 0;
        try {
            String[] parts = currentTime.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception ignored) {}
        new TimePickerDialog(this, (view, h, m) -> {
            String time = String.format(Locale.US, "%02d:%02d", h, m);
            listener.onPicked(time);
        }, hour, minute, true).show();
    }

    private void confirmClearCompletedTodos() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Completed To-Do Items")
                .setMessage("This will permanently delete all completed to-do tasks. This cannot be undone.")
                .setPositiveButton("Clear", (d, w) -> {
                    TodoRepository repo = new TodoRepository(this);
                    repo.clearAllCompleted();
                    Toast.makeText(this, "Completed tasks cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmResetSettings() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Notes Settings")
                .setMessage("All Notes settings will be restored to their defaults. Your notes will not be affected.")
                .setPositiveButton("Reset", (d, w) -> {
                    settings.resetToDefaults();
                    loadSettings();
                    Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showKeyboardShortcuts() {
        String shortcuts =
            "Keyboard & Gesture Shortcuts\n\n" +
            "Notes Home:\n" +
            "• Swipe right → open drawer\n" +
            "• Long press card → multi-select\n" +
            "• Swipe card left/right → quick actions\n\n" +
            "Editor:\n" +
            "• Ctrl+B → Bold\n" +
            "• Ctrl+I → Italic\n" +
            "• Ctrl+U → Underline\n" +
            "• Ctrl+Z → Undo\n" +
            "• Ctrl+S → Force save\n" +
            "• Swipe down → close editor\n\n" +
            "To-Do:\n" +
            "• Tap checkbox → complete task\n" +
            "• Long press → convert to note task\n" +
            "• Swipe left → delete task";
        new AlertDialog.Builder(this)
                .setTitle("Tips & Shortcuts")
                .setMessage(shortcuts)
                .setPositiveButton("Got it", null)
                .show();
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1);
    }

    private String formatInterval(long ms) {
        if (ms < 60_000) return (ms / 1000) + " seconds";
        return (ms / 60_000) + " minute" + (ms / 60_000 == 1 ? "" : "s");
    }

    private String formatLockAfter(long ms) {
        if (ms < 0) return "Never";
        if (ms < 60_000) return (ms / 1000) + " seconds";
        return (ms / 60_000) + " minute" + (ms / 60_000 == 1 ? "" : "s");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.US, "%.1f KB", bytes / 1024f);
        return String.format(Locale.US, "%.1f MB", bytes / (1024f * 1024));
    }

    private String sortLabel(String key) {
        switch (key) {
            case "modified": return "Date Modified";
            case "created":  return "Date Created";
            case "title":    return "Title A–Z";
            case "manual":   return "Manual";
            default:         return capitalize(key);
        }
    }

    private String accentColorName(String hex) {
        for (int i = 0; i < NotesSettings.ACCENT_COLORS.length; i++) {
            if (NotesSettings.ACCENT_COLORS[i].equalsIgnoreCase(hex)) {
                return NotesSettings.ACCENT_COLOR_NAMES[i];
            }
        }
        return hex;
    }

    private long estimateNotesStorage() {
        try {
            NoteRepository repo = new NoteRepository(this);
            long total = 0;
            for (Note n : repo.getAllNotes()) {
                if (n.body != null) total += n.body.length() * 2L;
                if (n.title != null) total += n.title.length() * 2L;
            }
            return total;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Simple functional interface for picker callbacks. */
    private interface OnPickedListener {
        void onPicked(String value);
    }
}
