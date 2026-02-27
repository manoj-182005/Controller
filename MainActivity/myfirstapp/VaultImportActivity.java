package com.prajwal.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Import preview screen. Shows selected files before encryption.
 */
public class VaultImportActivity extends Activity {

    public static final String EXTRA_IMPORT_URIS = "import_uris";
    public static final String EXTRA_IMPORT_MIME = "import_mime";

    private MediaVaultRepository repo;
    private List<Uri> uriList = new ArrayList<>();
    private List<String> displayNames = new ArrayList<>();
    private String selectedAlbumId = null;

    private ListView listViewFiles;
    private EditText etTags;
    private TextView btnBack, btnSelectAlbum, btnCopy, btnMove, tvStatus;
    private ProgressBar progressBar;

    /**
     * Convenience launcher.
     */
    public static void startImport(Activity from, ArrayList<Uri> uris, String singleMime) {
        android.content.Intent intent = new android.content.Intent(from, VaultImportActivity.class);
        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri u : uris) uriStrings.add(u.toString());
        intent.putStringArrayListExtra(EXTRA_IMPORT_URIS, uriStrings);
        if (singleMime != null) intent.putExtra(EXTRA_IMPORT_MIME, singleMime);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );
        setContentView(R.layout.activity_vault_import);

        repo = MediaVaultRepository.getInstance(this);
        if (!repo.isUnlocked()) { finish(); return; }

        listViewFiles = findViewById(R.id.listViewFiles);
        etTags = findViewById(R.id.etTags);
        btnBack = findViewById(R.id.btnBack);
        btnSelectAlbum = findViewById(R.id.btnSelectAlbum);
        btnCopy = findViewById(R.id.btnCopy);
        btnMove = findViewById(R.id.btnMove);
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);

        // Parse incoming URIs
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra(EXTRA_IMPORT_URIS);
        if (uriStrings != null) {
            for (String s : uriStrings) uriList.add(Uri.parse(s));
        }

        populateDisplayNames();
        updateListView();

        btnBack.setOnClickListener(v -> finish());
        btnSelectAlbum.setOnClickListener(v -> showAlbumPicker());
        btnCopy.setOnClickListener(v -> startImportProcess(false));
        btnMove.setOnClickListener(v -> confirmMove());
    }

    private void populateDisplayNames() {
        for (Uri uri : uriList) {
            displayNames.add(resolveDisplayName(uri));
        }
    }

    private String resolveDisplayName(Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri,
                    new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE}, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    long size = cursor.getLong(1);
                    return name + "  (" + formatSize(size) + ")";
                }
            } catch (Exception ignored) {}
        }
        return uri.getLastPathSegment();
    }

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024));
    }

    private void updateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, displayNames) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(0xFFF1F5F9);
                v.setBackgroundColor(0xFF1E293B);
                return v;
            }
        };
        listViewFiles.setAdapter(adapter);
    }

    private void showAlbumPicker() {
        List<VaultAlbum> albums = repo.getAlbums();
        String[] names = new String[albums.size() + 1];
        names[0] = "None";
        for (int i = 0; i < albums.size(); i++) names[i + 1] = albums.get(i).name;
        new AlertDialog.Builder(this)
                .setTitle("Select Album")
                .setItems(names, (d, w) -> {
                    if (w == 0) {
                        selectedAlbumId = null;
                        btnSelectAlbum.setText("📁  Select Album (optional)");
                    } else {
                        VaultAlbum album = albums.get(w - 1);
                        selectedAlbumId = album.id;
                        btnSelectAlbum.setText("📁  " + album.name);
                    }
                }).show();
    }

    private void confirmMove() {
        new AlertDialog.Builder(this)
                .setTitle("Move to Vault")
                .setMessage("Original files will be permanently deleted after import. Continue?")
                .setPositiveButton("Move", (d, w) -> startImportProcess(true))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startImportProcess(boolean move) {
        if (uriList.isEmpty()) {
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String tagsText = etTags.getText().toString().trim();
        List<String> tags = new ArrayList<>();
        if (!tagsText.isEmpty()) {
            for (String t : tagsText.split(",")) {
                String trimmed = t.trim();
                if (!trimmed.isEmpty()) tags.add(trimmed);
            }
        }

        setButtonsEnabled(false);
        progressBar.setMax(uriList.size());
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        final int total = uriList.size();
        final List<Uri> snapshot = new ArrayList<>(uriList);
        final String defaultMime = getIntent().getStringExtra(EXTRA_IMPORT_MIME);

        new Thread(() -> {
            int success = 0;
            int failed = 0;
            for (int i = 0; i < snapshot.size(); i++) {
                final int idx = i;
                runOnUiThread(() -> tvStatus.setText("Encrypting " + (idx + 1) + " of " + total + "..."));
                try {
                    Uri uri = snapshot.get(i);
                    String mime = resolveMimeType(uri, defaultMime);
                    VaultFileItem imported = repo.importFile(uri, mime, move);
                    if (imported != null) {
                        // apply tags
                        if (!tags.isEmpty()) {
                            imported.tags = new ArrayList<>(tags);
                            repo.updateFile(imported);
                        }
                        // add to album
                        if (selectedAlbumId != null) {
                            repo.addFileToAlbum(imported.id, selectedAlbumId);
                        }
                        success++;
                    } else {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                }
                final int prog = i + 1;
                runOnUiThread(() -> progressBar.setProgress(prog));
            }
            final int finalSuccess = success;
            final int finalFailed = failed;
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                String msg = finalSuccess + " file(s) imported";
                if (finalFailed > 0) msg += ", " + finalFailed + " failed";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }

    private String resolveMimeType(Uri uri, String fallback) {
        String mime = getContentResolver().getType(uri);
        if (mime != null && !mime.isEmpty()) return mime;
        return fallback != null ? fallback : "*/*";
    }

    private void setButtonsEnabled(boolean enabled) {
        btnCopy.setEnabled(enabled);
        btnMove.setEnabled(enabled);
        btnSelectAlbum.setEnabled(enabled);
        etTags.setEnabled(enabled);
    }
}
