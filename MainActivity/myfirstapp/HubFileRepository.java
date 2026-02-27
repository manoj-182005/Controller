package com.prajwal.myfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central repository for the Smart File Hub.
 * Persists data via SharedPreferences (JSON).
 */
public class HubFileRepository {

    private static final String TAG = "HubFileRepository";
    private static final long MILLIS_PER_DAY = 86_400_000L;
    private static final String PREFS_FILES = "hub_files";
    private static final String PREFS_FOLDERS = "hub_folders";
    private static final String PREFS_PROJECTS = "hub_projects";
    private static final String PREFS_DUPES = "hub_dupes";
    private static final String PREFS_ACTIVITY = "hub_activity";
    private static final String PREFS_INBOX = "hub_inbox";
    private static final String KEY_FILES = "files_json";
    private static final String KEY_FOLDERS = "folders_json";
    private static final String KEY_PROJECTS = "projects_json";
    private static final String KEY_DUPES = "dupes_json";
    private static final String KEY_ACTIVITY = "activity_json";
    private static final String KEY_INBOX = "inbox_json";
    private static final String KEY_LAST_SCAN = "last_scan_ts";
    private static final int MAX_ACTIVITY_ENTRIES = 100;

    private static HubFileRepository instance;
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    // In-memory caches
    private final List<HubFile> files = new ArrayList<>();
    private final List<HubFolder> folders = new ArrayList<>();
    private final List<HubProject> projects = new ArrayList<>();
    private final List<DuplicateGroup> duplicateGroups = new ArrayList<>();
    private final List<FileActivity> activities = new ArrayList<>();
    private final List<InboxItem> inboxItems = new ArrayList<>();

    private boolean loaded = false;

    private HubFileRepository(Context context) {
        this.context = context.getApplicationContext();
        load();
    }

