#!/usr/bin/env python3
"""
M3 Expressive - UI Components Reference Downloader & Formatter
Questo script scarica la documentazione ufficiale dei componenti Material 3 / Jetpack Compose
elencati in 'ui-components.md' e genera file di documentazione puliti, ordinati e strutturati
all'interno della cartella 'references/'.
"""

import os
import sys
import re
import json
import time
import hashlib
import argparse
import subprocess
import html
from pathlib import Path
from html.parser import HTMLParser

# Mappatura categorie standard M3 Expressive
CATEGORY_MAP = {
    # Navigation & Toolbars
    "app bars": "navigation",
    "toolbars": "navigation",
    "tabs": "navigation",
    "navigation": "navigation",
    "navigation bar": "navigation",
    "floating bar": "navigation",
    "navigation drawer": "navigation",
    "navigation rail": "navigation",

    # Actions & Buttons
    "buttons": "actions",
    "button groups": "actions",
    "common buttons": "actions",
    "split button": "actions",
    "toggle button groups": "actions",
    "icon buttons": "actions",
    "floating action buttons": "actions",
    "floating action buttons (fabs)": "actions",
    "extended floating action buttons": "actions",
    "extended floating action buttons (extended fabs)": "actions",

    # Inputs & Selection
    "inputs": "inputs",
    "text fields": "inputs",
    "sliders": "inputs",
    "switch": "inputs",
    "checkbox": "inputs",
    "radio button": "inputs",
    "date and time": "inputs",
    "date picker": "inputs",
    "time picker": "inputs",

    # Containment & Surfaces
    "cards": "containment-surfaces",
    "dialogs": "containment-surfaces",
    "divider": "containment-surfaces",
    "menus": "containment-surfaces",
    "search": "containment-surfaces",
    "tooltips": "containment-surfaces",
    "sheets": "containment-surfaces",
    "bottom sheets": "containment-surfaces",
    "side sheets": "containment-surfaces",

    # Display & Feedback
    "lists": "display-feedback",
    "grids": "display-feedback",
    "carousel": "display-feedback",
    "chips": "display-feedback",
    "snackbar": "display-feedback",
    "badges": "display-feedback",
    "loading & progress": "display-feedback",
    "loading indicator": "display-feedback",
    "progress indicator": "display-feedback"
}

CATEGORY_METADATA = {
    "navigation": {
        "title": "Navigation & App Bars",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per componenti di navigazione (TopAppBars, Toolbars, NavBars, WideNavigationRail, Drawer, Tabs) conformi a Material 3 Expressive."
    },
    "actions": {
        "title": "Actions & Buttons",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per pulsanti ed elementi di azione (Common Buttons, SplitButton, Connected ButtonGroups, IconButtons, FABs) conformi a Material 3 Expressive."
    },
    "inputs": {
        "title": "Inputs & Selection Controls",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per campi di testo, slider espressivi, selettori e date/time pickers conformi a Material 3 Expressive."
    },
    "containment-surfaces": {
        "title": "Containment & Surfaces",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per schede, fogli (bottom & side sheets), dialoghi, menu e ricerca conformi a Material 3 Expressive."
    },
    "display-feedback": {
        "title": "Display & Feedback",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per liste espressive, caroselli, chip, badge, indicatori di progresso ondulati (wavy) e snackbar conformi a Material 3 Expressive."
    }
}

