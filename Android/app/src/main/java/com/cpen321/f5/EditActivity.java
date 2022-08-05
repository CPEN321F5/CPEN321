package com.cpen321.f5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener{
    private final String TAG = "EditActivity";

    private String title;
    private String description;
    private String startPrice;
    private String deposit;
    private String category;
    private String status;
    private ImageView img0;
    private ImageView img1;
    private ImageView img2;
    private boolean img0Enabled;
    private boolean img1Enabled;
    private boolean img2Enabled;
    private TextView showLocation;
    private View imageButton;
    private View cameraButton;

    private EditText edit_title;
    private EditText edit_description;
    private TextView edit_startPrice;
    private TextView edit_remain_time;



    private List<String> uploadedImages = new ArrayList<>();
    private List<Bitmap> uploadedBitmaps = new ArrayList<>();
    public static double lat;
    public static double lon;
    private Uri imageUri;
    private String itemID;


    public static Bitmap bitmap0;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;

    public static Drawable drawable0;
    public static Drawable drawable1;
    public static Drawable drawable2;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        img0 = findViewById(R.id.edit_item_img0);
        img1 = findViewById(R.id.edit_item_img1);
        img2 = findViewById(R.id.edit_item_img2);

        itemID = getIntent().getStringExtra("itemID");
        requestQueue = Volley.newRequestQueue(this);

        Spinner dropdownCategory = findViewById(R.id.edit_category_spinner);
        String[] items = getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownCategory.setAdapter(adapter);
        dropdownCategory.setOnItemSelectedListener(this);

        showLocation = findViewById(R.id.edit_show_location);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);

        GETITEM();
        setContent();
        setButton();
    }

    private void setContent() {
        img0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (img0Enabled){
                    uploadedBitmaps.remove(0);
                    uploadedImages.remove(0);
                    refreshImg();
                }
                return true;
            }
        });

        img1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (img1Enabled){
                    uploadedBitmaps.remove(1);
                    uploadedImages.remove(1);
                    refreshImg();
                }
                return true;
            }
        });

        img2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (img2Enabled){
                    uploadedBitmaps.remove(2);
                    uploadedImages.remove(2);
                    refreshImg();
                }
                return true;
            }
        });
        
        //set remaining time here
        //edit_remain_time.setText();
    }

    private void setButton() {
        Button editButton = findViewById(R.id.edit_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                title = ((EditText)findViewById(R.id.edit_title)).getText().toString().trim();
                description = ((EditText)findViewById(R.id.edit_description)).getText().toString().trim();


                if (validCheck()){
                    editDataToServer();
                    Intent MainUI = new Intent(EditActivity.this, MyItemListActivity.class);
                    startActivity(MainUI);
                }
            }
        });

        Button cancelButton = findViewById(R.id.edit_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainUI = new Intent(EditActivity.this, MyItemListActivity.class);
                startActivity(MainUI);
            }
        });

        imageButton = findViewById(R.id.image_add_button);
        imageButton.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            requestForAlbum.launch(intent);
            Toast.makeText(EditActivity.this, "Trying to open album", Toast.LENGTH_SHORT).show();
        });

        cameraButton = findViewById(R.id.edit_camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File photo = new File(getExternalCacheDir(), "output_photo.jpg");

                //if the same photo exist, directly use the existing one
                try {
                    if (photo.exists()) {
                        photo.delete();
                    }
                    photo.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //if system version is higher than 7.0, use Uri is unsafe
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(photo);
                } else {
                    // use file provider to protect data
                    imageUri = FileProvider.getUriForFile(EditActivity.this, "com.f5.camera.fileprovider", photo);
                }

                //open camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //specify the saving path
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //save photo into output_photo.jpg
                requestForCamera.launch(intent);

            }
        });

        ImageView hintButton = findViewById(R.id.edit_hint);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog("Long click the image to remove it\n\n"
                        + "Image capacity is three");
            }
        });
    }


    private void editDataToServer(){
        String url = getString(R.string.url_item_put);
        edit_title = findViewById(R.id.edit_title);
        title = edit_title.getText().toString().trim();
        edit_description = findViewById(R.id.edit_description);
        description = edit_description.getText().toString().trim();
        RequestQueue queue = Volley.newRequestQueue(EditActivity.this);
        StringRequest editRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(EditActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(EditActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("ItemID", itemID);
                params.put("name", title);
                params.put("description", description);
                params.put("location_lat", Double.toString(lat));
                params.put("location_lon", Double.toString(lon));
                params.put("status", status);
                params.put("image_0", sendImg(0));
                params.put("image_1", sendImg(1));
                params.put("image_2", sendImg(2));

                params.put("catagory", category);

                return params;
            }
        };
        queue.add(editRequest);
    }

    private boolean validCheck(){
        if (title.equals("")){
            Toast.makeText(EditActivity.this, "Title should not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.equals("")){
            Toast.makeText(EditActivity.this, "Description should not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uploadedImages.size() == 0){
            Toast.makeText(EditActivity.this, "At least one image is needed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startPrice.equals("")){
            Toast.makeText(EditActivity.this, "Fail, Start Price is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (deposit.equals("")){
            Toast.makeText(EditActivity.this, "Fail, Deposit is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Lat: " + location.getLatitude() + " | Long: " + location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            //Log.d(TAG, "debug sign: " + addresses.size() + "---" + lat + "---" + lon);
            String cityName = addresses.get(0).getLocality();
            Log.d(TAG, "*************city name: " + cityName);

            Formatter formatter = new Formatter();
            formatter.format("%.5f", lat);
            formatter.format("%.5f", lon);
            showLocation.setText("publishing in " + cityName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    private final ActivityResultLauncher<Intent> requestForAlbum =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        try{
                            InputStream Is = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap image = BitmapFactory.decodeStream(Is);



                            Toast.makeText(EditActivity.this, "work", Toast.LENGTH_SHORT).show();
                            uploadedImages.add(getImage(image));
                            uploadedBitmaps.add(image);

                            refreshImg();
                        }catch (FileNotFoundException error){
                            Toast.makeText(EditActivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }
            });

    private ActivityResultLauncher requestForCamera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        try{
                            InputStream Is = getContentResolver().openInputStream(imageUri);
                            Bitmap image = BitmapFactory.decodeStream(Is);

                            uploadedImages.add(getImage(image));
                            uploadedBitmaps.add(image);

                            refreshImg();
                        }catch (FileNotFoundException error){
                            error.printStackTrace();
                        }
                    }
                }
            });


    private String getImage(Bitmap image) {
        ByteArrayOutputStream Os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50, Os);
        String b64String = Base64.encodeToString(Os.toByteArray(), Base64.DEFAULT);

        return b64String;
    }




    private String sendImg(int num){
        if (num >= uploadedImages.size()){
            return "";
        }else{
            return uploadedImages.get(num);
        }
    }

    private void refreshImg(){
        switch (uploadedImages.size()){
            case 0:
                img0Enabled = false;
                img1Enabled = false;
                img2Enabled = false;

                img0.setImageBitmap(null);
                img1.setImageBitmap(null);
                img2.setImageBitmap(null);
                break;
            case 1:
                img0Enabled = true;
                img1Enabled = false;
                img2Enabled = false;

                img0.setImageBitmap(uploadedBitmaps.get(0));
                img1.setImageBitmap(null);
                img2.setImageBitmap(null);
                break;
            case 2:
                img0Enabled = true;
                img1Enabled = true;
                img2Enabled = false;

                img0.setImageBitmap(uploadedBitmaps.get(0));
                img1.setImageBitmap(uploadedBitmaps.get(1));
                img2.setImageBitmap(null);

                imageButton.setVisibility(View.VISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
                break;
            case 3:
                img0Enabled = true;
                img1Enabled = true;
                img2Enabled = true;

                img0.setImageBitmap(uploadedBitmaps.get(0));
                img1.setImageBitmap(uploadedBitmaps.get(1));
                img2.setImageBitmap(uploadedBitmaps.get(2));

                imageButton.setVisibility(View.INVISIBLE);
                cameraButton.setVisibility(View.INVISIBLE);
                break;
            default:
                Log.d(TAG, "invalid image");
                break;
        }
    }

    private void showAlertDialog(String s){
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = getResources().getStringArray(R.array.categories)[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        category = getResources().getStringArray(R.array.categories)[0];
    }

    private void GETITEM ()
    {
        String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/" + itemID + "/";
        Log.d(TAG, GETITEMURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    title = response.getString("name");
                    deposit = response.getString("deposit");
                    String current_price = response.getString("currentPrice");
                    startPrice = response.getString("startPrice");
                    description = response.getString("description");
                    status = response.getString("status");


                    String img0_string = response.getString("image_0");
                    String img1_string = response.getString("image_1");
                    String img2_string = response.getString("image_2");

                    edit_title = findViewById(R.id.edit_title);
                    edit_title.setText(title);

                    edit_startPrice = findViewById(R.id.edit_start_price);
                    edit_startPrice.setText(startPrice);

                    edit_description = findViewById(R.id.edit_description);
                    edit_description.setText(description);

                    TextView edit_deposit = findViewById(R.id.edit_deposit);
                    edit_deposit.setText(deposit);

                    TextView edit_currentPrice = findViewById(R.id.edit_current_price);
                    edit_currentPrice.setText(current_price);

                    bitmap0 = base64ToBitmap(img0_string);
                    drawable0 = new BitmapDrawable(getResources(), bitmap0);
                    img0 = findViewById(R.id.edit_item_img0);
                    img0.setImageBitmap(bitmap0);
                    uploadedImages.add(getImage(bitmap0));
                    Log.d("img0Enabled", uploadedImages.get(0));
                    img0Enabled = true;
                    if(img0Enabled){
                        Log.d("img0Enabled", "is 1");
                    }
                    uploadedBitmaps.add(bitmap0);
                    img0.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (img0Enabled){
                                Log.d("img0Enabled", "is 1");
                                uploadedBitmaps.remove(0);
                                uploadedImages.remove(0);
                                refreshImg();
                            }
                            return true;
                        }
                    });
                    if(!img1_string.equals("")){
                        bitmap1 = base64ToBitmap(img1_string);
                        drawable1 = new BitmapDrawable(getResources(), bitmap1);
                        img1 = findViewById(R.id.edit_item_img1);
                        img1.setImageBitmap(bitmap1);
                        uploadedImages.add(getImage(bitmap1));
                        uploadedBitmaps.add(bitmap1);
                        img1.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (img0Enabled){
                                    uploadedBitmaps.remove(1);
                                    uploadedImages.remove(1);
                                    refreshImg();
                                }
                                return true;
                            }
                        });
                    }
                    if(!img2_string.equals("")){
                        bitmap2 = base64ToBitmap(img2_string);
                        drawable2 = new BitmapDrawable(getResources(), bitmap2);
                        img2 = findViewById(R.id.edit_item_img2);
                        img2.setImageBitmap(bitmap2);
                        uploadedImages.add(getImage(bitmap2));
                        uploadedBitmaps.add(bitmap2);
                        img2.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (img0Enabled){
                                    uploadedBitmaps.remove(2);
                                    uploadedImages.remove(2);
                                    refreshImg();
                                }
                                return true;
                            }
                        });
                    }
                }
                catch (Exception w)
                {
                    //Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private Bitmap base64ToBitmap (String img)
    {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}