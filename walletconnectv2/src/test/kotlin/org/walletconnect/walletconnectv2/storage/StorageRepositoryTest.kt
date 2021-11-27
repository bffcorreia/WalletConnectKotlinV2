package org.walletconnect.walletconnectv2.storage

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.walletconnect.walletconnectv2.Database
import org.walletconnect.walletconnectv2.common.AppMetaData
import org.walletconnect.walletconnectv2.util.CoroutineTestRule

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
internal class StorageRepositoryTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val app = ApplicationProvider.getApplicationContext<Application>()
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        Database.Schema.create(this)
    }
    private val storageRepository = StorageRepository(driver, app)

    @Test
    fun `Insert first instance of metadata and get row id`() {
        val rowId = storageRepository.insertOrGetMetaData(AppMetaData())

        assert(rowId > 0)
    }

    @Test
    fun `Try to insert null metadata and return invalid row id`() {
        val rowId = storageRepository.insertOrGetMetaData(null)

        assert(rowId < 0)
    }

    @Test
    fun `Trying to insert metadata with same name returns id of first instance instead`() {
        val metaData = AppMetaData()

        val rowIdFirst = storageRepository.insertOrGetMetaData(metaData)
        val rowIdSecond = storageRepository.insertOrGetMetaData(metaData)

        assert(rowIdFirst > 0)
        assert(rowIdSecond > 0)
        assert(rowIdFirst == rowIdSecond)
    }

    @Test
    fun `Trying multiple instances of MetaData and get the row ids`() {
        val metaDataFirst = AppMetaData(name = "First Peer")
        val metaDataSecond = AppMetaData(name = "Second Peer")

        val rowIdFirst = storageRepository.insertOrGetMetaData(metaDataFirst)

        val rowIdSecond = storageRepository.insertOrGetMetaData(metaDataSecond)

        assert(rowIdFirst > 0)
        assert(rowIdSecond > 0 && rowIdSecond > rowIdFirst)
    }
}