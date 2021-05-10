package com.mateusandreatta.somativa2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ProductsDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "prooducts.sqlite";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "Products";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PRICE = "price";
    private static final String COL_PHOTO_PATH = "path";

    private Context context;

    public ProductsDatabase(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = String.format("CREATE TABLE IF NOT EXISTS %s("+
                " %s INTEGER PRIMARY KEY AUTOINCREMENT, "+
                " %s TEXT, " +
                " %s INTEGER, " +
                " %s TEXT)",DB_TABLE,COL_ID,COL_NAME,COL_PRICE,COL_PHOTO_PATH);

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long createProductInDB(Product product){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,product.getName());
        values.put(COL_PHOTO_PATH,product.getPhotoPath());
        values.put(COL_PRICE,product.getPrice());
        long id = database.insert(DB_TABLE,null,values);
        database.close();
        return id;
    }

    public ArrayList<Product> retrieveProductsFromDB(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(DB_TABLE,null,null,
                null,null,null,null);
        ArrayList<Product> products = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(COL_ID));
                String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                int price = cursor.getInt(cursor.getColumnIndex(COL_PRICE));
                String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO_PATH));
                Product c = new Product(id,name,price, photo);
                products.add(c);

            }while (cursor.moveToNext());
        }
        database.close();
        return products;
    }

    public void updateProductInDB(Product product){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,product.getName());
        values.put(COL_PRICE,product.getPrice());
        values.put(COL_PHOTO_PATH,product.getPhotoPath());
        database.update(DB_TABLE,values,"id = ?", new String[]{String.valueOf(product.getId())});

        database.close();
    }

    public void deleteProductInDB(Product product) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(DB_TABLE,"id = ?", new String[]{String.valueOf(product.getId())});
        database.close();
    }
}
