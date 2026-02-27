package com.prajwal.myfirstapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * NoteFolderActivity — shown when navigating into a specific folder.
 * Shows breadcrumb nav, subfolders strip, and notes grid.
 */
public class NoteFolderActivity extends AppCompatActivity implements NotesAdapter.OnNoteActionListener {

    public static final String EXTRA_FOLDER_ID = "folder_id";

    private NoteRepository noteRepository;
    private NoteFolderRepository folderRepository;
    private String currentFolderId;
    private NoteFolder currentFolder;

    // Views
    private LinearLayout headerContainer;
    private TextView tvFolderIcon, tvFolderName, tvFolderNoteCount;
    private LinearLayout breadcrumbContainer;
    private HorizontalScrollView subfoldersScrollView;
    private LinearLayout subfoldersContainer;
    private LinearLayout subfoldersSection;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private ArrayList<Note> displayedNotes = new ArrayList<>();
    private LinearLayout emptyStateContainer;
    private TextView tvEmptyTitle, tvEmptyMsg, tvEmptyIcon;
    private FloatingActionButton fabNewNote;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_folder);

        currentFolderId = getIntent().getStringExtra(EXTRA_FOLDER_ID);

        noteRepository = new NoteRepository(this);
        folderRepository = new NoteFolderRepository(this, noteRepository);
        currentFolder = folderRepository.getFolderById(currentFolderId);

        if (currentFolder == null) {
            Toast.makeText(this, "Folder not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupHeader();
        setupBreadcrumb();
        setupSubfolders();
        setupNotesGrid();
        setupFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteRepository.reload();
        currentFolder = folderRepository.getFolderById(currentFolderId);
        if (currentFolder != null) {
            refreshAll();
        }
    }

    private void initViews() {
        headerContainer = findViewById(R.id.folderHeaderContainer);
        tvFolderIcon = findViewById(R.id.tvFolderIcon);
        tvFolderName = findViewById(R.id.tvFolderName);
        tvFolderNoteCount = findViewById(R.id.tvFolderNoteCount);
        breadcrumbContainer = findViewById(R.id.breadcrumbContainer);
        subfoldersScrollView = findViewById(R.id.subfoldersScrollView);
        subfoldersContainer = findViewById(R.id.subfoldersContainer);
        subfoldersSection = findViewById(R.id.subfoldersSection);
        notesRecyclerView = findViewById(R.id.folderNotesRecyclerView);
        emptyStateContainer = findViewById(R.id.folderEmptyState);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyMsg = findViewById(R.id.tvEmptyMsg);
        tvEmptyIcon = findViewById(R.id.tvEmptyIcon);
        fabNewNote = findViewById(R.id.fabNewNoteInFolder);
        btnBack = findViewById(R.id.btnBackFolder);
    }

    private void setupHeader() {
        btnBack.setOnClickListener(v -> finish());

        // Apply folder color gradient to header
        int startColor = currentFolder.getColorInt();
        int endColor = currentFolder.getGradientColorInt();
        GradientDrawable gradient = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[]{startColor, endColor});
        gradient.setCornerRadius(0);
        headerContainer.setBackground(gradient);

        tvFolderIcon.setText(currentFolder.getIconEmoji());
        tvFolderName.setText(currentFolder.name);
        updateNoteCount();
    }

    private void updateNoteCount() {
        int count = folderRepository.getNoteCountRecursive(currentFolderId);
        tvFolderNoteCount.setText(count + (count == 1 ? " note" : " notes"));
    }

    private void setupBreadcrumb() {
        breadcrumbContainer.removeAllViews();
        List<NoteFolder> path = folderRepository.getFolderPath(currentFolderId);

        // Add "All Notes" root item
        addBreadcrumbItem("All Notes", null, true);

        for (int i = 0; i < path.size(); i++) {
            NoteFolder folder = path.get(i);
            boolean isLast = (i == path.size() - 1);

            // Add separator
            TextView separator = new TextView(this);
            separator.setText(" › ");
            separator.setTextColor(isLast ? 0xFFFFFFFF : 0x99FFFFFF);
            separator.setTextSize(14);
            breadcrumbContainer.addView(separator);

            addBreadcrumbItem(folder.name, folder.id, isLast);
        }
    }

    private void addBreadcrumbItem(String name, String folderId, boolean isCurrentLevel) {
        TextView tv = new TextView(this);
        tv.setText(name);
        tv.setTextSize(13);
        tv.setTextColor(isCurrentLevel ? 0xFFFFFFFF : 0x99FFFFFF);
        if (!isCurrentLevel) {
            tv.setOnClickListener(v -> {
                if (folderId == null) {
                    // Navigate back to NotesActivity
                    Intent intent = new Intent(this, NotesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (!folderId.equals(currentFolderId)) {
                    // Navigate to that folder
                    Intent intent = new Intent(this, NoteFolderActivity.class);
                    intent.putExtra(EXTRA_FOLDER_ID, folderId);
                    startActivity(intent);
                }
            });
        }
        breadcrumbContainer.addView(tv);
    }

    private void setupSubfolders() {
        List<NoteFolder> subfolders = folderRepository.getSubfolders(currentFolderId);
        if (subfolders.isEmpty()) {
            subfoldersSection.setVisibility(View.GONE);
            return;
        }

        subfoldersSection.setVisibility(View.VISIBLE);
        subfoldersContainer.removeAllViews();

        for (NoteFolder subfolder : subfolders) {
            View card = createSubfolderCard(subfolder);
            subfoldersContainer.addView(card);
        }

        // Add "New Subfolder" button
        View addBtn = createAddSubfolderButton();
        subfoldersContainer.addView(addBtn);
    }

    private View createSubfolderCard(NoteFolder subfolder) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(16, 16, 16, 16);

        int cardWidth = (int) (getResources().getDisplayMetrics().density * 100);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(12);
        card.setLayoutParams(params);

        // Gradient background
        int startColor = subfolder.getColorInt();
        int endColor = subfolder.getGradientColorInt();
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR, new int[]{
                blendWithDark(startColor, 0.3f), blendWithDark(endColor, 0.3f)});
        bg.setCornerRadius(16 * getResources().getDisplayMetrics().density);
        card.setBackground(bg);

        TextView iconView = new TextView(this);
        iconView.setText(subfolder.getIconEmoji());
        iconView.setTextSize(28);
        iconView.setGravity(android.view.Gravity.CENTER);
        card.addView(iconView);

        TextView nameView = new TextView(this);
        nameView.setText(subfolder.name);
        nameView.setTextColor(0xFFFFFFFF);
        nameView.setTextSize(12);
        nameView.setGravity(android.view.Gravity.CENTER);
        nameView.setPadding(0, 8, 0, 0);
        card.addView(nameView);

        int count = folderRepository.getNoteCountRecursive(subfolder.id);
        TextView countView = new TextView(this);
        countView.setText(count + "");
        countView.setTextColor(0xCCFFFFFF);
        countView.setTextSize(11);
        countView.setGravity(android.view.Gravity.CENTER);
        card.addView(countView);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteFolderActivity.class);
            intent.putExtra(EXTRA_FOLDER_ID, subfolder.id);
            startActivity(intent);
        });

        card.setOnLongClickListener(v -> {
            showFolderContextMenu(subfolder);
            return true;
        });

        return card;
    }

    private View createAddSubfolderButton() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(16, 16, 16, 16);

        int cardWidth = (int) (getResources().getDisplayMetrics().density * 100);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(params);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x1AFFFFFF);
        bg.setCornerRadius(16 * getResources().getDisplayMetrics().density);
        bg.setStroke(1, 0x33FFFFFF);
        card.setBackground(bg);

        TextView iconView = new TextView(this);
        iconView.setText("➕");
        iconView.setTextSize(28);
        iconView.setGravity(android.view.Gravity.CENTER);
        card.addView(iconView);

        TextView nameView = new TextView(this);
        nameView.setText("New Subfolder");
        nameView.setTextColor(0x99FFFFFF);
        nameView.setTextSize(11);
        nameView.setGravity(android.view.Gravity.CENTER);
        nameView.setPadding(0, 8, 0, 0);
        card.addView(nameView);

        card.setOnClickListener(v -> showCreateSubfolderDialog());
        return card;
    }

    private void showCreateSubfolderDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Subfolder name");
        input.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
            .setTitle("New Subfolder in " + currentFolder.name)
            .setView(input)
            .setPositiveButton("Create", (d, w) -> {
                String name = input.getText().toString().trim();
                if (!name.isEmpty()) {
                    NoteFolder subfolder = new NoteFolder(name, currentFolder.colorHex,
                        "folder", currentFolderId, currentFolder.depth + 1);
                    folderRepository.addFolder(subfolder);
                    setupSubfolders();
                    Toast.makeText(this, "Subfolder created", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showFolderContextMenu(NoteFolder subfolder) {
        String[] options = {"Rename", "Change Color", "Move to...", "Delete"};
        new AlertDialog.Builder(this)
            .setTitle(subfolder.getIconEmoji() + " " + subfolder.name)
            .setItems(options, (d, which) -> {
                switch (which) {
                    case 0: showRenameFolderDialog(subfolder); break;
                    case 1: showColorPicker(subfolder); break;
                    case 2: showMoveFolderPicker(subfolder); break;
                    case 3: confirmDeleteFolder(subfolder); break;
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
                    setupSubfolders();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showColorPicker(NoteFolder folder) {
        String[] colorNames = NoteFolder.FOLDER_COLOR_NAMES;
        new AlertDialog.Builder(this)
            .setTitle("Change Color")
            .setItems(colorNames, (d, which) -> {
                folder.colorHex = NoteFolder.FOLDER_COLORS[which];
                folderRepository.updateFolder(folder);
                setupSubfolders();
            })
            .show();
    }

    private void showMoveFolderPicker(NoteFolder folder) {
        List<NoteFolder> allFolders = folderRepository.getAllFolders();
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        names.add("Root (All Notes)");
        ids.add(""); // empty string = root (null parent)
        for (NoteFolder f : allFolders) {
            if (!f.id.equals(folder.id)) {
                StringBuilder indent = new StringBuilder();
                for (int d = 0; d < f.depth; d++) indent.append("  ");
                names.add(indent + f.getIconEmoji() + " " + f.name);
                ids.add(f.id);
            }
        }

        String[] nameArr = names.toArray(new String[0]);
        new AlertDialog.Builder(this)
            .setTitle("Move '" + folder.name + "' to...")
            .setItems(nameArr, (d, which) -> {
                String targetId = ids.get(which);
                // empty string means root
                String newParent = targetId.isEmpty() ? null : targetId;
                boolean success = folderRepository.moveFolder(folder.id, newParent);
                if (!success) {
                    Toast.makeText(this, "Cannot move folder into its own subfolder", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Folder moved", Toast.LENGTH_SHORT).show();
                    setupSubfolders();
                }
            })
            .show();
    }

    private void confirmDeleteFolder(NoteFolder folder) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Folder")
            .setMessage("Delete '" + folder.name + "' and all its subfolders? Notes will be moved to All Notes.")
            .setPositiveButton("Delete", (d, w) -> {
                folderRepository.deleteFolder(folder.id);
                setupSubfolders();
                refreshNotes();
                Toast.makeText(this, "Folder deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void setupNotesGrid() {
        notesAdapter = new NotesAdapter(this, displayedNotes, this, true);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerView.setAdapter(notesAdapter);
        notesRecyclerView.setNestedScrollingEnabled(false);
        refreshNotes();
    }

    private void refreshNotes() {
        displayedNotes.clear();
        displayedNotes.addAll(folderRepository.getNotesInFolder(currentFolderId));
        notesAdapter.notifyDataSetChanged();

        boolean isEmpty = displayedNotes.isEmpty() && folderRepository.getSubfolders(currentFolderId).isEmpty();
        emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        notesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (isEmpty) {
            setupEmptyState();
        }
    }

    private void setupEmptyState() {
        switch (currentFolder.iconIdentifier) {
            case "graduation":
            case "book":
                tvEmptyIcon.setText("🎓");
                tvEmptyTitle.setText("No study notes yet");
                tvEmptyMsg.setText("Start capturing your lecture notes, assignments, and study materials here.");
                break;
            case "briefcase":
                tvEmptyIcon.setText("💼");
                tvEmptyTitle.setText("No work notes yet");
                tvEmptyMsg.setText("Add meeting notes, project plans, and work ideas here.");
                break;
            case "lightbulb":
                tvEmptyIcon.setText("💡");
                tvEmptyTitle.setText("No ideas yet");
                tvEmptyMsg.setText("Capture your brilliant ideas before they slip away.");
                break;
            case "person":
                tvEmptyIcon.setText("👤");
                tvEmptyTitle.setText("No personal notes yet");
                tvEmptyMsg.setText("Write your thoughts, diary entries, and personal notes here.");
                break;
            default:
                tvEmptyIcon.setText(currentFolder.getIconEmoji());
                tvEmptyTitle.setText("This folder is empty");
                tvEmptyMsg.setText("Tap the + button to create your first note here.");
                break;
        }
    }

    private void setupFab() {
        fabNewNote.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteEditorActivity.class);
            intent.putExtra("new_note", true);
            intent.putExtra("folder_id", currentFolderId);
            startActivity(intent);
        });
    }

    private void refreshAll() {
        setupHeader();
        setupBreadcrumb();
        setupSubfolders();
        refreshNotes();
    }

    private int blendWithDark(int color, float ratio) {
        int dark = 0xFF0F172A;
        float inv = 1f - ratio;
        int r = (int) (Color.red(dark) * inv + Color.red(color) * ratio);
        int g = (int) (Color.green(dark) * inv + Color.green(color) * ratio);
        int b = (int) (Color.blue(dark) * inv + Color.blue(color) * ratio);
        return Color.rgb(r, g, b);
    }

    // ─── NotesAdapter.OnNoteActionListener ─────────────────────────

    @Override
    public void onNoteClick(Note note) {
        folderRepository.recordNoteView(note.id);
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("note_id", note.id);
        startActivity(intent);
    }

    @Override
    public void onNoteLongClick(Note note) {
        String[] options = {"Pin/Unpin", "Move to Folder", "Archive", "Delete"};
        new AlertDialog.Builder(this)
            .setTitle(note.title.isEmpty() ? "Note" : note.title)
            .setItems(options, (d, which) -> {
                switch (which) {
                    case 0:
                        noteRepository.togglePin(note.id);
                        refreshNotes();
                        break;
                    case 1:
                        showMoveNotePicker(note);
                        break;
                    case 2:
                        noteRepository.archiveNote(note.id);
                        refreshNotes();
                        Toast.makeText(this, "Note archived", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        noteRepository.trashNote(note.id);
                        refreshNotes();
                        Toast.makeText(this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .show();
    }

    @Override
    public void onNoteSelectionChanged(String noteId, boolean selected) {
        // multi-select not implemented in folder view for simplicity
    }

    private void showMoveNotePicker(Note note) {
        List<NoteFolder> allFolders = folderRepository.getAllFolders();
        List<String> names = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        names.add("All Notes (root)");
        ids.add(""); // empty string = root (null folderId)
        for (NoteFolder f : allFolders) {
            StringBuilder indent = new StringBuilder();
            for (int d = 0; d < f.depth; d++) indent.append("  ");
            names.add(indent + f.getIconEmoji() + " " + f.name);
            ids.add(f.id);
        }

        String[] nameArr = names.toArray(new String[0]);
        new AlertDialog.Builder(this)
            .setTitle("Move note to folder")
            .setItems(nameArr, (d, which) -> {
                String targetId = ids.get(which);
                // empty string means root (no folder)
                folderRepository.moveNoteToFolder(note.id, targetId.isEmpty() ? null : targetId);
                refreshNotes();
                Toast.makeText(this, "Note moved", Toast.LENGTH_SHORT).show();
            })
            .show();
    }
}
