package com.codepath.gem.fragments;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.gem.MainActivity;
import com.codepath.gem.R;
import com.codepath.gem.SetLocationActivity;
import com.codepath.gem.models.Experience;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 * @source https://guides.codepath.org/android/Accessing-the-Camera-and-Stored-Media
 */
public class CreateFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "CreateFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    private EditText etTitle, etDescription;
    private ImageView ivImageOne, ivImageTwo;
    private Button btnCreate;
    List mBitmapsSelected, filesSelected;
    public static LatLng location;
    private TextView tvSetStartDate, tvSetEndDate, tvSetLocation, tvSetImages;
    private boolean startDatePicked, endDatePicked;
    Date startDate, endDate;
    KonfettiView konfettiView;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(false);
        etTitle = view.findViewById(R.id.etTitle);
        etDescription = view.findViewById(R.id.etDescription);
        tvSetImages = view.findViewById(R.id.tvSetImages);
        ivImageOne = view.findViewById(R.id.ivImageOne);
        ivImageTwo = view.findViewById(R.id.ivImageTwo);
        btnCreate = view.findViewById(R.id.btnCreate);
        tvSetStartDate = view.findViewById(R.id.tvSetStartDate);
        tvSetEndDate = view.findViewById(R.id.tvSetEndDate);
        tvSetLocation = view.findViewById(R.id.tvSetLocation);
        filesSelected = new ArrayList<ParseFile>();
        location = null;
        konfettiView = view.findViewById(R.id.viewKonfetti);

        tvSetImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImages();
                tvSetImages.setText("Images Selected!");
            }
        });

        tvSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SetLocationActivity.class);
                startActivity(intent);
                tvSetLocation.setText("Location Selected!");
            }
        });

        tvSetStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment startDatePicker = new DatePickerFragment();
                startDatePicker.setTargetFragment(CreateFragment.this, 0);
                startDatePicker.show(getFragmentManager(), "startDatePicker");
                startDatePicked = true;
            }
        });

        tvSetEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment endDatePicker = new DatePickerFragment();
                endDatePicker.setTargetFragment(CreateFragment.this, 0);
                endDatePicker.show(getFragmentManager(), "endDatePicker");
                endDatePicked = true;
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                String description = etDescription.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (filesSelected.size() == 0) {
                    Toast.makeText(getContext(), "Please add at least one image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (location == null) {
                    Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDate == null || endDate == null) {
                    Toast.makeText(getContext(), "Please enter start and end dates", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDate.before(Calendar.getInstance().getTime())) {
                    Toast.makeText(getContext(), "Start date cannot be before the current date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startDate.after(endDate)) {
                    Toast.makeText(getContext(), "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
                    return;
                }
                konfettiView.build()
                        .addColors(Color.argb(1, 224, 198, 233), Color.argb(1, 74, 51, 82))
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 4f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(500L)
                        .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(200, 2000L);
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveExperience(title, description, currentUser);
            }
        });
    }

    public void onSelectImages() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_CODE);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // invoked when child application returns to parent application
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                mBitmapsSelected = new ArrayList<Bitmap>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    Bitmap bitmap = loadFromUri(uri);
                    mBitmapsSelected.add(bitmap);
                    filesSelected.add(convertBitmapToParseFile(bitmap));
                }
            }
            switch (mBitmapsSelected.size()) {
                case 2:
                    ivImageTwo.setImageBitmap((Bitmap) mBitmapsSelected.get(1));
                case 1:
                    ivImageOne.setImageBitmap((Bitmap) mBitmapsSelected.get(0));
                case 0:
                    break;
            }
        }
    }

    public ParseFile convertBitmapToParseFile(Bitmap imgBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    private void saveExperience(String title, String description, ParseUser currentUser) {
        Experience experience = new Experience();
        experience.setTitle(title);
        experience.setDescription(description);
        if (filesSelected.size() == 2) {
            experience.setImageOne((ParseFile) filesSelected.get(0));
            experience.setImageTwo((ParseFile) filesSelected.get(1));
        } else if (filesSelected.size() == 1) {
            experience.setImageOne((ParseFile) filesSelected.get(0));
        }
        experience.setLocation(location);
        experience.setHost(currentUser);
        experience.setStartDate(startDate);
        experience.setEndDate(endDate);
        experience.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving", e);
                    Toast.makeText(getContext(), "error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "post save works!");
                etTitle.setText("");
                etDescription.setText("");
                tvSetStartDate.setText("Select Start Date");
                tvSetEndDate.setText("Select End Date");
                ivImageOne.setImageResource(R.drawable.photo_blank);
                ivImageTwo.setImageResource(R.drawable.photo_blank);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        String date = DateFormat.getDateInstance().format(cal.getTime());
        if (startDatePicked) {
            tvSetStartDate.setText(date);
            startDate = cal.getTime();
            startDatePicked = false;
        }
        if (endDatePicked) {
            tvSetEndDate.setText(date);
            endDate = cal.getTime();
            endDatePicked = false;
        }
    }
}