package com.example.myapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun normalizedNote_trimsAndDropsBlankValues() {
        assertEquals("note", "  note  ".normalizedNote())
        assertNull("   ".normalizedNote())
        assertNull(null.normalizedNote())
    }

    @Test
    fun favoriteEntity_usesStableCatalogMapPosition() {
        val entity = FavoriteCityEntity(
            cityId = "kazan",
            note = "memo",
            createdAt = 123L
        )

        val favorite = entity.toFavoriteCity()

        assertNotNull(favorite)
        assertEquals(WeatherCatalog.findCity("kazan")?.mapPosition, favorite?.mapPosition)
        assertEquals("memo", favorite?.note)
    }
}
