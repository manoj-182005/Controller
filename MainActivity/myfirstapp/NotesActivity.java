package com.prajwal.myfirstapp;

import android.animation.Animator;
import android.util.Log;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * NotesActivity — Redesigned premium notes experience.
 * 
 * Features:
 * - Staggered masonry grid with colored cards
 * - Pinned notes carousel
 * - Real-time search with voice support
 * - Filter chips for categories
 * - Multi-select mode with batch actions
 * - Speed dial FAB
 * - Archive, Trash, Tags management
 */
public class NotesActivity extends AppCompatActivity implements NotesAdapter.OnNoteActionListener {

    private static final String TAG = "NotesActivity";
    private static final int VOICE_SEARCH_REQUEST = 100;

    // Repository
    private NoteRepository repository;

    // Views - Header
    private ImageButton btnBackNotes, btnSyncNotes, btnViewToggle, btnMoreMenu;
    private ImageButton btnDrawerToggle;
    private TextView tvNotesTitle, tvNoteCount, tvDateLine;

    // Views - Drawer
    private DrawerLayout drawerLayout;
    private LinearLayout drawerFoldersList;
    private TextView tvDrawerNoteCount, tvDrawerAllCount, tvDrawerPinnedCount, drawerAddFolder;

    // Views - Folder Strip
    private LinearLayout folderStripSection, folderCardsContainer;
    private TextView btnViewAllFolders;
    private NoteFolderRepository folderRepository;

    // Views - Search
    private EditText etSearchNotes;
    private ImageButton btnVoiceSearch;
    private LinearLayout recentSearchesContainer, recentSearchesList;
    private boolean isSearchFocused = false;

    // Views - Filter Chips
    private LinearLayout chipContainer;
    private String currentFilter = "All";

    // Views - Pinned Section
    private LinearLayout pinnedSection, pinnedNotesContainer;
    private TextView tvOthersHeader;

    // Views - Grid
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private LinearLayout emptyStateContainer;
    private TextView btnEmptyCreateNote;

    // Views - Recently Viewed
    private LinearLayout recentlyViewedSection, recentlyViewedContainer;

    // Views - To-Do Lists section
    private LinearLayout todoListsSection;
    private RecyclerView todoListsRecycler;
    private TextView tvTodoHeader, btnViewAllTodos, tvTodoSummary;
    private TodoRepository todoRepository;
    private TodoListAdapter todoListAdapter;
    private ArrayList<TodoList> todoLists = new ArrayList<>();

    // Views - Sort
    private ImageButton btnSortNotes;

    // Views - Multi-select
    private LinearLayout multiSelectBar;
    private boolean isMultiSelectMode = false;
    private Set<String> selectedNoteIds = new HashSet<>();

    // Views - Speed Dial
    private View speedDialOverlay;
    private LinearLayout speedDialItem1, speedDialItem2, speedDialItem3, speedDialItem4;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAddNote;
    private boolean isSpeedDialOpen = false;

    // Data
    private ArrayList<Note> displayedNotes = new ArrayList<>();
    private String currentSearchQuery = "";
    private boolean isGridView = true;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // Network
    private ConnectionManager connectionManager;
    private String serverIp;

    // Singleton for receiving sync commands
    private static NotesActivity instance;
    public static NotesActivity getInstance() { return instance; }

    // ═══════════════════════════════════════════════════════════════
    //  LIFECYCLE
    // ═══════════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_new);
        instance = this;

        // Get server IP
        serverIp = getIntent().getStringExtra("server_ip");
        if (serverIp == null) {
            serverIp = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .getString("last_server_ip", null);
        }
        if (serverIp != null) {
            connectionManager = new ConnectionManager(serverIp);
        }

        // Initialize repository
        repository = new NoteRepository(this);
        folderRepository = new NoteFolderRepository(this, repository);

        // Initialize views
        initViews();
        setupHeader();
        setupSearch();
        setupFilterChips();
        setupNotesGrid();
        setupMultiSelectBar();
        setupSpeedDial();
        setupDrawer();
        setupFolderStrip();
        todoRepository = new TodoRepository(this);
        setupTodoSection();

        // Load data
        refreshNotes();
        updateDateLine();

