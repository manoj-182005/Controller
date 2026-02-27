package com.prajwal.myfirstapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data model for a file tracked by the Smart File Hub.
 */
public class HubFile {

    public enum FileType {
        PDF, DOCUMENT, IMAGE, SCREENSHOT, VIDEO, AUDIO,
        CODE, ARCHIVE, SPREADSHEET, PRESENTATION, OTHER
    }

    public enum Source {
        WHATSAPP, DOWNLOADS, GALLERY, SCREENSHOTS, CAMERA, INTERNAL, MANUAL, OTHER
    }

    public enum ColorLabel {
        NONE, RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE
    }

    public String id;
    public String originalFileName;
    public String displayName;
    public String fileExtension;
    public String mimeType;
    public FileType fileType;
    public String filePath;
    public long fileSize;
    public String thumbnailPath;
    public Source source;
    public String sourcePath;
    public String projectId;
    public String folderId;
    public List<String> tags;
    public boolean isFavourited;
    public boolean isPinned;
    public boolean isHidden;
    public boolean isDuplicate;
    public String duplicateGroupId;
    public FileType autoCategory;
    public FileType userConfirmedCategory;
    public ColorLabel colorLabel;
    public long lastAccessedAt;
    public long importedAt;
    public long originalCreatedAt;
    public long originalModifiedAt;
    public String notes;
    public long createdAt;
    public long updatedAt;

