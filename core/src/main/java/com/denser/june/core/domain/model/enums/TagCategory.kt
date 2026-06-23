package com.denser.june.core.domain.model.enums

import com.denser.june.core.R

enum class TagCategory(val labelRes: Int, val singularLabelRes: Int, val prefix: String?) {
    Spaces(R.string.tag_spaces, R.string.tag_spaces_singular, null),
    People(R.string.tag_people, R.string.tag_people_singular, "@"),
    Topics(R.string.tag_topics, R.string.tag_topics_singular, "#");
}
