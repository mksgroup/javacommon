/**
 * Copyright 2018, MKS GROUP.
 */
package rocky.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mksgroup.java.common.CommonUtil;

/**
 * @author ThachLN
 *
 */
class CommonUtilTest {

    /**
     * Test method for {@link mksgroup.java.common.CommonUtil#isNNNE(java.util.List)}.
     */
    @Test
    void testIsNNNEListOfObject() {
        List<Object> objList = new ArrayList<>();
        
        objList.add("");
        
        objList.add("A");
        objList.add("");
        
        boolean result = CommonUtil.isNNandNB(objList );
        
        assertTrue(result);
    }

}
