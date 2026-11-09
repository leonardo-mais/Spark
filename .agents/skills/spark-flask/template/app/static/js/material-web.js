/**
 * Material Web (@material/web) Custom Elements Bridge for Spark-Flask
 * Provides base custom elements definitions and lightweight Web Components
 */

class MdLinearProgress extends HTMLElement {
  connectedCallback() {
    if (!this.innerHTML.trim()) {
      this.innerHTML = '<div class="progress-bar-indeterminate"></div>';
    }
    this.classList.add('m3-linear-progress');
  }
}

if (!customElements.get('md-linear-progress')) {
  customElements.define('md-linear-progress', MdLinearProgress);
}

// Export for module loader compatibility
export { MdLinearProgress };
