package com.example.phometalk.Feed;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BitmapFactory;

import com.example.phometalk.Model.FeedItems;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL("CREATE TABLE image (_id INTEGER PRIMARY KEY AUTOINCREMENT, img BLOB, tag TEXT, star INTEGER);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS image");
        onCreate(db);
    }
    //이미지 추가
    public void insert(byte[]image, String tag) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement("INSERT INTO image(img, tag, star) VALUES(?,?,0);");

        p.bindBlob(1,image);
        p.bindString(2,tag);
        p.execute();
        db.close();
    }
    //이미지 조회(1이면 역순)
    public ArrayList<FeedItems> getItems(int mode){
        SQLiteDatabase db = getReadableDatabase();
        String sql=null;
        if(mode==1)
            sql="SELECT * FROM image ORDER BY _id DESC;";
        else if(mode==2)
            sql="SELECT * FROM image;";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<FeedItems> list=new ArrayList<FeedItems>();
        byte []image=null;
        while(cursor.moveToNext()){
            FeedItems entity = new FeedItems();
            image=cursor.getBlob(1);
            entity.setImage(BitmapFactory.decodeByteArray(image,0,image.length));
            entity.setTag(cursor.getString(2));
            entity.setStar(cursor.getInt(3));
            list.add(entity);
        }
        db.close();
        return list;
    }
    //즐겨찾기 이미지 조회(1이면 역순)
    public ArrayList<FeedItems> getStarItems(int mode){
        SQLiteDatabase db = getReadableDatabase();
        String sql=null;
        if(mode==1){
            sql="SELECT * FROM image WHERE star=1 ORDER BY _id DESC;";
        }
        else{
            sql="SELECT * FROM image WHERE star=1;";
        }
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<FeedItems> list=new ArrayList<FeedItems>();
        byte []image=null;
        while(cursor.moveToNext()){
            FeedItems entity = new FeedItems();
            image=cursor.getBlob(1);
            entity.setImage(BitmapFactory.decodeByteArray(image,0,image.length));
            entity.setTag(cursor.getString(2));
            entity.setStar(cursor.getInt(3));
            list.add(entity);
        }
        db.close();
        return list;
    }
    //태그내용에 대한 이미지출력
    public ArrayList<FeedItems> getTagItems(String tag){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM image WHERE tag='"+tag+"';", null);
        ArrayList<FeedItems> list=new ArrayList<FeedItems>();
        byte []image=null;
        while(cursor.moveToNext()){
            FeedItems entity = new FeedItems();
            image=cursor.getBlob(1);
            entity.setImage(BitmapFactory.decodeByteArray(image,0,image.length));
            entity.setTag(cursor.getString(2));
            entity.setStar(cursor.getInt(3));
            list.add(entity);
        }
        db.close();
        return list;
    }
    //즐겨찾기 설정
    public void setStar(int star, int position){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement p = db.compileStatement("UPDATE image SET star="+star+" WHERE _id="+position+";");

        p.execute();
        db.close();
    }
}
