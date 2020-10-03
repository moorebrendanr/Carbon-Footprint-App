package com.spice.carboncrushersrefactor

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.spice.carboncrushersrefactor.database.CarbonDatabase
import com.spice.carboncrushersrefactor.database.DailyLog
import com.spice.carboncrushersrefactor.database.DailyLogDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.util.*

class DatabaseTest {
    private lateinit var dailyLogDao: DailyLogDao
    private lateinit var db: CarbonDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, CarbonDatabase::class.java)
                .fallbackToDestructiveMigration().build()
        dailyLogDao = db.dailyLogDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDelete() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.MONTH, Calendar.JUNE)
        calendar.set(Calendar.DAY_OF_MONTH, 20)
        val dailyLogJohn = DailyLog(
                user = "john",
                date = calendar.time,
                receivedElectricBill = false,
                receivedGasBill = true,
                gasBillAmount = 100.0,
                gasBillUnit = "dollars",
                meatServings = 2,
                travelCar = 10
        )
        dailyLogDao.insert(dailyLogJohn)
        var logs = dailyLogDao.getAll()
        assertThat(logs.size).isEqualTo(1)
        assertThat(logs[0].id).isEqualTo(1)
        assertThat(logs[0].user).isEqualTo("john")
        val calendar2 = Calendar.getInstance()
        calendar2.time = logs[0].date
        assertThat(calendar2.get(Calendar.YEAR)).isEqualTo(calendar.get(Calendar.YEAR))
        assertThat(calendar2.get(Calendar.MONTH)).isEqualTo(calendar.get(Calendar.MONTH))
        assertThat(calendar2.get(Calendar.DAY_OF_MONTH)).isEqualTo(calendar.get(Calendar.DAY_OF_MONTH))
        assertThat(logs[0].receivedElectricBill).isFalse()
        assertThat(logs[0].electricBillAmount).isNull()
        assertThat(logs[0].electricBillUnit).isNull()
        assertThat(logs[0].receivedGasBill).isTrue()
        assertThat(logs[0].gasBillAmount).isEqualTo(100.0)
        assertThat(logs[0].gasBillUnit).isEqualTo("dollars")
        assertThat(logs[0].meatServings).isEqualTo(2)
        assertThat(logs[0].packagedMeals).isEqualTo(0)
        assertThat(logs[0].nonLocalProduceServings).isEqualTo(0)
        assertThat(logs[0].travelCar).isEqualTo(10)
        assertThat(logs[0].travelBus).isEqualTo(0)
        assertThat(logs[0].travelBicycle).isEqualTo(0)
        assertThat(logs[0].travelTrain).isEqualTo(0)
        assertThat(logs[0].travelPlane).isEqualTo(0)
        assertThat(logs[0].travelWalking).isEqualTo(0)

        val dailyLogMary = DailyLog(
                user = "mary",
                date = calendar.time,
                receivedElectricBill = false,
                receivedGasBill = false,
                packagedMeals = 2,
                travelWalking = 3
        )
        dailyLogDao.insert(dailyLogMary)
        logs = dailyLogDao.getAll()
        assertThat(logs.size).isEqualTo(2)
        assertThat(logs[0].id).isEqualTo(1)
        assertThat(logs[1].id).isEqualTo(2)
        assertThat(logs[1].user).isEqualTo("mary")

        dailyLogDao.delete(logs[1])
        logs = dailyLogDao.getAll()
        assertThat(logs.size).isEqualTo(1)
        assertThat(logs[0].id).isEqualTo(1)

        dailyLogDao.delete(logs[0])
        logs = dailyLogDao.getAll()
        assertThat(logs.size).isEqualTo(0)
    }
}