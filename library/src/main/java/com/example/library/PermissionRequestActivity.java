package com.example.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.library.core.IPermission;
import com.example.library.util.PermissionUtils;

/**
 * author: xpf
 * time: 2020/1/31 21:14
 * describe:
 */
public class PermissionRequestActivity extends Activity {
    // 定义权限处理的标识 -- 接收用户传递进来的
    private final static String PARAM_PREMISSION = "param_permission";
    private final static String PARAM_REQUEST_CODE = "param_request_code";
    public final static int PARAM_REQUEST_CODE_DEFAULT = 0;

    private String[] permissions;
    private int requestCode;
    private static IPermission permissionListener; // 这个Activity  已经授权，取消授权，被拒绝授权

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions = getIntent().getStringArrayExtra(PARAM_PREMISSION);
        requestCode = getIntent().getIntExtra(PARAM_REQUEST_CODE, PARAM_REQUEST_CODE_DEFAULT);

        if (permissions == null || permissionListener == null) {
            this.finish();
            return;
        }

        // 能走到这里，就开始去检查，是否已经授权了
        boolean permissionRequest = PermissionUtils.hasPermissionRequest(this, permissions);
        if (permissionRequest) { // 已经授权了，无需在申请
            // 通过监听接口，告诉外界，已经授权了
            permissionListener.onPermissionGranted();
            this.finish();
            return;
        }

        // 能走到这里，就证明，还需要去申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }


    // 申请权限之后的结果 方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) { // grantResults.length = 3
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 返回的结果，需要去验证一下，是否完全成功了
        if (PermissionUtils.requestPermissionSuccess(grantResults)) { // 真正申请成功了
            // 通过监听接口，告诉外界，已经授权成功
            permissionListener.onPermissionGranted();

        } else {
            // 如果用户点击了，拒绝，（不再提示打勾） 等操作，告诉外界
            if (PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
                // 如果执行到这里来了，就证明 权限被取消了
                permissionListener.onPermissionCanceled(requestCode);
            } else {
                // 用户拒绝，不再提醒
                // 通过接口监听，告诉外界，被拒绝，（不再提示打勾）
                permissionListener.onPermissionDenied(requestCode);
            }
        }
        this.finish();
    }

    // TODO 把当前整个Activity暴露给外界使用
    public static void requestPermissionAction(Context context, String[] permissions,
                                               int requestCode, IPermission iPermission) {
        permissionListener = iPermission;

        Intent intent = new Intent(context, PermissionRequestActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_REQUEST_CODE, requestCode);
        bundle.putStringArray(PARAM_PREMISSION, permissions);

        intent.putExtras(bundle);

        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    // 专门处理 当前Activity结束的时候，不需要有动画效果

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
