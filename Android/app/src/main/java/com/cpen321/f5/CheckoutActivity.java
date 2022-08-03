package com.cpen321.f5;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;

import com.cpen321.f5.viewmodel.CheckoutViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.cpen321.f5.databinding.ActivityCheckoutBinding;

/**
 * Checkout implementation for the app
 */
public class CheckoutActivity extends AppCompatActivity {

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private CheckoutViewModel model;

    private View googlePayButton;
    private final static String TAG = "CheckoutActivity";

    //new added code ***

    RequestQueue requestQueue;


    private Button loadButton;
    private EditText addedFundText;
    private TextView balance;
    private int balanceAmount = 0;
    private int addedFund = 0;

    String GETPROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
    String POSTPROFILEURL = "http://20.106.78.177:8081/user/updateprofile/";
    //**************
    /**
     * Initialize the Google Pay API on creation of the activity
     *
     * @see AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new added code*********
        setContentView(R.layout.activity_checkout);
        requestQueue = Volley.newRequestQueue(this);
        balance = findViewById(R.id.checkout_wallet_balance);
        addedFundText = findViewById(R.id.checkout_wallet_amount_to_load);
        GetBalance();




        initializeUi();

        model = new ViewModelProvider(this).get(CheckoutViewModel.class);
        model.canUseGooglePay.observe(this, this::setGooglePayAvailable);







    }

    private void initializeUi() {

        // Use view binding to access the UI elements
        ActivityCheckoutBinding layoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());

        // The Google Pay button is a layout file – take the root view
        googlePayButton = layoutBinding.googlePayButton.getRoot();
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validCheck()){
                    Toast.makeText(CheckoutActivity.this, "add fund is not set yet",Toast.LENGTH_SHORT).show();
                } else{
                    addedFund = Integer.parseInt(addedFundText.getText().toString().trim());


                    requestPayment(v);
                }
            }
        });
        //googlePayButton.setOnClickListener(this::requestPayment);
    }

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text.
     * Otherwise, notify the user that Google Pay is not available. Please adjust to fit in with
     * your current user flow. You are not required to explicitly let the user know if isReadyToPay
     * returns {@code false}.
     *
     * @param available isReadyToPay API response.
     */
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.googlepay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        googlePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        long dummyPriceCents = addedFund;
        long shippingCostCents = 0;
        long totalPriceCents = dummyPriceCents + shippingCostCents;
        final Task<PaymentData> task = model.getLoadPaymentDataTask(totalPriceCents);

        // Shows the payment sheet and forwards the result to the onActivityResult method.
        AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode  Result code returned by the Google Pay API.
     * @param data        Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @SuppressWarnings("deprecation")
    // Suppressing deprecation until `registerForActivityResult` can be used with the Google Pay API.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // value passed in AutoResolveHelper
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {

                case AppCompatActivity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    handlePaymentSuccess(paymentData);
                    break;

                case AppCompatActivity.RESULT_CANCELED:
                    // The user cancelled the payment attempt
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    handleError(status);
                    break;

                default:
                    break;
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.setClickable(true);
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(@Nullable PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, getString(R.string.payments_show_name, billingName),
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token: ", token);
            Log.d(TAG, "PAY SUCCESS");

            ReloadBalance();




        } catch (JSONException e) {
            Log.e("CheckoutActivity", "The selected garment cannot be parsed from the list of elements");
            //throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
            Log.d(TAG, "PAY FAIL");
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param status will hold the value of any constant from CommonStatusCode or one of the
     *               WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(@Nullable Status status) {
        String errorString = "Unknown error.";
        if (status != null) {
            int statusCode = status.getStatusCode();
            errorString = String.format(Locale.getDefault(), "Error code: %d", statusCode);
        }

        Log.e("loadPaymentData failed", errorString);
    }


    //***new added code*******
    private void GetBalance ()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFILEURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, response.toString());
                balance = findViewById(R.id.checkout_wallet_balance);
                try
                {
                    balanceAmount = Integer.parseInt(response.getString("balance"));
                    balance.setText(Integer.toString(balanceAmount));
                    Log.d(TAG, "balanceAmount = " + balanceAmount);
                }
                catch (Exception w)
                {
                    Toast.makeText(CheckoutActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(CheckoutActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void ReloadBalance ()
    {
        RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.PUT, POSTPROFILEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(CheckoutActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(CheckoutActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                int newBalance = balanceAmount + addedFund;

                params.put("UserID", MainActivity.idOfUser);
                params.put("balance", Integer.toString(newBalance));
                return params;
            }
        };
        balance = findViewById(R.id.checkout_wallet_balance);
        addedFundText = findViewById(R.id.checkout_wallet_amount_to_load);
        Log.d(TAG, "new balanceAmount = " + Integer.toString(balanceAmount + addedFund));
        balance.setText(Integer.toString(balanceAmount + addedFund));
        addedFundText.setText("");
        Toast.makeText(CheckoutActivity.this, "fund is added successfully",Toast.LENGTH_SHORT).show();
        queue.add(postRequest);
    }

    private boolean validCheck(){
        addedFundText = findViewById(R.id.checkout_wallet_amount_to_load);
        if (addedFundText.getText().toString().equals("")){
            return false;
        }
        return true;
    }
}