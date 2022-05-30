package com.example.dailyselfie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Database database;
    ArrayList<IMG> listIMG;
    IMGListViewAdapter IMGListViewAdapter;
    ListView listViewIMG;
    int REQUEST_CODE_CAMERA = 1;
    private PendingIntent pendingIntent;
    private static final long INTERVAL_TWO_MINUTES = 10 * 1000L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new Database(this,"Bai4.sqlite",null,1);
        database.queryData("CREATE TABLE IF NOT EXISTS IMG(NAME VARCHAR(150),DATA BLOB)");
        listIMG = new ArrayList<>();
        IMGListViewAdapter = new IMGListViewAdapter(listIMG);
        listViewIMG = findViewById(R.id.lv_img);
        listViewIMG.setAdapter(IMGListViewAdapter);
        Cursor cursor = database.getData("SELECT * FROM IMG");
        while (cursor.moveToNext()){
            listIMG.add(new IMG(
                    cursor.getString(0),
                    cursor.getBlob(1)
            ));
        }
        IMGListViewAdapter.notifyDataSetChanged();
        listViewIMG.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IMG img = (IMG) listIMG.get(position);
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                intent.putExtra("img_show",img.img);
                startActivity(intent);
            }
        });
        registerForContextMenu(listViewIMG);
        createNotificationChannel();
        createSelfieAlarm();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        MenuInflater im = getMenuInflater();
        im.inflate(R.menu.context_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        IMG img = listIMG.get(index);

        switch (item.getItemId()) {
            case R.id.mView:
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                intent.putExtra("img_show",img.img);
                startActivity(intent);
                return true;
            case R.id.mDelete:
                listIMG.remove(index);
                IMGListViewAdapter.notifyDataSetChanged();
                database.deleteIMG(img.name);
                Toast.makeText(MainActivity.this, img.name, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.camera_cIcon){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE_CAMERA);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == this.REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CODE_CAMERA);
        }else{
            Toast.makeText(MainActivity.this,"Bạn không được phép mở máy ảnh!",Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == this.REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArray);
            byte[] hinhAnh = byteArray.toByteArray();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            database.insertIMG(timeStamp,hinhAnh);
            listIMG.add(new IMG(timeStamp,hinhAnh));
            IMGListViewAdapter.notifyDataSetChanged();
            Toast.makeText(this,timeStamp,Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "dailyselfieNotification";
            String description = "Notification by Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("dailyselfie", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createSelfieAlarm(){
        Intent intent = new Intent(this, SelfieNotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL_TWO_MINUTES,
                INTERVAL_TWO_MINUTES,
                pendingIntent);
    }
}