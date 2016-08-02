    package com.practice.venturesitychallenge.activity;

    import android.content.Context;
    import android.content.Intent;
    import android.content.pm.ActivityInfo;
    import android.graphics.Color;
    import android.graphics.Typeface;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.DisplayMetrics;
    import android.util.Log;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.Window;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.CompoundButton;
    import android.widget.EditText;
    import android.widget.ProgressBar;
    import android.widget.RadioButton;
    import android.widget.RadioGroup;
    import android.widget.RelativeLayout;
    import android.widget.ScrollView;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;
    import com.practice.venturesitychallenge.R;
    import com.practice.venturesitychallenge.callbacks.INetworkCallback;
    import com.practice.venturesitychallenge.constants.Constants;
    import com.practice.venturesitychallenge.model.FormField;
    import com.practice.venturesitychallenge.model.ModelTO;
    import com.practice.venturesitychallenge.model.SingletonModel;
    import com.practice.venturesitychallenge.parser.JSONHandler;
    import com.practice.venturesitychallenge.threads.ServiceRequest;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    public class HomeActivity extends BaseActivity implements View.OnClickListener{

        Button getformbtn, submitbtn;
        TextView formtv;
        Context context;
        ProgressBar progressBar;
        RelativeLayout mainrl, subrl;
        int screenWidth;
        int layoutYAxisSetter =1;
        private final String TAG = getClass().getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {


            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            this.getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.colorPrimary)));

            getActionBar().setLogo(R.drawable.ic_action_name);
            getActionBar().setDisplayUseLogoEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
            init();

        }

        private void init() {

            context = getApplicationContext();
            mainrl = (RelativeLayout)findViewById(R.id.mainRl);
            subrl = (RelativeLayout)findViewById(R.id.subrl);

            getformbtn = (Button)findViewById(R.id.getformbtn);
            getformbtn.setOnClickListener(this);

            submitbtn = (Button)findViewById(R.id.submitbtn);
            submitbtn.setOnClickListener(this);

            formtv = (TextView)findViewById(R.id.formtv);
            formtv.setOnClickListener(this);

            progressBar = (ProgressBar)findViewById(R.id.marker_progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
            screenWidth = getScreenWidth();
        }

        public int getScreenWidth(){

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){

                case R.id.getformbtn:

                    clearAllValues();
                    ServiceRequest serviceRequest = new ServiceRequest(Constants.FORM_URL_GET, Constants.GET_REQUEST, null, new INetworkCallback() {
                        @Override
                        public void response(final String modelString) {
                            Log.i(TAG, "modelString is "+ modelString);
                            if(TextUtils.isEmpty(modelString)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, Constants.FAILED_REQUEST,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ModelTO modelTO = JSONHandler.toModel(modelString);
                                        if(modelTO!=null){
                                            if(modelTO.isSuccess()){
                                                if(modelTO.getData().getForm_fields()!=null){
                                                    boolean isFirstView = true;
                                                    for(FormField formField : modelTO.getData().getForm_fields()){
                                                        decideView(formField, isFirstView);
                                                        isFirstView = false;
                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    getformbtn.setX(screenWidth /6);
                                                    submitbtn.setX(screenWidth /2);
                                                    submitbtn.setVisibility(View.VISIBLE);
                                                    SingletonModel.getSingletonModel().setModelTO(modelTO);

                                                }else{
                                                    Toast.makeText(context, Constants.FAILED_REQUEST_FORM_FIELDS,Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Toast.makeText(context, Constants.FAILED_REQUEST_STATUS,Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(context, Constants.FAILED_REQUEST_MODEL,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                    });
                    serviceRequest.execute();
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case R.id.submitbtn:


                    if(isInputValid()){
                        ModelTO modelTO= SingletonModel.getSingletonModel().getModelTO();
                        setValues(modelTO);
                        String json = JSONHandler.toJSON(modelTO);
                        //Log.i(TAG,"The value of the model in json format going to the server is " + json);
                        ServiceRequest serviceRequest1 = new ServiceRequest(Constants.FORM_URL_POST, Constants.POST_REQUEST, JSONHandler.toJSON(modelTO), new INetworkCallback() {
                            @Override
                            public void response(String model) {


                                System.out.println("The response from the server is \n" + model);
                                Intent showResponse = new Intent(getApplicationContext(), SubmitResponseActivity.class);
                                showResponse.putExtra(Constants.ServerResponse, model);
                                progressBar.setVisibility(View.INVISIBLE);
                                startActivity(showResponse);

                            }
                        });
                        serviceRequest1.execute();
                        progressBar.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(context, Constants.FAILED_VALIDITY,Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }


        private void clearAllValues(){

            layoutYAxisSetter = 1;
            subrl.removeAllViews();

        }

        private void setValues(ModelTO modelTO){



            for(FormField formField : modelTO.getData().getForm_fields()){
                if(Constants.CHECKBOX.equalsIgnoreCase(formField.getComponent())){
                    setCheckBoxValue(formField);
                }else if(Constants.RADIO.equalsIgnoreCase(formField.getComponent())){
                    setRadioButtonValue(formField);
                }else if(Constants.SELECT.equalsIgnoreCase(formField.getComponent())){
                    setDropDownValue(formField);
                }else if(Constants.TEXT_AREA.equalsIgnoreCase(formField.getComponent())){
                    setTextInputValue(formField);
                }else if(Constants.TEXT_INPUT.equalsIgnoreCase(formField.getComponent())){
                    setTextInputValue(formField);
                }
            }

        }

        private void setCheckBoxValue(FormField formField){
            List<String> optionsSelected = new ArrayList<String>();
            for(String option: formField.getOptions()) {
                CheckBox checkBox = (CheckBox) findViewById(formField.getOptionsId().get(option));
                if (checkBox.isChecked())
                    optionsSelected.add(option);
            }
            formField.setUserInputSelected(optionsSelected);
        }

        private void setDropDownValue(FormField formField){
            //Spinner value gets selected using onItemClickListener while creation only
        }

        private void setRadioButtonValue(FormField formField){

            String userInput=null;
            RadioGroup radioGroup = (RadioGroup)findViewById(formField.getComponentId());
            if (radioGroup.getCheckedRadioButtonId()!=-1){
                for(String option: formField.getOptions()) {
                    RadioButton radioButton = (RadioButton) findViewById(formField.getOptionsId().get(option));
                    if(radioButton.getId()==radioGroup.getCheckedRadioButtonId()){
                            userInput = option;
                    }
                }
            }
            /*for(String option: formField.getOptions()) {
                RadioButton radioButton = (RadioButton) findViewById(formField.getOptionsId().get(option));
                if(radioButton.isChecked()){
                   userInput = option;
                }
            }*/
            formField.setUserInput(userInput);
        }


        private void setTextInputValue(FormField formField){

            EditText ed = (EditText)findViewById(formField.getComponentId());
            formField.setUserInput(ed.getText().toString());
        }

        /*private void setTextAreaValue(FormField formField){

            TextView tv = (TextView)findViewById(formField.getComponentId());
            formField.setUserInput(tv.getText().toString());
        };*/

        private boolean isInputValid(){

            ModelTO modelTO = SingletonModel.getSingletonModel().getModelTO();
            for(FormField formField : modelTO.getData().getForm_fields()){
                if(Constants.CHECKBOX.equalsIgnoreCase(formField.getComponent())){
                    if(!validateCheckBox(formField)){
                        return false;
                    }
                }else if(Constants.RADIO.equalsIgnoreCase(formField.getComponent())){
                    if(!validateRadio(formField)){
                        return false;
                    }
                }else if(Constants.SELECT.equalsIgnoreCase(formField.getComponent())){
                    if(!validateSelect(formField)){
                        return false;
                    }
                }else if(Constants.TEXT_AREA.equalsIgnoreCase(formField.getComponent())){
                    if(!validateTextInput(formField)){
                        return false;
                    }
                }else if(Constants.TEXT_INPUT.equalsIgnoreCase(formField.getComponent())){
                    if(!validateTextInput(formField)){
                        return false;
                    }
                }
            }
            return true;
        }

        /*private boolean isTextAreaValid(FormField formField){
            if(formField.getRequired()){
                TextView tv = (TextView)findViewById(formField.getComponentId());
                if (TextUtils.isEmpty(tv.getText().toString())){
                    return false;
                }
            }
            return true;
        }*/

        private boolean validateSelect(FormField formField){

            if(formField.getRequired()){
                if(formField.getUserInput()==null){
                    Toast.makeText(context, Constants.REQUEST_DROPDOWN_SELECT,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }

        private boolean validateCheckBox(FormField formField){

            List<String> optionsSelected = new ArrayList<String>();
            if(formField.getRequired()){
                for(String option: formField.getOptions()) {
                    CheckBox checkBox = (CheckBox) findViewById(formField.getOptionsId().get(option));
                    if (checkBox.isChecked())
                        optionsSelected.add(option);
                }
                if(optionsSelected.size()==0){
                    Toast.makeText(context, Constants.REQUEST_CHECKBOX_SELECT,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        }

        private boolean validateTextInput(FormField formField){
            EditText ed = (EditText)findViewById(formField.getComponentId());
            if(formField.getRequired()){
                if (TextUtils.isEmpty(ed.getText().toString())){
                    Toast.makeText(context, Constants.REQUEST_TEXTINPUT_ENTER,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if(formField.getValidation()!=null){
                Pattern p = Pattern.compile(formField.getValidation());
                Matcher m = p.matcher(ed.getText());
               if(!m.matches()){
                   TextView format = (TextView)findViewById(formField.getValidationWarningId());
                   format.setVisibility(View.VISIBLE);
                   Toast.makeText(context, Constants.REQUEST_TEXTINPUT_FORM + formField.getValidation(),Toast.LENGTH_LONG).show();
                   return false;
               }else{
                   TextView format = (TextView)findViewById(formField.getValidationWarningId());
                   format.setVisibility(View.INVISIBLE);
               }
            }
            return true;
        }

        private boolean validateRadio(FormField formField){

            if(formField.getRequired()){
                RadioGroup radioGroup = (RadioGroup)findViewById(formField.getComponentId());

                if (radioGroup.getCheckedRadioButtonId()==-1){
                    Toast.makeText(context, Constants.REQUEST_RADIOBUTTON_SELECT,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }



        private void decideView(FormField formField, boolean isFirstView){

            RelativeLayout rl = createRelativelayout();
            switch(formField.getComponent()){

                case "checkbox":
                    if(!isFirstView) {
                        layoutYAxisSetter++;
                    }
                    createCheckBox(rl, formField);
                    break;

                case "select":

                    if(!isFirstView) {
                        layoutYAxisSetter++;
                    }
                    createDropdownMenu(rl, formField);
                    break;

                case "textarea":
                    if(!isFirstView) {
                        layoutYAxisSetter++;
                    }
                    createEditText(rl, formField);
                    break;

                case "textinput":
                    if(!isFirstView) {
                        layoutYAxisSetter++;
                    }
                    createEditText(rl, formField);
                    break;


                case "radio":
                    if(!isFirstView) {
                        layoutYAxisSetter++;
                    }
                    createRadioButton(rl, formField);
                    break;
            }
            subrl.addView(rl);
        }

        //creating a relative layout
        public RelativeLayout createRelativelayout(){

            RelativeLayout rl = new RelativeLayout(this);
            return rl;
        }

        public void createEditText(RelativeLayout rl, FormField formField){

            TextView tvLabel = new TextView(this);
            tvLabel.setText(formField.getLabel());
            tvLabel.setId(View.generateViewId());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = layoutYAxisSetter++ *75;
            rl.addView(tvLabel, params);

            tvLabel.setTypeface(null, Typeface.BOLD);

            TextView TvDesc = new TextView(this);
            RelativeLayout.LayoutParams paramsDesc = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsDesc.topMargin = layoutYAxisSetter++ *75;
            TvDesc.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            TvDesc.setText(formField.getDescription());
            rl.addView(TvDesc, paramsDesc);


            TextView validationWarning = new TextView(this);
            validationWarning.setText("(" + formField.getValidation()+ ")");
            validationWarning.setTextColor(Color.RED);
            validationWarning.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams paramswarning = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramswarning.topMargin = layoutYAxisSetter++ *75;
            validationWarning.setId(View.generateViewId());
            rl.addView(validationWarning, paramswarning);
            formField.setValidationWarningId(validationWarning.getId());

            EditText myEditText = new EditText(context); // Pass it an Activity or Context
            myEditText.setHintTextColor(Color.GRAY);
            myEditText.setId(View.generateViewId());
            myEditText.setBackgroundResource(R.drawable.edittextstyle);
            myEditText.setTextColor(getResources().getColor(R.color.colorPrimary));
            formField.setComponentId(myEditText.getId());
            if(!formField.getEditable()){
                if(formField.getAutofill()!=null) {
                    myEditText.setText(formField.getAutofill());
                }
                myEditText.setText(formField.getAutofill());
                myEditText.setFocusable(false);
                myEditText.setClickable(false);
            }

            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.topMargin = layoutYAxisSetter++ *75;
            rl.addView(myEditText,params1);
            if(formField.getAutofill()!=null) {
                layoutYAxisSetter += myEditText.getText().length() / 25;
            }
        }

        //Created this thinking that TextArea is a TextView
        /*public void createTextView(RelativeLayout rl, FormField formField){


            TextView tvLabel = new TextView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = layoutYAxisSetter++ *50;
            tvLabel.setText(formField.getLabel());
            rl.addView(tvLabel,params);

            TextView tv = new TextView(this);
            tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.topMargin = layoutYAxisSetter++ *50;
            tv.setText(formField.getDescription());
            tv.setTextColor(Color.BLACK);
            tv.setId(View.generateViewId());
            formField.setComponentId(tv.getId());
            rl.addView(tv,params1);

        }*/

        public void createCheckBox(RelativeLayout rl, FormField formField){


            TextView checkBoxLabel = new TextView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = layoutYAxisSetter++ *75;
            checkBoxLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            checkBoxLabel.setText(formField.getLabel());
            checkBoxLabel.setTypeface(null, Typeface.BOLD);

            TextView checkBoxDesc = new TextView(this);
            RelativeLayout.LayoutParams paramsDesc = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsDesc.topMargin = layoutYAxisSetter++ *75;
            checkBoxDesc.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            checkBoxDesc.setText(formField.getDescription());
            rl.addView(checkBoxDesc, paramsDesc);

            rl.addView(checkBoxLabel, params);
            rl.setId(View.generateViewId());
            formField.setComponentId(rl.getId());
            formField.setOptionsId(new HashMap<String, Integer>());
            for(String option: formField.getOptions()){


                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.topMargin= layoutYAxisSetter++ * 75;
                final CheckBox checkBox = new CheckBox(getApplicationContext());
                checkBox.setText(option);
                checkBox.setId(View.generateViewId());
                checkBox.setButtonDrawable(R.drawable.checkbox_selector);
                final float scale = this.getResources().getDisplayMetrics().density;
                checkBox.setPadding(checkBox.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
                        checkBox.getPaddingTop(),
                        checkBox.getPaddingRight(),
                        checkBox.getPaddingBottom());

                formField.getOptionsId().put(option,checkBox.getId());
                checkBox.setHighlightColor(Color.GRAY);
                if(!formField.getEditable()){
                    for(String selectOption: formField.getAutoselect()){
                        if(option.equalsIgnoreCase(selectOption)){
                            checkBox.setChecked(true);
                            checkBox.setEnabled(false);
                        }
                    }
                }
                rl.addView(checkBox, params1);
            }
            layoutYAxisSetter+= formField.getOptions().size()/4;

        }

        public void createDropdownMenu(RelativeLayout rl, final FormField formField){

            TextView spinnerLabel = new TextView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = layoutYAxisSetter++ *75;
            spinnerLabel.setTypeface(null, Typeface.BOLD);

            TextView spinnerDesc = new TextView(this);
            RelativeLayout.LayoutParams paramsDesc = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsDesc.topMargin = layoutYAxisSetter++ *75;
            spinnerDesc.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            spinnerDesc.setText(formField.getDescription());
            rl.addView(spinnerDesc, paramsDesc);

            spinnerLabel.setText(formField.getLabel());
            rl.addView(spinnerLabel,params);

            Spinner dropdown = new Spinner(this);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.topMargin = layoutYAxisSetter++ *75;

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, formField.getOptions());
            dropdown.setAdapter(adapter);
            dropdown.setId(View.generateViewId());

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    formField.setUserInput((String)adapterView.getItemAtPosition(i));
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
            if(!formField.getEditable()){
                for(String selectOption: formField.getAutoselect()){
                        dropdown.setSelection(adapter.getPosition(selectOption));
                        dropdown.setEnabled(false);
                }
            }
            formField.setComponentId(dropdown.getId());
            rl.addView(dropdown, params1);
        }


        public void createRadioButton(RelativeLayout rl, FormField formField){

            TextView radioButtonLabel = new TextView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = layoutYAxisSetter++ *75;
            radioButtonLabel.setText(formField.getLabel());
            radioButtonLabel.setTypeface(null, Typeface.BOLD);
            rl.addView(radioButtonLabel, params);

            TextView radioButtonDesc = new TextView(this);
            RelativeLayout.LayoutParams paramsDesc = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsDesc.topMargin = layoutYAxisSetter++ *75;
            radioButtonDesc.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            radioButtonDesc.setText(formField.getDescription());
            rl.addView(radioButtonDesc, paramsDesc);

            ScrollView sv = new ScrollView(this);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params3.topMargin = layoutYAxisSetter++ *75;

            //create the RadioGroup
            RadioGroup rg = new RadioGroup(this);
            rg.setOrientation(RadioGroup.VERTICAL);
            RadioGroup.LayoutParams params1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rg.setId(View.generateViewId());
            params1.topMargin = layoutYAxisSetter++ *75;
            formField.setComponentId(rg.getId());
            formField.setOptionsId(new HashMap<String, Integer>());

            for(String option: formField.getOptions()){
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioButton.setButtonDrawable(R.drawable.radiobutton_selector);
                final float scale = this.getResources().getDisplayMetrics().density;
                radioButton.setPadding(radioButton.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
                        radioButton.getPaddingTop(),
                        radioButton.getPaddingRight(),
                        radioButton.getPaddingBottom());
                radioButton.setId(View.generateViewId());
                if(!formField.getEditable()){
                    for(String selectOption: formField.getAutoselect()){
                        if(option.equalsIgnoreCase(selectOption)){
                            radioButton.setChecked(true);
                        }
                    }
                    radioButton.setEnabled(false);
                }
                formField.getOptionsId().put(option, radioButton.getId());
                rg.addView(radioButton, params);
            }

            sv.setFillViewport(true);
            sv.addView(rg,params1);
            rl.addView(sv,params3);
            layoutYAxisSetter+= formField.getOptions().size()*3/5;
        }

    }
