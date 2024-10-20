package com.example.mindwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;

public class Survey extends AppCompatActivity {

    TextView question, questionNumberText;
    RadioGroup options;
    Button submit;
    boolean isLastQuestion = false;
    TextInputLayout lastQuestion;
    String question_text = "";
    String jsonFile;
    String currentSelectedOption;
    int totalOptions, currentOptionIndex, questionNumber = 0, currentScore, totalQuestion = 30;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    TextToSpeech textToSpeech;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        questionNumberText = findViewById(R.id.currentQuestionText);
        question = findViewById(R.id.questionTextView);
        options = findViewById(R.id.optionsRadioGroup);
        submit = findViewById(R.id.submitButton);
        lastQuestion = findViewById(R.id.lastQuestion);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        fetchQuestionsFromApi();
        getCurrentQuestion(questionNumber);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(Survey.this, "Language not supported for TTS", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(Survey.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_progress, null);
        builder.setView(dialogView);

        final AlertDialog customDialog = builder.create();

        final RadioButton[] previousCheckedRadioButton = {null};

        options.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton newCheckedRadioButton = findViewById(checkedId);

            if (previousCheckedRadioButton[0] != null && previousCheckedRadioButton[0] != newCheckedRadioButton) {
                previousCheckedRadioButton[0].setTextColor(Color.BLACK);
                previousCheckedRadioButton[0].setBackgroundResource(R.drawable.stroke_background);
            }

            previousCheckedRadioButton[0] = newCheckedRadioButton;
            newCheckedRadioButton.setTextColor(Color.WHITE);
            newCheckedRadioButton.setBackgroundResource(R.drawable.option_button);

            currentSelectedOption = newCheckedRadioButton.getText().toString();
            currentOptionIndex = checkedId;

            // Speak the selected answer
            speakText(currentSelectedOption);
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100));

                if (isLastQuestion) {
                    customDialog.show();
                    LocalDate currentDate = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        currentDate = LocalDate.now();
                    }
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("Score", "" + currentScore);
                    data.put("Date", "" + currentDate);
                    ref.child("User").child(mAuth.getCurrentUser().getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ref.child("Data").child(mAuth.getCurrentUser().getUid()).push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            customDialog.dismiss();
                                            HomeActivity();
                                        } else {
                                            customDialog.dismiss();
                                            Toast.makeText(Survey.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                customDialog.dismiss();
                                Toast.makeText(Survey.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return;
                }

                if (TextUtils.isEmpty(currentSelectedOption)) {
                    Toast.makeText(Survey.this, "please Select Option", Toast.LENGTH_SHORT).show();
                } else {
                    currentScore += getCurrentScore(questionNumber, currentOptionIndex);
                    questionNumber++;
                    if (questionNumber == totalQuestion) {
                        submit.setText("Submit");
                    }
                    getCurrentQuestion(questionNumber);
                }
            }
        });
    }

    private void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    int getCurrentScore(int questionIndex, int optionIndex) {
        int score = 0;
        try {
            JSONArray jsonArray = new JSONArray(jsonFile);
            JSONObject questionObject = jsonArray.getJSONObject(questionIndex);
            JSONObject optionObject = questionObject.getJSONObject("option" + optionIndex);
            score = optionObject.getInt("score");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    void getCurrentQuestion(int index) {
        if (questionNumber >= totalQuestion) {
            questionNumberText.setText("Q" + (index + 1));
            options.removeAllViews();
            question.setText("Do you like to share something..");
            lastQuestion.setVisibility(View.VISIBLE);
            isLastQuestion = true;
        } else {
            try {
                Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
                question.startAnimation(slideOut);
                options.startAnimation(slideOut);
                questionNumberText.setText("Q" + (index + 1));
                currentSelectedOption = null;
                options.removeAllViews();
                JSONArray jsonArray = new JSONArray(jsonFile);
                totalQuestion = jsonArray.length();
                JSONObject questionObject = jsonArray.getJSONObject(index);
                question.setText(questionObject.getString("question"));

                // Speak the current question
                speakText(questionObject.getString("question"));

                Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                question.startAnimation(fadeIn);
                options.startAnimation(fadeIn);
                totalOptions = questionObject.getInt("totalOptions");
                for (int i = 1; i <= totalOptions; i++) {
                    RadioButton radioButton = new RadioButton(this);
                    JSONObject option1Object = questionObject.getJSONObject("option" + i);
                    radioButton.setText(option1Object.getString("text"));
                    radioButton.setTextColor(Color.BLACK);
                    radioButton.setBackgroundResource(R.drawable.stroke_background);
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    radioButton.setPaddingRelative(20, 0, 0, 0);
                    radioButton.setTypeface(null, Typeface.BOLD);
                    radioButton.setButtonDrawable(android.R.color.transparent);
                    radioButton.setTextSize(18);

                    int marginTopInDp = 18;
                    float scale = getResources().getDisplayMetrics().density;
                    int marginTopInPixels = (int) (marginTopInDp * scale + 0.5f);
                    int desiredHeightInDp = 40;
                    int desiredHeightInPixels = (int) (desiredHeightInDp * scale + 0.5f);
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.MATCH_PARENT,
                            desiredHeightInPixels
                    );

                    layoutParams.setMargins(0, marginTopInPixels, 0, 0);
                    radioButton.setLayoutParams(layoutParams);
                    radioButton.setId(i);
                    Typeface customFont = Typeface.createFromAsset(getAssets(), "robotoregular.ttf");
                    radioButton.setTypeface(customFont);
                    radioButton.startAnimation(fadeIn);
                    options.addView(radioButton);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fetchQuestionsFromApi() {
        new FetchQuestionsTask().execute();
    }

    private class FetchQuestionsTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://mindwell-api.vercel.app/api/questions";
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } else {
                    return null; // Handle error case
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            jsonFile=response.toString();
            return jsonFile;// This will return the JSON response
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse != null) {
                // Handle the JSON response
                jsonFile = jsonResponse;
                getCurrentQuestion(questionNumber); // Call this to update the UI with questions
            } else {
                Toast.makeText(Survey.this, "Failed to fetch questions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void HomeActivity()
    {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
        finish();
    }
}