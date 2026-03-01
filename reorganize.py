#!/usr/bin/env python3
"""
Reorganize com.prajwal.myfirstapp into feature-based subpackages.

This script:
1. Moves Java files into subpackage directories
2. Updates package declarations
3. Adds cross-package import statements
4. Updates AndroidManifest.xml component references
5. Updates layout XMLs with custom view package paths
"""

import os
import re
import shutil
from pathlib import Path
from collections import defaultdict

# ===== CONFIGURATION =====
WORKSPACE_JAVA = Path(r"D:\Study\Projects\Small Project\mobile_controller\MainActivity\myfirstapp")
WORKSPACE_MANIFEST = Path(r"D:\Study\Projects\Small Project\mobile_controller\MainActivity\manifests\AndroidManifest.xml")
WORKSPACE_LAYOUT = Path(r"D:\Study\Projects\Small Project\mobile_controller\MainActivity\layout")

STUDIO_JAVA = Path(r"C:\Users\praju\AndroidStudioProjects\MyFirstApp\app\src\main\java\com\prajwal\myfirstapp")
STUDIO_MANIFEST = Path(r"C:\Users\praju\AndroidStudioProjects\MyFirstApp\app\src\main\AndroidManifest.xml")
STUDIO_LAYOUT = Path(r"C:\Users\praju\AndroidStudioProjects\MyFirstApp\app\src\main\res\layout")

BASE_PKG = "com.prajwal.myfirstapp"