# Snippet Compose di riferimento M3 Expressive per componenti MDC (documentati primariamente in XML su GitHub)
COMPOSE_FALLBACK_SNIPPETS = {
    "common buttons": """// 5 Configurazioni graduate di enfasi in Material 3 Expressive:
// Button (Filled) -> enfasi massima (primary / on-primary)
// ElevatedButton  -> enfasi elevata su sfondi dinamici
// FilledTonalButton -> enfasi media (secondary-container)
// OutlinedButton   -> enfasi media-bassa (outline / primary)
// TextButton       -> enfasi minima (dialoghi, azioni terziarie)

Button(onClick = { /* azione */ }, modifier = Modifier.fillMaxWidth()) {
    Icon(Icons.Default.Add, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text("Azione Primaria", style = MaterialTheme.typography.labelLarge)
}""",
    "button groups": """> **M3 Expressive Update:** Il vecchio SegmentedButton è deprecato in favore del Connected Button Group.
// Connected Button Group in M3 Expressive:
ButtonGroup(
    modifier = Modifier.fillMaxWidth()
) {
    // Pulsanti collegati con raggio di curvatura morfologico
}""",
    "split button": """// SplitButton per azione primaria + menu a tendina associato
SplitButtonLayout(
    leadingButton = {
        Button(onClick = onDefaultAction) {
            Text("Salva")
        }
    },
    trailingButton = {
        FilledIconButton(onClick = onOpenMenu) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Opzioni")
        }
    }
)""",
    "icon buttons": """// Icon Buttons M3 (Standard, Filled, FilledTonal, Outlined)
IconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.FavoriteBorder, contentDescription = "Preferiti")
}
FilledIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.Bookmark, contentDescription = "Salvati")
}
FilledTonalIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.Share, contentDescription = "Condividi")
}
OutlinedIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.MoreVert, contentDescription = "Altro")
}""",
    "floating action buttons (fabs)": """// Floating Action Button espressivo (forma Extra Large 28dp o morfica)
FloatingActionButton(
    onClick = { /* azione */ },
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    shape = RoundedCornerShape(28.dp)
) {
    Icon(Icons.Default.Add, contentDescription = "Crea nuovo")
}""",
    "extended floating action buttons (extended fabs)": """ExtendedFloatingActionButton(
    onClick = { /* azione */ },
    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
    text = { Text("Nuovo Messaggio", style = MaterialTheme.typography.labelLarge) },
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
)"""
}


def slugify(text: str) -> str:
    """Trasforma un titolo in slug per nomi file."""
    s = text.lower().strip()
    s = re.sub(r'[\(\)\&]', '', s)
    s = re.sub(r'[^a-z0-9\-]+', '-', s)
    return s.strip('-')


def format_signature(sig: str) -> str:
    """Formatta una firma composable in modo pulito e leggibile."""
    s = re.sub(r'[ \t]+', ' ', sig.strip())
    s = re.sub(r'^@Composable\s*fun\s+', '@Composable\nfun ', s)
    s = re.sub(r'fun\s+(\w+)\s*\(', r'fun \1(\n    ', s)
    s = re.sub(r',\s*([a-zA-Z0-9_]+:\s*)', r',\n    \1', s)
    s = re.sub(r'\)\s*$', r'\n)', s)
    return s


class CacheManager:
    """Gestione cache locale su disco per evitare richieste HTTP ripetute."""
    def __init__(self, cache_dir: Path):
        self.cache_dir = cache_dir
        self.cache_dir.mkdir(parents=True, exist_ok=True)

    def _get_key(self, url: str) -> Path:
        h = hashlib.sha256(url.encode('utf-8')).hexdigest()
        return self.cache_dir / f"{h}.cache"

    def get(self, url: str) -> str | None:
        p = self._get_key(url)
        if p.exists():
            try:
                with open(p, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    return data.get("content")
            except Exception:
                return None
        return None

    def set(self, url: str, content: str):
        p = self._get_key(url)
        try:
            with open(p, 'w', encoding='utf-8') as f:
                json.dump({"url": url, "timestamp": time.time(), "content": content}, f, ensure_ascii=False)
        except Exception as e:
            print(f"  [!] Impossibile salvare cache: {e}", flush=True)

    def clear(self):
        for f in self.cache_dir.glob("*.cache"):
            f.unlink()


class Downloader:
    """Downloader HTTP con gestione trasparente di redirect e cache locale."""
    def __init__(self, cache: CacheManager, bypass_cache: bool = False):
        self.cache = cache
        self.bypass_cache = bypass_cache

    def fetch(self, url: str) -> str | None:
        target_url = url.split('#')[0]

        if 'developer.android.com' in target_url and 'hl=' not in target_url:
            target_url += '&hl=en' if '?' in target_url else '?hl=en'
        elif 'github.com' in target_url and '/blob/' in target_url:
            target_url = target_url.replace('github.com', 'raw.githubusercontent.com').replace('/blob/', '/')

        if not self.bypass_cache:
            cached = self.cache.get(target_url)
            if cached is not None:
                return cached

        cmd = [
            "curl",
            "-sL",
            "--max-time", "25",
            "-A", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko)",
            target_url
        ]

        try:
            res = subprocess.run(cmd, capture_output=True, text=True, timeout=30)
            if res.returncode == 0 and res.stdout.strip():
                content = res.stdout
                self.cache.set(target_url, content)
                return content
            else:
                print(f"  [!] Download fallito per {target_url} (codice {res.returncode})", flush=True)
        except Exception as e:
            print(f"  [!] Eccezione durante download di {target_url}: {e}", flush=True)

        return None


