package com.ecom.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Constant {
    public static Locale vietnam = new Locale("vi", "VN");
    // Định dạng số thành tiền tệ Việt Nam Đồng
    public static NumberFormat formatter = NumberFormat.getCurrencyInstance(vietnam);
}