# ===== FILE-TO-PACKAGE MAPPING (296 files) =====
MAPPING = {
    # ── core (8) ──
    "ActivityLogEntry.java": "core",
    "ActivityLogRepository.java": "core",
    "BootReceiver.java": "core",
    "KeepAliveService.java": "core",
    "MainActivity.java": "core",
    "MyDeviceAdminReceiver.java": "core",
    "SecurityUtils.java": "core",
    "SettingsActivity.java": "core",

    # ── notes (61) ──
    "AmbientSoundPlayer.java": "notes",
    "BlockEditorAdapter.java": "notes",
    "BlockPickerBottomSheet.java": "notes",
    "ConceptMapActivity.java": "notes",
    "ConceptMapView.java": "notes",
    "ContentBlock.java": "notes",
    "DateTimeDetector.java": "notes",
    "DrawingCanvasView.java": "notes",
    "DrawingNoteActivity.java": "notes",
    "ExportService.java": "notes",
    "Flashcard.java": "notes",
    "FlashcardListActivity.java": "notes",
    "FlashcardManager.java": "notes",
    "FlashcardReviewActivity.java": "notes",
    "Note.java": "notes",
    "NoteContextTracker.java": "notes",
    "NoteDNAGenerator.java": "notes",
    "NoteDNAView.java": "notes",
    "NoteEditorActivity.java": "notes",
    "NoteExportManager.java": "notes",
    "NoteFolder.java": "notes",
    "NoteFolderActivity.java": "notes",
    "NoteFolderRepository.java": "notes",
    "NoteFoldersHomeActivity.java": "notes",
    "NoteHtmlExporter.java": "notes",
    "NoteInsightsActivity.java": "notes",
    "NoteInsightsManager.java": "notes",
    "NoteLockManager.java": "notes",
    "NoteRelationsManager.java": "notes",
    "NoteReminderManager.java": "notes",
    "NoteReminderReceiver.java": "notes",
    "NoteRepository.java": "notes",
    "NotesActivity.java": "notes",
    "NotesAdapter.java": "notes",
    "NotesArchiveActivity.java": "notes",
    "NotesCrossFeatureManager.java": "notes",
    "NoteShareManager.java": "notes",
    "NoteShareViewerActivity.java": "notes",
    "NotesSettings.java": "notes",
    "NotesSettingsActivity.java": "notes",
    "NotesTrashActivity.java": "notes",
    "NoteTemplatesManager.java": "notes",
    "NoteVersionManager.java": "notes",
    "NoteWidgetDataProvider.java": "notes",
    "PersonDetailActivity.java": "notes",
    "QuizActivity.java": "notes",
    "QuizGenerator.java": "notes",
    "QuizQuestion.java": "notes",
    "QuizResultsActivity.java": "notes",
    "QuizResultsManager.java": "notes",
    "SmartFeaturesHelper.java": "notes",
    "SmartNotesHelper.java": "notes",
    "SmartWritingAssistant.java": "notes",
    "StudyDashboardActivity.java": "notes",
    "StudyProgressActivity.java": "notes",
    "StudyProgressTracker.java": "notes",
    "StudySessionActivity.java": "notes",
    "StudySessionManager.java": "notes",
    "TagsManagerActivity.java": "notes",
    "WeatherContextProvider.java": "notes",
    "WritingAssistantBottomSheet.java": "notes",

    # ── calendar (13) ──
    "CalendarActivity.java": "calendar",
    "CalendarAnalyticsActivity.java": "calendar",
    "CalendarCategoriesActivity.java": "calendar",
    "CalendarDashboardActivity.java": "calendar",
    "CalendarEvent.java": "calendar",
    "CalendarEventDetailActivity.java": "calendar",
    "CalendarNotificationHelper.java": "calendar",
    "CalendarReminderReceiver.java": "calendar",
    "CalendarRepository.java": "calendar",
    "CalendarSearchActivity.java": "calendar",
    "CalendarSettings.java": "calendar",
    "CalendarSettingsActivity.java": "calendar",
    "EventCategory.java": "calendar",

    # ── meetings (13) ──
    "ActionItem.java": "meetings",
    "AgendaItem.java": "meetings",
    "Attendee.java": "meetings",
    "CreateMeetingActivity.java": "meetings",
    "Meeting.java": "meetings",
    "MeetingAnalyticsActivity.java": "meetings",
    "MeetingDetailActivity.java": "meetings",
    "MeetingNotificationHelper.java": "meetings",
    "MeetingReminderReceiver.java": "meetings",
    "MeetingRepository.java": "meetings",
    "MeetingSearchActivity.java": "meetings",
    "MeetingsListActivity.java": "meetings",
    "MeetingTemplatesManager.java": "meetings",

    # ── tasks (27) ──
    "CompletionRingView.java": "tasks",
    "FocusModeActivity.java": "tasks",
    "KanbanBoardActivity.java": "tasks",
    "QuickAddTaskSheet.java": "tasks",
    "SubTask.java": "tasks",
    "SubtaskItem.java": "tasks",
    "Task.java": "tasks",
    "TaskAdapter.java": "tasks",
    "TaskAlarmHelper.java": "tasks",
    "TaskCategoriesActivity.java": "tasks",
    "TaskCategory.java": "tasks",
    "TaskDetailActivity.java": "tasks",
    "TaskEditorSheet.java": "tasks",
    "TaskExportManager.java": "tasks",
    "TaskImportActivity.java": "tasks",
    "TaskManagerActivity.java": "tasks",
    "TaskManagerSettings.java": "tasks",
    "TaskManagerSettingsActivity.java": "tasks",
    "TaskNotificationHelper.java": "tasks",
    "TaskReminderReceiver.java": "tasks",
    "TaskRepository.java": "tasks",
    "TaskSearchActivity.java": "tasks",
    "TaskStatsActivity.java": "tasks",
    "TaskTemplatesActivity.java": "tasks",
    "TaskTemplatesManager.java": "tasks",
    "TaskTrashActivity.java": "tasks",
    "TimeBlockActivity.java": "tasks",

    # ── todo (13) ──
    "ChecklistAdapter.java": "todo",
    "CreateTodoListSheet.java": "todo",
    "TimerSession.java": "todo",
    "TodoItem.java": "todo",
    "TodoItemAdapter.java": "todo",
    "TodoItemDetailActivity.java": "todo",
    "TodoItemEditorSheet.java": "todo",
    "TodoList.java": "todo",
    "TodoListAdapter.java": "todo",
    "TodoListDetailActivity.java": "todo",
    "TodoNotificationHelper.java": "todo",
    "TodoReminderReceiver.java": "todo",
    "TodoRepository.java": "todo",

    # ── expenses (53) ──
    "BorrowLendActivity.java": "expenses",
    "BudgetComparisonChartView.java": "expenses",
    "BudgetGoalsActivity.java": "expenses",
    "BudgetHealthRingView.java": "expenses",
    "BudgetHistory.java": "expenses",
    "CashFlowMiniBarView.java": "expenses",
    "CategoryBudget.java": "expenses",
    "CategoryBudgetRepository.java": "expenses",
    "Expense.java": "expenses",
    "ExpenseChartView.java": "expenses",
    "ExpenseDonutView.java": "expenses",
    "ExpenseLineChartView.java": "expenses",
    "ExpenseMigrationManager.java": "expenses",
    "ExpenseNotificationHelper.java": "expenses",
    "ExpenseRepository.java": "expenses",
    "ExpenseTrackerActivity.java": "expenses",
    "FinancialHealthRingView.java": "expenses",
    "GroupDetailActivity.java": "expenses",
    "Income.java": "expenses",
    "IncomeCategory.java": "expenses",
    "IncomeCategoryRepository.java": "expenses",
    "IncomeRepository.java": "expenses",
    "IncomeTrackerActivity.java": "expenses",
    "IouNotificationHelper.java": "expenses",
    "MemberSplit.java": "expenses",
    "MoneyRecord.java": "expenses",
    "MoneyRecordDetailActivity.java": "expenses",
    "MoneyRecordRepository.java": "expenses",
    "NetWorthBarChartView.java": "expenses",
    "NetWorthCalculationService.java": "expenses",
    "NetWorthDashboardActivity.java": "expenses",
    "NetWorthLineChartView.java": "expenses",
    "NetWorthRepository.java": "expenses",
    "NetWorthSnapshot.java": "expenses",
    "RecurringExpense.java": "expenses",
    "RecurringExpenseRepository.java": "expenses",
    "RecurringIncome.java": "expenses",
    "RecurringIncomeRepository.java": "expenses",
    "Repayment.java": "expenses",
    "SavingsGaugeView.java": "expenses",
    "Settlement.java": "expenses",
    "SplitExpense.java": "expenses",
    "SplitGroup.java": "expenses",
    "SplitGroupsActivity.java": "expenses",
    "SplitMember.java": "expenses",
    "SplitRepository.java": "expenses",
    "SubscriptionsActivity.java": "expenses",
    "SubscriptionTrendChartView.java": "expenses",
    "Wallet.java": "expenses",
    "WalletDetailActivity.java": "expenses",
    "WalletRepository.java": "expenses",
    "WalletsActivity.java": "expenses",
    "WalletTransfer.java": "expenses",

    # ── vault (31) ──
    "MediaVaultCrypto.java": "vault",
    "MediaVaultRepository.java": "vault",
    "SecurityScoreView.java": "vault",
    "VaultActivityLog.java": "vault",
    "VaultActivityLogActivity.java": "vault",
    "VaultAlbum.java": "vault",
    "VaultAlbumAdapter.java": "vault",
    "VaultAlbumDetailActivity.java": "vault",
    "VaultAudioPlayerActivity.java": "vault",
    "VaultAutoDestroyManager.java": "vault",
    "VaultBackupManager.java": "vault",
    "VaultCollection.java": "vault",
    "VaultCollectionActivity.java": "vault",
    "VaultCryptoManager.java": "vault",
    "VaultDocumentViewerActivity.java": "vault",
    "VaultFileBrowserActivity.java": "vault",
    "VaultFileGridAdapter.java": "vault",
    "VaultFileItem.java": "vault",
    "VaultHealthScoreHelper.java": "vault",
    "VaultHomeActivity.java": "vault",
    "VaultImageEditorActivity.java": "vault",
    "VaultImageViewerActivity.java": "vault",
    "VaultImportActivity.java": "vault",
    "VaultPrivacyScannerActivity.java": "vault",
    "VaultSearchActivity.java": "vault",
    "VaultSettingsActivity.java": "vault",
    "VaultSmartOrganizeActivity.java": "vault",
    "VaultUnlockActivity.java": "vault",
    "VaultVideoPlayerActivity.java": "vault",
    "VaultWatermarkHelper.java": "vault",
    "VaultWaveformView.java": "vault",

    # ── hub (54) ──
    "DuplicateGroup.java": "hub",
    "FileActivity.java": "hub",
    "FileManagerActivity.java": "hub",
    "HubAnalyticsActivity.java": "hub",
    "HubAnomalyDetector.java": "hub",
    "HubAuditLogActivity.java": "hub",
    "HubAutoCategorizeEngine.java": "hub",
    "HubBatchOperationsActivity.java": "hub",
    "HubCollection.java": "hub",
    "HubCollectionsActivity.java": "hub",
    "HubCommandPaletteActivity.java": "hub",
    "HubContentIndexer.java": "hub",
    "HubDuplicateManagerActivity.java": "hub",
    "HubExpiryCalendarActivity.java": "hub",
    "HubFile.java": "hub",
    "HubFileBrowserActivity.java": "hub",
    "HubFileDnaView.java": "hub",
    "HubFileExpiryManager.java": "hub",
    "HubFileRepository.java": "hub",
    "HubFileTimelineActivity.java": "hub",
    "HubFileViewerActivity.java": "hub",
    "HubFocusModeActivity.java": "hub",
    "HubFolder.java": "hub",
    "HubHeatmapView.java": "hub",
    "HubInboxActivity.java": "hub",
    "HubIntegrityManager.java": "hub",
    "HubLineChartView.java": "hub",
    "HubPackageAndSendActivity.java": "hub",
    "HubPredictiveEngine.java": "hub",
    "HubPrivacyAnalyzerActivity.java": "hub",
    "HubProject.java": "hub",
    "HubProjectDetailActivity.java": "hub",
    "HubProjectsActivity.java": "hub",
    "HubQuickShareActivity.java": "hub",
    "HubRelationshipEngine.java": "hub",
    "HubSearchActivity.java": "hub",
    "HubSettingsActivity.java": "hub",
    "HubShareHistoryActivity.java": "hub",
    "HubShareProfilesActivity.java": "hub",
    "HubSmartFolderBuilderActivity.java": "hub",
    "HubStealthModeActivity.java": "hub",
    "HubStorageIntelligenceActivity.java": "hub",
    "HubStoryModeActivity.java": "hub",
    "HubTimeCapsuleActivity.java": "hub",
    "HubVersionChain.java": "hub",
    "HubVersionHistoryActivity.java": "hub",
    "HubVersionManager.java": "hub",
    "HubWatchlistActivity.java": "hub",
    "HubWeeklyReportReceiver.java": "hub",
    "InboxItem.java": "hub",
    "LaptopHubActivity.java": "hub",
    "SmartFileHubActivity.java": "hub",
    "StorageArcView.java": "hub",
    "StorageFileBrowserActivity.java": "hub",

    # ── chat (4) ──
    "ChatActivity.java": "chat",
    "ChatAdapter.java": "chat",
    "ChatMessage.java": "chat",
    "ChatRepository.java": "chat",

    # ── passwords (3) ──
    "PasswordEntry.java": "passwords",
    "PasswordManagerActivity.java": "passwords",
    "PasswordRepository.java": "passwords",

    # ── connectivity (12) ──
    "BackgroundServices.java": "connectivity",
    "BluetoothFileHelper.java": "connectivity",
    "CameraStreamService.java": "connectivity",
    "ConnectionManager.java": "connectivity",
    "DynamicBarService.java": "connectivity",
    "NotificationForwarder.java": "connectivity",
    "NotifMirrorService.java": "connectivity",
    "QRPairingManager.java": "connectivity",
    "ReverseCommandListener.java": "connectivity",
    "SensorHandler.java": "connectivity",
    "SyncOutbox.java": "connectivity",
    "TouchpadHandler.java": "connectivity",

    # ── timecapsule (3) ──
    "TimeCapsuleActivity.java": "timecapsule",
    "TimeCapsuleManager.java": "timecapsule",
    "TimeCapsuleNotificationReceiver.java": "timecapsule",

    # ── widgets (1) ──
    "IdeaCaptureWidgetProvider.java": "widgets",
}

