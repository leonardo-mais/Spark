# Material 3 Expressive - Carousel

**Categoria:** `display-feedback`  
**Sezione Principale:** [Display & Feedback](../display-feedback.md)

---

## 2. Carousel

A carousel displays a scrollable list of items that adapt dynamically based on
window size. Use carousels to showcase a collection of related content.
Carousel items emphasize visuals, but can also contain brief text that adapts to
the item size.

### Esempio Jetpack Compose
```kotlin
@Composable fun CarouselExample_MultiBrowse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.cupcake, "cupcake"),
            CarouselItem(1, R.drawable.donut, "donut"),
            CarouselItem(2, R.drawable.eclair, "eclair"),
            CarouselItem(3, R.drawable.froyo, "froyo"),
            CarouselItem(4, R.drawable.gingerbread, "gingerbread"),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/carousel](https://developer.android.com/develop/ui/compose/components/carousel)

---
