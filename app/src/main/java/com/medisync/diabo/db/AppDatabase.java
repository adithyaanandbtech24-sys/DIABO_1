package com.medisync.diabo.db;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.medisync.diabo.model.*;
import java.util.List;

@Database(entities = {MedicalReport.class, UserProfile.class, LabResult.class, Medication.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "medisync_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    @Dao
    public interface AppDao {
        // User Profile
        @Query("SELECT * FROM user_profiles LIMIT 1")
        LiveData<UserProfile> getUserProfile();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertUserProfile(UserProfile profile);

        // Medical Reports
        @Query("SELECT * FROM medical_reports ORDER BY uploadDate DESC")
        LiveData<List<MedicalReport>> getAllReports();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertReport(MedicalReport report);

        @Delete
        void deleteReport(MedicalReport report);

        // Lab Results
        @Query("SELECT * FROM lab_results WHERE reportId = :reportId")
        LiveData<List<LabResult>> getLabResultsForReport(String reportId);

        @Query("SELECT * FROM lab_results ORDER BY testDate DESC")
        LiveData<List<LabResult>> getAllLabResults();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertLabResults(List<LabResult> results);

        // Medications
        @Query("SELECT * FROM medications WHERE isActive = 1 ORDER BY startDate DESC")
        LiveData<List<Medication>> getActiveMedications();

        @Query("SELECT * FROM medications WHERE isActive = 0 ORDER BY endDate DESC")
        LiveData<List<Medication>> getPastMedications();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertMedications(List<Medication> medications);
        
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertMedication(Medication medication);

        @Delete
        void deleteMedication(Medication medication);
    }
}