class DevSiteHTMLCleaner(HTMLParser):
    """Estrae testo esplicativo, titoli, firme e snippet di codice puliti."""
    def __init__(self):
        super().__init__()
        self.output_parts = []
        self.code_blocks = []
        self.signatures = []
        self.ignore_depth = 0
        self.in_pre = False
        self.current_pre = []
        self.in_heading = False
        self.current_heading_level = 2
        self.current_heading = []

    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        tag_lower = tag.lower()

        if tag_lower in ('script', 'style', 'devsite-page-rating', 'devsite-feedback',
                         'devsite-bookmark', 'devsite-header', 'devsite-footer', 'nav'):
            self.ignore_depth += 1
            return

        cls = attrs_dict.get('class', '')
        if 'devsite-nav' in cls or 'devsite-banner' in cls or 'devsite-github-link' in cls:
            self.ignore_depth += 1
            return

        if self.ignore_depth > 0:
            return

        if tag_lower in ('h1', 'h2', 'h3', 'h4', 'h5', 'h6'):
            self.in_heading = True
            self.current_heading_level = int(tag_lower[1])
            self.current_heading = []
        elif tag_lower == 'pre':
            self.in_pre = True
            self.current_pre = []
        elif tag_lower == 'p':
            self.output_parts.append('\n\n')
        elif tag_lower == 'li':
            self.output_parts.append('\n* ')
        elif tag_lower == 'code' and not self.in_pre:
            self.output_parts.append('`')
        elif tag_lower == 'blockquote':
            self.output_parts.append('\n\n> ')

    def handle_endtag(self, tag):
        tag_lower = tag.lower()
        if tag_lower in ('script', 'style', 'devsite-page-rating', 'devsite-feedback',
                         'devsite-bookmark', 'devsite-header', 'devsite-footer', 'nav'):
            self.ignore_depth = max(0, self.ignore_depth - 1)
            return

        if self.ignore_depth > 0:
            return

        if self.in_heading and tag_lower.startswith('h'):
            self.in_heading = False
            htext = ''.join(self.current_heading).strip()
            if htext and not any(skip in htext.lower() for skip in ['additional resources', 'feedback', 'more android']):
                self.output_parts.append(f"\n\n{'#' * self.current_heading_level} {htext}\n\n")
            self.current_heading = []
        elif tag_lower == 'pre':
            self.in_pre = False
            code_str = html.unescape(''.join(self.current_pre)).replace('\xa0', ' ').strip()
            code_str = re.sub(r'\s*\b[A-Za-z0-9_]+\.(?:kt|java)\s*$', '', code_str).strip()
            if code_str:
                self._classify_and_store_code(code_str)
                self.output_parts.append(f"\n\n```kotlin\n{code_str}\n```\n\n")
            self.current_pre = []
        elif tag_lower == 'code' and not self.in_pre:
            self.output_parts.append('`')

    def _classify_and_store_code(self, code: str):
        trimmed = code.strip()
        trimmed_norm = re.sub(r'@Composable\s*fun', '@Composable fun', trimmed)

        is_signature = (
            bool(re.match(r'^\s*@?Composable\s+fun\s+[A-Z]\w*\s*\(', trimmed_norm)) and
            not any(ex in trimmed_norm for ex in ['Example', 'Showcase', 'Preview', 'Screen']) and
            not 'import ' in trimmed_norm
        )

        if is_signature:
            self.signatures.append(trimmed_norm)
        else:
            self.code_blocks.append(trimmed_norm)

    def handle_data(self, data):
        if self.ignore_depth > 0:
            return
        if self.in_pre:
            self.current_pre.append(data)
        elif self.in_heading:
            self.current_heading.append(data)
        else:
            self.output_parts.append(data)

    def get_markdown(self) -> str:
        text = ''.join(self.output_parts)
        text = re.sub(r'\n{3,}', '\n\n', text)
        return text.strip()


