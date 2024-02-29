package com.thirtythreelabs.oktopus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.thirtythreelabs.util.Config;

public class EditURL extends Activity {


    private Intent iURL;
    private Intent toLogin;
    private Button submit;
    private EditText editURLText;
    private String newURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editurl);

        submit = (Button) findViewById(R.id.submit);
        editURLText = (EditText) findViewById(R.id.editURLText);
        editURLText.setText(Config.URL);
        toLogin = new Intent (this, LoginActivity.class);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newURL = editURLText.getText().toString();
                Config.changeURL(newURL);
                startActivity(toLogin);
            }

        });



    }
}
