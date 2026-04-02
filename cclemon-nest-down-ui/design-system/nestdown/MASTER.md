# Design System Master File

> **LOGIC:** When building a specific page, first check `design-system/pages/[page-name].md`.
> If that file exists, its rules **override** this Master file.
> If not, strictly follow the rules below.

---

**Project:** NestDown
**Style:** Organic Biophilic + Glassmorphism (Caring Teal palette)
**Updated:** 2026-03-18
**Source:** ui-ux-pro-max Veterinary/Caring palette (database match)

---

## Color Palette

| Role | Hex | CSS Variable | Notes |
|------|-----|--------------|-------|
| Primary | `#0A9488` | `--color-primary` | 青碧療癒 teal |
| Primary Dark | `#087870` | `--color-primary-dark` | Hover/active state |
| Primary Text | `#083830` | `--color-primary-text` | Deep teal for headings |
| Secondary | `#2AACA8` | `--color-secondary` | Light teal accent |
| CTA / Accent | `#E86010` | `--color-cta` | Warm orange — action buttons |
| CTA Dark | `#C05010` | `--color-cta-dark` | CTA hover |
| Background | `#F0FDFA` | `--color-bg` | Page background (light mint) |
| Surface | `#A8E8E0` | `--color-surface` | Screen/card background gradient |
| Text | `#083830` | `--color-text` | Body text |
| Text Muted | `#0A7870` | `--color-text-muted` | Labels, captions |
| Border Glass | `rgba(255,255,255,0.42)` | `--color-border-glass` | Glass card borders |

**Color Notes:** Teal = healing + calm + nature (water/forest). Orange CTA = energy without aggression.
No dark mode — light mode only.

---

## Typography

- **Heading Font:** Lora (serif) — warm, literary, non-athletic
- **Body Font:** Raleway (sans-serif) — clean, modern, approachable
- **Mood:** calm, wellness, health, relaxing, natural, organic

```css
@import url('https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,500;0,600;0,700;1,400&family=Raleway:wght@300;400;500;600;700&display=swap');
```

| Role | Font | Size | Weight | Line Height |
|------|------|------|--------|-------------|
| Display score | Lora | 48–52px | 700 | 1.0 |
| Page title | Lora | 20–24px | 600 | 1.2 |
| Section heading | Lora | 16–18px | 600 | 1.3 |
| Body | Raleway | 16px | 400 | 1.6 |
| Label / Caption | Raleway | 8–11px | 600 | 1.4 |
| Italic tag | Lora italic | 11–13px | 400 | 1.5 |

---

## Spacing

| Token | Value | Usage |
|-------|-------|-------|
| `--space-xs` | `4px` | Tight gaps |
| `--space-sm` | `8px` | Icon gaps, inline |
| `--space-md` | `16px` | Standard padding |
| `--space-lg` | `24px` | Section padding |
| `--space-xl` | `32px` | Large gaps |
| `--space-2xl` | `48px` | Section margins |
| `--space-3xl` | `64px` | Hero padding |

---

## Border Radius (Organic Biophilic)

Use varied radii per card corner for organic feel — never uniform on all 4 corners.

| Token | Value | Usage |
|-------|-------|-------|
| `--r-sm` | `8px` | Small inner elements |
| `--r-md` | `14px` | Sub-cards, badges |
| `--r-lg` | `22px` | Main cards |
| `--r-xl` | `28px` | Card wrappers, sheets |
| `--r-full` | `9999px` | Pills, tags |

**Organic variant pattern:**
```
border-radius: 14px 6px 14px 6px;   /* top-left / top-right / btm-right / btm-left */
border-radius: 6px 14px 6px 14px;   /* alternating */
border-radius: 22px;                 /* uniform for main cards */
```

---

## Glassmorphism Spec

```css
/* Primary Glass Card */
.glass {
  background: rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 22px;
  position: relative;
}

/* Top light reflection line */
.glass::before {
  content: '';
  position: absolute;
  top: 0; left: 12px; right: 12px; height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.9), transparent);
  border-radius: 22px 22px 0 0;
}

/* Secondary Glass (smaller cards) */
.glass-sm {
  background: rgba(255, 255, 255, 0.26);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.42);
}
```

---

## Background (Organic Biophilic Blobs)

Every screen must have an ambient gradient background + 2 radial blob overlays:

```css
/* Screen/page background */
.screen-bg {
  background: linear-gradient(148deg, #a8e8e0 0%, #88ddd0 45%, #a0e8d4 100%);
  position: relative;
  overflow: hidden;
}

/* Blob 1 — top right, primary teal glow */
.blob-1 {
  position: absolute;
  top: -45px; right: -25px;
  width: 170px; height: 170px;
  background: radial-gradient(circle, rgba(10,200,180,0.30) 0%, transparent 65%);
  border-radius: 50%;
  pointer-events: none;
}

/* Blob 2 — bottom left, warm orange glow */
.blob-2 {
  position: absolute;
  bottom: -40px; left: -20px;
  width: 130px; height: 130px;
  background: radial-gradient(circle, rgba(232,96,16,0.18) 0%, transparent 65%);
  border-radius: 50%;
  pointer-events: none;
}
```

---

## Shadow Depths

| Level | Value | Usage |
|-------|-------|-------|
| `--shadow-sm` | `0 1px 2px rgba(0,0,0,0.05)` | Subtle lift |
| `--shadow-md` | `0 4px 12px rgba(10,148,136,0.12)` | Glass cards |
| `--shadow-lg` | `0 8px 32px rgba(10,148,136,0.15)` | Score card, modals |
| `--shadow-cta` | `0 4px 16px rgba(232,96,16,0.25)` | CTA buttons |