def parse_ui_components_file(filepath: Path) -> list[dict]:
    """Legge 'ui-components.md' e genera una lista ordinata di componenti con i rispettivi link."""
    if not filepath.exists():
        raise FileNotFoundError(f"File non trovato: {filepath}")

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    lines = content.split('\n')
    components = []
    current_h3 = ""
    current_item = None

    for line in lines:
        line_s = line.strip()
        if line_s.startswith('### '):
            if current_item:
                components.append(current_item)
            title = line_s.replace('### ', '').strip()
            current_h3 = title
            current_item = {
                "name": title,
                "parent": "",
                "category": CATEGORY_MAP.get(title.lower(), "containment-surfaces"),
                "updates": [],
                "notes": [],
                "links": []
            }
        elif line_s.startswith('#### '):
            if current_item:
                components.append(current_item)
            title = line_s.replace('#### ', '').strip()
            category = CATEGORY_MAP.get(title.lower(), CATEGORY_MAP.get(current_h3.lower(), "containment-surfaces"))
            current_item = {
                "name": title,
                "parent": current_h3,
                "category": category,
                "updates": [],
                "notes": [],
                "links": []
            }
        elif current_item:
            # Estrai eventuali sottocomponenti elencati direttamente con link (es. in Buttons)
            named_link_m = re.match(r'^\*\*([A-Za-z0-9_\- \(\)]+)\*\*:\s*(https?://[^\s\)]+)', line_s)
            if named_link_m and current_h3.lower() in ["buttons"]:
                sub_label, sub_url = named_link_m.groups()
                # Crea un componente dedicato per questo elemento
                sub_comp = {
                    "name": sub_label,
                    "parent": current_h3,
                    "category": "actions",
                    "updates": [],
                    "notes": [],
                    "links": [{"label": sub_label, "url": sub_url}]
                }
                components.append(sub_comp)
                continue

            if line_s.startswith('**Update') or '**Updates**:' in line_s:
                current_item["updates"].append(line_s)
            elif any(line_s.startswith(p) for p in [
                '**Remember**:', '**Preferenze**:', '**Old**:', '**Toggle button groups**:',
                '**Floating**:', '**Docked**:', '**M3**:', '**Linear**:', '**Circular**:'
            ]):
                current_item["notes"].append(line_s)

            urls = re.findall(r'https?://[^\s\)\>]+', line_s)
            for u in urls:
                label = "Doc"
                if line_s.startswith('**') and ':' in line_s:
                    label = line_s.split(':')[0].replace('*', '').strip()
                elif 'Link' in line_s:
                    label = "Android Developers"
                current_item["links"].append({"label": label, "url": u})

    if current_item:
        components.append(current_item)

    # Filtra contenitori padre vuoti senza link diretti
    result = []
    for c in components:
        if c["links"] or c["updates"] or c["notes"]:
            result.append(c)

    return result


