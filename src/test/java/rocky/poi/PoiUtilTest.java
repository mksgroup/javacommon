package rocky.poi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import mksgroup.java.common.CommonUtil;
import mksgroup.java.poi.PoiUtil;

public class PoiUtilTest {

//    @Test
//    public void testLoadWorkbook_Excel2007() {
//        try {
//            InputStream is = CommonUtil.loadResource("/test-data/Excel2007.xlsx");
//            assertNotNull(is);
//            Workbook wb = PoiUtil.loadWorkbook(is);
//
//            assertNotNull(wb);
//
//            assertNotNull(wb.getSheetAt(0));
//        } catch (FileNotFoundException ex) {
//            fail(ex.getMessage());
//        }
//    }

    @Test
    public void testLoadWorkbook_Excel2003() {
        try {
            InputStream is = CommonUtil.loadResource("/test-data/Excel2003.xls");
            assertNotNull(is);
            Workbook wb = PoiUtil.loadWorkbook(is);

            assertNotNull(wb);

            assertNotNull(wb.getSheetAt(0));
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }
    }
}
