package edu.vanderbilt.vm.smallstorms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import edu.vanderbilt.vm.smallstorms.R;

public class MainActivity extends Activity {

/**
 * Called when the activity is first created.
 */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    findViewById(R.id.main_control).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
            BtTestActivity.open(MainActivity.this); }});

    findViewById(R.id.main_workspace).setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
            WorkActivity.open(MainActivity.this); }});
}

}
