package com.prajwal.myfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════
 *  NOTE TEMPLATES MANAGER — Built-in and custom note templates
 * ═══════════════════════════════════════════════════════════════════════════════════════
 *
 * Features:
 * - 7 built-in templates (Meeting, Journal, Study, Project, Book, Recipe, Blank)
 * - Custom template creation, retrieval, and deletion via SharedPreferences
 * - Applies current date when inserting a template
 */
public class NoteTemplatesManager {

    private static final String TAG = "NoteTemplatesManager";
    private static final String PREFS_NAME = "note_templates_prefs";
    private static final String CUSTOM_TEMPLATES_KEY = "custom_templates";

    private final SharedPreferences prefs;

    // ═══════════════════════════════════════════════════════════════════════════════
    //  TEMPLATE DATA CLASS
    // ═══════════════════════════════════════════════════════════════════════════════

    public static class NoteTemplate {
        public String id;
        public String name;
        public String body; // HTML/rich text content
        public boolean isCustom;

        public NoteTemplate(String id, String name, String body, boolean isCustom) {
            this.id = id;
            this.name = name;
            this.body = body;
            this.isCustom = isCustom;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("id", id);
                json.put("name", name);
                json.put("body", body);
                json.put("isCustom", isCustom);
            } catch (JSONException e) {
                Log.e("NoteTemplate", "Error serializing template: " + e.getMessage());
            }
            return json;
        }

        public static NoteTemplate fromJson(JSONObject json) {
            if (json == null) return null;
            try {
                return new NoteTemplate(
                        json.optString("id", UUID.randomUUID().toString().substring(0, 12)),
                        json.optString("name", ""),
                        json.optString("body", ""),
                        json.optBoolean("isCustom", true)
                );
            } catch (Exception e) {
                Log.e("NoteTemplate", "Error deserializing template: " + e.getMessage());
                return null;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════════════

    public NoteTemplatesManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  BUILT-IN TEMPLATES
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Returns the list of built-in templates. "[date]" placeholders are replaced
     * with the actual date when the template is applied via {@link #applyTemplate}.
     */
    public static List<NoteTemplate> getBuiltInTemplates() {
        List<NoteTemplate> templates = new ArrayList<>();

        templates.add(new NoteTemplate(
                "builtin_meeting",
                "Meeting Notes",
                "<b>Meeting Notes</b><br><br>" +
                "<b>Date:</b> [date]<br>" +
                "<b>Attendees:</b><br>" +
                "• <br><br>" +
                "<b>Agenda:</b><br>" +
                "• <br><br>" +
                "<b>Action Items:</b><br>" +
                "• <br><br>" +
                "<b>Decisions:</b><br>" +
                "• ",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_journal",
                "Daily Journal",
                "<b>Daily Journal</b><br><br>" +
                "<b>Date:</b> [date]<br>" +
                "<b>Mood:</b> 😊<br><br>" +
                "<b>Today's thoughts:</b><br>" +
                "<br><br>" +
                "<b>Gratitude:</b><br>" +
                "• <br><br>" +
                "<b>Tomorrow's goals:</b><br>" +
                "• ",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_study",
                "Study Notes",
                "<b>Study Notes</b><br><br>" +
                "<b>Topic:</b> <br><br>" +
                "<b>Key Concepts:</b><br>" +
                "• <br><br>" +
                "<b>Summary:</b><br>" +
                "<br><br>" +
                "<b>Questions to follow up:</b><br>" +
                "• ",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_project",
                "Project Plan",
                "<b>Project Plan</b><br><br>" +
                "<b>Overview:</b><br>" +
                "<br><br>" +
                "<b>Goals:</b><br>" +
                "• <br><br>" +
                "<b>Tasks:</b><br>" +
                "• <br><br>" +
                "<b>Deadline:</b> <br><br>" +
                "<b>Notes:</b><br>" +
                "• ",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_book",
                "Book Notes",
                "<b>Book Notes</b><br><br>" +
                "<b>Title:</b> <br>" +
                "<b>Author:</b> <br><br>" +
                "<b>Key Takeaways:</b><br>" +
                "• <br><br>" +
                "<b>Favourite Quotes:</b><br>" +
                "• <br><br>" +
                "<b>Rating:</b> ⭐⭐⭐⭐⭐",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_recipe",
                "Recipe",
                "<b>Recipe</b><br><br>" +
                "<b>Ingredients:</b><br>" +
                "• <br><br>" +
                "<b>Instructions:</b><br>" +
                "1. <br><br>" +
                "<b>Tips:</b><br>" +
                "• ",
                false
        ));

        templates.add(new NoteTemplate(
                "builtin_blank",
                "Blank",
                "",
                false
        ));

        return templates;
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  PUBLIC API
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Returns all built-in templates followed by all custom templates.
     */
    public List<NoteTemplate> getAllTemplates() {
        List<NoteTemplate> all = new ArrayList<>(getBuiltInTemplates());
        all.addAll(getCustomTemplates());
        return all;
    }

    /**
     * Loads and returns the list of custom templates from SharedPreferences.
     */
    public List<NoteTemplate> getCustomTemplates() {
        List<NoteTemplate> customs = new ArrayList<>();
        String json = prefs.getString(CUSTOM_TEMPLATES_KEY, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                NoteTemplate template = NoteTemplate.fromJson(array.getJSONObject(i));
                if (template != null) customs.add(template);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error loading custom templates: " + e.getMessage());
        }
        return customs;
    }

    /**
     * Creates a new custom template with the given name and body, and persists it.
     */
    public void saveCustomTemplate(String name, String body) {
        String id = "custom_" + UUID.randomUUID().toString().substring(0, 12);
        NoteTemplate newTemplate = new NoteTemplate(id, name, body != null ? body : "", true);

        List<NoteTemplate> customs = getCustomTemplates();
        customs.add(newTemplate);
        persistCustomTemplates(customs);
        Log.d(TAG, "Saved custom template: " + name);
    }

    /**
     * Removes the custom template with the given id from SharedPreferences.
     */
    public void deleteCustomTemplate(String id) {
        List<NoteTemplate> customs = getCustomTemplates();
        boolean removed = false;
        for (int i = 0; i < customs.size(); i++) {
            if (customs.get(i).id.equals(id)) {
                customs.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            persistCustomTemplates(customs);
            Log.d(TAG, "Deleted custom template: " + id);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  TEMPLATE APPLICATION
    // ═══════════════════════════════════════════════════════════════════════════════

    /**
     * Returns the template body with "[date]" replaced by the current date
     * formatted as "MMM dd, yyyy" (e.g., "Dec 15, 2024").
     */
    public static String applyTemplate(NoteTemplate template) {
        if (template == null || template.body == null) return "";
        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(new Date());
        return template.body.replace("[date]", currentDate);
    }

    // ═══════════════════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════════════════

    private void persistCustomTemplates(List<NoteTemplate> customs) {
        JSONArray array = new JSONArray();
        for (NoteTemplate t : customs) array.put(t.toJson());
        prefs.edit().putString(CUSTOM_TEMPLATES_KEY, array.toString()).apply();
    }
}