    public static synchronized HubFileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new HubFileRepository(context);
        }
        return instance;
    }

    // ─── Load / Save ──────────────────────────────────────────────────────────

    private void load() {
        if (loaded) return;
        loadFiles();
        loadFolders();
        loadProjects();
        loadDuplicateGroups();
        loadActivities();
        loadInboxItems();
        if (folders.isEmpty()) seedDefaultSmartFolders();
        loadQuickSharePins();
        loaded = true;
    }

    private void loadFiles() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FILES, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_FILES, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                HubFile f = HubFile.fromJson(arr.getJSONObject(i));
                if (f != null) files.add(f);
            }
        } catch (Exception e) { Log.e(TAG, "loadFiles", e); }
    }

    private void loadFolders() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FOLDERS, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_FOLDERS, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                HubFolder f = HubFolder.fromJson(arr.getJSONObject(i));
                if (f != null) folders.add(f);
            }
        } catch (Exception e) { Log.e(TAG, "loadFolders", e); }
    }

    private void loadProjects() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_PROJECTS, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_PROJECTS, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                HubProject p = HubProject.fromJson(arr.getJSONObject(i));
                if (p != null) projects.add(p);
            }
        } catch (Exception e) { Log.e(TAG, "loadProjects", e); }
    }

    private void loadDuplicateGroups() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_DUPES, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_DUPES, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                DuplicateGroup g = DuplicateGroup.fromJson(arr.getJSONObject(i));
                if (g != null) duplicateGroups.add(g);
            }
        } catch (Exception e) { Log.e(TAG, "loadDuplicateGroups", e); }
    }

    private void loadActivities() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_ACTIVITY, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_ACTIVITY, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                FileActivity a = FileActivity.fromJson(arr.getJSONObject(i));
                if (a != null) activities.add(a);
            }
        } catch (Exception e) { Log.e(TAG, "loadActivities", e); }
    }

    private void loadInboxItems() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_INBOX, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_INBOX, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                InboxItem item = InboxItem.fromJson(arr.getJSONObject(i));
                if (item != null) inboxItems.add(item);
            }
        } catch (Exception e) { Log.e(TAG, "loadInboxItems", e); }
    }

    private void saveFiles() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (files) { for (HubFile f : files) arr.put(f.toJson()); }
                context.getSharedPreferences(PREFS_FILES, Context.MODE_PRIVATE)
                        .edit().putString(KEY_FILES, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveFiles", e); }
        });
    }

    private void saveFolders() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (folders) { for (HubFolder f : folders) arr.put(f.toJson()); }
                context.getSharedPreferences(PREFS_FOLDERS, Context.MODE_PRIVATE)
                        .edit().putString(KEY_FOLDERS, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveFolders", e); }
        });
    }

    private void saveProjects() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (projects) { for (HubProject p : projects) arr.put(p.toJson()); }
                context.getSharedPreferences(PREFS_PROJECTS, Context.MODE_PRIVATE)
                        .edit().putString(KEY_PROJECTS, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveProjects", e); }
        });
    }

    private void saveDuplicateGroups() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (duplicateGroups) { for (DuplicateGroup g : duplicateGroups) arr.put(g.toJson()); }
                context.getSharedPreferences(PREFS_DUPES, Context.MODE_PRIVATE)
                        .edit().putString(KEY_DUPES, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveDuplicateGroups", e); }
        });
    }

    private void saveActivities() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (activities) { for (FileActivity a : activities) arr.put(a.toJson()); }
                context.getSharedPreferences(PREFS_ACTIVITY, Context.MODE_PRIVATE)
                        .edit().putString(KEY_ACTIVITY, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveActivities", e); }
        });
    }

    private void saveInboxItems() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (inboxItems) { for (InboxItem item : inboxItems) arr.put(item.toJson()); }
                context.getSharedPreferences(PREFS_INBOX, Context.MODE_PRIVATE)
                        .edit().putString(KEY_INBOX, arr.toString()).apply();
            } catch (Exception e) { Log.e(TAG, "saveInboxItems", e); }
        });
    }

    // ─── Default Smart Folders ────────────────────────────────────────────────

    private void seedDefaultSmartFolders() {
        folders.add(HubFolder.createSmartFolder("WhatsApp Files", "#25D366", "💬",
                "{\"source\":\"WHATSAPP\"}"));
        folders.add(HubFolder.createSmartFolder("Screenshots", "#8B5CF6", "📸",
                "{\"fileType\":\"SCREENSHOT\"}"));
        folders.add(HubFolder.createSmartFolder("Large Files", "#EF4444", "🗂️",
                "{\"minSize\":52428800}"));
        folders.add(HubFolder.createSmartFolder("Recent Downloads", "#3B82F6", "⬇️",
                "{\"source\":\"DOWNLOADS\",\"maxAgeDays\":7}"));
        folders.add(HubFolder.createSmartFolder("Unorganized", "#6B7280", "📁",
                "{\"unorganized\":true}"));
        saveFolders();
    }

    // ─── File CRUD ────────────────────────────────────────────────────────────

    public synchronized void addFile(HubFile file) {
        files.add(0, file);
        saveFiles();
        logActivity(new FileActivity(file.id, file.displayName != null ? file.displayName : file.originalFileName,
                file.getTypeEmoji(), FileActivity.Action.IMPORTED,
                "Imported from " + (file.source != null ? file.source.name() : "Unknown")));
    }

    public synchronized HubFile getFileById(String id) {
        for (HubFile f : files) if (f.id.equals(id)) return f;
        return null;
    }

    public synchronized List<HubFile> getAllFiles() {
        return new ArrayList<>(files);
    }

    public synchronized List<HubFile> getFilesByType(HubFile.FileType type) {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) if (f.fileType == type && !f.isHidden) result.add(f);
        return result;
    }

    public synchronized List<HubFile> getFilesBySource(HubFile.Source source) {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) if (f.source == source && !f.isHidden) result.add(f);
        return result;
    }

    public synchronized List<HubFile> getFavourites() {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) if (f.isFavourited && !f.isHidden) result.add(f);
        return result;
    }

    public synchronized List<HubFile> getRecentFiles(int limit) {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) {
            if (!f.isHidden) result.add(f);
            if (result.size() >= limit) break;
        }
        return result;
    }

    public synchronized List<HubFile> searchFiles(String query) {
        String q = query.toLowerCase();
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) {
            if (f.isHidden) continue;
            String name = (f.displayName != null ? f.displayName : f.originalFileName);
            if (name != null && name.toLowerCase().contains(q)) { result.add(f); continue; }
            if (f.tags != null) {
                for (String tag : f.tags) if (tag.toLowerCase().contains(q)) { result.add(f); break; }
            }
            if (f.notes != null && f.notes.toLowerCase().contains(q)) result.add(f);
        }
        return result;
    }

    public synchronized void updateFile(HubFile file) {
        file.updatedAt = System.currentTimeMillis();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).id.equals(file.id)) { files.set(i, file); break; }
        }
        saveFiles();
    }

    public synchronized void deleteFile(String id) {
        files.removeIf(f -> f.id.equals(id));
        saveFiles();
    }

    public synchronized List<HubFile> getDuplicateFiles() {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) if (f.isDuplicate) result.add(f);
        return result;
    }

    public synchronized List<HubFile> getFilesForSmartFolder(HubFolder folder) {
        if (!folder.isSmartFolder || folder.smartFolderRules == null) return new ArrayList<>();
        List<HubFile> result = new ArrayList<>();
        try {
            JSONObject rules = new JSONObject(folder.smartFolderRules);
            for (HubFile f : files) {
                if (f.isHidden) continue;
                boolean matches = true;
                if (rules.has("source")) {
                    String src = rules.getString("source");
                    if (f.source == null || !f.source.name().equals(src)) matches = false;
                }
                if (matches && rules.has("fileType")) {
                    String ft = rules.getString("fileType");
                    if (f.fileType == null || !f.fileType.name().equals(ft)) matches = false;
                }
                if (matches && rules.has("minSize")) {
                    long minSize = rules.getLong("minSize");
                    if (f.fileSize < minSize) matches = false;
                }
                if (matches && rules.has("maxAgeDays")) {
                    int maxDays = rules.getInt("maxAgeDays");
                    long cutoff = System.currentTimeMillis() - (long) maxDays * MILLIS_PER_DAY;
                    if (f.importedAt < cutoff) matches = false;
                }
                if (matches && rules.has("unorganized")) {
                    if (f.folderId != null || f.projectId != null) matches = false;
                }
                if (matches) result.add(f);
            }
        } catch (Exception e) { Log.e(TAG, "getFilesForSmartFolder", e); }
        return result;
    }

    // ─── Folder CRUD ──────────────────────────────────────────────────────────

    public synchronized void addFolder(HubFolder folder) {
        folders.add(folder);
        saveFolders();
    }

    public synchronized List<HubFolder> getAllFolders() { return new ArrayList<>(folders); }

    public synchronized List<HubFolder> getSmartFolders() {
        List<HubFolder> result = new ArrayList<>();
        for (HubFolder f : folders) if (f.isSmartFolder) result.add(f);
        return result;
    }

    public synchronized List<HubFolder> getCustomFolders() {
        List<HubFolder> result = new ArrayList<>();
        for (HubFolder f : folders) if (!f.isSmartFolder) result.add(f);
        return result;
    }

    // ─── Project CRUD ─────────────────────────────────────────────────────────

    public synchronized void addProject(HubProject project) {
        projects.add(project);
        saveProjects();
    }

    public synchronized List<HubProject> getAllProjects() { return new ArrayList<>(projects); }

    public synchronized void updateProject(HubProject project) {
        project.updatedAt = System.currentTimeMillis();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).id.equals(project.id)) { projects.set(i, project); break; }
        }
        saveProjects();
    }

    public synchronized void deleteProject(String id) {
        projects.removeIf(p -> p.id.equals(id));
        saveProjects();
    }

    // ─── Inbox ────────────────────────────────────────────────────────────────

    public synchronized void addInboxItem(InboxItem item) {
        // Avoid duplicates by path
        for (InboxItem existing : inboxItems) {
            if (existing.filePath != null && existing.filePath.equals(item.filePath)
                    && existing.status == InboxItem.Status.PENDING) return;
        }
        inboxItems.add(0, item);
        saveInboxItems();
    }

    public synchronized List<InboxItem> getPendingInboxItems() {
        List<InboxItem> result = new ArrayList<>();
        for (InboxItem item : inboxItems) {
            if (item.status == InboxItem.Status.PENDING) result.add(item);
        }
        return result;
    }

    public synchronized int getPendingInboxCount() {
        int count = 0;
        for (InboxItem item : inboxItems) if (item.status == InboxItem.Status.PENDING) count++;
        return count;
    }

    public synchronized void updateInboxItemStatus(String id, InboxItem.Status status) {
        for (InboxItem item : inboxItems) {
            if (item.id.equals(id)) { item.status = status; break; }
        }
        saveInboxItems();
    }

    // ─── Duplicate Groups ─────────────────────────────────────────────────────

    public synchronized void addDuplicateGroup(DuplicateGroup group) {
        duplicateGroups.add(group);
        saveDuplicateGroups();
    }

    public synchronized List<DuplicateGroup> getAllDuplicateGroups() {
        return new ArrayList<>(duplicateGroups);
    }

    public synchronized int getTotalDuplicateCount() {
        int total = 0;
        for (DuplicateGroup g : duplicateGroups) total += g.duplicateCount;
        return total;
    }

    public synchronized long getTotalWastedBytes() {
        long total = 0;
        for (DuplicateGroup g : duplicateGroups) total += g.totalWastedBytes;
        return total;
    }

    // ─── Activity Feed ────────────────────────────────────────────────────────

    public synchronized void logActivity(FileActivity activity) {
        activities.add(0, activity);
        if (activities.size() > MAX_ACTIVITY_ENTRIES) {
            activities.subList(MAX_ACTIVITY_ENTRIES, activities.size()).clear();
        }
        saveActivities();
    }

    public synchronized List<FileActivity> getRecentActivities(int limit) {
        List<FileActivity> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, activities.size()); i++) result.add(activities.get(i));
        return result;
    }

    // ─── Storage Statistics ───────────────────────────────────────────────────

    public long getDeviceTotalBytes() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return stat.getTotalBytes();
        } catch (Exception e) { return 0; }
    }

    public long getDeviceUsedBytes() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            return stat.getTotalBytes() - stat.getAvailableBytes();
        } catch (Exception e) { return 0; }
    }

    public synchronized Map<HubFile.FileType, Long> getStorageBreakdown() {
        Map<HubFile.FileType, Long> map = new HashMap<>();
        for (HubFile.FileType t : HubFile.FileType.values()) map.put(t, 0L);
        for (HubFile f : files) {
            HubFile.FileType type = f.fileType != null ? f.fileType : HubFile.FileType.OTHER;
            map.put(type, map.get(type) + f.fileSize);
        }
        return map;
    }

    public synchronized long getTotalTrackedBytes() {
        long total = 0;
        for (HubFile f : files) total += f.fileSize;
        return total;
    }

    public synchronized int getTotalFileCount() { return files.size(); }
    public synchronized int getTotalFolderCount() { return folders.size(); }

    // ─── File Scanning ────────────────────────────────────────────────────────

    public long getLastScanTimestamp() {
        return context.getSharedPreferences(PREFS_FILES, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_SCAN, 0);
    }

    public void setLastScanTimestamp(long ts) {
        context.getSharedPreferences(PREFS_FILES, Context.MODE_PRIVATE)
                .edit().putLong(KEY_LAST_SCAN, ts).apply();
    }

    /**
     * Scans common directories for new files and adds them to the inbox.
     * Runs on a background thread; calls callback on main thread when done.
     */
    public void scanForNewFiles(Runnable onComplete) {
        executor.execute(() -> {
            long lastScan = getLastScanTimestamp();
            long now = System.currentTimeMillis();
            List<File> scanDirs = getScanDirectories();
            for (File dir : scanDirs) {
                if (!dir.exists() || !dir.isDirectory()) continue;
                scanDirectory(dir, lastScan, getSourceForDirectory(dir));
            }
            setLastScanTimestamp(now);
            if (onComplete != null) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(onComplete);
            }
        });
    }

    private void scanDirectory(File dir, long since, HubFile.Source source) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, since, source);
            } else if (file.lastModified() > since && file.length() > 0) {
                createInboxItemForFile(file, source);
            }
        }
    }

    private void createInboxItemForFile(File file, HubFile.Source source) {
        InboxItem item = new InboxItem();
        item.filePath = file.getAbsolutePath();
        item.fileName = file.getName();
        item.fileSize = file.length();
        item.source = source;
        item.detectedAt = System.currentTimeMillis();

        String ext = getExtension(file.getName());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
        item.mimeType = mime != null ? mime : "application/octet-stream";
        item.fileType = HubFile.fileTypeFromMime(item.mimeType, ext);

        // Auto-categorization confidence based on source and type
        item.autoCategorizationConfidence = computeConfidence(source, item.fileType);
        addInboxItem(item);
    }

    private int computeConfidence(HubFile.Source source, HubFile.FileType type) {
        if (source == HubFile.Source.WHATSAPP) return 90;
        if (source == HubFile.Source.SCREENSHOTS) return 95;
        if (source == HubFile.Source.CAMERA) return 85;
        if (source == HubFile.Source.DOWNLOADS) return 70;
        return 50;
    }

    private List<File> getScanDirectories() {
        List<File> dirs = new ArrayList<>();
        File extStorage = Environment.getExternalStorageDirectory();
        dirs.add(new File(extStorage, "WhatsApp/Media/WhatsApp Images"));
        dirs.add(new File(extStorage, "WhatsApp/Media/WhatsApp Documents"));
        dirs.add(new File(extStorage, "WhatsApp/Media/WhatsApp Video"));
        dirs.add(new File(extStorage, "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images"));
        dirs.add(new File(extStorage, "Download"));
        dirs.add(new File(extStorage, "Downloads"));
        dirs.add(new File(extStorage, "Pictures/Screenshots"));
        dirs.add(new File(extStorage, "DCIM/Camera"));
        dirs.add(new File(extStorage, "DCIM"));
        dirs.add(new File(extStorage, "Pictures"));
        return dirs;
    }

    private HubFile.Source getSourceForDirectory(File dir) {
        String path = dir.getAbsolutePath().toLowerCase();
        if (path.contains("whatsapp")) return HubFile.Source.WHATSAPP;
        if (path.contains("screenshot")) return HubFile.Source.SCREENSHOTS;
        if (path.contains("camera") || path.contains("dcim")) return HubFile.Source.CAMERA;
        if (path.contains("download")) return HubFile.Source.DOWNLOADS;
        if (path.contains("picture") || path.contains("gallery")) return HubFile.Source.GALLERY;
        return HubFile.Source.INTERNAL;
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot > 0 && dot < fileName.length() - 1) ? fileName.substring(dot + 1) : "";
    }

    // ─── Duplicate Detection ──────────────────────────────────────────────────

    /**
     * Runs MD5-based duplicate detection on all tracked files in the background.
     */
    public void detectDuplicates(Runnable onComplete) {
        executor.execute(() -> {
            Map<String, List<HubFile>> hashMap = new HashMap<>();
            List<HubFile> allFiles;
            synchronized (this) { allFiles = new ArrayList<>(files); }

            for (HubFile f : allFiles) {
                if (f.filePath == null || f.filePath.isEmpty()) continue;
                File file = new File(f.filePath);
                if (!file.exists()) continue;
                String hash = computeMD5(file);
                if (hash == null) continue;
                if (!hashMap.containsKey(hash)) hashMap.put(hash, new ArrayList<>());
                hashMap.get(hash).add(f);
            }

            synchronized (this) {
                duplicateGroups.clear();
                for (HubFile f : files) f.isDuplicate = false;
            }

            for (Map.Entry<String, List<HubFile>> entry : hashMap.entrySet()) {
                List<HubFile> group = entry.getValue();
                if (group.size() > 1) {
                    DuplicateGroup dg = new DuplicateGroup();
                    dg.fileHash = entry.getKey();
                    dg.fileSize = group.get(0).fileSize;
                    dg.fileName = group.get(0).originalFileName;
                    dg.duplicateCount = group.size();
                    dg.totalWastedBytes = (group.size() - 1) * dg.fileSize;
                    synchronized (this) {
                        duplicateGroups.add(dg);
                        for (HubFile f : group) {
                            f.isDuplicate = true;
                            f.duplicateGroupId = dg.id;
                        }
                    }
                }
            }
            saveFiles();
            saveDuplicateGroups();

            if (onComplete != null) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
            }
        });
    }

    private String computeMD5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = fis.read(buffer)) != -1) digest.update(buffer, 0, read);
            }
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // ─── Quick Share ──────────────────────────────────────────────────────────

    private static final String PREFS_SETTINGS = "hub_settings";
    private final List<String> quickSharePins = new ArrayList<>();
    private static final String KEY_QUICK_SHARE = "quick_share_pins";

    private void loadQuickSharePins() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_QUICK_SHARE, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) quickSharePins.add(arr.getString(i));
        } catch (Exception e) {}
    }

    public synchronized List<String> getQuickSharePins() { return new ArrayList<>(quickSharePins); }

    public synchronized void addQuickSharePin(String fileId) {
        int maxPins = context.getSharedPreferences("hub_settings", Context.MODE_PRIVATE)
                .getInt("max_pins", 10);
        if (!quickSharePins.contains(fileId) && quickSharePins.size() < maxPins) {
            quickSharePins.add(fileId);
            saveQuickSharePins();
        }
    }

    public synchronized void removeQuickSharePin(String fileId) {
        quickSharePins.remove(fileId);
        saveQuickSharePins();
    }

    private void saveQuickSharePins() {
        executor.execute(() -> {
            try {
                JSONArray arr = new JSONArray();
                synchronized (quickSharePins) { for (String id : quickSharePins) arr.put(id); }
                context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
                        .edit().putString(KEY_QUICK_SHARE, arr.toString()).apply();
            } catch (Exception e) {}
        });
    }

    // ─── Access Tracking ──────────────────────────────────────────────────────

    public synchronized void recordFileAccess(String fileId) {
        HubFile f = getFileById(fileId);
        if (f != null) {
            f.lastAccessedAt = System.currentTimeMillis();
            f.accessCount = Math.max(0, f.accessCount) + 1;
            updateFile(f);
        }
    }

    // ─── Widget Support API ───────────────────────────────────────────────────

    public synchronized List<HubFile> getRecentlyAccessedFiles(int limit) {
        List<HubFile> sorted = new ArrayList<>(files);
        sorted.sort((a, b) -> Long.compare(b.lastAccessedAt, a.lastAccessedAt));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    // ─── Storage Health Score ─────────────────────────────────────────────────

    public synchronized int computeStorageHealthScore() {
        int score = 100;
        int total = files.size();
        if (total == 0) return 80;
        int organized = 0;
        for (HubFile f : files) if (f.folderId != null || f.projectId != null) organized++;
        float organizedPct = (float) organized / total;
        score -= (int) ((1 - organizedPct) * 25);
        int dupeCount = getTotalDuplicateCount();
        if (dupeCount > 0) score -= Math.min(20, dupeCount / 2);
        int inboxCount = getPendingInboxCount();
        if (inboxCount > 5) score -= Math.min(15, inboxCount / 3);
        return Math.max(0, Math.min(100, score));
    }

    // ─── Backup Readiness ─────────────────────────────────────────────────────

    public synchronized List<HubFile> getFilesWithNoBackup() {
        List<HubFile> result = new ArrayList<>();
        for (HubFile f : files) {
            if (f.source == HubFile.Source.MANUAL || f.source == HubFile.Source.INTERNAL) {
                result.add(f);
            }
        }
        return result;
    }

    // ─── Largest Files ────────────────────────────────────────────────────────

    public synchronized List<HubFile> getLargestFiles(int limit) {
        List<HubFile> sorted = new ArrayList<>(files);
        sorted.sort((a, b) -> Long.compare(b.fileSize, a.fileSize));
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }
}