    public HubFile() {
        this.id = UUID.randomUUID().toString();
        this.tags = new ArrayList<>();
        this.isFavourited = false;
        this.isPinned = false;
        this.isHidden = false;
        this.isDuplicate = false;
        this.colorLabel = ColorLabel.NONE;
        long now = System.currentTimeMillis();
        this.importedAt = now;
        this.lastAccessedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static FileType fileTypeFromMime(String mime, String ext) {
        if (mime == null) mime = "";
        if (ext == null) ext = "";
        mime = mime.toLowerCase();
        ext = ext.toLowerCase();
        if (mime.equals("application/pdf") || ext.equals("pdf")) return FileType.PDF;
        if (mime.startsWith("image/")) {
            if (ext.equals("screenshot") || mime.contains("screenshot")) return FileType.SCREENSHOT;
            return FileType.IMAGE;
        }
        if (mime.startsWith("video/")) return FileType.VIDEO;
        if (mime.startsWith("audio/")) return FileType.AUDIO;
        if (mime.contains("zip") || mime.contains("rar") || mime.contains("7z") || mime.contains("tar")
                || ext.equals("zip") || ext.equals("rar") || ext.equals("7z") || ext.equals("tar") || ext.equals("gz"))
            return FileType.ARCHIVE;
        if (mime.contains("spreadsheet") || mime.contains("excel") || ext.equals("xlsx") || ext.equals("xls") || ext.equals("csv"))
            return FileType.SPREADSHEET;
        if (mime.contains("presentation") || mime.contains("powerpoint") || ext.equals("pptx") || ext.equals("ppt"))
            return FileType.PRESENTATION;
        if (ext.equals("java") || ext.equals("py") || ext.equals("js") || ext.equals("ts") || ext.equals("kt")
                || ext.equals("cpp") || ext.equals("c") || ext.equals("h") || ext.equals("cs") || ext.equals("go")
                || ext.equals("rs") || ext.equals("rb") || ext.equals("php") || ext.equals("swift") || ext.equals("dart"))
            return FileType.CODE;
        if (mime.contains("word") || mime.contains("text") || mime.contains("document")
                || ext.equals("doc") || ext.equals("docx") || ext.equals("txt") || ext.equals("rtf") || ext.equals("odt"))
            return FileType.DOCUMENT;
        return FileType.OTHER;
    }

    public String getFormattedSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024L * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        return String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024));
    }

    public String getTypeEmoji() {
        if (fileType == null) return "📄";
        switch (fileType) {
            case PDF: return "📕";
            case DOCUMENT: return "📝";
            case IMAGE: return "🖼️";
            case SCREENSHOT: return "📸";
            case VIDEO: return "🎬";
            case AUDIO: return "🎵";
            case CODE: return "💻";
            case ARCHIVE: return "🗜️";
            case SPREADSHEET: return "📊";
            case PRESENTATION: return "📽️";
            default: return "📄";
        }
    }

    public JSONObject toJson() {
        try {
            JSONObject o = new JSONObject();
            o.put("id", id != null ? id : "");
            o.put("originalFileName", originalFileName != null ? originalFileName : "");
            o.put("displayName", displayName != null ? displayName : "");
            o.put("fileExtension", fileExtension != null ? fileExtension : "");
            o.put("mimeType", mimeType != null ? mimeType : "");
            o.put("fileType", fileType != null ? fileType.name() : FileType.OTHER.name());
            o.put("filePath", filePath != null ? filePath : "");
            o.put("fileSize", fileSize);
            o.put("thumbnailPath", thumbnailPath != null ? thumbnailPath : "");
            o.put("source", source != null ? source.name() : Source.OTHER.name());
            o.put("sourcePath", sourcePath != null ? sourcePath : "");
            o.put("projectId", projectId != null ? projectId : "");
            o.put("folderId", folderId != null ? folderId : "");
            JSONArray tagsArr = new JSONArray();
            if (tags != null) for (String t : tags) tagsArr.put(t);
            o.put("tags", tagsArr);
            o.put("isFavourited", isFavourited);
            o.put("isPinned", isPinned);
            o.put("isHidden", isHidden);
            o.put("isDuplicate", isDuplicate);
            o.put("duplicateGroupId", duplicateGroupId != null ? duplicateGroupId : "");
            o.put("autoCategory", autoCategory != null ? autoCategory.name() : "");
            o.put("userConfirmedCategory", userConfirmedCategory != null ? userConfirmedCategory.name() : "");
            o.put("colorLabel", colorLabel != null ? colorLabel.name() : ColorLabel.NONE.name());
            o.put("lastAccessedAt", lastAccessedAt);
            o.put("importedAt", importedAt);
            o.put("originalCreatedAt", originalCreatedAt);
            o.put("originalModifiedAt", originalModifiedAt);
            o.put("notes", notes != null ? notes : "");
            o.put("createdAt", createdAt);
            o.put("updatedAt", updatedAt);
            return o;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static HubFile fromJson(JSONObject o) {
        if (o == null) return null;
        try {
            HubFile f = new HubFile();
            f.id = o.optString("id", UUID.randomUUID().toString());
            f.originalFileName = o.optString("originalFileName", "");
            f.displayName = o.optString("displayName", "");
            f.fileExtension = o.optString("fileExtension", "");
            f.mimeType = o.optString("mimeType", "");
            f.fileType = parseFileType(o.optString("fileType", "OTHER"));
            f.filePath = o.optString("filePath", "");
            f.fileSize = o.optLong("fileSize", 0);
            f.thumbnailPath = o.optString("thumbnailPath", "");
            f.source = parseSource(o.optString("source", "OTHER"));
            f.sourcePath = o.optString("sourcePath", "");
            f.projectId = o.optString("projectId", "");
            if (f.projectId.isEmpty()) f.projectId = null;
            f.folderId = o.optString("folderId", "");
            if (f.folderId.isEmpty()) f.folderId = null;
            f.tags = new ArrayList<>();
            JSONArray tagsArr = o.optJSONArray("tags");
            if (tagsArr != null) {
                for (int i = 0; i < tagsArr.length(); i++) f.tags.add(tagsArr.getString(i));
            }
            f.isFavourited = o.optBoolean("isFavourited", false);
            f.isPinned = o.optBoolean("isPinned", false);
            f.isHidden = o.optBoolean("isHidden", false);
            f.isDuplicate = o.optBoolean("isDuplicate", false);
            f.duplicateGroupId = o.optString("duplicateGroupId", "");
            if (f.duplicateGroupId.isEmpty()) f.duplicateGroupId = null;
            String ac = o.optString("autoCategory", "");
            f.autoCategory = ac.isEmpty() ? null : parseFileType(ac);
            String uc = o.optString("userConfirmedCategory", "");
            f.userConfirmedCategory = uc.isEmpty() ? null : parseFileType(uc);
            f.colorLabel = parseColorLabel(o.optString("colorLabel", "NONE"));
            f.lastAccessedAt = o.optLong("lastAccessedAt", System.currentTimeMillis());
            f.importedAt = o.optLong("importedAt", System.currentTimeMillis());
            f.originalCreatedAt = o.optLong("originalCreatedAt", 0);
            f.originalModifiedAt = o.optLong("originalModifiedAt", 0);
            f.notes = o.optString("notes", "");
            f.createdAt = o.optLong("createdAt", System.currentTimeMillis());
            f.updatedAt = o.optLong("updatedAt", System.currentTimeMillis());
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    private static FileType parseFileType(String s) {
        if (s == null || s.isEmpty()) return FileType.OTHER;
        try { return FileType.valueOf(s); } catch (Exception e) { return FileType.OTHER; }
    }

    private static Source parseSource(String s) {
        if (s == null || s.isEmpty()) return Source.OTHER;
        try { return Source.valueOf(s); } catch (Exception e) { return Source.OTHER; }
    }

    private static ColorLabel parseColorLabel(String s) {
        if (s == null || s.isEmpty()) return ColorLabel.NONE;
        try { return ColorLabel.valueOf(s); } catch (Exception e) { return ColorLabel.NONE; }
    }
}
