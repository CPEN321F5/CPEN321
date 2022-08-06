package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;

    public static String nameOfUser;
    public static String idOfUser;
    public static String emailOfUser;

    SharedPreferences sharedpreferences;
    public static String MYPREFERENCES = "MyPrefs";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermissions();
        Log.d(TAG, "Trying to request location permissions");
        Toast.makeText(MainActivity.this, "Trying to request location permissions", Toast.LENGTH_SHORT).show();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1073711084344-vvjarmtoahi6mqjur4dglgnocjfm8j4i.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });

        sharedpreferences = getSharedPreferences(MYPREFERENCES, 0);
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        }

        catch (ApiException e)
        {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account)
    {
        if (account == null)
        {
            Log.d(TAG, "THERE IS NO USERS SIGNED IN!");
        }

        else
        {
            Log.d(TAG, "EMAIL: " + account.getEmail());
            Log.d(TAG, "GIVEN NAME: " + account.getGivenName());
            Log.d(TAG, "FAMILY NAME: " + account.getFamilyName());
            Log.d(TAG, "PREFERRED NAME: " + account.getDisplayName());
            Log.d(TAG, "PROFILE IMAGE: " + account.getPhotoUrl());
            Log.d(TAG, "PROFILE ID: " + account.getId());

            nameOfUser = account.getDisplayName();
            idOfUser = account.getId();
            emailOfUser = account.getEmail();

            String URL = "http://20.106.78.177:8081/user/signin/";
            URL = URL + idOfUser + "/";
            Log.d(TAG, URL);
            Log.d(TAG, "ID token" + account.getIdToken());
            GETUSERID(URL, account.getIdToken());


            // Send token to backend server
            //account.getIdToken();

            //admin email: cpen321f5ad@gmail.com
            //password: 321f5admin

//            if (Objects.equals(account.getEmail(), "cpen321f5ad@gmail.com"))
//            {
//                Intent adminIntent = new Intent(MainActivity.this, AdminMain.class);
//                startActivity(adminIntent);
//            }
        }
    }

    public void GETUSERID (final String URL, final String idToken)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.d(TAG, response);

                    //allow login for varified user
                    {
                        if (Objects.equals(emailOfUser, "cpen321f5ad@gmail.com"))
                        {
                            Intent adminIntent = new Intent(MainActivity.this, AdminMain.class);
                            startActivity(adminIntent);
                        }
                        else
                        {
                            Intent userIntent = new Intent(MainActivity.this, MainUI.class);
                            startActivity(userIntent);
                        }
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    // error
                    Log.d("Error.Response", String.valueOf(error));
                }
            }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Token", idToken);

                return params;
            }
        };

        queue.add(stringRequest);
    }


    private void checkLocationPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(MainActivity.this, "We have these permissions yay!", Toast.LENGTH_SHORT).show();
            return;
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(MainActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(this)
                        .setTitle("Need Location Permissions")
                        .setMessage("We need the location permissions to mark your location on a map")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogIntergace, int i) {
                                Toast.makeText(MainActivity.this, "We need these location permissions to run!",  Toast.LENGTH_LONG).show();
                                dialogIntergace.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogIntergace, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


}