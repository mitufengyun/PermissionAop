package com.example.library.aspect;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.library.PermissionRequestActivity;
import com.example.library.annotation.PermissionCanceled;
import com.example.library.annotation.PermissionDenied;
import com.example.library.annotation.PermissionNeed;
import com.example.library.core.IPermission;
import com.example.library.util.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * author: xpf
 * time: 2020/1/31 21:16
 * describe:
 */
@Aspect
public class PermissionAspect {
	
	// 选取普通方法作为切面点
	//匹配语法  方法权限 全类名 返回值类型 函数名(参数)
	//@Pointcut("execution(public * packageName.className  * *(..)) && @annotation(permission)")
	
	// 选取带注解的方法作为切面点匹配语法  @注解 方法权限 返回值类型 函数名(参数)
    @Pointcut("execution(@com.example.library.annotation.PermissionNeed * *(..)) && @annotation(permission)")
    public void requestPermission(PermissionNeed permission) {/* 方法内部不做任何事情，只为了@Pointcut服务*/}

    @Around("requestPermission(permission)")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint, PermissionNeed permission) throws Throwable {
        // 先定义一个上下文操作环境
        Context context = null;

        final Object obj = joinPoint.getThis(); // 如果有兼容问题，obj == null

        // 给context 初始化
        if (obj instanceof Context) {
            context = (Context) obj;
        } else if (obj instanceof Fragment) {
            context = ((Fragment) obj).getActivity();
        }

        // 判断是否为null
        if (null == context || permission == null) {
            throw new IllegalAccessException("null == context || permission == null is null");
        }

        // 调用权限处理的Activity 申请 检测 处理权限操作  permission.value() == Manifest.permission.READ_EXTERNAL_STORAGE
        final Context finalContext = context;

        PermissionRequestActivity.requestPermissionAction
                (context, permission.value(), permission.requestCode(), new IPermission() {
                    @Override
                    public void onPermissionGranted() { // 申请成功 授权成功
                        // 让被 @PermissionNeed 的方法 正常的执行下去
                        try {
                            joinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionCanceled(int requestCode) { // 被拒绝
                        // 调用到 被 @PermissionCanceled 的方法
                        PermissionUtils.invokeAnnotation(obj, PermissionCanceled.class, requestCode);
                    }

                    @Override
                    public void onPermissionDenied(int requestCode) { // 严重拒绝 勾选了 不再提醒
                        // 调用到 被 @PermissionDenied 的方法
                        PermissionUtils.invokeAnnotation(obj, PermissionDenied.class, requestCode);

                        // 不仅仅要提醒用户，还需要 自动跳转到 手机设置界面
                        PermissionUtils.startAndroidSettings(finalContext);
                    }
        });

    }



}
