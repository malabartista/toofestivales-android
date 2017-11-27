package es.toofestival.toofestivales.app;

import android.app.Activity;
import android.os.Bundle;
import es.toofestival.toofestivales.R;
/*
* dummy activity to display PanicAR fragment
* */
public abstract class ARActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PanicARFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
