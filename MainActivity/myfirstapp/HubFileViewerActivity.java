package com.prajwal.myfirstapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;

/**
 * Universal file viewer — dispatches to appropriate viewer based on file type.
 */
public class HubFileViewerActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_ID = "file_id";
    private static final int MAX_FILE_LINES = 5000;

    private HubFileRepository repo;
    private HubFile file;

    private TextView tvViewerFileName;
    private View imageViewerSection;
    private View codeViewerSection;
    private View textViewerSection;
    private View unsupportedSection;
    private LinearLayout fileInfoPanel;

    private float codeFontSize = 13f;
    private TextView tvCodeContent;
    private boolean infoPanelVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_file_viewer);

        repo = HubFileRepository.getInstance(this);

        String fileId = getIntent().getStringExtra(EXTRA_FILE_ID);
        if (fileId != null) file = repo.getFileById(fileId);

        bindViews();

        if (file == null) {
            showUnsupported("Unknown File", "File not found", "📄");
            return;
        }

        tvViewerFileName.setText(file.displayName != null ? file.displayName :
                (file.originalFileName != null ? file.originalFileName : "File"));

        populateFileInfo();
        dispatchViewer();

        // Log access
        repo.logActivity(new FileActivity(
                file.id,
                file.displayName != null ? file.displayName : file.originalFileName,
                file.getTypeEmoji(),
                FileActivity.Action.VIEWED,
                "Opened in viewer"
        ));
    }

    private void bindViews() {
        tvViewerFileName = findViewById(R.id.tvViewerFileName);
        imageViewerSection = findViewById(R.id.imageViewerSection);
        codeViewerSection = findViewById(R.id.codeViewerSection);
        textViewerSection = findViewById(R.id.textViewerSection);
        unsupportedSection = findViewById(R.id.unsupportedSection);
        fileInfoPanel = findViewById(R.id.fileInfoPanel);
        tvCodeContent = findViewById(R.id.tvCodeContent);

        findViewById(R.id.btnViewerBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnViewerShare).setOnClickListener(v -> shareFile());
        findViewById(R.id.btnViewerMore).setOnClickListener(v -> showMoreMenu());
        findViewById(R.id.btnFileInfo).setOnClickListener(v -> toggleInfoPanel());

        // Code viewer controls
        View codeMinus = findViewById(R.id.btnCodeFontMinus);
        if (codeMinus != null) codeMinus.setOnClickListener(v -> {
            if (codeFontSize > 8) codeFontSize -= 1;
            tvCodeContent.setTextSize(codeFontSize);
        });

        View codePlus = findViewById(R.id.btnCodeFontPlus);
        if (codePlus != null) codePlus.setOnClickListener(v -> {
            if (codeFontSize < 24) codeFontSize += 1;
            tvCodeContent.setTextSize(codeFontSize);
        });

        View codeCopy = findViewById(R.id.btnCodeCopyAll);
        if (codeCopy != null) codeCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && tvCodeContent.getText() != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("code", tvCodeContent.getText()));
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchViewer() {
        if (file.fileType == null) { showUnsupported(); return; }

        switch (file.fileType) {
            case IMAGE:
            case SCREENSHOT:
                showImage();
                break;
            case CODE:
                showCode();
                break;
            case DOCUMENT:
            case SPREADSHEET:
            case PRESENTATION:
                showText();
                break;
            case PDF:
                showPdfOrExternal();
                break;
            case AUDIO:
                openAudioPlayer();
                break;
            case VIDEO:
                openVideoPlayer();
                break;
            case ARCHIVE:
                showArchiveContents();
                break;
            default:
                showUnsupported();
                break;
        }
    }

    private void showImage() {
        imageViewerSection.setVisibility(View.VISIBLE);
        codeViewerSection.setVisibility(View.GONE);
        textViewerSection.setVisibility(View.GONE);
        unsupportedSection.setVisibility(View.GONE);

        ImageView iv = findViewById(R.id.ivImageViewer);
        if (file.filePath != null && !file.filePath.isEmpty()) {
            try {
                File f = new File(file.filePath);
                if (f.exists()) {
                    iv.setImageURI(Uri.fromFile(f));
                    return;
                }
            } catch (Exception e) { /* fall through */ }
        }
        // Placeholder
        iv.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    private void showCode() {
        imageViewerSection.setVisibility(View.GONE);
        codeViewerSection.setVisibility(View.VISIBLE);
        textViewerSection.setVisibility(View.GONE);
        unsupportedSection.setVisibility(View.GONE);

        // Set language badge
        TextView tvLang = findViewById(R.id.tvCodeLanguage);
        if (tvLang != null) tvLang.setText(detectLanguage(file.fileExtension));

        // Read file content
        String content = readFileContent(file.filePath);
        if (content != null) {
            // Add line numbers
            String[] lines = content.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                sb.append(String.format(Locale.getDefault(), "%4d  ", i + 1)).append(lines[i]).append("\n");
            }
            tvCodeContent.setTypeface(Typeface.MONOSPACE);
            tvCodeContent.setTextSize(codeFontSize);
            tvCodeContent.setText(sb.toString());
        } else {
            tvCodeContent.setText("(Could not read file)");
        }
    }

    private void showText() {
        imageViewerSection.setVisibility(View.GONE);
        codeViewerSection.setVisibility(View.GONE);
        textViewerSection.setVisibility(View.VISIBLE);
        unsupportedSection.setVisibility(View.GONE);

        String content = readFileContent(file.filePath);
        TextView tvText = findViewById(R.id.tvTextContent);
        tvText.setText(content != null ? content : "(Could not read file)");
    }

    private void showPdfOrExternal() {
        // Try to open as text first; if it fails show external button
        String content = readFileContent(file.filePath);
        if (content != null && !content.isEmpty() && !content.startsWith("%PDF")) {
            showText();
        } else {
            showUnsupported();
        }
    }

    private void openAudioPlayer() {
        try {
            Intent intent = new Intent(this, VaultAudioPlayerActivity.class);
            intent.putExtra("file_path", file.filePath);
            startActivity(intent);
        } catch (Exception e) {
            showUnsupported();
        }
    }

    private void openVideoPlayer() {
        try {
            Intent intent = new Intent(this, VaultVideoPlayerActivity.class);
            intent.putExtra("file_path", file.filePath);
            startActivity(intent);
        } catch (Exception e) {
            showUnsupported();
        }
    }

    private void showArchiveContents() {
        imageViewerSection.setVisibility(View.GONE);
        codeViewerSection.setVisibility(View.GONE);
        textViewerSection.setVisibility(View.VISIBLE);
        unsupportedSection.setVisibility(View.GONE);

        StringBuilder sb = new StringBuilder("Archive Contents:\n\n");
        if (file.filePath != null && !file.filePath.isEmpty()) {
            try (ZipFile zip = new ZipFile(file.filePath)) {
                Enumeration<? extends ZipEntry> entries = zip.entries();
                int count = 0;
                while (entries.hasMoreElements() && count < 200) {
                    ZipEntry entry = entries.nextElement();
                    sb.append(entry.isDirectory() ? "📁 " : "📄 ");
                    sb.append(entry.getName());
                    if (!entry.isDirectory()) {
                        sb.append(" (").append(formatBytes(entry.getSize())).append(")");
                    }
                    sb.append("\n");
                    count++;
                }
                if (entries.hasMoreElements()) sb.append("... and more");
            } catch (Exception e) {
                sb.append("(Could not read archive: ").append(e.getMessage()).append(")");
            }
        } else {
            sb.append("(No file path available)");
        }

        TextView tvText = findViewById(R.id.tvTextContent);
        tvText.setText(sb.toString());
    }

    private void showUnsupported() {
        String name = file.displayName != null ? file.displayName : file.originalFileName;
        String details = (file.fileType != null ? file.fileType.name() : "FILE") + " · " + file.getFormattedSize();
        showUnsupported(name, details, file.getTypeEmoji());
    }

    private void showUnsupported(String name, String details, String icon) {
        imageViewerSection.setVisibility(View.GONE);
        codeViewerSection.setVisibility(View.GONE);
        textViewerSection.setVisibility(View.GONE);
        unsupportedSection.setVisibility(View.VISIBLE);

        TextView tvIcon = findViewById(R.id.tvUnsupportedIcon);
        TextView tvName = findViewById(R.id.tvUnsupportedName);
        TextView tvDetails = findViewById(R.id.tvUnsupportedDetails);
        View btnOpen = findViewById(R.id.btnOpenExternal);

        tvIcon.setText(icon != null ? icon : "📄");
        tvName.setText(name != null ? name : "Unknown File");
        tvDetails.setText(details != null ? details : "");

        btnOpen.setOnClickListener(v -> openWithExternalApp());
    }

    private void openWithExternalApp() {
        if (file == null || file.filePath == null || file.filePath.isEmpty()) {
            Toast.makeText(this, "No file path available", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File f = new File(file.filePath);
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", f);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mime = file.mimeType != null && !file.mimeType.isEmpty() ? file.mimeType : "*/*";
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Open with"));
        } catch (Exception e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile() {
        if (file == null || file.filePath == null || file.filePath.isEmpty()) {
            Toast.makeText(this, "No file to share", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File f = new File(file.filePath);
            Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", f);
            Intent intent = new Intent(Intent.ACTION_SEND);
            String mime = file.mimeType != null && !file.mimeType.isEmpty() ? file.mimeType : "*/*";
            intent.setType(mime);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            Toast.makeText(this, "Cannot share file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMoreMenu() {
        Toast.makeText(this, "More options — coming soon", Toast.LENGTH_SHORT).show();
    }

    private void toggleInfoPanel() {
        infoPanelVisible = !infoPanelVisible;
        fileInfoPanel.setVisibility(infoPanelVisible ? View.VISIBLE : View.GONE);
        TextView btnInfo = findViewById(R.id.btnFileInfo);
        btnInfo.setText(infoPanelVisible ? "File Info ▼" : "File Info ▲");
    }

    private void populateFileInfo() {
        if (file == null) return;
        TextView tvInfoName = findViewById(R.id.tvInfoName);
        TextView tvInfoType = findViewById(R.id.tvInfoType);
        TextView tvInfoSize = findViewById(R.id.tvInfoSize);
        TextView tvInfoDate = findViewById(R.id.tvInfoDate);
        TextView tvInfoPath = findViewById(R.id.tvInfoPath);

        String name = file.displayName != null ? file.displayName : file.originalFileName;
        if (tvInfoName != null) tvInfoName.setText(name != null ? name : "—");
        if (tvInfoType != null) tvInfoType.setText(file.fileType != null ? file.fileType.name() : "—");
        if (tvInfoSize != null) tvInfoSize.setText(file.getFormattedSize());
        if (tvInfoDate != null) tvInfoDate.setText(
                new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()).format(new Date(file.importedAt)));
        if (tvInfoPath != null) tvInfoPath.setText(file.filePath != null ? file.filePath : "—");
    }

    private String readFileContent(String path) {
        if (path == null || path.isEmpty()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < MAX_FILE_LINES) {
                sb.append(line).append("\n");
                lineCount++;
            }
            if (lineCount >= MAX_FILE_LINES) sb.append("\n... (file truncated at " + MAX_FILE_LINES + " lines)");
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String detectLanguage(String ext) {
        if (ext == null) return "TEXT";
        switch (ext.toLowerCase()) {
            case "js": return "JS";
            case "ts": return "TypeScript";
            case "py": return "Python";
            case "java": return "Java";
            case "kt": return "Kotlin";
            case "cpp": case "cc": return "C++";
            case "c": return "C";
            case "cs": return "C#";
            case "go": return "Go";
            case "rs": return "Rust";
            case "rb": return "Ruby";
            case "php": return "PHP";
            case "swift": return "Swift";
            case "dart": return "Dart";
            case "html": case "htm": return "HTML";
            case "css": return "CSS";
            case "json": return "JSON";
            case "xml": return "XML";
            case "sh": case "bash": return "Shell";
            case "sql": return "SQL";
            default: return ext.toUpperCase(Locale.getDefault());
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) return "?";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
