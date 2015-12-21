package com.artvi.ms.lab4;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureOverlayView gestures;
    private GestureLibrary gestureLibraryb;
    private TextView infoTextView;
    private EditText numberEditText;
    private Button button;
    private int number;
    private boolean gameEnded;
    private TextInputLayout numberEditTextWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.gestureLibraryb = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if(!gestureLibraryb.load()) {
            finish();
        }

        this.gestures = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
        gestures.addOnGesturePerformedListener(this);

        infoTextView = (TextView) findViewById(R.id.infoTextView);
        numberEditTextWrapper = (TextInputLayout) findViewById(R.id.numberEditTextWrapper);
        numberEditText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameEnded) {
                    infoTextView.setText(R.string.try_to_guess);
                    numberEditText.setText("");
                    button.setText(R.string.guess);
                    number = (new Random()).nextInt(100) + 1;
                    gameEnded = false;
                    return;
                }
                int i;
                try {
                    i = Integer.parseInt(numberEditText.getText().toString());
                    if (i < 1 || i > 100)
                        throw new IllegalArgumentException();
                } catch (RuntimeException e) {
                    infoTextView.setText(R.string.input_error);
                    numberEditText.setText("");
                    return;
                }

                if (i > number)
                    infoTextView.setText(R.string.ahead);
                else if (i < number)
                    infoTextView.setText(R.string.behind);
                else if (i == number) {
                    infoTextView.setText(R.string.hit);
                    button.setText(R.string.play_more);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
                    gameEnded = true;
                }
                numberEditText.setText("");
            }
        });
        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Log.d("Percentage", "input: " + s);
                    int i = Integer.parseInt(s.toString());
                    if (i > 100 || i < 1) {
                        s.replace(0, s.length(), "");
                        numberEditTextWrapper.setError(getString(R.string.input_error));
                    } else
                        numberEditTextWrapper.setError("");
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        List<Prediction> predictions = gestureLibraryb.recognize(gesture);
        if(predictions.size() > 0) {
            if(predictions.get(0).score > 1.0) {
                if(predictions.get(0).name.equals("s")) {
                    this.button.callOnClick();
                }
                else {
                    this.numberEditText.append(predictions.get(0).name);
                }
            }
        }
    }
}