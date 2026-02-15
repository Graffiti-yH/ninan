package com.denser.june.core.domain.enums

enum class TagCategory(val label: String, val singularLabel: String, val prefix: String?) {
    Spaces("Spaces", "Space", null),
    People("People", "Person", "@"),
    Themes("Themes", "Theme", "#");
}