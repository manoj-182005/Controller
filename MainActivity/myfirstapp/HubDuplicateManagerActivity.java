package com.prajwal.myfirstapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Duplicate Manager — view and resolve duplicate file groups.
 */
public class HubDuplicateManagerActivity extends AppCompatActivity {

    private HubFileRepository repo;

    private TextView tvDupGroupCount;
    private TextView tvDupWasted;
    private Button btnDupScan;
    private android.widget.ProgressBar dupScanProgress;
    private TextView tvDupScanStatus;
    private LinearLayout dupGroupsContainer;
    private LinearLayout dupEmptyState;
    private LinearLayout dupBottomBar;
    private TextView tvDupSelectedCount;
    private Button btnDupDeleteSelected;

    private Set<String> selectedGroupIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_duplicate_manager);

        repo = HubFileRepository.getInstance(this);

        tvDupGroupCount = findViewById(R.id.tvDupGroupCount);
        tvDupWasted = findViewById(R.id.tvDupWasted);
        btnDupScan = findViewById(R.id.btnDupScan);
        dupScanProgress = findViewById(R.id.dupScanProgress);
        tvDupScanStatus = findViewById(R.id.tvDupScanStatus);
        dupGroupsContainer = findViewById(R.id.dupGroupsContainer);
        dupEmptyState = findViewById(R.id.dupEmptyState);
        dupBottomBar = findViewById(R.id.dupBottomBar);
        tvDupSelectedCount = findViewById(R.id.tvDupSelectedCount);
        btnDupDeleteSelected = findViewById(R.id.btnDupDeleteSelected);

        findViewById(R.id.btnDupBack).setOnClickListener(v -> finish());

        btnDupScan.setOnClickListener(v -> startScan());
        findViewById(R.id.btnDupAutoResolve).setOnClickListener(v -> showAutoResolveDialog());
        findViewById(R.id.btnDupSelectAll).setOnClickListener(v -> selectAll());
        btnDupDeleteSelected.setOnClickListener(v -> deleteSelected());

        loadGroups();
    }

    private void loadGroups() {
        List<DuplicateGroup> groups = repo.getAllDuplicateGroups();
        tvDupGroupCount.setText(String.valueOf(groups.size()));
        tvDupWasted.setText(formatBytes(repo.getTotalWastedBytes()));
        renderGroups(groups);
    }

    private void renderGroups(List<DuplicateGroup> groups) {
        dupGroupsContainer.removeAllViews();

        if (groups.isEmpty()) {
            dupEmptyState.setVisibility(View.VISIBLE);
            return;
        }
        dupEmptyState.setVisibility(View.GONE);

        float dp = getResources().getDisplayMetrics().density;

        for (DuplicateGroup group : groups) {
            LinearLayout card = buildGroupCard(group, dp);
            dupGroupsContainer.addView(card);
        }
    }

    private LinearLayout buildGroupCard(DuplicateGroup group, float dp) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(12 * dp);
        bg.setColor(Color.parseColor("#1A1A2E"));
        card.setBackground(bg);
        card.setPadding((int)(14*dp), (int)(14*dp), (int)(14*dp), (int)(14*dp));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, (int)(10*dp));
        card.setLayoutParams(lp);

        // Header row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView emoji = new TextView(this);
        emoji.setText("🔁");
        emoji.setTextSize(20);
        emoji.setLayoutParams(new LinearLayout.LayoutParams((int)(32*dp), ViewGroup.LayoutParams.WRAP_CONTENT));
        headerRow.addView(emoji);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView fname = new TextView(this);
        fname.setText(group.fileName != null ? group.fileName : "Unknown File");
        fname.setTextColor(Color.WHITE);
        fname.setTextSize(14);
        fname.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        fname.setSingleLine(true);
        fname.setEllipsize(android.text.TextUtils.TruncateAt.END);
        info.addView(fname);

        TextView meta = new TextView(this);
        meta.setText(group.duplicateCount + " copies · " + group.getFormattedWasted() + " wasted");
        meta.setTextColor(Color.parseColor("#EF4444"));
        meta.setTextSize(12);
        info.addView(meta);

        headerRow.addView(info);
        card.addView(headerRow);

        // Expandable file list (collapsed by default)
        LinearLayout fileList = new LinearLayout(this);
        fileList.setOrientation(LinearLayout.VERTICAL);
        fileList.setVisibility(View.GONE);
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.setMargins(0, (int)(8*dp), 0, 0);
        fileList.setLayoutParams(flp);

        // Load files for this group
        List<HubFile> groupFiles = getFilesForGroup(group.id);
        for (HubFile f : groupFiles) {
            LinearLayout fileRow = new LinearLayout(this);
            fileRow.setOrientation(LinearLayout.HORIZONTAL);
            fileRow.setGravity(Gravity.CENTER_VERTICAL);
            fileRow.setPadding(0, (int)(4*dp), 0, (int)(4*dp));

            TextView fEmoji = new TextView(this);
            fEmoji.setText(f.getTypeEmoji());
            fEmoji.setTextSize(16);
            fEmoji.setLayoutParams(new LinearLayout.LayoutParams((int)(28*dp), ViewGroup.LayoutParams.WRAP_CONTENT));
            fileRow.addView(fEmoji);

            TextView fName = new TextView(this);
            String display = f.displayName != null ? f.displayName : f.originalFileName;
            fName.setText(display != null ? display : "File");
            fName.setTextColor(Color.parseColor("#94A3B8"));
            fName.setTextSize(12);
            fName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            fName.setSingleLine(true);
            fName.setEllipsize(android.text.TextUtils.TruncateAt.END);
            fileRow.addView(fName);

            fileList.addView(fileRow);
        }
        card.addView(fileList);

        // Toggle expand on header tap
        headerRow.setClickable(true);
        headerRow.setFocusable(true);
        headerRow.setForeground(getDrawable(android.R.attr.selectableItemBackground));
        headerRow.setOnClickListener(v -> {
            fileList.setVisibility(fileList.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        // Action buttons
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.setMargins(0, (int)(10*dp), 0, 0);
        buttons.setLayoutParams(blp);

        Button keepNewest = new Button(this);
        keepNewest.setText("Keep Newest");
        keepNewest.setTextColor(Color.WHITE);
        keepNewest.setTextSize(12);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setShape(GradientDrawable.RECTANGLE);
        btnBg.setCornerRadius(6 * dp);
        btnBg.setColor(Color.parseColor("#1E3A5F"));
        keepNewest.setBackground(btnBg);
        LinearLayout.LayoutParams kblp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        kblp.setMarginEnd((int)(6*dp));
        keepNewest.setLayoutParams(kblp);
        keepNewest.setOnClickListener(v -> keepNewestInGroup(group, groupFiles));
        buttons.addView(keepNewest);

        Button deleteAll = new Button(this);
        deleteAll.setText("Delete All But One");
        deleteAll.setTextColor(Color.WHITE);
        deleteAll.setTextSize(12);
        GradientDrawable delBg = new GradientDrawable();
        delBg.setShape(GradientDrawable.RECTANGLE);
        delBg.setCornerRadius(6 * dp);
        delBg.setColor(Color.parseColor("#5F1E1E"));
        deleteAll.setBackground(delBg);
        deleteAll.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        deleteAll.setOnClickListener(v -> confirmDeleteAllButOne(group, groupFiles));
        buttons.addView(deleteAll);

        card.addView(buttons);
        return card;
    }

    private List<HubFile> getFilesForGroup(String groupId) {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : repo.getAllFiles()) {
            if (groupId != null && groupId.equals(f.duplicateGroupId)) result.add(f);
        }
        return result;
    }

    private void keepNewestInGroup(DuplicateGroup group, List<HubFile> files) {
        if (files.isEmpty()) return;
        HubFile newest = files.get(0);
        for (HubFile f : files) {
            if (f.originalModifiedAt > newest.originalModifiedAt) newest = f;
        }
        final HubFile keep = newest;
        for (HubFile f : files) {
            if (!f.id.equals(keep.id)) repo.deleteFile(f.id);
        }
        Toast.makeText(this, "Kept newest, deleted " + (files.size() - 1) + " copies", Toast.LENGTH_SHORT).show();
        loadGroups();
    }

    private void confirmDeleteAllButOne(DuplicateGroup group, List<HubFile> files) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Duplicates")
                .setMessage("Keep one copy of \"" + group.fileName + "\" and delete the rest?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (!files.isEmpty()) {
                        HubFile keep = files.get(0);
                        for (int i = 1; i < files.size(); i++) repo.deleteFile(files.get(i).id);
                        Toast.makeText(this, "Deleted " + (files.size() - 1) + " copies", Toast.LENGTH_SHORT).show();
                        loadGroups();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startScan() {
        btnDupScan.setEnabled(false);
        dupScanProgress.setVisibility(View.VISIBLE);
        tvDupScanStatus.setVisibility(View.VISIBLE);
        tvDupScanStatus.setText("Scanning files...");

        repo.detectDuplicates(() -> {
            dupScanProgress.setVisibility(View.GONE);
            tvDupScanStatus.setText("Scan complete!");
            btnDupScan.setEnabled(true);
            loadGroups();
        });
    }

    private void showAutoResolveDialog() {
        String[] options = {"Keep Most Recent", "Keep Largest"};
        new AlertDialog.Builder(this)
                .setTitle("Auto-Resolve All")
                .setItems(options, (dialog, which) -> {
                    String strategy = options[which];
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Auto-Resolve")
                            .setMessage("Resolve all duplicates using \"" + strategy + "\" strategy?")
                            .setPositiveButton("Resolve", (d, w) -> autoResolveAll(which))
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .show();
    }

    private void autoResolveAll(int strategy) {
        List<DuplicateGroup> groups = repo.getAllDuplicateGroups();
        int totalDeleted = 0;
        for (DuplicateGroup group : groups) {
            List<HubFile> files = getFilesForGroup(group.id);
            if (files.size() < 2) continue;
            HubFile keep;
            if (strategy == 0) { // Most recent
                keep = files.get(0);
                for (HubFile f : files) if (f.originalModifiedAt > keep.originalModifiedAt) keep = f;
            } else { // Largest
                keep = files.get(0);
                for (HubFile f : files) if (f.fileSize > keep.fileSize) keep = f;
            }
            for (HubFile f : files) {
                if (!f.id.equals(keep.id)) { repo.deleteFile(f.id); totalDeleted++; }
            }
        }
        Toast.makeText(this, "Deleted " + totalDeleted + " duplicate files", Toast.LENGTH_LONG).show();
        loadGroups();
    }

    private void selectAll() {
        for (DuplicateGroup g : repo.getAllDuplicateGroups()) selectedGroupIds.add(g.id);
        updateBottomBar();
    }

    private void deleteSelected() {
        if (selectedGroupIds.isEmpty()) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete Selected")
                .setMessage("Delete duplicates in " + selectedGroupIds.size() + " group(s)?")
                .setPositiveButton("Delete", (d, w) -> {
                    for (String gid : selectedGroupIds) {
                        List<HubFile> files = getFilesForGroup(gid);
                        if (files.size() > 1) {
                            HubFile keep = files.get(0);
                            for (int i = 1; i < files.size(); i++) repo.deleteFile(files.get(i).id);
                        }
                    }
                    selectedGroupIds.clear();
                    updateBottomBar();
                    loadGroups();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateBottomBar() {
        if (selectedGroupIds.isEmpty()) {
            dupBottomBar.setVisibility(View.GONE);
        } else {
            dupBottomBar.setVisibility(View.VISIBLE);
            tvDupSelectedCount.setText(selectedGroupIds.size() + " groups selected");
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
