package me.bayee.datasource.util;

/**
 * Created by mofan on 16-9-10.
 */
public class Tools {
    public static String formatLogclass(int logclass) {
        try {
            return
                    sp.AuditEnum.LOGCLASS.getDescriptor().findValueByNumber(logclass).getName();
        } catch (Exception e) {
        }

        return "unknown logclass(" + logclass + ") in SP AuditEnum";
    }
}