def extract_content_from_html(raw_html: str, url: str) -> dict:
    """Estrae testo pulito, firme Kotlin e composable code snippets da una pagina HTML."""
    result = {
        "title": "",
        "markdown": "",
        "code_blocks": [],
        "signatures": []
    }

    article_match = re.search(r'<div class=\"devsite-article-body[^\"]*\">(.*?)</div>\s*</article>', raw_html, re.DOTALL)
    if not article_match:
        article_match = re.search(r'<devsite-content>(.*?)</devsite-content>', raw_html, re.DOTALL)

    body_html = article_match.group(1) if article_match else raw_html

    body_html = re.sub(r'<div class="devsite-github-link.*?</div>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-page-rating.*?</devsite-page-rating>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-feature-tooltip.*?</devsite-feature-tooltip>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-heading id=\"additional_resources\".*$', '', body_html, flags=re.DOTALL)

    cleaner = DevSiteHTMLCleaner()
    cleaner.feed(body_html)

    result["markdown"] = cleaner.get_markdown()
    result["code_blocks"] = cleaner.code_blocks
    result["signatures"] = cleaner.signatures

    title_m = re.search(r'<title>(.*?)</title>', raw_html, re.DOTALL)
    if title_m:
        raw_title = title_m.group(1).split('|')[0].replace('&nbsp;', ' ').strip()
        result["title"] = raw_title

    return result


def extract_content_from_github_md(raw_md: str) -> dict:
    """Pulisce la documentazione markdown da repository GitHub (es. MDC-Android)."""
    clean_md = re.sub(r'<!--docs:.*?-->', '', raw_md, flags=re.DOTALL).strip()
    clean_md = re.sub(r'!\[.*?\]\(assets/.*?\)', '', clean_md)
    code_blocks = re.findall(r'```(?:kotlin|java|xml)?\n(.*?)```', clean_md, flags=re.DOTALL)

    return {
        "title": "",
        "markdown": clean_md,
        "code_blocks": [c.strip() for c in code_blocks if c.strip()],
        "signatures": []
    }


def format_component_section(comp: dict, downloaded_docs: list[dict], index: int) -> str:
    """Formatta una sezione completa di un componente nel formato curato M3 Expressive."""
    title = comp["name"]
    parent = comp.get("parent")
    full_title = f"{parent} - {title}" if parent and parent.lower() not in title.lower() else title

    lines = [f"## {index}. {full_title}\n"]

    # Callout aggiornamenti M3 Expressive
    if comp.get("updates"):
        lines.append("> [!IMPORTANT]")
        for u in comp["updates"]:
            clean_u = re.sub(r'^\*\*Updates?\*\*:\s*', '', u)
            lines.append(f"> **M3 Expressive Update:** {clean_u}")
        lines.append("")

    # Note di design specifiche
    if comp.get("notes"):
        for n in comp["notes"]:
            lines.append(f"> **Linee Guida di Design:** {n}")
        lines.append("")

    all_code = []
    all_signatures = []
    overview_text_snippets = []

    for doc in downloaded_docs:
        if doc.get("signatures"):
            all_signatures.extend(doc["signatures"])
        if doc.get("code_blocks"):
            all_code.extend(doc["code_blocks"])

        md = doc.get("markdown", "")
        paragraphs = [
            p.strip() for p in md.split('\n\n')
            if p.strip() and not p.strip().startswith('#') and not p.strip().startswith('```')
            and not p.strip().startswith('*') and len(p.strip()) > 50
            and not p.strip().startswith('@Composable') and not p.strip() in ['Unit', 'Modifier']
        ]
        if paragraphs:
            overview_text_snippets.extend(paragraphs[:2])

    if overview_text_snippets:
        lines.append(overview_text_snippets[0])
        lines.append("")

    # Firme Composable Principali
    if all_signatures:
        lines.append("### Firme Composable Principali")
        lines.append("```kotlin")
        for sig in all_signatures[:2]:
            lines.append(format_signature(sig))
            lines.append("")
        lines.append("```\n")

    # Esempi Jetpack Compose (o fallback curato se la doc originale era solo XML)
    compose_code = None
    slug_key = comp["name"].lower().strip()
    if slug_key in COMPOSE_FALLBACK_SNIPPETS:
        compose_code = COMPOSE_FALLBACK_SNIPPETS[slug_key]
    elif all_code:
        for code in all_code:
            if "@Composable" in code and ("Scaffold" in code or "Column" in code or "Row" in code or "Button" in code):
                if compose_code is None or len(code) > len(compose_code):
                    compose_code = code

        if not compose_code and all_code:
            # Filtra snippet non XML
            non_xml = [c for c in all_code if not c.startswith('<')]
            if non_xml:
                compose_code = non_xml[0]

    if compose_code:
        lines.append("### Esempio Jetpack Compose")
        lines.append("```kotlin")
        lines.append(compose_code.strip())
        lines.append("```\n")

    # Link ufficiali
    if comp.get("links"):
        lines.append("### Riferimenti Ufficiali")
        for l in comp["links"]:
            label = l.get("label", "Documentazione")
            url = l.get("url")
            lines.append(f"* **{label}:** [{url}]({url})")
        lines.append("")

    lines.append("---\n")
    return '\n'.join(lines)


