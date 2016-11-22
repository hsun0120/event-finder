package edu.ucsd.cse110.group50.eventfinder;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CheckEventPassword extends AppCompatActivity {
    String password_to_match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        password_to_match = getIntent().getStringExtra("password");
        //System.out.println("Password to match is "+ password_to_match);



        setContentView(R.layout.activity_check_event_password);

    }



    public void checkPassword(View v)
    {
        EditText userEnteredPasswordEditText = (EditText)(findViewById(R.id.event_detail_user_entered_password));
        String userEnteredPassword = userEnteredPasswordEditText.getText().toString();
        System.out.println("User entered password is "+ userEnteredPassword);

        if(!userEnteredPassword.equals(password_to_match))
        {
            TextView prompUser = (TextView) findViewById(R.id.promp_user_enter_password);
            prompUser.setText("Password does not match, please enter again.");
            prompUser.setTextColor(Color.rgb(200,0,0));
        }
        else
        {
            EventDetailActivity.userEnteredCorrectPassword = true;
            TextView prompUser = (TextView) findViewById(R.id.promp_user_enter_password);
            prompUser.setText("Password Matched, loading...");
            prompUser.setTextColor(Color.rgb(0,0,0));
            finish();
        }
    }


}
