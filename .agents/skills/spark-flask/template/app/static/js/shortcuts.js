/**
 * Google Shortcuts Engine for Spark! Flask Applications
 * Implements Google Calendar & Tasks keyboard interaction models
 */

document.addEventListener("DOMContentLoaded", () => {
  document.addEventListener("keydown", (e) => {
    // Ignore keyboard shortcuts if the user is typing in an input, textarea, or contenteditable
    const activeTag = document.activeElement ? document.activeElement.tagName.toLowerCase() : "";
    const isEditing = activeTag === "input" || activeTag === "textarea" || activeTag === "select" || (document.activeElement && document.activeElement.isContentEditable);

    if (isEditing) {
      if (e.key === "Escape") {
        document.activeElement.blur();
      }
      return;
    }

    // Google shortcut 'c': trigger Create Task
    if (e.key === "c" || e.key === "C") {
      e.preventDefault();
      const createButton = document.querySelector(".m3-extended-fab, .m3-fab-compact, .mobile-create-btn");
      if (createButton) {
        createButton.click();
      }
    }

    // Google shortcut '/': focus Search bar
    if (e.key === "/") {
      e.preventDefault();
      const searchInput = document.getElementById("global-search-input");
      if (searchInput) {
        searchInput.focus();
        searchInput.select();
      }
    }

    // Google shortcut '?': Open keyboard shortcuts dialog
    if (e.key === "?") {
      e.preventDefault();
      openShortcutsModal();
    }

    // Escape: close open dialogs
    if (e.key === "Escape") {
      const shortcutsDialog = document.getElementById("shortcuts-dialog");
      if (shortcutsDialog && shortcutsDialog.open) {
        shortcutsDialog.close();
      }
    }
  });
});

window.openShortcutsModal = function () {
  const dialog = document.getElementById("shortcuts-dialog");
  if (dialog) {
    dialog.showModal();
  }
};