def process_references(ui_components_path: Path, references_dir: Path, bypass_cache: bool = False, category_filter: str | None = None):
    """Flusso principale di scaricamento, elaborazione e scrittura delle referenze."""
    print(f"[*] Lettura componenti da: {ui_components_path}", flush=True)
    components = parse_ui_components_file(ui_components_path)
    print(f"[✓] Trovati {len(components)} componenti con specifiche e link.", flush=True)

    cache_dir = ui_components_path.parent / ".cache"
    cache = CacheManager(cache_dir)
    downloader = Downloader(cache, bypass_cache=bypass_cache)

    components_dir = references_dir / "components"
    components_dir.mkdir(parents=True, exist_ok=True)

    categories: dict[str, list[dict]] = {k: [] for k in CATEGORY_METADATA.keys()}
    for c in components:
        cat = c["category"]
        if cat not in categories:
            cat = "containment-surfaces"
        categories[cat].append(c)

    selected_categories = categories.items()
    if category_filter:
        if category_filter not in categories:
            print(f"[X] Categoria '{category_filter}' non valida. Opzioni: {list(categories.keys())}", flush=True)
            sys.exit(1)
        selected_categories = [(category_filter, categories[category_filter])]

    for cat_name, cat_components in selected_categories:
        cat_meta = CATEGORY_METADATA[cat_name]
        print(f"\n==========================================", flush=True)
        print(f"[*] Elaborazione Categoria: {cat_meta['title']} ({len(cat_components)} componenti)", flush=True)
        print(f"==========================================", flush=True)

        category_sections = []
        cat_index = 1

        for comp in cat_components:
            comp_name = comp["name"]
            slug = slugify(comp_name)
            print(f"\n  -> [{cat_index}/{len(cat_components)}] Componente: {comp_name}", flush=True)

            downloaded_docs = []
            for link_info in comp.get("links", []):
                url = link_info["url"]
                label = link_info.get("label", "Link")
                print(f"     [+] Scaricamento: {url}", flush=True)
                raw_content = downloader.fetch(url)
                if not raw_content:
                    continue

                if "github.com" in url or url.endswith(".md"):
                    parsed_doc = extract_content_from_github_md(raw_content)
                else:
                    parsed_doc = extract_content_from_html(raw_content, url)

                downloaded_docs.append(parsed_doc)

            section_md = format_component_section(comp, downloaded_docs, cat_index)
            category_sections.append(section_md)

            # Scheda individuale del componente
            individual_file = components_dir / f"{slug}.md"
            individual_content = [
                f"# Material 3 Expressive - {comp_name}\n",
                f"**Categoria:** `{cat_name}`  ",
                f"**Sezione Principale:** [{cat_meta['title']}](../{cat_name}.md)\n",
                "---\n",
                section_md
            ]
            with open(individual_file, 'w', encoding='utf-8') as f:
                f.write('\n'.join(individual_content))

            cat_index += 1

        # File consolidato della categoria
        category_file = references_dir / f"{cat_name}.md"
        cat_doc_parts = [
            f"# Material 3 Expressive - {cat_meta['title']}\n",
            f"{cat_meta['desc']}\n",
            "> **Riferimenti Locali:** Per la scheda dettagliata di ogni singolo componente, consulta i file dedicati in `references/components/`.\n",
            "---\n",
            '\n'.join(category_sections)
        ]

        with open(category_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(cat_doc_parts))
        print(f"\n  [✓] File categoria generato: {category_file}", flush=True)

    # Indice generale navigabile README.md
    readme_path = references_dir / "README.md"
    readme_lines = [
        "# Material 3 Expressive - Documentazione Locale Offline (References)\n",
        "Questo archivio contiene le linee guida ufficiali, firme Composable, token e snippet di codice pronti all'uso per Jetpack Compose conformi a **Material 3 Expressive**.\n",
        "Tutti i dati sono generati ed aggiornati automaticamente tramite lo script `scripts/download_references.py` a partire da `scripts/ui-components.md`.\n",
        "## Categorie Principali\n",
        "| Categoria | Descrizione | File di Riferimento | Componenti Inclusi |",
        "|---|---|---|---|"
    ]

    for cat_name, cat_meta in CATEGORY_METADATA.items():
        comp_names = ", ".join([c["name"] for c in categories.get(cat_name, [])])
        readme_lines.append(f"| **{cat_meta['title']}** | {cat_meta['desc']} | [{cat_name}.md](./{cat_name}.md) | {comp_names} |")

    readme_lines.append("\n## Schede Componenti Individuali\n")
    readme_lines.append("Per consultare direttamente le specifiche e i codici di un singolo elemento:\n")
    for comp in sorted(components, key=lambda x: x["name"]):
        s = slugify(comp["name"])
        readme_lines.append(f"* [{comp['name']}](./components/{s}.md) (`{comp['category']}`)")

    with open(readme_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(readme_lines))

    print(f"\n==========================================", flush=True)
    print(f"[✓] Documentazione references completata con successo!", flush=True)
    print(f"[✓] File di categoria aggiornati in: {references_dir}", flush=True)
    print(f"[✓] {len(components)} schede componenti individuali create in: {components_dir}", flush=True)
    print(f"[✓] Indice generale generato in: {readme_path}", flush=True)
    print(f"==========================================", flush=True)


