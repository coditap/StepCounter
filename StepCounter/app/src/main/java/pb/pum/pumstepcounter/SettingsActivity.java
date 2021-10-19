package pb.pum.pumstepcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    EditText weightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        weightEdit = (EditText) findViewById(R.id.weightEdit);
        weightEdit.setText(String.valueOf(Settings.getInstance().getWeightInKgs()));
    }

    public final void saveSettings(View view) {
        Settings settings = Settings.getInstance();
        settings.setWeightInKgs(Integer.parseInt(weightEdit.getText().toString()));
        finish();
    }

}
