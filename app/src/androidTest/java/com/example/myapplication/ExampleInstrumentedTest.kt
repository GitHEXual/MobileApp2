package com.example.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteCityDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteCityDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myapplication", appContext.packageName)
    }

    @Test
    fun favoriteDao_performsCrudOperations() = runBlocking {
        dao.insertFavorite(FavoriteCityEntity(cityId = "moscow", note = "first", createdAt = 10L))

        val inserted = dao.getFavoritesFlow().first()
        assertEquals(1, inserted.size)
        assertEquals("first", inserted.first().note)

        dao.insertFavorite(FavoriteCityEntity(cityId = "moscow", note = "duplicate", createdAt = 20L))
        assertEquals(1, dao.getFavoritesFlow().first().size)

        dao.updateNote("moscow", "updated")
        assertEquals("updated", dao.getFavoriteByCityId("moscow")?.note)

        dao.deleteFavorite("moscow")
        assertNull(dao.getFavoriteByCityId("moscow"))
    }
}
