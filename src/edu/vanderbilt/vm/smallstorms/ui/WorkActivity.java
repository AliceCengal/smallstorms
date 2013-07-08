package edu.vanderbilt.vm.smallstorms.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import edu.vanderbilt.vm.smallstorms.R;
import edu.vanderbilt.vm.smallstorms.framework.Screen;
import edu.vanderbilt.vm.smallstorms.framework.impl.AndroidGame;

/**
 * Date: 6/27/13
 * Time: 10:58 PM
 */
public class WorkActivity extends AndroidGame {

public static void open(Context ctx) {
    ctx.startActivity(new Intent(ctx, WorkActivity.class));
}

@Override
public Screen getStartScreen() {
    return new LoadingScreen(this);
}

/*@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    WorkSurface surface = new WorkSurface(this);
    surface.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));

    setContentView(surface);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.workspace, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_bluetooth_connection) {
        return true; }

    else if (item.getItemId() == R.id.menu_run) {
        return true; }

    else return false;
}*/

}
