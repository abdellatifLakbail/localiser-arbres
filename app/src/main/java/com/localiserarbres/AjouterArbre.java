package com.localiserarbres;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AjouterArbre extends AppCompatActivity {

    private TextView textViewLatitude, textViewLongitude;
    private EditText editTextName;

    Spinner spinner;

    public double receiveLatitude,receiveLongitude;

    public String name, taille;

    public static int code = 1;





    private Button ajouter, annuler;
    @SuppressLint({"MissingInflatedId", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajouter_arbre);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewLatitude = findViewById(R.id.latitude);
        textViewLongitude = findViewById(R.id.longitude);
        editTextName = findViewById(R.id.nameTree);
        spinner = findViewById(R.id.spinner);
        ajouter = findViewById(R.id.ajouteBtn);
        annuler = findViewById(R.id.annulerBtn);

        code = 1;


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taille = parent.getItemAtPosition(position).toString();
                Toast.makeText(AjouterArbre.this," selected item : "+taille, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("petite");
        arrayList.add("moyenne");
        arrayList.add("grande");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);


        name =editTextName.getText().toString();

        receiveLatitude = getIntent().getDoubleExtra("newlatitude",0.0);
        receiveLongitude = getIntent().getDoubleExtra("newlongitude",0.0);

        textViewLatitude.setText(String.format("%.15f",receiveLatitude));
        textViewLongitude.setText(String.format("%.15f",receiveLongitude));

        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName != null) {
                    if(!editTextName.getText().toString().isEmpty()){
                        name = editTextName.getText().toString().trim();
                        Toast.makeText(AjouterArbre.this," bb "+name,Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(AjouterArbre.this,"please enter a name to your tree", Toast.LENGTH_LONG).show();
                        return;                    }
                }


                if(spinner == null || taille.isEmpty()){
                    Toast.makeText(AjouterArbre.this,"please enter a size to your tree", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    Toast.makeText(AjouterArbre.this,name+" est : "+taille,Toast.LENGTH_LONG).show();
                }



                Intent returnIntent = new Intent();
                returnIntent.putExtra("nameValue", name);
                returnIntent.putExtra("spinnerValue",taille);
                setResult(RESULT_OK, returnIntent);

                finish();
            }
        });

        annuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = 0;
                finish();
            }
        });



    }
}