def main():
    parser = argparse.ArgumentParser(description="Scarica e organizza la documentazione di UI Components per M3 Expressive")
    parser.add_argument(
        "--input", "-i",
        type=Path,
        default=Path(__file__).parent / "ui-components.md",
        help="Percorso al file markdown sorgente (default: scripts/ui-components.md)"
    )
    parser.add_argument(
        "--output-dir", "-o",
        type=Path,
        default=Path(__file__).parent.parent / "references",
        help="Directory di destinazione per le referenze (default: ../references)"
    )
    parser.add_argument(
        "--category", "-c",
        type=str,
        default=None,
        help="Scarica/aggiorna solo una categoria specifica (navigation, actions, inputs, containment-surfaces, display-feedback)"
    )
    parser.add_argument(
        "--force", "-f",
        action="store_true",
        help="Bypassa la cache locale e riscarica tutte le pagine dal web"
    )
    parser.add_argument(
        "--clean-cache",
        action="store_true",
        help="Cancella tutti i file nella cache locale ed esce"
    )

    args = parser.parse_args()

    if args.clean_cache:
        cache_dir = args.input.parent / ".cache"
        CacheManager(cache_dir).clear()
        print("[✓] Cache locale pulita con successo.", flush=True)
        return

    process_references(
        ui_components_path=args.input,
        references_dir=args.output_dir,
        bypass_cache=args.force,
        category_filter=args.category
    )


if __name__ == "__main__":
    main()
