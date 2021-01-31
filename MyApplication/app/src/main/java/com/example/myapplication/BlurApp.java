package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurApp {

    private void onDraw(Context context) {
        // 创建一个缩小后的bitmap
        Bitmap image = null;
        int width = 0;
        int height = 0;

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        //创建将在ondraw中使用到的经过模糊处理后的bitmap
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        //创建RenderScript，ScriptIntrinsicBlur固定写法
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //根据inputBitmap，outputBitmap分别分配内存
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        //设置模糊半径取值0-25之间，不同半径得到的模糊效果不同
        blurScript.setRadius(15);
        blurScript.setInput(tmpIn);
        blurScript.forEach(tmpOut);

        //得到最终的模糊bitmap
        tmpOut.copyTo(outputBitmap);
    }

}
