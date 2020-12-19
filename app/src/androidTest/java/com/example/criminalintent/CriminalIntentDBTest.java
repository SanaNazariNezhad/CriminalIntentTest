package com.example.criminalintent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.criminalintent.data.room.CrimeRoomDataBase;
import com.example.criminalintent.data.room.dao.CrimeDAO;
import com.example.criminalintent.data.room.entities.Crime;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class CriminalIntentDBTest {

    private CrimeRoomDataBase mCrimeRoomDataBase;
    private CrimeDAO mCrimeDAO;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        mCrimeRoomDataBase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                CrimeRoomDataBase.class)
                .build();

        mCrimeDAO = mCrimeRoomDataBase.getCrimeDAO();
    }

    @Test
    public void insertCrimeAndGet() throws InterruptedException {
        Crime demoCrime = new Crime(
                UUID.randomUUID(),
                "testTitle",
                new Date(),
                true,
                "testSuspect");

        mCrimeDAO.insertCrimes(demoCrime);

        LiveData<Crime> crimeLiveData = mCrimeDAO.getCrime(demoCrime.getId());
        Crime crime = LiveDataTestUtil.getValue(crimeLiveData);
        MatcherAssert.assertThat(crime, Matchers.equalTo(demoCrime));
    }

    @Test
    public void insertAndUpdateCrime() throws InterruptedException {
        Crime demoCrime = new Crime(
                UUID.randomUUID(),
                "testTitle",
                new Date(),
                true,
                "testSuspect");

        mCrimeDAO.insertCrimes(demoCrime);

        Crime updateCrime = new Crime(
                demoCrime.getId(),
                "updateTitle",
                new Date(),
                true,
                "updateSuspect");
        mCrimeDAO.updateCrimes(updateCrime);

        //TEST
        LiveData<Crime> crimeLiveData = mCrimeDAO.getCrime(demoCrime.getId());
        Crime crime = LiveDataTestUtil.getValue(crimeLiveData);
        MatcherAssert.assertThat(crime, Matchers.equalTo(updateCrime));
    }

    @Test
    public void insertAndDeleteCrime() throws InterruptedException {
        // BEFORE : Adding demo user & demo item. Next, get the item added & delete it.
        Crime demoCrime = new Crime(
                UUID.randomUUID(),
                "testTitle",
                new Date(),
                true,
                "testSuspect");

        mCrimeDAO.insertCrimes(demoCrime);
        LiveData<Crime> crimeLiveData = mCrimeDAO.getCrime(demoCrime.getId());
        Crime crimeAdded = LiveDataTestUtil.getValue(crimeLiveData);
        mCrimeDAO.deleteCrimes(crimeAdded);

        //TEST
        LiveData<List<Crime>> crimeListLiveData = mCrimeDAO.getCrimes();
        List<Crime> crimeList = LiveDataTestUtil.getValue(crimeListLiveData);
        MatcherAssert.assertThat(crimeList.isEmpty(),Matchers.equalTo(true));

    }

    @After
    public void closeDb() {
        mCrimeRoomDataBase.close();
    }
}