Colored shadows (per card type):
- Calories card: `0 3px 14px rgba(240,110,20,0.12)`
- Exercise card: `0 3px 14px rgba(10,148,136,0.12)`
- Weight card: `0 3px 14px rgba(10,148,136,0.08)`
- Streak card: `0 3px 14px rgba(180,130,20,0.08)`

---

## Component Specs

### Touch Targets
Minimum **44×44px** for all interactive elements (PWA mobile-first requirement).

### Buttons

```css
/* CTA Button */
.btn-cta {
  background: #E86010;
  color: white;
  padding: 13px 24px;
  border-radius: 13px;
  font-family: 'Raleway', sans-serif;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.9px;
  text-transform: uppercase;
  cursor: pointer;
  min-height: 44px;
  transition: all 200ms ease;
  box-shadow: 0 4px 16px rgba(232,96,16,0.25);
}
.btn-cta:hover {
  background: #C05010;
  transform: translateY(-1px);
}

/* Glass Button */
.btn-glass {
  background: rgba(255,255,255,0.35);
  border: 1px solid rgba(255,255,255,0.55);
  color: #083830;
  padding: 12px;
  border-radius: 13px;
  font-family: 'Raleway', sans-serif;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.9px;
  text-transform: uppercase;
  cursor: pointer;
  min-height: 44px;
  transition: all 200ms ease;
}
```

### Inputs

```css
.input {
  padding: 13px 16px;
  border: 1.5px solid rgba(10,148,136,0.25);
  border-radius: 13px;
  font-family: 'Raleway', sans-serif;
  font-size: 16px;
  background: rgba(255,255,255,0.7);
  min-height: 44px;
  transition: border-color 200ms ease;
}
.input:focus {
  border-color: #0A9488;
  outline: none;
  box-shadow: 0 0 0 3px rgba(10,148,136,0.15);
}
```

### Badges / Tags

```css
.badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 9999px;
  font-family: 'Raleway', sans-serif;
  font-size: 9px;
  font-weight: 600;
  background: rgba(255,255,255,0.35);
  border: 1px solid rgba(255,255,255,0.55);
}
```

### Bottom Navigation (Mobile)

```css
.bottom-nav {
  display: flex;
  justify-content: space-around;
  padding: 10px 0 env(safe-area-inset-bottom, 15px);
  background: rgba(120,220,204,0.88);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-top: 1px solid rgba(255,255,255,0.5);
  position: fixed;
  bottom: 0; left: 0; right: 0;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  min-width: 44px;
  min-height: 44px;
  justify-content: center;
  cursor: pointer;
}
```

### Modals / Bottom Sheets

```css
.bottom-sheet {
  background: rgba(240,253,250,0.95);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border-radius: 28px 28px 0 0;
  border-top: 1px solid rgba(255,255,255,0.6);
  padding: 20px 20px env(safe-area-inset-bottom, 20px);
}
```

---

## Animation

| Type | Duration | Easing |
|------|----------|--------|
| Micro-interaction | 150ms | ease |
| Page transition | 250ms | ease-out |
| Sheet slide-up | 300ms | cubic-bezier(0.32,0.72,0,1) |
| Score count-up | 800ms | ease-out |
| Blob pulse | 6s | ease-in-out infinite |

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

---

## Psychology Design Tokens

| Principle | Implementation | Component |
|-----------|---------------|-----------|
| Peak-End Rule | 48–52px display score, colored shadow | HealthScoreCard |
| Zeigarnik Effect | Progress rings, incomplete state visible | GoalRings |
| Loss Aversion | Streak counter with "不要中斷" framing | StreakCard |
| Implementation Intention | If-Then plan builder | IfThenPlanner |
| Non-pressuring language | "身體正在好的方向" not "達成目標" | All microcopy |

---

## Anti-Patterns (Do NOT Use)

- ❌ **Dark mode / dark backgrounds** — light mode only
- ❌ **Sporty / athletic aesthetic** — Barlow Condensed, neon, gym imagery
- ❌ **Emojis as icons** — Use SVG icons (Lucide)
- ❌ **Missing cursor:pointer** — all clickable elements must have it
- ❌ **Layout-shifting hovers** — no scale transforms that shift layout
- ❌ **Low contrast text** — maintain 4.5:1 minimum
- ❌ **Instant state changes** — always use transitions (150–300ms)
- ❌ **Invisible focus states** — focus rings required
- ❌ **Achievement / competition language** — wellness tone only
- ❌ **Uniform border-radius** — use organic varied corners

---

## Pre-Delivery Checklist

- [ ] No emojis as icons (Lucide SVG only)
- [ ] `cursor-pointer` on all interactive elements
- [ ] Hover transitions 150–300ms
- [ ] Light mode contrast ≥ 4.5:1
- [ ] Touch targets ≥ 44×44px
- [ ] Focus states visible
- [ ] `prefers-reduced-motion` respected
- [ ] Responsive: 375px / 768px / 1024px / 1440px
- [ ] No content behind fixed bottom nav (add padding-bottom)
- [ ] No horizontal scroll on mobile
- [ ] Glass cards have top light-reflection `::before`
- [ ] Background has radial blob overlays
- [ ] Organic border-radius (not uniform)
