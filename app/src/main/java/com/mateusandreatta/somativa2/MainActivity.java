package com.mateusandreatta.somativa2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProductAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        DataModel.getInstance().setContext(MainActivity.this);
        adapter = new ProductAdapter();

        adapter.setClickListener(new ProductAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Product product = DataModel.getInstance().getProducts().get(position);

                Intent intent = new Intent(MainActivity.this, AddProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("pos", position);
                startActivity(intent);


                adapter.notifyItemChanged(position);
            }

            @Override
            public boolean onItemLongClick(int position, View view) {
                Product productDeleted = DataModel.getInstance().getProducts().get(position);
                DataModel.getInstance().deleteProduct(productDeleted);
                adapter.notifyItemRemoved(position);
                return true;
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    public void goToAddProduct(View view){
        startActivity(new Intent(MainActivity.this, AddProduct.class));
    }

}