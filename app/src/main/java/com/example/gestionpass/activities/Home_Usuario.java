package com.example.gestionpass.activities;

import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionpass.R;
import com.example.gestionpass.models.Pass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Home_Usuario extends AppCompatActivity {

    private EditText txtSitio, txtNombreRegistro, txtPassRegistro, txtNotaRegistro;
    private Button btnRegistar;
    private ImageButton btnLeer, btnCerrarSesion;
    private RelativeLayout relativeVerPass;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference passRef;

    private static final String KEY_ALIAS = "password_key";
    private KeyStore keyStore;

    private RecyclerView recyclerView;
    private List<Pass> passList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        passRef = db.collection("users").document(userId).collection("PassRegistradas");

        txtSitio = findViewById(R.id.txtSitio);
        txtNombreRegistro = findViewById(R.id.txtNombreRegistro);
        txtPassRegistro = findViewById(R.id.txtPassRegistro);
        txtNotaRegistro = findViewById(R.id.txtNotaRegistro);

        btnRegistar = findViewById(R.id.btnRegistar);
        btnLeer = findViewById(R.id.btnLeer);
        btnCerrarSesion = findViewById(R.id.btnCerrar);

        btnCerrarSesion.setOnClickListener(View -> finish());

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnRegistar.setOnClickListener(v -> {
            String sitio = txtSitio.getText().toString();
            String nombre = txtNombreRegistro.getText().toString();
            String pass = txtPassRegistro.getText().toString();
            String nota = txtNotaRegistro.getText().toString();

            if (!sitio.isEmpty() && !nombre.isEmpty() && !pass.isEmpty()) {
                guardarContraseña(sitio, nombre, pass, nota);
            } else {
                Toast.makeText(Home_Usuario.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        btnLeer.setOnClickListener(v -> {
            String pass = txtPassRegistro.getText().toString();
            if (!pass.isEmpty()) {
                // Mostrar la contraseña
                txtPassRegistro.setText(pass);
            } else {
                Toast.makeText(Home_Usuario.this, "No hay contraseña registrada", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        passList = new ArrayList<>();
        passList.add(new Pass("Sitio 1", "usuario1@example.com", "contrasena1", "Nota adicional"));
        passList.add(new Pass("Sitio 2", "usuario2@example.com", "contrasena2", "Otra nota"));
    }

    private SecretKey getSecretKey() throws Exception {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
            return keyGenerator.generateKey();
        }
    }

    private String cifrarContraseña(String contraseña) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(contraseña.getBytes());

            String ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT);
            String encryptedPasswordBase64 = Base64.encodeToString(encryption, Base64.DEFAULT);

            return ivBase64 + ":" + encryptedPasswordBase64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void guardarContraseña(String sitio, String nombre, String pass, String nota) {
        String encryptedPassword = cifrarContraseña(pass);

        if (encryptedPassword != null) {
            Map<String, Object> passData = new HashMap<>();
            passData.put("sitio", sitio);
            passData.put("nombre", nombre);
            passData.put("contraseña", encryptedPassword);
            passData.put("nota", nota);

            passRef.add(passData).addOnSuccessListener(documentReference -> {
                Toast.makeText(Home_Usuario.this, "Contraseña guardada", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }).addOnFailureListener(e -> {
                Toast.makeText(Home_Usuario.this, "Error al guardar la contraseña", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(Home_Usuario.this, "Error al cifrar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        txtSitio.setText("");
        txtNombreRegistro.setText("");
        txtPassRegistro.setText("");
        txtNotaRegistro.setText("");
    }
}
