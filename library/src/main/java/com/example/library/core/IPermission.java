package com.example.library.core;

public interface IPermission {

    void onPermissionGranted();// 同意授权

    void onPermissionCanceled(int requestCode);// 取消授权

    void onPermissionDenied(int requestCode);// 拒绝权限并选中不再提示
}