# ===== HELPER FUNCTIONS =====

def strip_for_analysis(content):
    """Remove comments and string literals for clean class reference scanning."""
    # Remove multi-line comments
    content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    # Remove single-line comments
    content = re.sub(r'//.*?$', '', content, flags=re.MULTILINE)
    # Remove string literals (but keep the quotes so line structure is preserved)
    content = re.sub(r'"(?:[^"\\]|\\.)*"', '""', content)
    return content


def insert_imports(content, new_imports):
    """Insert import statements after the package declaration, before existing imports."""
    existing = set(re.findall(r'^import [^;]+;', content, re.MULTILINE))
    to_add = sorted(new_imports - existing)

    if not to_add:
        return content

    # Find position after package line
    m = re.search(r'^package [^;]+;\s*\n', content, re.MULTILINE)
    if not m:
        return content

    pos = m.end()
    import_block = '\n'.join(to_add) + '\n'
    content = content[:pos] + '\n' + import_block + content[pos:]
    return content


# ===== PHASE 1: REORGANIZE JAVA FILES =====

def reorganize_java(java_dir):
    """Move Java files into subpackage dirs, update package declarations, add imports."""
    print(f"  Processing: {java_dir}")

    # Build class-name -> package map (only for files that exist)
    cls_pkg = {}
    existing_files = {}
    for filename, pkg in MAPPING.items():
        src = java_dir / filename
        if src.exists():
            cls = filename.replace('.java', '')
            cls_pkg[cls] = pkg
            existing_files[filename] = pkg
        else:
            print(f"    SKIP (not found): {filename}")

    # Create subdirectories
    for pkg in set(MAPPING.values()):
        (java_dir / pkg).mkdir(exist_ok=True)

    # Move files and update package declarations
    moved = 0
    for filename, pkg in existing_files.items():
        src = java_dir / filename
        dst = java_dir / pkg / filename

        content = src.read_text(encoding='utf-8')

        # Update package declaration
        old_pkg_decl = f'package {BASE_PKG};'
        new_pkg_decl = f'package {BASE_PKG}.{pkg};'
        content = content.replace(old_pkg_decl, new_pkg_decl, 1)

        dst.write_text(content, encoding='utf-8')
        src.unlink()
        moved += 1

    print(f"    Moved {moved} files")

    # Add cross-package imports
    imports_added = 0
    for pkg in set(MAPPING.values()):
        pkg_dir = java_dir / pkg
        if not pkg_dir.exists():
            continue
        for filepath in pkg_dir.glob('*.java'):
            content = filepath.read_text(encoding='utf-8')
            file_cls = filepath.stem
            file_pkg = MAPPING.get(filepath.name)
            if not file_pkg:
                continue

            # Strip comments/strings for analysis
            analysis = strip_for_analysis(content)

            needed = set()

            # R class import (almost all files use R.layout, R.id, etc.)
            if re.search(r'\bR\.', analysis):
                needed.add(f'import {BASE_PKG}.R;')

            # BuildConfig import
            if re.search(r'\bBuildConfig\b', analysis):
                needed.add(f'import {BASE_PKG}.BuildConfig;')

            # Cross-package class references
            for cls, target_pkg in cls_pkg.items():
                if target_pkg == file_pkg:
                    continue  # Same package
                if cls == file_cls:
                    continue  # Self reference

                # Word-boundary match for class usage
                if re.search(r'\b' + re.escape(cls) + r'\b', analysis):
                    needed.add(f'import {BASE_PKG}.{target_pkg}.{cls};')

            if needed:
                new_content = insert_imports(content, needed)
                if new_content != content:
                    filepath.write_text(new_content, encoding='utf-8')
                    imports_added += 1

    print(f"    Updated imports in {imports_added} files")


