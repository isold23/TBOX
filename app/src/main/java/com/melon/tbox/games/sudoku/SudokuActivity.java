package com.melon.tbox.games.sudoku;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.melon.tbox.databinding.ActivitySudokuBinding;
import com.melon.tbox.R;
import games.sudoku.Sudoku;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

public class SudokuActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySudokuBinding binding;

    private Sudoku sudoku;

    private GridLayout gridLayout;
    private Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySudokuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_sudoku);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        gridLayout = findViewById(R.id.gridLayout);
        checkButton = findViewById(R.id.checkButton);

        // Add 9x9 EditText cells to the GridLayout
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                EditText editText = new EditText(this);
                editText.setLayoutParams(new GridLayout.LayoutParams());
                editText.setHint("0");
                editText.setPadding(8, 8, 8, 8);
                gridLayout.addView(editText);
            }
        }

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_sudoku);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkAnswer() {
        // TODO: Implement the logic to check the answer
        // You can retrieve the values from the EditText cells and compare with the solution
    }
}