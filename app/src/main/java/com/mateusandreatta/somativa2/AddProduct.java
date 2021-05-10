package com.mateusandreatta.somativa2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProduct extends AppCompatActivity {

    ImageView imageView;
    static final int CAMERA_PERMISSION_CODE = 2001;
    static final int CAMERA_INTENT_CODE = 3001;
    static final int GALLERY_INTENT_CODE = 4001;
    String picturePath;
    SeekBar seekBar;
    TextView textViewProgress;
    int price = 0;
    EditText editTextProductName;
    boolean edit = false;
    int pos = 0;
    Product oldProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        imageView = findViewById(R.id.imageView2);
        seekBar = findViewById(R.id.seekBar);
        textViewProgress = findViewById(R.id.textViewProgress);
        editTextProductName = findViewById(R.id.editTextProductName);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                price = progress * 10;
                textViewProgress.setText("R$ " + price + ",00");
                textViewProgress.setX(seekBar.getX() + val - 110 + seekBar.getThumbOffset() / 2);
                Log.i("[SEEKBAR]", progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Product product = (Product) getIntent().getSerializableExtra("product");
            oldProduct = product;
            pos = extras.getInt("pos");
            editTextProductName.setText(product.getName());
            File file = new File(product.getPhotoPath());
            if(file.exists()){
                imageView.setImageURI(Uri.fromFile(file));
            }
            this.picturePath = oldProduct.getPhotoPath();
            textViewProgress.setText("R$ " + product.getPrice() + ",00");

            int progress = product.getPrice() / 10;
            seekBar.setProgress(progress);

            textViewProgress.setX(seekBar.getX());
            edit = true;
        }
    }

    public void onSaveButtonClicked(View view){
        String name = editTextProductName.getText().toString();
        Log.i("[ADDPRODUCT]", "onSaveButtonClicked - " + picturePath);
        if(!name.isEmpty() && !picturePath.isEmpty()){
            DataModel.getInstance().setContext(AddProduct.this);
            if(!edit){
                Product product = new Product(name, price, picturePath);
                DataModel.getInstance().addProduct(product);
            }else{
                oldProduct.setName(name);
                oldProduct.setPrice(price);
                oldProduct.setPhotoPath(picturePath);
                DataModel.getInstance().updateProduct(oldProduct);
            }

            startActivity(new Intent(AddProduct.this, MainActivity.class));
        }else{
            Toast.makeText(this,"Erro ao salvar a o produto", Toast.LENGTH_LONG).show();
        }
    }

    public void buttonCameraClicked(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestCameraPermission();
        }else{
            sendCameraIntent();
        }
    }

    public void buttonGalleryClicked(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION,true);
        if(photoPickerIntent.resolveActivity(getPackageManager()) != null){
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String picName = "pic_"+timeStamp;
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File pictureFile = null;
            try {
                pictureFile = File.createTempFile(picName,".jpg",dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(pictureFile != null){
                picturePath = pictureFile.getAbsolutePath();
                Log.i("[ADDPRODUCT]", "buttonGalleryClicked - " + picturePath);
                Uri photoUri = FileProvider.getUriForFile(
                        AddProduct.this,
                        "com.mateusandreatta.somativa2.fileprovider",
                        pictureFile
                );
                photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(photoPickerIntent, GALLERY_INTENT_CODE);

            }

        }
//        startActivityForResult(photoPickerIntent, GALLERY_INTENT_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestCameraPermission(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            if(checkSelfPermission(Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.CAMERA
                },CAMERA_PERMISSION_CODE);
            }else{
                sendCameraIntent();
            }
        }else{
            Toast.makeText(AddProduct.this, "Não conseguimos acessar sua camera :( ",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendCameraIntent();
            }else{
                Toast.makeText(AddProduct.this, "Infelizmente não temos permissão para usar sua camera :/ ",Toast.LENGTH_LONG).show();
            }
        }
    }

    void sendCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION,true);
        if(intent.resolveActivity(getPackageManager()) != null){
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date());
            String picName = "pic_"+timeStamp;
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File pictureFile = null;
            try {
                pictureFile = File.createTempFile(picName,".jpg",dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(pictureFile != null){
                picturePath = pictureFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(
                        AddProduct.this,
                        "com.mateusandreatta.somativa2.fileprovider",
                        pictureFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent,CAMERA_INTENT_CODE);

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("[ADDPRODUCT] Result - ", picturePath);
        if(requestCode == CAMERA_INTENT_CODE){
            if(resultCode == RESULT_OK){
                File file = new File(picturePath);
                if(file.exists()){
                    imageView.setImageURI(Uri.fromFile(file));
                }
            }else{
                Toast.makeText(AddProduct.this, "Ocorreu um erro ao tentar acessar a camera :/ ", Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == GALLERY_INTENT_CODE){
            if(resultCode == RESULT_OK){

                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);

                    OutputStream fOut = null;
                    File file = new File(picturePath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream

                    MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            }else{
                Toast.makeText(AddProduct.this, "Ocorreu um erro ao tentar acessar a foto :/ ", Toast.LENGTH_LONG).show();
            }
        }
    }

}