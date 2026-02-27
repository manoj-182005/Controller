package com.prajwal.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Vault settings: Security, Storage, Import, Display, Danger Zone.
 */
public class VaultSettingsActivity extends Activity {

    private static final String PREFS_SETTINGS = "vault_settings";
    private static final String KEY_AUTO_LOCK = "auto_lock_ms";
    private static final String KEY_FAILED_LIMIT = "failed_limit";
    private static final String KEY_IMPORT_DEFAULT = "import_default"; // "copy", "move", "ask"
    private static final String KEY_GRID_COLUMNS = "grid_columns";
    private static final String KEY_SHOW_NAMES = "show_names";
    private static final String KEY_SHOW_SIZES = "show_sizes";

    private MediaVaultRepository repo;
    private SharedPreferences prefs;

    private TextView btnBack;
    private TextView itemChangePin, itemAutoLock, itemFailedLimit;
    private TextView tvStorageBreakdown, itemClearThumbs;
    private TextView itemImportDefault;
    private TextView itemGridColumns, itemWipeVault;
    private Switch switchShowNames, switchShowSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );
        setContentView(R.layout.activity_vault_settings);

        repo = MediaVaultRepository.getInstance(this);
        if (!repo.isUnlocked()) { finish(); return; }
        prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        btnBack = findViewById(R.id.btnBack);
        itemChangePin = findViewById(R.id.itemChangePin);
        itemAutoLock = findViewById(R.id.itemAutoLock);
        itemFailedLimit = findViewById(R.id.itemFailedLimit);
        tvStorageBreakdown = findViewById(R.id.tvStorageBreakdown);
        itemClearThumbs = findViewById(R.id.itemClearThumbs);
        itemImportDefault = findViewById(R.id.itemImportDefault);
        itemGridColumns = findViewById(R.id.itemGridColumns);
        switchShowNames = findViewById(R.id.switchShowNames);
        switchShowSizes = findViewById(R.id.switchShowSizes);
        itemWipeVault = findViewById(R.id.itemWipeVault);

        btnBack.setOnClickListener(v -> finish());

        loadSettings();
        setupListeners();
        populateStorageBreakdown();
    }

    private void loadSettings() {
        long autoLockMs = prefs.getLong(KEY_AUTO_LOCK, 5 * 60 * 1000L);
        itemAutoLock.setText("⏱  Auto-lock: " + formatAutoLock(autoLockMs));

        int failedLimit = prefs.getInt(KEY_FAILED_LIMIT, 5);
        itemFailedLimit.setText("🚫  Failed Attempts: " + failedLimit);

        String importDef = prefs.getString(KEY_IMPORT_DEFAULT, "ask");
        itemImportDefault.setText("📥  Default Import: " + capitalize(importDef));

        int cols = prefs.getInt(KEY_GRID_COLUMNS, 3);
        itemGridColumns.setText("⊞  Grid Columns: " + cols);

        switchShowNames.setChecked(prefs.getBoolean(KEY_SHOW_NAMES, true));
        switchShowSizes.setChecked(prefs.getBoolean(KEY_SHOW_SIZES, true));
    }

    private void setupListeners() {
        itemChangePin.setOnClickListener(v -> showChangePinDialog());
        itemAutoLock.setOnClickListener(v -> showAutoLockPicker());
        itemFailedLimit.setOnClickListener(v -> showFailedLimitPicker());
        itemClearThumbs.setOnClickListener(v -> clearThumbnailCache());
        itemImportDefault.setOnClickListener(v -> showImportDefaultPicker());
        itemGridColumns.setOnClickListener(v -> showGridColumnsPicker());
        itemWipeVault.setOnClickListener(v -> showWipeVaultDialog());

        switchShowNames.setOnCheckedChangeListener((btn, checked) ->
                prefs.edit().putBoolean(KEY_SHOW_NAMES, checked).apply());
        switchShowSizes.setOnCheckedChangeListener((btn, checked) ->
                prefs.edit().putBoolean(KEY_SHOW_SIZES, checked).apply());
    }

    private void populateStorageBreakdown() {
        new Thread(() -> {
            long total = repo.getTotalStorageUsed();
            int images = repo.getCountByType(VaultFileItem.FileType.IMAGE);
            int videos = repo.getCountByType(VaultFileItem.FileType.VIDEO);
            int audio = repo.getCountByType(VaultFileItem.FileType.AUDIO);
            int docs = repo.getCountByType(VaultFileItem.FileType.DOCUMENT);
            int others = repo.getCountByType(VaultFileItem.FileType.OTHER);

            String text = "Total: " + formatSize(total)
                    + "\nImages: " + images
                    + "  |  Videos: " + videos
                    + "  |  Audio: " + audio
                    + "  |  Docs: " + docs
                    + "  |  Other: " + others;
            runOnUiThread(() -> tvStorageBreakdown.setText(text));
        }).start();
    }

    // ---- PIN change ----

    private void showChangePinDialog() {
        android.widget.EditText etCurrent = new android.widget.EditText(this);
        etCurrent.setHint("Current PIN");
        etCurrent.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Change PIN")
                .setMessage("Enter your current PIN:")
                .setView(etCurrent)
                .setPositiveButton("Next", (d, w) -> {
                    String current = etCurrent.getText().toString();
                    if (!repo.verifyPin(current)) {
                        Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showNewPinDialog(current);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNewPinDialog(String currentPin) {
        android.widget.EditText etNew = new android.widget.EditText(this);
        etNew.setHint("New PIN (4-8 digits)");
        etNew.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("New PIN")
                .setView(etNew)
                .setPositiveButton("Confirm", (d, w) -> {
                    String newPin = etNew.getText().toString().trim();
                    if (newPin.length() < 4) {
                        Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showConfirmNewPinDialog(currentPin, newPin);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showConfirmNewPinDialog(String currentPin, String newPin) {
        android.widget.EditText etConfirm = new android.widget.EditText(this);
        etConfirm.setHint("Confirm new PIN");
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Confirm New PIN")
                .setView(etConfirm)
                .setPositiveButton("Save", (d, w) -> {
                    String confirm = etConfirm.getText().toString().trim();
                    if (!confirm.equals(newPin)) {
                        Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean ok = repo.changePin(currentPin, newPin);
                    Toast.makeText(this, ok ? "PIN changed successfully" : "Failed to change PIN",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---- Auto-lock ----

    private void showAutoLockPicker() {
        String[] options = {"1 minute", "2 minutes", "5 minutes", "10 minutes", "Never"};
        long[] values = {60_000L, 120_000L, 300_000L, 600_000L, 0L};
        new AlertDialog.Builder(this)
                .setTitle("Auto-lock Timer")
                .setItems(options, (d, w) -> {
                    prefs.edit().putLong(KEY_AUTO_LOCK, values[w]).apply();
                    itemAutoLock.setText("⏱  Auto-lock: " + options[w]);
                }).show();
    }

    private String formatAutoLock(long ms) {
        if (ms == 0) return "Never";
        long mins = ms / 60_000;
        return mins + " minute" + (mins > 1 ? "s" : "");
    }

    // ---- Failed attempt limit ----

    private void showFailedLimitPicker() {
        String[] options = {"3 attempts", "5 attempts", "10 attempts"};
        int[] values = {3, 5, 10};
        new AlertDialog.Builder(this)
                .setTitle("Failed Attempt Limit")
                .setItems(options, (d, w) -> {
                    prefs.edit().putInt(KEY_FAILED_LIMIT, values[w]).apply();
                    itemFailedLimit.setText("🚫  Failed Attempts: " + values[w]);
                }).show();
    }

    // ---- Thumbnail cache ----

    private void clearThumbnailCache() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Thumbnail Cache")
                .setMessage("This will free up space but thumbnails will be regenerated on next browse.")
                .setPositiveButton("Clear", (d, w) -> {
                    File thumbsDir = repo.getThumbsDir();
                    int deleted = 0;
                    if (thumbsDir != null && thumbsDir.exists()) {
                        File[] files = thumbsDir.listFiles();
                        if (files != null) {
                            for (File f : files) {
                                if (f.delete()) deleted++;
                            }
                        }
                    }
                    Toast.makeText(this, deleted + " thumbnails cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---- Import default ----

    private void showImportDefaultPicker() {
        String[] options = {"Ask every time", "Always Copy", "Always Move"};
        String[] keys = {"ask", "copy", "move"};
        new AlertDialog.Builder(this)
                .setTitle("Default Import Behavior")
                .setItems(options, (d, w) -> {
                    prefs.edit().putString(KEY_IMPORT_DEFAULT, keys[w]).apply();
                    itemImportDefault.setText("📥  Default Import: " + options[w]);
                }).show();
    }

    // ---- Grid columns ----

    private void showGridColumnsPicker() {
        String[] options = {"2 columns", "3 columns", "4 columns"};
        int[] values = {2, 3, 4};
        new AlertDialog.Builder(this)
                .setTitle("Grid Columns")
                .setItems(options, (d, w) -> {
                    prefs.edit().putInt(KEY_GRID_COLUMNS, values[w]).apply();
                    itemGridColumns.setText("⊞  Grid Columns: " + values[w]);
                }).show();
    }

    // ---- Wipe vault ----

    private void showWipeVaultDialog() {
        android.widget.EditText etPin = new android.widget.EditText(this);
        etPin.setHint("Enter PIN to confirm");
        etPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("⚠ Wipe Entire Vault")
                .setMessage("This will permanently delete ALL files from the vault. This cannot be undone.\n\nEnter your PIN to continue:")
                .setView(etPin)
                .setPositiveButton("Continue", (d, w) -> {
                    if (!repo.verifyPin(etPin.getText().toString())) {
                        Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showWipeConfirmDialog();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showWipeConfirmDialog() {
        android.widget.EditText etConfirm = new android.widget.EditText(this);
        etConfirm.setHint("Type DELETE to confirm");

        new AlertDialog.Builder(this)
                .setTitle("Final Confirmation")
                .setMessage("Type DELETE (all caps) to permanently wipe the vault:")
                .setView(etConfirm)
                .setPositiveButton("Wipe Vault", (d, w) -> {
                    if (!"DELETE".equals(etConfirm.getText().toString().trim())) {
                        Toast.makeText(this, "You must type DELETE exactly", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    performWipe();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performWipe() {
        new Thread(() -> {
            List<VaultFileItem> all = repo.getAllFiles();
            for (VaultFileItem f : all) {
                repo.deleteFile(f);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Vault wiped", Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }

    // ---- Helpers ----

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
