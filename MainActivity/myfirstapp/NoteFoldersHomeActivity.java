package com.prajwal.myfirstapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * NoteFoldersHomeActivity — Folder home screen with 2-column folder grid,
 * slide-out drawer for full folder tree, and recently viewed notes.
 *
 * This activity is the new default notes home when homeMode = "folders".
 * Users can toggle back to classic NotesActivity (all notes flat grid).
 */
public class NoteFoldersHomeActivity extends AppCompatActivity {

    private NoteRepository noteRepository;
    private NoteFolderRepository folderRepository;

    // Views
    private DrawerLayout drawerLayout;
    private LinearLayout folderGrid;
    private LinearLayout recentlyViewedContainer;
    private LinearLayout recentlyViewedList;
    private ImageButton btnMenuDrawer;
    private ImageButton btnViewToggle;
    private ImageButton btnAddFolder;
    private LinearLayout drawerFolderTree;
    private LinearLayout tvDrawerAllNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_folders_home);

        noteRepository = new NoteRepository(this);
        folderRepository = new NoteFolderRepository(this, noteRepository);

        initViews();
        setupDrawer();
        loadFolderGrid();
        loadRecentlyViewed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteRepository.reload();
        loadFolderGrid();
        loadRecentlyViewed();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.foldersDrawerLayout);
        folderGrid = findViewById(R.id.folderHomeGrid);
        recentlyViewedContainer = findViewById(R.id.recentlyViewedContainer);
        recentlyViewedList = findViewById(R.id.recentlyViewedList);
        btnMenuDrawer = findViewById(R.id.btnMenuDrawer);
        btnViewToggle = findViewById(R.id.btnFolderViewToggle);
        btnAddFolder = findViewById(R.id.btnAddFolderHome);
        drawerFolderTree = findViewById(R.id.drawerFolderTree);
        tvDrawerAllNotes = findViewById(R.id.tvDrawerAllNotes);
    }

    private void setupDrawer() {
        btnMenuDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        tvDrawerAllNotes.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
        });

        btnViewToggle.setOnClickListener(v -> {
            folderRepository.setHomeMode("all");
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
            finish();
        });

        btnAddFolder.setOnClickListener(v -> showCreateFolderDialog());
        findViewById(R.id.btnBackFolderHome).setOnClickListener(v -> finish());
    }

    private void loadFolderGrid() {
        folderGrid.removeAllViews();
        List<NoteFolder> rootFolders = folderRepository.getRootFolders();

        // Build 2-column grid manually
        LinearLayout currentRow = null;
        for (int i = 0; i < rootFolders.size(); i++) {
            if (i % 2 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setWeightSum(2);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, 12);
                currentRow.setLayoutParams(rowParams);
                folderGrid.addView(currentRow);
            }

            View folderCard = createFolderCard(rootFolders.get(i));
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            cardParams.setMargins(i % 2 == 0 ? 0 : 6, 0, i % 2 == 0 ? 6 : 0, 0);
            folderCard.setLayoutParams(cardParams);
            if (currentRow != null) currentRow.addView(folderCard);
        }

        // If odd number, add placeholder
        if (rootFolders.size() % 2 != 0 && currentRow != null) {
            View placeholder = new View(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, 1, 1);
            placeholder.setLayoutParams(p);
            currentRow.addView(placeholder);
        }

        // Refresh drawer tree
        loadDrawerTree();
    }

    private View createFolderCard(NoteFolder folder) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_folder_card, null);

        TextView tvIcon = card.findViewById(R.id.tvFolderCardIcon);
        TextView tvName = card.findViewById(R.id.tvFolderCardName);
        TextView tvCount = card.findViewById(R.id.tvFolderCardCount);
        TextView tvPreview = card.findViewById(R.id.tvFolderCardPreview);
        TextView tvDate = card.findViewById(R.id.tvFolderCardDate);
        LinearLayout cardRoot = card.findViewById(R.id.folderCardRoot);

        tvIcon.setText(folder.getIconEmoji());
        tvName.setText(folder.name);

        int count = folderRepository.getNoteCountRecursive(folder.id);
        tvCount.setText(count + (count == 1 ? " note" : " notes"));

        // Preview note titles
        List<String> previewTitles = folderRepository.getPreviewNoteTitles(folder.id, 2);
        if (!previewTitles.isEmpty()) {
            tvPreview.setText(String.join(", ", previewTitles));
            tvPreview.setVisibility(View.VISIBLE);
        } else {
            tvPreview.setVisibility(View.GONE);
        }

        // Last modified date
        long lastMod = getLastModifiedForFolder(folder.id);
        if (lastMod > 0) {
            tvDate.setText(formatRelativeTime(lastMod));
            tvDate.setVisibility(View.VISIBLE);
        } else {
            tvDate.setVisibility(View.GONE);
        }

        // Apply gradient background
        int startColor = folder.getColorInt();
        int endColor = folder.getGradientColorInt();
        int darkStart = blendWithDark(startColor, 0.25f);
        int darkEnd = blendWithDark(endColor, 0.25f);
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[]{darkStart, darkEnd});
        bg.setCornerRadius(20 * getResources().getDisplayMetrics().density);
        bg.setStroke(1, blendColors(startColor, 0x00000000, 0.5f));
        cardRoot.setBackground(bg);

        // Click - navigate into folder
        card.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(80)
                    .withEndAction(() -> {
                        Intent intent = new Intent(this, NoteFolderActivity.class);
                        intent.putExtra(NoteFolderActivity.EXTRA_FOLDER_ID, folder.id);
                        startActivity(intent);
                    }).start())
                .start();
        });

        // Long press - context menu
        card.setOnLongClickListener(v -> {
            showFolderContextMenu(folder);
            return true;
        });

        return card;
    }

    private void loadDrawerTree() {
        drawerFolderTree.removeAllViews();
        List<NoteFolder> rootFolders = folderRepository.getRootFolders();
        for (NoteFolder folder : rootFolders) {
            addDrawerFolderItem(drawerFolderTree, folder, 0);
        }
    }

    private void addDrawerFolderItem(LinearLayout parent, NoteFolder folder, int indent) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        item.setPadding(
            (int) (getResources().getDisplayMetrics().density * (16 + indent * 16)),
            12, 16, 12);
        item.setBackground(getResources().getDrawable(android.R.drawable.list_selector_background));

        TextView tvIcon = new TextView(this);
        tvIcon.setText(folder.getIconEmoji());
        tvIcon.setTextSize(18);
        item.addView(tvIcon);

        TextView tvName = new TextView(this);
        tvName.setText("  " + folder.name);
        tvName.setTextColor(0xFFE2E8F0);
        tvName.setTextSize(14);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        item.addView(tvName);

        int count = folderRepository.getNoteCountRecursive(folder.id);
        TextView tvCount = new TextView(this);
        tvCount.setText(String.valueOf(count));
        tvCount.setTextColor(0xFF64748B);
        tvCount.setTextSize(12);
        item.addView(tvCount);

        item.setOnClickListener(v -> {
            drawerLayout.closeDrawers();
            Intent intent = new Intent(this, NoteFolderActivity.class);
            intent.putExtra(NoteFolderActivity.EXTRA_FOLDER_ID, folder.id);
            startActivity(intent);
        });

        parent.addView(item);

        // Add subfolders
        List<NoteFolder> subs = folderRepository.getSubfolders(folder.id);
        for (NoteFolder sub : subs) {
            addDrawerFolderItem(parent, sub, indent + 1);
        }
    }

    private void loadRecentlyViewed() {
        List<Note> recentNotes = folderRepository.getRecentlyViewedNotes(5);
        if (recentNotes.size() < 3) {
            recentlyViewedContainer.setVisibility(View.GONE);
            return;
        }

        recentlyViewedContainer.setVisibility(View.VISIBLE);
        recentlyViewedList.removeAllViews();

        for (Note note : recentNotes) {
            View chip = createRecentNoteChip(note);
            recentlyViewedList.addView(chip);
        }
    }

    private View createRecentNoteChip(Note note) {
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setPadding(16, 12, 16, 12);

        int chipWidth = (int) (getResources().getDisplayMetrics().density * 130);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(chipWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(12);
        chip.setLayoutParams(params);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF1E293B);
        bg.setCornerRadius(12 * getResources().getDisplayMetrics().density);
        chip.setBackground(bg);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(note.title.isEmpty() ? "Untitled" : note.title);
        tvTitle.setTextColor(0xFFE2E8F0);
        tvTitle.setTextSize(13);
        tvTitle.setMaxLines(2);
        tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);
        chip.addView(tvTitle);

        TextView tvDate = new TextView(this);
        tvDate.setText(note.getFormattedDate());
        tvDate.setTextColor(0xFF64748B);
        tvDate.setTextSize(11);
        tvDate.setPadding(0, 4, 0, 0);
        chip.addView(tvDate);

        chip.setOnClickListener(v -> {
            folderRepository.recordNoteView(note.id);
            Intent intent = new Intent(this, NoteEditorActivity.class);
            intent.putExtra("note_id", note.id);
            startActivity(intent);
        });

        return chip;
    }

    private void showCreateFolderDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Folder name");
        input.setPadding(48, 24, 48, 24);

        final int[] selectedColor = {0};

        new AlertDialog.Builder(this)
            .setTitle("New Folder")
            .setView(input)
            .setPositiveButton("Create", (d, w) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    NoteFolder folder = new NoteFolder(name,
                        NoteFolder.FOLDER_COLORS[selectedColor[0]],
                        "folder", null, 0);
                    folder.sortOrder = folderRepository.getRootFolders().size();
                    folderRepository.addFolder(folder);
                    loadFolderGrid();
                    Toast.makeText(this, "Folder created", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showFolderContextMenu(NoteFolder folder) {
        String[] options = {"Rename", "Change Color", "New Subfolder", "Move to...", "Delete"};
        new AlertDialog.Builder(this)
            .setTitle(folder.getIconEmoji() + " " + folder.name)
            .setItems(options, (d, which) -> {
                switch (which) {
                    case 0: showRenameFolderDialog(folder); break;
                    case 1: showColorPickerForFolder(folder); break;
                    case 2: showCreateSubfolderDialog(folder); break;
                    case 3: showMoveFolderPicker(folder); break;
                    case 4: confirmDeleteFolder(folder); break;
                }
            })
            .show();
    }

    private void showRenameFolderDialog(NoteFolder folder) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(folder.name);
        input.setSelection(folder.name.length());
        input.setPadding(48, 24, 48, 24);
        new AlertDialog.Builder(this)
            .setTitle("Rename Folder")
            .setView(input)
            .setPositiveButton("Save", (d, w) -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    folder.name = newName;
                    folderRepository.updateFolder(folder);
                    loadFolderGrid();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showColorPickerForFolder(NoteFolder folder) {
        new AlertDialog.Builder(this)
            .setTitle("Change Color")
            .setItems(NoteFolder.FOLDER_COLOR_NAMES, (d, which) -> {
                folder.colorHex = NoteFolder.FOLDER_COLORS[which];
                folderRepository.updateFolder(folder);
                loadFolderGrid();
            })
            .show();
    }

    private void showCreateSubfolderDialog(NoteFolder parent) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Subfolder name");
        input.setPadding(48, 24, 48, 24);
        new AlertDialog.Builder(this)
            .setTitle("New subfolder in " + parent.name)
            .setView(input)
            .setPositiveButton("Create", (d, w) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    NoteFolder sub = new NoteFolder(name, parent.colorHex, "folder", parent.id, parent.depth + 1);
                    folderRepository.addFolder(sub);
                    loadFolderGrid();
                    Toast.makeText(this, "Subfolder created", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showMoveFolderPicker(NoteFolder folder) {
        List<NoteFolder> all = folderRepository.getAllFolders();
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        names.add("Root (Top Level)");
        ids.add(null);
        for (NoteFolder f : all) {
            if (!f.id.equals(folder.id)) {
                names.add("  ".repeat(f.depth) + f.getIconEmoji() + " " + f.name);
                ids.add(f.id);
            }
        }
        new AlertDialog.Builder(this)
            .setTitle("Move '" + folder.name + "' to...")
            .setItems(names.toArray(new String[0]), (d, which) -> {
                boolean ok = folderRepository.moveFolder(folder.id, ids.get(which));
                if (!ok) Toast.makeText(this, "Cannot move into own subfolder", Toast.LENGTH_SHORT).show();
                else { loadFolderGrid(); Toast.makeText(this, "Folder moved", Toast.LENGTH_SHORT).show(); }
            })
            .show();
    }

    private void confirmDeleteFolder(NoteFolder folder) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Folder")
            .setMessage("Delete '" + folder.name + "' and all its subfolders? Notes will be moved to All Notes.")
            .setPositiveButton("Delete", (d, w) -> {
                folderRepository.deleteFolder(folder.id);
                loadFolderGrid();
                Toast.makeText(this, "Folder deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private long getLastModifiedForFolder(String folderId) {
        long latest = 0;
        for (Note note : noteRepository.getAllNotes()) {
            if (!note.isTrashed && !note.isArchived && folderId.equals(note.folderId)) {
                if (note.updatedAt > latest) latest = note.updatedAt;
            }
        }
        return latest;
    }

    private String formatRelativeTime(long timeMs) {
        long diff = System.currentTimeMillis() - timeMs;
        long hours = diff / (60 * 60 * 1000);
        long days = hours / 24;
        if (hours < 1) return "Just now";
        if (hours < 24) return hours + "h ago";
        if (days < 7) return days + "d ago";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd", java.util.Locale.US);
        return sdf.format(new java.util.Date(timeMs));
    }

    private int blendWithDark(int color, float ratio) {
        int dark = 0xFF0F172A;
        float inv = 1f - ratio;
        int r = (int) (Color.red(dark) * inv + Color.red(color) * ratio);
        int g = (int) (Color.green(dark) * inv + Color.green(color) * ratio);
        int b = (int) (Color.blue(dark) * inv + Color.blue(color) * ratio);
        return Color.rgb(r, g, b);
    }

    private int blendColors(int c1, int c2, float ratio) {
        float inv = 1f - ratio;
        int r = (int) (Color.red(c1) * inv + Color.red(c2) * ratio);
        int g = (int) (Color.green(c1) * inv + Color.green(c2) * ratio);
        int b = (int) (Color.blue(c1) * inv + Color.blue(c2) * ratio);
        return Color.rgb(r, g, b);
    }
}
