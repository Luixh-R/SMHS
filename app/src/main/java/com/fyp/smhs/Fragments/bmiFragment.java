package com.fyp.smhs.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fyp.smhs.R;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bmiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bmiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final static String TAG = "this.class.getSimpleName();";
    private final static String PREF_IS_METRIC = "system_of_unit";

    TextView txt_result_bmi;
    TextView txt_result_cat;
    AutoCompleteTextView txt_height;
    AutoCompleteTextView txt_weight;
    SharedPreferences sharedPreferences;
    RadioButton btn_metric;
    RadioButton btn_imperial;
    int run1,run2;

    public bmiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bmiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static bmiFragment newInstance(String param1, String param2) {
        bmiFragment fragment = new bmiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bmi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View v = getView();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        txt_height = v.findViewById(R.id.txt_height);
        txt_weight = v.findViewById(R.id.txt_weight);
        initTextField(txt_height);
        initTextField(txt_weight);

        txt_result_bmi = v.findViewById(R.id.txt_result_bmi);
        txt_result_cat = v.findViewById(R.id.txt_result_cat);
        Button btn_more_info = v.findViewById(R.id.btn_more_info);
        btn_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.wikipedia_bmi_link)));
                startActivity(browserIntent);
            }
        });

         btn_metric = v.findViewById(R.id.btn_metric);
        btn_metric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout txt_weight_outer = v.findViewById(R.id.txt_weight_outer);
                TextInputLayout txt_height_outer = v.findViewById(R.id.txt_height_outer);
                TextView textView1 = v.findViewById(R.id.textView6);
                TextView textView2 = v.findViewById(R.id.textView5);
                textView1.setText(R.string.height_metric);
                textView2.setText(R.string.weight_metric);


                if(txt_weight.getText() != null)
                    if ((txt_height.getText() != null)) {
                        if (run2 == 0) {

                            run1 = 0;
                            run2 = 1;
                            double h = getTextAsDouble(txt_height);
                            double w = getTextAsDouble(txt_weight);
                            double h2 = h / 39.37;
                            double w2 = w / 2.205;
                            //     String h3  = getString(h2);
                            String s = String.format(String.valueOf(h2));
                            String s2 = String.format(String.valueOf(w2));
                            txt_height.setText(s);
                            txt_weight.setText(s2);
                        }
                    }

                btn_metric.setChecked(true);
                calculateBmiIfPossible();
            }
        });

         btn_imperial = v.findViewById(R.id.btn_imperial);
        btn_imperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputLayout txt_weight_outer = v.findViewById(R.id.txt_weight_outer);
                TextInputLayout txt_height_outer = v.findViewById(R.id.txt_height_outer);


                TextView textView1 = v.findViewById(R.id.textView6);
                TextView textView2 = v.findViewById(R.id.textView5);
                textView1.setText(R.string.height_imperial);
                textView2.setText(R.string.weight_imperial);


                if(txt_height.getText() != null)
                    if ((txt_weight.getText() != null)){
        if (run1 == 0) {

            run1 = 1;
            run2 = 0;
            double h = getTextAsDouble(txt_height);
            double w = getTextAsDouble(txt_weight);
            double h2 = h * 39.37;
            double w2 = w * 2.205;
            //     String h3  = getString(h2);
            String s = String.format(String.valueOf(h2));
            String s2 = String.format(String.valueOf(w2));

            txt_height.setText(s);
            txt_weight.setText(s2);
            //   txt_weight.setText(();
        }
        }

                btn_imperial.setChecked(true);
                calculateBmiIfPossible();


            }
        });



       // setSystemOfUnits();

    }



    private Object convertMetrictoimperial(double textAsDouble) {

           double convert1 = textAsDouble * 39.37;


        return convert1;

    }

    private void initTextField(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateBmiIfPossible();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    private void calculateBmiIfPossible() {
        if (isValidInput(txt_height) && isValidInput(txt_weight)) {
            if(btn_imperial.isChecked()) {
                double bmi = calculateBmiAndCast(getTextAsDouble(txt_height), getTextAsDouble(txt_weight));
                txt_result_bmi.setText(getString(R.string.bmi_result, bmi));
                txt_result_cat.setText(getCategory(bmi));
            }
            else
            {

                double bmi = calculateBmi(getTextAsDouble(txt_height), getTextAsDouble(txt_weight));
                txt_result_bmi.setText(getString(R.string.bmi_result, bmi));
                txt_result_cat.setText(getCategory(bmi));



            }
        } else {
            txt_result_bmi.setText("");
            txt_result_cat.setText("");
        }
    }

    private boolean isValidInput(EditText editText) {
        return getTextAsDouble(editText) > 0;
    }

    private double getTextAsDouble(EditText editText) {
        String input = editText.getText().toString().replace(',', '.');
        try {
            return Double.valueOf(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private double calculateBmiAndCast(double height, double weight) {
        height =  height / 39.37008;
        weight = weight / 2.204623;
        return calculateBmi(height, weight);
    }

    private double calculateBmiAndCastIfNeeded(double height, double weight) {
        height = isMetric() ? height : height / 39.37008;
        weight = isMetric() ? weight : weight / 2.204623;
        return calculateBmi(height, weight);
    }

    public static double calculateBmi(double height, double weight) {
        return Math.round(weight / Math.pow(height, 2) * 10d) / 10d;
    }

    private String getCategory(double bmi) {
        if (bmi < 15) {
            return getString(R.string.bmi_cat_1);
        }
        if (bmi < 16) {
            return getString(R.string.bmi_cat_2);
        }
        if (bmi < 18.5) {
            return getString(R.string.bmi_cat_3);
        }
        if (bmi < 25) {
            return getString(R.string.bmi_cat_4);
        }
        if (bmi < 30) {
            return getString(R.string.bmi_cat_5);
        }
        if (bmi < 35) {
            return getString(R.string.bmi_cat_6);
        }
        if (bmi < 40) {
            return getString(R.string.bmi_cat_7);
        }
        if (bmi < 45) {
            return getString(R.string.bmi_cat_8);
        }
        if (bmi < 50) {
            return getString(R.string.bmi_cat_9);
        }
        if (bmi < 60) {
            return getString(R.string.bmi_cat_10);
        }
        return getString(R.string.bmi_cat_11);
    }

    private void setSystemOfUnits() {
        View v = getView();
        RadioButton btn_metric = v.findViewById(R.id.btn_metric);
        RadioButton btn_imperial = v.findViewById(R.id.btn_imperial);
        btn_metric.setChecked(isMetric());
        btn_imperial.setChecked(!isMetric());

        TextInputLayout txt_weight_outer = v.findViewById(R.id.txt_weight_outer);
        TextInputLayout txt_height_outer = v.findViewById(R.id.txt_height_outer);
        txt_weight_outer.setHint(isMetric() ? getString(R.string.weight_metric) : getString(R.string.weight_imperial));
        txt_height_outer.setHint(isMetric() ? getString(R.string.height_metric) : getString(R.string.height_imperial));
    }

    private boolean isMetric() {
        boolean defaultToMetric = getString(R.string.default_unit).equals(getString(R.string.metric));
        return sharedPreferences.getBoolean(PREF_IS_METRIC, defaultToMetric);
    }

    public void setSystemOfUnits(View v) {
        sharedPreferences.edit().putBoolean(PREF_IS_METRIC, v.getId() == R.id.btn_metric).apply();
        setSystemOfUnits();
        calculateBmiIfPossible();
    }
}