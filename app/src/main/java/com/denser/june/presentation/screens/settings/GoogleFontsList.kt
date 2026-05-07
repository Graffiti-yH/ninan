package com.denser.june.presentation.screens.settings

import com.denser.june.core.domain.model.enums.FontCategory

data class GoogleFontMetadata(
    val name: String,
    val category: FontCategory
)

val googleFontsMetadata = listOf(
    // Sans Serif
    GoogleFontMetadata("Roboto", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Inter", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Open Sans", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Poppins", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Montserrat", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Raleway", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Nunito", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Outfit", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Figtree", FontCategory.SANS_SERIF),
    GoogleFontMetadata("Mulish", FontCategory.SANS_SERIF),

    // Serif
    GoogleFontMetadata("Playfair Display", FontCategory.SERIF),
    GoogleFontMetadata("Lora", FontCategory.SERIF),
    GoogleFontMetadata("Merriweather", FontCategory.SERIF),
    GoogleFontMetadata("EB Garamond", FontCategory.SERIF),
    GoogleFontMetadata("Bitter", FontCategory.SERIF),
    GoogleFontMetadata("Crimson Text", FontCategory.SERIF),
    GoogleFontMetadata("Libre Baskerville", FontCategory.SERIF),
    GoogleFontMetadata("Zilla Slab", FontCategory.SERIF),
    GoogleFontMetadata("Cormorant Garamond", FontCategory.SERIF),
    GoogleFontMetadata("Domine", FontCategory.SERIF),

    // Handwriting
    GoogleFontMetadata("Dancing Script", FontCategory.HANDWRITING),
    GoogleFontMetadata("Pacifico", FontCategory.HANDWRITING),
    GoogleFontMetadata("Caveat", FontCategory.HANDWRITING),
    GoogleFontMetadata("Indie Flower", FontCategory.HANDWRITING),
    GoogleFontMetadata("Satisfy", FontCategory.HANDWRITING),
    GoogleFontMetadata("Great Vibes", FontCategory.HANDWRITING),
    GoogleFontMetadata("Shadows Into Light", FontCategory.HANDWRITING),
    GoogleFontMetadata("Kaushan Script", FontCategory.HANDWRITING),
    GoogleFontMetadata("Yellowtail", FontCategory.HANDWRITING),
    GoogleFontMetadata("Courgette", FontCategory.HANDWRITING),

    // Display
    GoogleFontMetadata("Abril Fatface", FontCategory.DISPLAY),
    GoogleFontMetadata("Lobster", FontCategory.DISPLAY),
    GoogleFontMetadata("Bebas Neue", FontCategory.DISPLAY),
    GoogleFontMetadata("Oswald", FontCategory.DISPLAY),
    GoogleFontMetadata("Cinzel", FontCategory.DISPLAY),
    GoogleFontMetadata("Comfortaa", FontCategory.DISPLAY),
    GoogleFontMetadata("Righteous", FontCategory.DISPLAY),
    GoogleFontMetadata("Teko", FontCategory.DISPLAY),
    GoogleFontMetadata("Orbitron", FontCategory.DISPLAY),
    GoogleFontMetadata("Permanent Marker", FontCategory.DISPLAY),

    // Monospace
    GoogleFontMetadata("Roboto Mono", FontCategory.MONOSPACE),
    GoogleFontMetadata("Space Grotesk", FontCategory.MONOSPACE),
    GoogleFontMetadata("JetBrains Mono", FontCategory.MONOSPACE),
    GoogleFontMetadata("Fira Code", FontCategory.MONOSPACE),
    GoogleFontMetadata("Inconsolata", FontCategory.MONOSPACE),
    GoogleFontMetadata("IBM Plex Mono", FontCategory.MONOSPACE),
    GoogleFontMetadata("Courier Prime", FontCategory.MONOSPACE),
    GoogleFontMetadata("Source Code Pro", FontCategory.MONOSPACE),
    GoogleFontMetadata("Anonymous Pro", FontCategory.MONOSPACE),
    GoogleFontMetadata("Share Tech Mono", FontCategory.MONOSPACE)
).sortedBy { it.name }

val googleFontsList = googleFontsMetadata.map { it.name }
