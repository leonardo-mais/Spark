/**
 * Spark! Application Client Engine
 * Theme switching, drawer interactions, and responsive helper functions
 */

(function () {
  // Theme Management
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
  }

  window.toggleTheme = function () {
    const current = document.documentElement.getAttribute("data-theme") || getPreferredTheme();
    const next = current === "dark" ? "light" : "dark";
    localStorage.setItem(STORAGE_KEY, next);
    applyTheme(next);
  };

  // Drawer Toggle
  function initDrawer() {
    const toggleBtn = document.getElementById("drawer-toggle");
    const drawer = document.getElementById("app-nav-drawer");
    if (toggleBtn && drawer) {
      toggleBtn.addEventListener("click", () => {
        drawer.classList.toggle("collapsed");
      });
    }
  }

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

  // HTMX Event Listeners for smooth transitions
  document.addEventListener("htmx:afterSwap", (e) => {
    // If detail pane was populated, mark has-selection for mobile viewports
    if (e.detail.target.id === "detail-pane") {
      e.detail.target.classList.add("has-selection");
    }
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

