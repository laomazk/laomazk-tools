package com.laomazk.tools;

import net.sf.cglib.core.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverterBeanCopier implements Converter {
    public DateConverterBeanCopier() {
    }

    @Override
    public Object convert(Object arg1, Class arg0, Object context) {
        if (arg1 instanceof String && arg0 != String.class) {
            String p = (String) arg1;
            if (p != null && p.trim().length() != 0) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return df.parse(p.trim());
                } catch (Exception var8) {
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        return df.parse(p.trim());
                    } catch (ParseException var7) {
                        return arg1;
                    }
                }
            } else {
                return null;
            }
        } else if (arg1 instanceof String && arg0 == String.class) {
            return arg1;
        } else {
            SimpleDateFormat df;
            if (arg1 instanceof Date) {
                try {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return df.format((Date) arg1);
                } catch (Exception var9) {
                    return null;
                }
            } else if (arg1 instanceof java.sql.Date) {
                try {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return df.format((java.sql.Date) arg1);
                } catch (Exception var10) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