        // Request sync from PC
        if (connectionManager != null) {
            connectionManager.sendCommand("NOTE_SYNC");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotes();
        setupFolderStrip();
        setupRecentlyViewed();
        refreshTodoSection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) instance = null;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else if (isSpeedDialOpen) {
            closeSpeedDial();
        } else if (isMultiSelectMode) {
            exitMultiSelectMode();
        } else {
            super.onBackPressed();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  VIEW INITIALIZATION
    // ═══════════════════════════════════════════════════════════════

    private void initViews() {
        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerFoldersList = findViewById(R.id.drawerFoldersList);
        tvDrawerNoteCount = findViewById(R.id.tvDrawerNoteCount);
        tvDrawerAllCount = findViewById(R.id.tvDrawerAllCount);
        tvDrawerPinnedCount = findViewById(R.id.tvDrawerPinnedCount);
        drawerAddFolder = findViewById(R.id.drawerAddFolder);
        btnDrawerToggle = findViewById(R.id.btnDrawerToggle);

        // Header
        btnBackNotes = findViewById(R.id.btnBackNotes);
        btnSyncNotes = findViewById(R.id.btnSyncNotes);
        btnViewToggle = findViewById(R.id.btnViewToggle);
        btnMoreMenu = findViewById(R.id.btnMoreMenu);
        tvNotesTitle = findViewById(R.id.tvNotesTitle);
        tvNoteCount = findViewById(R.id.tvNoteCount);
        tvDateLine = findViewById(R.id.tvDateLine);

        // Search
        etSearchNotes = findViewById(R.id.etSearchNotes);
        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        recentSearchesContainer = findViewById(R.id.recentSearchesContainer);
        recentSearchesList = findViewById(R.id.recentSearchesList);

        // Filter chips
        chipContainer = findViewById(R.id.chipContainer);

        // Folder strip
        folderStripSection = findViewById(R.id.folderStripSection);
        folderCardsContainer = findViewById(R.id.folderCardsContainer);
        btnViewAllFolders = findViewById(R.id.btnViewAllFolders);

        // Pinned section
        pinnedSection = findViewById(R.id.pinnedSection);
        pinnedNotesContainer = findViewById(R.id.pinnedNotesContainer);
        tvOthersHeader = findViewById(R.id.tvOthersHeader);

        // Grid
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        emptyStateContainer = findViewById(R.id.emptyStateContainer);
        btnEmptyCreateNote = findViewById(R.id.btnEmptyCreateNote);

        // Recently Viewed
        recentlyViewedSection = findViewById(R.id.recentlyViewedSection);
        recentlyViewedContainer = findViewById(R.id.recentlyViewedContainer);

        // Sort button
        btnSortNotes = findViewById(R.id.btnSortNotes);

        // Multi-select
        multiSelectBar = findViewById(R.id.multiSelectBar);

        // Speed dial
        speedDialOverlay = findViewById(R.id.speedDialOverlay);
        speedDialItem1 = findViewById(R.id.speedDialItem1);
        speedDialItem2 = findViewById(R.id.speedDialItem2);
        speedDialItem3 = findViewById(R.id.speedDialItem3);
        speedDialItem4 = findViewById(R.id.speedDialItem4);
        fabAddNote = findViewById(R.id.fabAddNote);

        // Load view mode preference
        isGridView = repository.getViewMode().equals("grid");
        updateViewToggleIcon();
    }

    // ═══════════════════════════════════════════════════════════════
    //  HEADER
    // ═══════════════════════════════════════════════════════════════

    private void setupHeader() {
        btnBackNotes.setOnClickListener(v -> finish());

        btnSyncNotes.setOnClickListener(v -> {
            if (connectionManager != null) {
                // Rotate animation
                ObjectAnimator rotation = ObjectAnimator.ofFloat(btnSyncNotes, "rotation", 0f, 360f);
                rotation.setDuration(600);
                rotation.setInterpolator(new AccelerateDecelerateInterpolator());
                rotation.start();

                connectionManager.sendCommand("NOTE_SYNC");
                Toast.makeText(this, "Syncing notes...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewToggle.setOnClickListener(v -> {
            isGridView = !isGridView;
            repository.setViewMode(isGridView ? "grid" : "list");
            updateViewToggleIcon();
            setupNotesGrid();
            refreshNotes();
        });

        btnMoreMenu.setOnClickListener(this::showMoreMenu);

        if (btnSortNotes != null) {
            btnSortNotes.setOnClickListener(this::showSortMenu);
        }

        if (btnEmptyCreateNote != null) {
            btnEmptyCreateNote.setOnClickListener(v -> createNewNote("note"));
        }
    }

    private void updateViewToggleIcon() {
        btnViewToggle.setImageResource(isGridView ? 
            android.R.drawable.ic_dialog_dialer : 
            android.R.drawable.ic_menu_sort_by_size);
    }

    private void updateDateLine() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        tvDateLine.setText(sdf.format(new Date()));
    }

    private void updateNoteCount(int count) {
        int folderCount = 0;
        try {
            folderCount = folderRepository.getAllFolders().size();
        } catch (Exception ignored) {}

        int fCount = folderCount;
        String text = count + (count == 1 ? " Note" : " Notes") + " · " + fCount + " Folder" + (fCount == 1 ? "" : "s");

        tvNoteCount.animate()
            .scaleX(1.1f).scaleY(1.1f)
            .setDuration(100)
            .withEndAction(() -> {
                tvNoteCount.setText(text);
                tvNoteCount.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    private void showMoreMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 0, 0, "📂  Folders View");
        popup.getMenu().add(0, 1, 1, "📦  Archive");
        popup.getMenu().add(0, 2, 2, "🗑️  Trash");
        popup.getMenu().add(0, 3, 3, "🏷️  Tags");
        popup.getMenu().add(0, 4, 4, "⚙️  Settings");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    startActivity(new Intent(this, NoteFoldersHomeActivity.class));
                    return true;
                case 1:
                    startActivity(new Intent(this, NotesArchiveActivity.class));
                    return true;
                case 2:
                    startActivity(new Intent(this, NotesTrashActivity.class));
                    return true;
                case 3:
                    startActivity(new Intent(this, TagsManagerActivity.class));
                    return true;
                case 4:
                    startActivity(new Intent(this, NotesSettingsActivity.class));
                    return true;
            }
            return false;
        });

        popup.show();
    }

    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add(0, 0, 0, "📅  By Date");
        popup.getMenu().add(0, 1, 1, "🔤  By Title");
        popup.getMenu().add(0, 2, 2, "📂  By Category");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0: // Sort by date
                    displayedNotes.sort((a, b) -> Long.compare(b.updatedAt, a.updatedAt));
                    notesAdapter.notifyDataSetChanged();
                    return true;
                case 1: // Sort by title
                    displayedNotes.sort((a, b) -> {
                        String ta = a.title != null ? a.title : "";
                        String tb = b.title != null ? b.title : "";
                        return ta.compareToIgnoreCase(tb);
                    });
                    notesAdapter.notifyDataSetChanged();
                    return true;
                case 2: // Sort by category
                    displayedNotes.sort((a, b) -> {
                        String ca = a.category != null ? a.category : "";
                        String cb = b.category != null ? b.category : "";
                        return ca.compareToIgnoreCase(cb);
                    });
                    notesAdapter.notifyDataSetChanged();
                    return true;
            }
            return false;
        });

        popup.show();
    }

    // ═══════════════════════════════════════════════════════════════
    //  SEARCH

    private void setupSearch() {
        etSearchNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Debounce search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    currentSearchQuery = s.toString().trim();
                    refreshNotes();
                    
                    // Hide recent searches when typing
                    if (!currentSearchQuery.isEmpty()) {
                        recentSearchesContainer.setVisibility(View.GONE);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 200);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearchNotes.setOnFocusChangeListener((v, hasFocus) -> {
            isSearchFocused = hasFocus;
            if (hasFocus && currentSearchQuery.isEmpty()) {
                showRecentSearches();
                // Add glow effect
                etSearchNotes.setBackgroundResource(R.drawable.notes_search_focused_bg);
            } else {
                recentSearchesContainer.setVisibility(View.GONE);
                etSearchNotes.setBackgroundResource(R.drawable.notes_search_bg);
            }
        });

        // Save search on submit
        etSearchNotes.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearchNotes.getText().toString().trim();
            if (!query.isEmpty()) {
                repository.addRecentSearch(query);
                hideKeyboard();
            }
            return true;
        });

        btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());
    }

    private void showRecentSearches() {
        List<String> searches = repository.getRecentSearches();
        if (searches.isEmpty()) {
            recentSearchesContainer.setVisibility(View.GONE);
            return;
        }

        recentSearchesList.removeAllViews();
        for (String search : searches) {
            TextView tv = new TextView(this);
            tv.setText("🕐  " + search);
            tv.setTextColor(0xFF94A3B8);
            tv.setTextSize(14);
            tv.setPadding(0, 16, 0, 16);
            tv.setOnClickListener(v -> {
                etSearchNotes.setText(search);
                etSearchNotes.setSelection(search.length());
                recentSearchesContainer.setVisibility(View.GONE);
            });
            recentSearchesList.addView(tv);
        }

        // Add clear button
        TextView clearBtn = new TextView(this);
        clearBtn.setText("Clear recent searches");
        clearBtn.setTextColor(0xFFEF4444);
        clearBtn.setTextSize(12);
        clearBtn.setPadding(0, 20, 0, 8);
        clearBtn.setOnClickListener(v -> {
            repository.clearRecentSearches();
            recentSearchesContainer.setVisibility(View.GONE);
        });
        recentSearchesList.addView(clearBtn);

        recentSearchesContainer.setVisibility(View.VISIBLE);
    }

    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Search notes...");
        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, "Voice search not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_SEARCH_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String voiceQuery = results.get(0);
                etSearchNotes.setText(voiceQuery);
                etSearchNotes.setSelection(voiceQuery.length());
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        etSearchNotes.clearFocus();
    }

    // ═══════════════════════════════════════════════════════════════
    //  FILTER CHIPS
    // ═══════════════════════════════════════════════════════════════

    private void setupFilterChips() {
        List<String> categories = repository.getAllCategories();
        chipContainer.removeAllViews();

        for (String category : categories) {
            TextView chip = createFilterChip(category);
            chipContainer.addView(chip);
        }

        // Add To-Do chip
        TextView todoChip = createFilterChip("Todo");
        todoChip.setText("To-Do");
        chipContainer.addView(todoChip);
    }

    private TextView createFilterChip(String category) {
        TextView chip = new TextView(this);
        chip.setText(category);
        chip.setTextSize(13);
        chip.setPadding(40, 20, 40, 20);

        boolean isSelected = category.equals(currentFilter);

        if (isSelected) {
            // Vibrant gradient background per category
            int chipColor = getCategoryColor(category);
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{chipColor, adjustAlpha(chipColor, 0.75f)});
            gd.setCornerRadius(50f);
            chip.setBackground(gd);
            chip.setTextColor(0xFFFFFFFF);
        } else {
            chip.setTextColor(0xFF9CA3AF);
            chip.setBackgroundResource(R.drawable.notes_chip_bg);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(12);
        chip.setLayoutParams(params);

        chip.setOnClickListener(v -> {
            if (!category.equals(currentFilter)) {
                currentFilter = category;
                
                // Animate selection
                chip.animate()
                    .scaleX(1.05f).scaleY(1.05f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        chip.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    })
                    .start();

                setupFilterChips();
                refreshNotes();
            }
        });

        return chip;
    }

    private int getCategoryColor(String category) {
        switch (category) {
            case "All":      return 0xFFF59E0B;
            case "Pinned":   return 0xFFFCD34D;
            case "Personal": return 0xFF3B82F6;
            case "Work":     return 0xFF6366F1;
            case "Ideas":    return 0xFFF97316;
            case "Study":    return 0xFFA855F7;
            case "Todo":     return 0xFF34D399;
            default:         return 0xFFF59E0B;
        }
    }

    private int adjustAlpha(int color, float factor) {
        int a = Math.round(Color.alpha(color) * factor);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, r, g, b);
    }

    // ═══════════════════════════════════════════════════════════════
    //  NOTES GRID
    // ═══════════════════════════════════════════════════════════════

    private void setupNotesGrid() {
        int spanCount = isGridView ? 2 : 1;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
            spanCount, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        notesRecyclerView.setLayoutManager(layoutManager);

        notesAdapter = new NotesAdapter(this, displayedNotes, this, isGridView);
        notesRecyclerView.setAdapter(notesAdapter);
    }

    private void refreshNotes() {
        // Get filtered notes
        ArrayList<Note> pinnedNotes = repository.filterPinnedNotes(currentFilter, currentSearchQuery);
        ArrayList<Note> unpinnedNotes = repository.filterUnpinnedNotes(currentFilter, currentSearchQuery);

        // Update pinned section
        updatePinnedSection(pinnedNotes);

        // Update others header
        if (!pinnedNotes.isEmpty() && !unpinnedNotes.isEmpty()) {
            tvOthersHeader.setText("Others");
            tvOthersHeader.setVisibility(View.VISIBLE);
        } else if (pinnedNotes.isEmpty() && !unpinnedNotes.isEmpty()) {
            tvOthersHeader.setText("All Notes");
            tvOthersHeader.setVisibility(View.VISIBLE);
        } else {
            tvOthersHeader.setVisibility(View.GONE);
        }

        // Update main grid with unpinned notes
        displayedNotes.clear();
        displayedNotes.addAll(unpinnedNotes);
        notesAdapter.notifyDataSetChanged();

        // Update count (total active notes for current filter)
        int totalCount = pinnedNotes.size() + unpinnedNotes.size();
        updateNoteCount(totalCount);

        // Show/hide empty state
        boolean isEmpty = pinnedNotes.isEmpty() && unpinnedNotes.isEmpty();
        emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        notesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updatePinnedSection(ArrayList<Note> pinnedNotes) {
        if (pinnedNotes.isEmpty()) {
            pinnedSection.setVisibility(View.GONE);
            return;
        }

        pinnedSection.setVisibility(View.VISIBLE);
        pinnedNotesContainer.removeAllViews();

        for (Note note : pinnedNotes) {
            View card = createPinnedNoteCard(note);
            pinnedNotesContainer.addView(card);
        }
    }

    private View createPinnedNoteCard(Note note) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.item_pinned_note, pinnedNotesContainer, false);

        TextView tvTitle = card.findViewById(R.id.tvPinnedTitle);
        TextView tvPreview = card.findViewById(R.id.tvPinnedPreview);
        TextView tvCategory = card.findViewById(R.id.tvPinnedCategory);

        tvTitle.setText(note.title.isEmpty() ? "Untitled" : note.title);
        tvPreview.setText(note.plainTextPreview.isEmpty() ? "No content" : note.plainTextPreview);
        tvCategory.setText(note.category);

        // Apply note color tint
        if (!note.colorHex.equals(Note.NOTE_COLORS[0])) {
            int color = Note.parseColorSafe(note.colorHex);
            card.getBackground().setTint(color);
        }

        card.setOnClickListener(v -> openNote(note));
        card.setOnLongClickListener(v -> {
            if (!isMultiSelectMode) {
                enterMultiSelectMode();
                toggleNoteSelection(note.id);
            }
            return true;
        });

        return card;
    }

    // ═══════════════════════════════════════════════════════════════
    //  MULTI-SELECT MODE
    // ═══════════════════════════════════════════════════════════════

    private void setupMultiSelectBar() {
        LinearLayout btnMultiPin = findViewById(R.id.btnMultiPin);
        LinearLayout btnMultiColor = findViewById(R.id.btnMultiColor);
        LinearLayout btnMultiCategory = findViewById(R.id.btnMultiCategory);
        LinearLayout btnMultiArchive = findViewById(R.id.btnMultiArchive);
        LinearLayout btnMultiDelete = findViewById(R.id.btnMultiDelete);

        btnMultiPin.setOnClickListener(v -> {
            if (!selectedNoteIds.isEmpty()) {
                repository.batchPin(new ArrayList<>(selectedNoteIds), true);
                exitMultiSelectMode();
                refreshNotes();
                Toast.makeText(this, "Pinned " + selectedNoteIds.size() + " notes", Toast.LENGTH_SHORT).show();
            }
        });

        btnMultiColor.setOnClickListener(v -> showColorPicker());

        btnMultiCategory.setOnClickListener(v -> showCategoryPicker());

        btnMultiArchive.setOnClickListener(v -> {
            if (!selectedNoteIds.isEmpty()) {
                repository.batchArchive(new ArrayList<>(selectedNoteIds));
                exitMultiSelectMode();
                refreshNotes();
                Toast.makeText(this, "Archived " + selectedNoteIds.size() + " notes", Toast.LENGTH_SHORT).show();
            }
        });

        btnMultiDelete.setOnClickListener(v -> {
            if (!selectedNoteIds.isEmpty()) {
                new AlertDialog.Builder(this)
                    .setTitle("Move to Trash")
                    .setMessage("Move " + selectedNoteIds.size() + " notes to trash?")
                    .setPositiveButton("Move", (d, w) -> {
                        repository.batchTrash(new ArrayList<>(selectedNoteIds));
                        exitMultiSelectMode();
                        refreshNotes();
                        Toast.makeText(this, "Moved to trash", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
    }

    private void enterMultiSelectMode() {
        isMultiSelectMode = true;
        selectedNoteIds.clear();
        
        // Show bottom bar with animation
        multiSelectBar.setVisibility(View.VISIBLE);
        multiSelectBar.setTranslationY(multiSelectBar.getHeight());
        multiSelectBar.animate()
            .translationY(0)
            .setDuration(200)
            .setInterpolator(new DecelerateInterpolator())
            .start();

        // Hide FAB
        fabAddNote.animate()
            .scaleX(0).scaleY(0)
            .setDuration(150)
            .start();

        notesAdapter.setMultiSelectMode(true);
    }

    private void exitMultiSelectMode() {
        isMultiSelectMode = false;
        selectedNoteIds.clear();

        // Hide bottom bar
        multiSelectBar.animate()
            .translationY(multiSelectBar.getHeight())
            .setDuration(200)
            .withEndAction(() -> multiSelectBar.setVisibility(View.GONE))
            .start();

        // Show FAB
        fabAddNote.animate()
            .scaleX(1).scaleY(1)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .start();

        notesAdapter.setMultiSelectMode(false);
        notesAdapter.clearSelections();
    }

    private void toggleNoteSelection(String noteId) {
        if (selectedNoteIds.contains(noteId)) {
            selectedNoteIds.remove(noteId);
        } else {
            selectedNoteIds.add(noteId);
        }
        notesAdapter.toggleSelection(noteId);

        // Exit if nothing selected
        if (selectedNoteIds.isEmpty()) {
            exitMultiSelectMode();
        }
    }

    private void showColorPicker() {
        String[] colorNames = Note.NOTE_COLOR_NAMES;
        String[] colorHexes = Note.NOTE_COLORS;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Color");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        for (int i = 0; i < colorNames.length; i++) {
            final String hex = colorHexes[i];
            
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(0, 20, 0, 20);

            View colorDot = new View(this);
            colorDot.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            colorDot.setBackgroundColor(Note.parseColorSafe(hex));
            ((LinearLayout.LayoutParams) colorDot.getLayoutParams()).setMarginEnd(24);

            TextView name = new TextView(this);
            name.setText(colorNames[i]);
            name.setTextColor(0xFFE2E8F0);
            name.setTextSize(16);

            row.addView(colorDot);
            row.addView(name);

            row.setOnClickListener(v -> {
                repository.batchSetColor(new ArrayList<>(selectedNoteIds), hex);
                exitMultiSelectMode();
                refreshNotes();
            });

            layout.addView(row);
        }

        builder.setView(layout);
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCategoryPicker() {
        List<String> categories = repository.getAllCategories();
        // Remove "All", "Pinned", "Locked" filters
        categories.remove("All");
        categories.remove("Pinned");
        categories.remove("Locked");

        String[] items = categories.toArray(new String[0]);

        new AlertDialog.Builder(this)
            .setTitle("Move to Category")
            .setItems(items, (d, which) -> {
                String category = items[which];
                repository.batchSetCategory(new ArrayList<>(selectedNoteIds), category);
                exitMultiSelectMode();
                refreshNotes();
                Toast.makeText(this, "Moved to " + category, Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ═══════════════════════════════════════════════════════════════
    //  SPEED DIAL FAB
    // ═══════════════════════════════════════════════════════════════

    private void setupSpeedDial() {
        fabAddNote.setOnClickListener(v -> {
            if (isSpeedDialOpen) {
                closeSpeedDial();
            } else {
                openSpeedDial();
            }
        });

        speedDialOverlay.setOnClickListener(v -> closeSpeedDial());

        speedDialItem1.setOnClickListener(v -> {
            closeSpeedDial();
            createNewNote("note");
        });

        speedDialItem2.setOnClickListener(v -> {
            closeSpeedDial();
            createNewNote("checklist");
        });

        speedDialItem3.setOnClickListener(v -> {
            closeSpeedDial();
            createNewNote("voice");
        });

        speedDialItem4.setOnClickListener(v -> {
            closeSpeedDial();
            createNewNote("image");
        });
    }

    private void openSpeedDial() {
        isSpeedDialOpen = true;

        // Show overlay
        speedDialOverlay.setVisibility(View.VISIBLE);
        speedDialOverlay.setAlpha(0f);
        speedDialOverlay.animate().alpha(1f).setDuration(200).start();

        // Rotate FAB
        fabAddNote.animate()
            .rotation(45f)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .start();

        // Animate items in radially
        animateSpeedDialItem(speedDialItem1, 0);
        animateSpeedDialItem(speedDialItem2, 50);
        animateSpeedDialItem(speedDialItem3, 100);
        animateSpeedDialItem(speedDialItem4, 150);
    }

    private void animateSpeedDialItem(View item, long delay) {
        item.setVisibility(View.VISIBLE);
        item.setAlpha(0f);
        item.setTranslationY(60f);
        item.setScaleX(0.6f);
        item.setScaleY(0.6f);

        item.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(delay)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .start();
    }

    private void closeSpeedDial() {
        isSpeedDialOpen = false;

        // Hide overlay
        speedDialOverlay.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction(() -> speedDialOverlay.setVisibility(View.GONE))
            .start();

        // Rotate FAB back
        fabAddNote.animate()
            .rotation(0f)
            .setDuration(200)
            .start();

        // Hide items
        hideSpeedDialItem(speedDialItem4, 0);
        hideSpeedDialItem(speedDialItem3, 30);
        hideSpeedDialItem(speedDialItem2, 60);
        hideSpeedDialItem(speedDialItem1, 90);
    }

    private void hideSpeedDialItem(View item, long delay) {
        item.animate()
            .alpha(0f)
            .translationY(30f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setStartDelay(delay)
            .setDuration(150)
            .withEndAction(() -> item.setVisibility(View.GONE))
            .start();
    }

    private void createNewNote(String type) {
        String category = currentFilter.equals("All") || currentFilter.equals("Pinned") || 
                       currentFilter.equals("Locked") ? "Personal" : currentFilter;

        switch (type) {
            case "note":
                // Open editor for new note
                Intent intent = NoteEditorActivity.createNewNoteIntent(this, category);
                startActivityForResult(intent, 200);
                break;
            case "checklist":
                // Create a note with checklist template
                Note checklistNote = new Note();
                checklistNote.category = category;
                checklistNote.title = "Checklist";
                checklistNote.body = "[ ] Item 1\n[ ] Item 2\n[ ] Item 3";
                checklistNote.updatePlainTextPreview();
                repository.addNote(checklistNote);
                Intent checklistIntent = NoteEditorActivity.createIntent(this, checklistNote.id);
                startActivityForResult(checklistIntent, 200);
                break;
            case "voice":
                // Create note and show voice input hint
                Intent voiceIntent = NoteEditorActivity.createNewNoteIntent(this, category);
                voiceIntent.putExtra("start_voice", true);
                startActivityForResult(voiceIntent, 200);
                break;
            case "image":
                // Create note and trigger image insert
                Intent imageIntent = NoteEditorActivity.createNewNoteIntent(this, category);
                imageIntent.putExtra("start_image", true);
                startActivityForResult(imageIntent, 200);
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  NOTE ACTIONS (Adapter Callbacks)
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void onNoteClick(Note note) {
        if (isMultiSelectMode) {
            toggleNoteSelection(note.id);
        } else {
            openNote(note);
        }
    }

    @Override
    public void onNoteLongClick(Note note) {
        if (!isMultiSelectMode) {
            enterMultiSelectMode();
        }
        toggleNoteSelection(note.id);
    }

    @Override
    public void onNoteSelectionChanged(String noteId, boolean selected) {
        if (selected) {
            selectedNoteIds.add(noteId);
        } else {
            selectedNoteIds.remove(noteId);
        }
        if (selectedNoteIds.isEmpty() && isMultiSelectMode) {
            exitMultiSelectMode();
        }
    }

    private void openNote(Note note) {
        // Open full editor activity - handles locked notes internally
        Intent intent = NoteEditorActivity.createIntent(this, note.id);
        startActivityForResult(intent, 200);
    }

    // Kept for backwards compatibility with quick actions
    private void showQuickEditDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        EditText etTitle = new EditText(this);
        etTitle.setHint("Title");
        etTitle.setText(note.title);
        etTitle.setTextColor(0xFFE2E8F0);
        etTitle.setHintTextColor(0xFF64748B);
        layout.addView(etTitle);

        EditText etBody = new EditText(this);
        etBody.setHint("Content");
        etBody.setText(note.body.replaceAll("<[^>]*>", "")); // Strip HTML for simple edit
        etBody.setTextColor(0xFFE2E8F0);
        etBody.setHintTextColor(0xFF64748B);
        etBody.setMinLines(5);
        etBody.setGravity(android.view.Gravity.TOP);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bodyParams.topMargin = 24;
        etBody.setLayoutParams(bodyParams);
        layout.addView(etBody);

        builder.setView(layout);
        builder.setPositiveButton("Save", (d, w) -> {
            note.title = etTitle.getText().toString().trim();
            note.body = etBody.getText().toString().trim();
            note.updatePlainTextPreview();
            repository.updateNote(note);
            refreshNotes();
        });
        builder.setNeutralButton("More Options", (d, w) -> {
            showNoteOptionsDialog(note);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showNoteOptionsDialog(Note note) {
        String[] options = {
            note.isPinned ? "📌 Unpin" : "📌 Pin",
            note.isLocked ? "🔓 Unlock" : "🔒 Lock",
            "🎨 Change Color",
            "📂 Change Category",
            "🏷️ Edit Tags",
            "📦 Archive",
            "🗑️ Move to Trash"
        };

        new AlertDialog.Builder(this)
            .setTitle(note.title.isEmpty() ? "Untitled Note" : note.title)
            .setItems(options, (d, which) -> {
                switch (which) {
                    case 0: // Pin/Unpin
                        repository.togglePin(note.id);
                        refreshNotes();
                        break;
                    case 1: // Lock/Unlock
                        repository.toggleLock(note.id);
                        refreshNotes();
                        Toast.makeText(this, note.isLocked ? "Note unlocked" : "Note locked", 
                            Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // Change Color
                        showSingleNoteColorPicker(note);
                        break;
                    case 3: // Change Category
                        showSingleNoteCategoryPicker(note);
                        break;
                    case 4: // Edit Tags
                        showTagsEditor(note);
                        break;
                    case 5: // Archive
                        repository.archiveNote(note.id);
                        refreshNotes();
                        Toast.makeText(this, "Note archived", Toast.LENGTH_SHORT).show();
                        break;
                    case 6: // Trash
                        repository.trashNote(note.id);
                        refreshNotes();
                        Toast.makeText(this, "Note moved to trash", Toast.LENGTH_SHORT).show();
                        break;
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showSingleNoteColorPicker(Note note) {
        String[] colorNames = Note.NOTE_COLOR_NAMES;
        String[] colorHexes = Note.NOTE_COLORS;

        new AlertDialog.Builder(this)
            .setTitle("Choose Color")
            .setItems(colorNames, (d, which) -> {
                repository.setNoteColor(note.id, colorHexes[which]);
                refreshNotes();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showSingleNoteCategoryPicker(Note note) {
        List<String> categories = repository.getAllCategories();
        categories.remove("All");
        categories.remove("Pinned");
        categories.remove("Locked");
        String[] items = categories.toArray(new String[0]);

        new AlertDialog.Builder(this)
            .setTitle("Move to Category")
            .setItems(items, (d, which) -> {
                repository.setNoteCategory(note.id, items[which]);
                refreshNotes();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showTagsEditor(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);

        // Current tags
        TextView tvCurrent = new TextView(this);
        String currentTags = note.tags != null && !note.tags.isEmpty() ? 
            String.join(", ", note.tags) : "No tags";
        tvCurrent.setText("Current: " + currentTags);
        tvCurrent.setTextColor(0xFF94A3B8);
        layout.addView(tvCurrent);

        // Input for new tags
        EditText etTags = new EditText(this);
        etTags.setHint("Enter tags (comma separated)");
        etTags.setText(note.tags != null ? String.join(", ", note.tags) : "");
        etTags.setTextColor(0xFFE2E8F0);
        etTags.setHintTextColor(0xFF64748B);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 24;
        etTags.setLayoutParams(params);
        layout.addView(etTags);

        builder.setView(layout);
        builder.setPositiveButton("Save", (d, w) -> {
            String input = etTags.getText().toString().trim();
            note.tags = new ArrayList<>();
            if (!input.isEmpty()) {
                String[] parts = input.split(",");
                for (String part : parts) {
                    String tag = part.trim();
                    if (!tag.isEmpty()) {
                        note.tags.add(tag);
                    }
                }
            }
            repository.updateNote(note);
            refreshNotes();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ═══════════════════════════════════════════════════════════════
    //  NAVIGATION DRAWER
    // ═══════════════════════════════════════════════════════════════

    private void setupDrawer() {
        if (btnDrawerToggle != null && drawerLayout != null) {
            btnDrawerToggle.setOnClickListener(v ->
                    drawerLayout.openDrawer(GravityCompat.START));
        }

        // Nav item click listeners
        LinearLayout drawerNavAllNotes = findViewById(R.id.drawerNavAllNotes);
        LinearLayout drawerNavPinned = findViewById(R.id.drawerNavPinned);
        LinearLayout drawerNavArchive = findViewById(R.id.drawerNavArchive);
        LinearLayout drawerNavTrash = findViewById(R.id.drawerNavTrash);

        if (drawerNavAllNotes != null) {
            drawerNavAllNotes.setOnClickListener(v -> {
                currentFilter = "All";
                setupFilterChips();
                refreshNotes();
                if (drawerLayout != null) drawerLayout.closeDrawers();
            });
        }
        if (drawerNavPinned != null) {
            drawerNavPinned.setOnClickListener(v -> {
                currentFilter = "Pinned";
                setupFilterChips();
                refreshNotes();
                if (drawerLayout != null) drawerLayout.closeDrawers();
            });
        }
        if (drawerNavArchive != null) {
            drawerNavArchive.setOnClickListener(v -> {
                startActivity(new Intent(this, NotesArchiveActivity.class));
                if (drawerLayout != null) drawerLayout.closeDrawers();
            });
        }
        if (drawerNavTrash != null) {
            drawerNavTrash.setOnClickListener(v -> {
                startActivity(new Intent(this, NotesTrashActivity.class));
                if (drawerLayout != null) drawerLayout.closeDrawers();
            });
        }
        if (drawerAddFolder != null) {
            drawerAddFolder.setOnClickListener(v -> {
                startActivity(new Intent(this, NoteFoldersHomeActivity.class));
                if (drawerLayout != null) drawerLayout.closeDrawers();
            });
        }

        updateDrawerCounts();
        populateDrawerFolders();
    }

    private void updateDrawerCounts() {
        try {
            java.util.ArrayList<Note> all = repository.filterUnpinnedNotes("All", "");
            java.util.ArrayList<Note> pinned = repository.filterPinnedNotes("All", "");
            int totalCount = all.size() + pinned.size();
            int folderCount = folderRepository.getAllFolders().size();

            if (tvDrawerNoteCount != null) {
                tvDrawerNoteCount.setText(totalCount + " notes · " + folderCount + " folders");
            }
            if (tvDrawerAllCount != null) {
                tvDrawerAllCount.setText(String.valueOf(totalCount));
            }
            if (tvDrawerPinnedCount != null) {
                tvDrawerPinnedCount.setText(String.valueOf(pinned.size()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to update drawer counts", e);
        }
    }

    private void populateDrawerFolders() {
        if (drawerFoldersList == null) return;
        drawerFoldersList.removeAllViews();

        try {
            java.util.ArrayList<NoteFolder> folders = folderRepository.getAllFolders();
            for (NoteFolder folder : folders) {
                TextView tv = new TextView(this);
                tv.setText("  📁  " + folder.name);
                tv.setTextColor(0xFFE2E8F0);
                tv.setTextSize(14);
                tv.setPadding(dpToPx(20), dpToPx(14), dpToPx(20), dpToPx(14));
                tv.setBackgroundResource(android.R.drawable.list_selector_background);
                tv.setOnClickListener(v -> {
                    Intent intent = new Intent(this, NoteFolderActivity.class);
                    intent.putExtra("folder_id", folder.id);
                    startActivity(intent);
                    if (drawerLayout != null) drawerLayout.closeDrawers();
                });
                drawerFoldersList.addView(tv);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to populate drawer folders", e);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ═══════════════════════════════════════════════════════════════
    //  TO-DO LISTS SECTION
    // ═══════════════════════════════════════════════════════════════

    private void setupTodoSection() {
        todoListsSection = findViewById(R.id.todoListsSection);
        todoListsRecycler = findViewById(R.id.todoListsRecycler);
        tvTodoHeader = findViewById(R.id.tvTodoHeader);
        btnViewAllTodos = findViewById(R.id.btnViewAllTodos);
        tvTodoSummary = findViewById(R.id.tvTodoSummary);

        if (todoListsSection == null) return;

        // Setup horizontal recycler
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        todoListsRecycler.setLayoutManager(layoutManager);

        // Create adapter
        todoListAdapter = new TodoListAdapter(this, todoLists, todoRepository,
            new TodoListAdapter.OnListActionListener() {
                @Override
                public void onListClick(TodoList list) {
                    startActivity(TodoListDetailActivity.createIntent(NotesActivity.this, list.id));
                }
                @Override
                public void onAddListClick() {
                    CreateTodoListSheet sheet = new CreateTodoListSheet();
                    sheet.setOnListCreatedListener(list -> refreshTodoSection());
                    sheet.show(getSupportFragmentManager(), CreateTodoListSheet.TAG);
                }
            });
        todoListsRecycler.setAdapter(todoListAdapter);

        refreshTodoSection();
    }

    private void refreshTodoSection() {
        if (todoRepository == null) return;
        todoLists.clear();
        todoLists.addAll(todoRepository.getAllLists());
        if (todoListAdapter != null) todoListAdapter.notifyDataSetChanged();

        // Update summary
        if (tvTodoSummary != null) {
            int active = todoRepository.getGlobalActiveCount();
            int overdue = todoRepository.getGlobalOverdueCount();
            String summary = active + " active";
            if (overdue > 0) summary += " · " + overdue + " overdue";
            tvTodoSummary.setText(summary);
        }

        // Show section
        if (todoListsSection != null) {
            todoListsSection.setVisibility(View.VISIBLE);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  FOLDER STRIP
    // ═══════════════════════════════════════════════════════════════

    private void setupFolderStrip() {
        if (folderStripSection == null || folderCardsContainer == null) return;

        try {
            java.util.ArrayList<NoteFolder> folders = folderRepository.getAllFolders();
            if (folders.isEmpty()) {
                folderStripSection.setVisibility(View.GONE);
                return;
            }

            folderStripSection.setVisibility(View.VISIBLE);
            folderCardsContainer.removeAllViews();

            // Pre-compute per-folder note counts
            java.util.HashMap<String, Integer> countMap = new java.util.HashMap<>();
            for (Note n : repository.getActiveNotes()) {
                if (n.folderId != null) {
                    countMap.put(n.folderId, countMap.getOrDefault(n.folderId, 0) + 1);
                }
            }

            for (NoteFolder folder : folders) {
                View card = LayoutInflater.from(this).inflate(
                        R.layout.item_notes_folder_strip_card, folderCardsContainer, false);

                TextView tvIcon  = card.findViewById(R.id.tvFolderIcon);
                TextView tvName  = card.findViewById(R.id.tvFolderName);
                TextView tvCount = card.findViewById(R.id.tvFolderCount);

                tvIcon.setText("📁");
                tvName.setText(folder.name);

                int count = countMap.getOrDefault(folder.id, 0);
                tvCount.setText(count + (count == 1 ? " note" : " notes"));

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, NoteFolderActivity.class);
                    intent.putExtra("folder_id", folder.id);
                    startActivity(intent);
                });

                folderCardsContainer.addView(card);
            }

            // Add "+" create card
            View plusCard = LayoutInflater.from(this).inflate(
                    R.layout.item_notes_folder_strip_card, folderCardsContainer, false);
            TextView tvPlusIcon  = plusCard.findViewById(R.id.tvFolderIcon);
            TextView tvPlusName  = plusCard.findViewById(R.id.tvFolderName);
            TextView tvPlusCount = plusCard.findViewById(R.id.tvFolderCount);
            tvPlusIcon.setText("➕");
            tvPlusName.setText("New");
            tvPlusCount.setText("folder");
            plusCard.setOnClickListener(v ->
                    startActivity(new Intent(this, NoteFoldersHomeActivity.class)));
            folderCardsContainer.addView(plusCard);

            if (btnViewAllFolders != null) {
                btnViewAllFolders.setOnClickListener(v ->
                        startActivity(new Intent(this, NoteFoldersHomeActivity.class)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup folder strip", e);
            if (folderStripSection != null) folderStripSection.setVisibility(View.GONE);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  RECENTLY VIEWED STRIP
    // ═══════════════════════════════════════════════════════════════

    private void setupRecentlyViewed() {
        if (recentlyViewedSection == null || recentlyViewedContainer == null) return;

        try {
            ArrayList<Note> candidates = repository.filterUnpinnedNotes("All", "");
            List<Note> recentNotes = new ArrayList<>();
            for (Note n : candidates) {
                if (!n.isLocked) {
                    recentNotes.add(n);
                    if (recentNotes.size() >= 5) break;
                }
            }

            if (recentNotes.size() < 3) {
                recentlyViewedSection.setVisibility(View.GONE);
                return;
            }

            recentlyViewedSection.setVisibility(View.VISIBLE);
            recentlyViewedContainer.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(this);
            for (Note note : recentNotes) {
                View card = inflater.inflate(R.layout.item_recently_viewed_card, recentlyViewedContainer, false);
                TextView tvTitle = card.findViewById(R.id.tvRecentNoteTitle);
                View accent = card.findViewById(R.id.recentCategoryAccent);

                tvTitle.setText(note.title.isEmpty() ? "Untitled" : note.title);

                int accentColor = Note.getCategoryColor(note.category);
                accent.setBackgroundColor(accentColor);

                card.setOnClickListener(v -> openNote(note));
                recentlyViewedContainer.addView(card);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup recently viewed", e);
            if (recentlyViewedSection != null) recentlyViewedSection.setVisibility(View.GONE);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  SYNC CALLBACKS (Called by ReverseCommandListener)
    // ═══════════════════════════════════════════════════════════════

    public void onNotesSyncReceived(String jsonData) {
        runOnUiThread(() -> {
            repository.importFromJson(jsonData);
            refreshNotes();
            Toast.makeText(this, "Notes synced!", Toast.LENGTH_SHORT).show();
        });
    }

    public void onNoteAddedFromPC(String noteId, String name) {
        runOnUiThread(() -> {
            repository.reload();
            refreshNotes();
        });
    }

    public void onNoteUpdatedFromPC(String noteId) {
        runOnUiThread(() -> {
            repository.reload();
            refreshNotes();
        });
    }

    public void onNoteDeletedFromPC(String noteId) {
        runOnUiThread(() -> {
            repository.reload();
            refreshNotes();
        });
    }

    /**
     * Called by ReverseCommandListener for note events from PC
     */
    public void onNoteEventReceived(String eventType, String noteId) {
        runOnUiThread(() -> {
            switch (eventType) {
                case "ADDED":
                    onNoteAddedFromPC(noteId, "");
                    break;
                case "UPDATED":
                    onNoteUpdatedFromPC(noteId);
                    break;
                case "DELETED":
                    onNoteDeletedFromPC(noteId);
                    break;
            }
        });
    }
}
