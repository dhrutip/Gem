package com.codepath.gem;

import android.os.Parcel;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.codepath.gem.models.Experience;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

/**
 * Instrumented unit test to verify that the Parcelable interface is
 * implemented correctly for the Experience model
 */
@RunWith(AndroidJUnit4.class)
public class ExperienceParcelableTest {
    private Experience experience;
    public static final String TEST_TITLE = "test title";
    public static final String TEST_DESCRIPTION = "test description";

    @Before
    public void createExperience() {
        experience = new Experience();
    }

    @Test
    public void experience_ParcelableWriteRead() {
        experience.setTitle(TEST_TITLE);
        experience.setDescription(TEST_DESCRIPTION);

        Parcel parcel = Parcel.obtain();
        experience.writeToParcel(parcel, experience.describeContents());

        parcel.setDataPosition(0);
        Experience createdFromParcel = (Experience) Experience.CREATOR.createFromParcel(parcel);
        String titleFromParcelData = createdFromParcel.getTitle();
        String descriptionFromParcelData = createdFromParcel.getDescription();

        assertThat(titleFromParcelData).isEqualTo(TEST_TITLE);
        assertThat(descriptionFromParcelData).isEqualTo(TEST_DESCRIPTION);
    }
}