# ===== PHASE 2: UPDATE MANIFEST =====

def update_manifest(manifest_path):
    """Update android:name references from .Class to .pkg.Class"""
    if not manifest_path.exists():
        print(f"    SKIP (not found): {manifest_path}")
        return

    content = manifest_path.read_text(encoding='utf-8')
    changes = 0

    for filename, pkg in MAPPING.items():
        cls = filename.replace('.java', '')

        # Update android:name=".ClassName" -> android:name=".pkg.ClassName"
        old = f'android:name=".{cls}"'
        new = f'android:name=".{pkg}.{cls}"'
        if old in content:
            content = content.replace(old, new)
            changes += 1

        # Also handle inner class references like .HubFileExpiryManager$ExpiryReceiver
        # The outer class gets the package prefix
        old_inner = f'android:name=".{cls}$'
        new_inner = f'android:name=".{pkg}.{cls}$'
        if old_inner in content:
            content = content.replace(old_inner, new_inner)
            changes += 1

    manifest_path.write_text(content, encoding='utf-8')
    print(f"    Updated {changes} manifest entries")


# ===== PHASE 3: UPDATE LAYOUT XMLs =====

def update_layouts(layout_dir):
    """Update custom view fully-qualified class references in layout XML files."""
    if not layout_dir.exists():
        print(f"    SKIP (not found): {layout_dir}")
        return

    changes = 0
    for xml_file in layout_dir.glob('*.xml'):
        content = xml_file.read_text(encoding='utf-8')
        modified = False

        for filename, pkg in MAPPING.items():
            cls = filename.replace('.java', '')

            # Update com.prajwal.myfirstapp.ClassName -> com.prajwal.myfirstapp.pkg.ClassName
            old_ref = f'{BASE_PKG}.{cls}'
            new_ref = f'{BASE_PKG}.{pkg}.{cls}'
            if old_ref in content:
                content = content.replace(old_ref, new_ref)
                modified = True

            # Update tools:context=".ClassName" -> tools:context=".pkg.ClassName"
            old_ctx = f'tools:context=".{cls}"'
            new_ctx = f'tools:context=".{pkg}.{cls}"'
            if old_ctx in content:
                content = content.replace(old_ctx, new_ctx)
                modified = True

        if modified:
            xml_file.write_text(content, encoding='utf-8')
            changes += 1

    print(f"    Updated {changes} layout files")


