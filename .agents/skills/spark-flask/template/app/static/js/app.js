/**
 * Spark! Application Client Engine
 * Theme switching, drawer interactions, snackbar system with undo, and responsive helpers
 */

(function () {
  const STORAGE_KEY = "spark_theme_preference";

  function getPreferredTheme() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) return saved;
    return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  }

  function applyTheme(theme) {
    document.documentElement.setAttribute("data-theme", theme);
    const themeIcon = document.getElementById("theme-icon");
    if (themeIcon) {
      themeIcon.textContent = theme === "dark" ? "light_mode" : "dark_mode";
    }
    const themeSwitch = document.getElementById("theme-switch");
    if (themeSwitch) {
      themeSwitch.checked = theme === "dark";
    }
  }

  window.toggleTheme = function () {
    const current = document.documentElement.getAttribute("data-theme") || getPreferredTheme();
    const next = current === "dark" ? "light" : "dark";
    localStorage.setItem(STORAGE_KEY, next);
    applyTheme(next);
  };

  // Drawer Toggle with .collapsed state
  function initDrawer() {
    const toggleBtn = document.getElementById("drawer-toggle");
    const drawer = document.getElementById("app-nav-drawer");
    if (toggleBtn && drawer) {
      toggleBtn.addEventListener("click", () => {
        drawer.classList.toggle("collapsed");
      });
    }
  }

  // Snackbar with Undo Support
  window.showSnackbar = function (message, actionText, actionCallback) {
    const host = document.getElementById("m3-snackbar-host");
    if (!host) return;

    // Clear existing
    host.innerHTML = "";

    const snackbar = document.createElement("div");
    snackbar.className = "m3-snackbar";
    snackbar.setAttribute("role", "status");

    const msgSpan = document.createElement("span");
    msgSpan.className = "snackbar-message";
    msgSpan.textContent = message;
    snackbar.appendChild(msgSpan);

    if (actionText && actionCallback) {
      const actionBtn = document.createElement("button");
      actionBtn.type = "button";
      actionBtn.className = "m3-text-button snackbar-action";
      actionBtn.textContent = actionText;
      actionBtn.addEventListener("click", () => {
        actionCallback();
        snackbar.remove();
      });
      snackbar.appendChild(actionBtn);
    }

    const closeBtn = document.createElement("button");
    closeBtn.type = "button";
    closeBtn.className = "m3-icon-button snackbar-close";
    closeBtn.setAttribute("aria-label", "Close notification");
    closeBtn.innerHTML = '<span class="material-symbols-rounded">close</span>';
    closeBtn.addEventListener("click", () => snackbar.remove());
    snackbar.appendChild(closeBtn);

    host.appendChild(snackbar);

    // Auto dismiss after 4 seconds
    setTimeout(() => {
      if (snackbar.parentNode) {
        snackbar.classList.add("fade-out");
        setTimeout(() => snackbar.remove(), 250);
      }
    }, 4000);
  };

  // Confirmation Dialog
  let currentConfirmCallback = null;
  window.showConfirmDialog = function (title, message, onConfirm) {
    const dialog = document.getElementById("confirm-dialog");
    const titleEl = document.getElementById("confirm-dialog-title");
    const msgEl = document.getElementById("confirm-dialog-message");
    const actionBtn = document.getElementById("confirm-dialog-action-btn");

    if (dialog && titleEl && msgEl && actionBtn) {
      titleEl.textContent = title || "Confirm Action";
      msgEl.textContent = message || "Are you sure you want to proceed?";
      currentConfirmCallback = onConfirm;

      actionBtn.onclick = () => {
        if (currentConfirmCallback) currentConfirmCallback();
        dialog.close();
      };

      dialog.showModal();
    }
  };

  window.closeConfirmDialog = function () {
    const dialog = document.getElementById("confirm-dialog");
    if (dialog) dialog.close();
  };

  // Mobile detail screen back handler
  window.closeMobileDetail = function () {
    const detailPane = document.getElementById("detail-pane");
    if (detailPane) {
      detailPane.classList.remove("has-selection");
    }
  };

  window.clearDetailPane = function () {
    const detailPane = document.getElementById("detail-pane");
    if (detailPane) {
      detailPane.innerHTML = `
        <div class="m3-pane-placeholder">
          <span class="material-symbols-rounded placeholder-icon">task_alt</span>
          <h3 class="m3-title-medium">Select a task</h3>
          <p class="m3-body-medium">Choose a task from the list or press <kbd class="shortcut-tag">c</kbd> to create one.</p>
        </div>
      `;
      detailPane.classList.remove("has-selection");
    }
  };

  // HTMX Event Listeners for smooth transitions and toasts
  document.addEventListener("htmx:afterSwap", (e) => {
    if (e.detail.target.id === "detail-pane") {
      e.detail.target.classList.add("has-selection");
    }
  });

  document.addEventListener("taskCreated", () => {
    window.showSnackbar("Task created successfully");
  });

  document.addEventListener("taskUpdated", () => {
    window.showSnackbar("Task updated");
  });

  document.addEventListener("taskDeleted", () => {
    window.showSnackbar("Task deleted");
    window.clearDetailPane();
  });

  // Initialize on load
  document.addEventListener("DOMContentLoaded", () => {
    applyTheme(getPreferredTheme());
    initDrawer();

    const themeToggleBtn = document.getElementById("theme-toggle");
    if (themeToggleBtn) {
      themeToggleBtn.addEventListener("click", window.toggleTheme);
    }
  });
})();
