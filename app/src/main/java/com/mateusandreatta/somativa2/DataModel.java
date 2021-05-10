package com.mateusandreatta.somativa2;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class DataModel {

    private static DataModel INSTANCE = new DataModel();
    private DataModel(){}
    public static DataModel getInstance(){
        return INSTANCE;
    }

    private ProductsDatabase database;
    private ArrayList<Product> products;
    private Context context;

    public void setContext(Context context){
        this.context = context;
        database = new ProductsDatabase(context);
        products = database.retrieveProductsFromDB();
    }

    public ArrayList<Product> getProducts(){
        return products;
    }

    public void addProduct(Product product){
        long id = database.createProductInDB(product);
        if(id > 0){
            product.setId(id);
            products.add(product);
        }else{
            Toast.makeText(context, "Não foi possivel cadastrar esse produto", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateProduct(Product product){
        long id = product.getId();
        if(id > 0){
            database.updateProductInDB(product);
        }else{
            Toast.makeText(context, "Não foi possivel cadastrar esse produto", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteProduct(Product product){
        database.deleteProductInDB(product);
        products.remove(product);
    }

}
