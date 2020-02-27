package com.example.permission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.library.annotation.PermissionCanceled;
import com.example.library.annotation.PermissionDenied;
import com.example.library.annotation.PermissionNeed;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_request_single_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
            }
        });

        findViewById(R.id.btn_request_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });


    }

    @PermissionNeed(value = Manifest.permission.ACCESS_FINE_LOCATION, requestCode = 11)
    public void requestLocation() {
        Toast.makeText(this, "权限申请成功...", Toast.LENGTH_LONG).show();
//        Log.d("xpf", "requestLocation: 权限申请成功...");
    }

    @PermissionNeed(value = {Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA}, requestCode = 12)
    public void requestPermission() {
//        Toast.makeText(this, "权限申请成功...", Toast.LENGTH_SHORT).show();
        Log.d("xpf", "requestPermission: 权限申请成功...");
    }


    @PermissionCanceled()
    public void permissionCancel(int requestCode) {
        Toast.makeText(this, "权限被拒绝 " + requestCode, Toast.LENGTH_SHORT).show();
//        Log.d("xpf", "permissionCancel: 权限取消 " + requestCode);
    }

    @PermissionDenied()
    public void permissionDenied(int requestCode) {
        Toast.makeText(this, "权限被拒绝(用户勾选了不再提箱)" + requestCode, Toast.LENGTH_SHORT).show();
//        Log.d("xpf", "permissionDenied: 权限拒绝 " + requestCode);
    }
}



