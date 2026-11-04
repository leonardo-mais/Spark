# Typography Archetype A: Spark Impact Flex (`typography-impact.md`)

This archetype brings the expressive, fluid typography of the **Spark! Impact style** to the web using **Google Sans Flex** as an authentic variable font leveraging its 6 distinct variation axes:

- `wght`: Weight (100 – 1000)
- `wdth`: Width (25% – 151%)
- `opsz`: Optical Size (6 – 144)
- `GRAD`: Grade (-200 – 150)
- `slnt`: Slant (-10 – 0)
- `ROND`: Roundness (0% – 100%)

---

## 1. Font Face Definition

```css
@font-face {
  font-family: 'Google Sans Flex';
  src: url('../fonts/GoogleSansFlex.woff2') format('woff2-variations'),
       url('../fonts/GoogleSansFlex.woff2') format('woff2');
  font-weight: 100 1000;
  font-stretch: 25% 151%;
  font-style: oblique -10deg 0deg;
  font-display: swap;
}

:root {
  --font-family-spark: 'Google Sans Flex', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
```

---

## 2. Hero Numerics & Expressive Headings

For dashboards, task counts, countdowns, and summary statistics, Archetype A defines high-impact hero numerals with variable roundness and maximum weight:

```css
/* Hero Numeric Style for KPI cards, countdowns, and counters */
.spark-hero-numeric {
  font-family: var(--font-family-spark);
  font-size: clamp(3rem, 7vw, 4.5rem); /* Fluid clamp between 48px and 72px */
  line-height: 1.05;
  letter-spacing: -0.04em;
  font-variation-settings:
    'wght' 820,
    'wdth' 105,
    'opsz' 72,
    'GRAD' 50,
    'ROND' 100;
  color: var(--md-sys-color-primary);
  margin: 0;
}

/* Expressive Hero Greeting */
.spark-expressive-greeting {
  font-family: var(--font-family-spark);
  font-size: clamp(1.75rem, 4vw, 2.75rem);
  line-height: 1.15;
  letter-spacing: -0.03em;
  font-variation-settings:
    'wght' 780,
    'wdth' 102,
    'opsz' 48,
    'GRAD' 20,
    'ROND' 60;
  color: var(--md-sys-color-on-surface);
}

/* Section Header Impact */
.spark-heading-impact {
  font-family: var(--font-family-spark);
  font-size: 1.5rem;
  line-height: 1.25;
  letter-spacing: -0.02em;
  font-variation-settings:
    'wght' 680,
    'wdth' 100,
    'opsz' 28,
    'ROND' 40;
  color: var(--md-sys-color-on-surface);
}

/* Pill Badges & Chips Typography */
.spark-badge-label {
  font-family: var(--font-family-spark);
  font-size: 0.75rem;
  line-height: 1;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-variation-settings:
    'wght' 720,
    'wdth' 95,
    'opsz' 14,
    'ROND' 80;
}
```

---

## 3. Usage Guidelines

- **Best Suited For**: Modern consumer apps, personal task trackers, wellness, habits, dynamic dashboards, and high-energy productivity apps.
- **Micro-Interactions**: When hovering over hero cards or badges, subtly interpolate weight (`'wght' 820` -> `'wght' 860`) using CSS `transition: font-variation-settings 0.2s cubic-bezier(0.2, 0, 0, 1)`.
- **Contrast Requirement**: Ensure large variable font weights maintain high contrast (minimum 4.5:1 for body and 3:1 for large hero headings) against container surfaces.

