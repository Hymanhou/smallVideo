package com.hyuan.smallvideo.utils;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;

import java.nio.ByteBuffer;

public class ImageUtil {

    public static byte[] generateNV21Data(Image image) {
        Rect cropRect = image.getCropRect();
        int format = image.getFormat();
        int width = cropRect.width();
        int height = cropRect.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        int channelOffset = 0;
        int outputStride = 1;

        for (int i = 0; i < planes.length; i++) {
            if (i == 0) {
                channelOffset = 0;
                outputStride = 1;
            } else if (i == 1) {
                channelOffset = width * height + 1;
                outputStride = 2;
            } else if (i == 2) {
                channelOffset = width * height;
                outputStride = 2;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;

            buffer.position(rowStride * (cropRect.top >> shift) + pixelStride * (cropRect.left >> shift));
            for (int row = 0; row < h; row++) {
                int length = 0;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }

        return data;
    }

}