# ===== MAIN =====

def main():
    # Verify total
    total = len(MAPPING)
    pkgs = defaultdict(int)
    for f, p in MAPPING.items():
        pkgs[p] += 1
    print(f"=== Java File Reorganization ===")
    print(f"Total files: {total}")
    print(f"Packages ({len(pkgs)}):")
    for p in sorted(pkgs.keys()):
        print(f"  {p}: {pkgs[p]} files")
    print()

    # Phase 1A: Reorganize workspace Java files
    print("[1/6] Reorganizing workspace Java files...")
    reorganize_java(WORKSPACE_JAVA)
    print()

    # Phase 2: Update workspace manifest
    print("[2/6] Updating workspace manifest...")
    update_manifest(WORKSPACE_MANIFEST)
    print()

    # Phase 3: Update workspace layouts
    print("[3/6] Updating workspace layouts...")
    update_layouts(WORKSPACE_LAYOUT)
    print()

    # Phase 4: Reorganize Android Studio Java files
    print("[4/6] Reorganizing Android Studio Java files...")
    reorganize_java(STUDIO_JAVA)
    print()

    # Phase 5: Update Android Studio manifest
    print("[5/6] Updating Android Studio manifest...")
    update_manifest(STUDIO_MANIFEST)
    print()

    # Phase 6: Update Android Studio layouts
    print("[6/6] Updating Android Studio layouts...")
    update_layouts(STUDIO_LAYOUT)
    print()

    print("=== Reorganization Complete ===")
    print(f"Moved {total} files into {len(pkgs)} subpackages")
    print()
    print("Next steps:")
    print("  1. Open Android Studio project")
    print("  2. Build > Rebuild Project")
    print("  3. Fix any remaining import errors")


if __name__ == '__main__':
    main()
