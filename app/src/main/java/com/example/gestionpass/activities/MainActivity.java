package com.example.gestionpass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gestionpass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView txtSitio, txtEmailRegistro, txtPassRegistro, txtNotaRegistro;
    private EditText txtContrasenaInicio, txtEmailInicio;
    private Button btnIniciar, btnIraRegistrar, btnCerrar, btnRegistar;

    private RelativeLayout relativeRegistro;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        txtEmailInicio = findViewById(R.id.txtEmailInicio);
        txtContrasenaInicio = findViewById(R.id.txtContrasenaInicio);
        txtEmailRegistro = findViewById(R.id.txtEmailRegistro);
        txtPassRegistro = findViewById(R.id.txtPassRegistro);

        btnIniciar = findViewById(R.id.btnIniciar);
        btnIraRegistrar = findViewById(R.id.btnIraRegistrar);
        btnCerrar = findViewById(R.id.btnCerrar);
        btnRegistar = findViewById(R.id.btnRegistar);

        relativeRegistro = findViewById(R.id.relativeRegistro);
        relativeRegistro.setVisibility(View.GONE);

        btnIniciar.setOnClickListener(view -> {
            loginUser();
        });

        btnIraRegistrar.setOnClickListener(view -> {
            relativeRegistro.setVisibility(View.VISIBLE);
        });

        btnCerrar.setOnClickListener(view -> {
            relativeRegistro.setVisibility(View.GONE);
        });

        btnRegistar.setOnClickListener(view -> {
            registrarUsuario();
        });
    }

    private void loginUser() {
        String email = txtEmailInicio.getText().toString().trim();
        String password = txtContrasenaInicio.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Home_Usuario.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarUsuario() {

        String email = txtEmailRegistro.getText().toString().trim();
        String pass = txtPassRegistro.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        relativeRegistro.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MainActivity.this, "Error de registro" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}