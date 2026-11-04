# Typography Archetype B: Google Standard M3 (`typography-standard.md`)

This archetype adheres strictly to the official **15 Material 3 Typography Roles**, rendered using **Google Sans Flex** for clean, sober, high-density productivity applications modeled directly after Google Calendar, Google Docs, and enterprise Google Workspace interfaces.

---

## 1. Font Face Definition

```css
@font-face {
  font-family: 'Google Sans Flex';
  src: url('../fonts/GoogleSansFlex.woff2') format('woff2-variations'),
       url('../fonts/GoogleSansFlex.woff2') format('woff2');
  font-weight: 100 1000;
  font-display: swap;
}

:root {
  --font-family-standard: 'Google Sans Flex', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
```

---

## 2. The 15 Standard Material 3 Typography Scale Roles

| Role | Font Size | Line Height | Tracking | Weight | CSS Token / Class |
|---|---|---|---|---|---|
| **Display Large** | 57px (3.56rem) | 64px (1.12) | -0.25px | Regular (400) | `.m3-display-large` |
| **Display Medium** | 45px (2.81rem) | 52px (1.15) | 0px | Regular (400) | `.m3-display-medium` |
| **Display Small** | 36px (2.25rem) | 44px (1.22) | 0px | Regular (400) | `.m3-display-small` |
| **Headline Large** | 32px (2.00rem) | 40px (1.25) | 0px | Regular (400) | `.m3-headline-large` |
| **Headline Medium** | 28px (1.75rem) | 36px (1.28) | 0px | Regular (400) | `.m3-headline-medium` |
| **Headline Small** | 24px (1.50rem) | 32px (1.33) | 0px | Regular (400) | `.m3-headline-small` |
| **Title Large** | 22px (1.37rem) | 28px (1.27) | 0px | Regular (400) | `.m3-title-large` |
| **Title Medium** | 16px (1.00rem) | 24px (1.50) | 0.15px | Medium (500) | `.m3-title-medium` |
| **Title Small** | 14px (0.87rem) | 20px (1.42) | 0.1px | Medium (500) | `.m3-title-small` |
| **Body Large** | 16px (1.00rem) | 24px (1.50) | 0.5px | Regular (400) | `.m3-body-large` |
| **Body Medium** | 14px (0.87rem) | 20px (1.42) | 0.25px | Regular (400) | `.m3-body-medium` |
| **Body Small** | 12px (0.75rem) | 16px (1.33) | 0.4px | Regular (400) | `.m3-body-small` |
| **Label Large** | 14px (0.87rem) | 20px (1.42) | 0.1px | Medium (500) | `.m3-label-large` |
| **Label Medium** | 12px (0.75rem) | 16px (1.33) | 0.5px | Medium (500) | `.m3-label-medium` |
| **Label Small** | 11px (0.68rem) | 16px (1.45) | 0.5px | Medium (500) | `.m3-label-small` |

---

## 3. CSS Tokens & Class Implementations

```css
.m3-display-large {
  font-family: var(--font-family-standard);
  font-size: 3.5625rem;
  line-height: 4rem;
  letter-spacing: -0.015em;
  font-variation-settings: 'wght' 400, 'opsz' 57;
}

.m3-display-medium {
  font-family: var(--font-family-standard);
  font-size: 2.8125rem;
  line-height: 3.25rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 45;
}

.m3-display-small {
  font-family: var(--font-family-standard);
  font-size: 2.25rem;
  line-height: 2.75rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 36;
}

.m3-headline-large {
  font-family: var(--font-family-standard);
  font-size: 2rem;
  line-height: 2.5rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 32;
}

.m3-headline-medium {
  font-family: var(--font-family-standard);
  font-size: 1.75rem;
  line-height: 2.25rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 28;
}

.m3-headline-small {
  font-family: var(--font-family-standard);
  font-size: 1.5rem;
  line-height: 2rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 24;
}

.m3-title-large {
  font-family: var(--font-family-standard);
  font-size: 1.375rem;
  line-height: 1.75rem;
  letter-spacing: 0;
  font-variation-settings: 'wght' 400, 'opsz' 22;
}

.m3-title-medium {
  font-family: var(--font-family-standard);
  font-size: 1rem;
  line-height: 1.5rem;
  letter-spacing: 0.009em;
  font-variation-settings: 'wght' 500, 'opsz' 16;
}

.m3-title-small {
  font-family: var(--font-family-standard);
  font-size: 0.875rem;
  line-height: 1.25rem;
  letter-spacing: 0.007em;
  font-variation-settings: 'wght' 500, 'opsz' 14;
}

.m3-body-large {
  font-family: var(--font-family-standard);
  font-size: 1rem;
  line-height: 1.5rem;
  letter-spacing: 0.031em;
  font-variation-settings: 'wght' 400, 'opsz' 16;
}

.m3-body-medium {
  font-family: var(--font-family-standard);
  font-size: 0.875rem;
  line-height: 1.25rem;
  letter-spacing: 0.018em;
  font-variation-settings: 'wght' 400, 'opsz' 14;
}

.m3-body-small {
  font-family: var(--font-family-standard);
  font-size: 0.75rem;
  line-height: 1rem;
  letter-spacing: 0.025em;
  font-variation-settings: 'wght' 400, 'opsz' 12;
}

.m3-label-large {
  font-family: var(--font-family-standard);
  font-size: 0.875rem;
  line-height: 1.25rem;
  letter-spacing: 0.007em;
  font-variation-settings: 'wght' 500, 'opsz' 14;
}

.m3-label-medium {
  font-family: var(--font-family-standard);
  font-size: 0.75rem;
  line-height: 1rem;
  letter-spacing: 0.031em;
  font-variation-settings: 'wght' 500, 'opsz' 12;
}

.m3-label-small {
  font-family: var(--font-family-standard);
  font-size: 0.6875rem;
  line-height: 1rem;
  letter-spacing: 0.031em;
  font-variation-settings: 'wght' 500, 'opsz' 11;
}
```

---

## 4. Usage Guidelines

- **Best Suited For**: Data-heavy applications, multi-row tables, complex form validation, utility screens, calendars, settings views, and dense workspaces.
- **Visual Distinction**: Gives the app the canonical look and feel of official Google web tools (Google Calendar, Google Contacts, Google Keep).

