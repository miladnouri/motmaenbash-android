package nu.milad.motmaenbash.models

data class Link(
    val type: Int,
    val title: String,
    val description: String?,
    val image: String?,
    val link: String,
    val color: String?
)
