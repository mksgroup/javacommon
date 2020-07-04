/**
 * Copyright 2018, MKS GROUP.
 */
package mksgroup.java.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author ThachLN
 *
 */
class BeanUtilTest {
    final String[] HEADERS = {"name", "length", "width", "height", "weight"};
    /**
     * Test method for {@link mksgroup.java.common.BeanUtil#getDataList(java.util.List, java.lang.String[], java.lang.Class)}.
     */

    /**
     * Test data row of Product without set author and created time.
     */
    @Test
    void testGetDataList_N_1() {
        List data = new ArrayList<>();
        String[] row1 = {"A", "1", "2", "3", "10"};
        String[] row2 = {"B", "1", "2", "3", ""};
        
        List<String> row = new ArrayList<>();
        data.add(Arrays.asList(row1));
        data.add(Arrays.asList(row2));
        
        List<Product> listProduct = (List<Product>) BeanUtil.getDataList(data, HEADERS, Product.class, true);
        assertNotNull(listProduct);
        assertEquals(2, listProduct.size());
        
        // Get item 1
        Product p = listProduct.get(0);
        assertNotNull(p);
        assertEquals("A", p.getName());
        assertEquals(0.0, p.getWeight() - 10.0);
        assertNull(p.getCreatedBy());
        assertNull(p.getCreatedTime());
                
    }

    /**
     * Test data row of Product set author and created time..
     */
    @Test
    void testGetDataList_N_2() {
        List data = new ArrayList<>();
        String[] row1 = {"A", "1", "2", "3", "10"};
        String[] row2 = {"B", "1", "2", "3", ""};
        
        List<String> row = new ArrayList<>();
        data.add(Arrays.asList(row1));
        data.add(Arrays.asList(row2));
        
        List<Product> listProduct = (List<Product>) BeanUtil.getDataList(data, HEADERS, Product.class, true, "createdBy", "ThachLN", "createdTime");
        assertNotNull(listProduct);
        assertEquals(2, listProduct.size());
        
        // Get item 1
        Product p = listProduct.get(0);
        assertNotNull(p);
        assertEquals("A", p.getName());
        assertEquals(0.0, p.getWeight() - 10.0);
        assertEquals(p.getCreatedBy(), "ThachLN");
        assertNotNull(p.getCreatedTime());
    }
    
    /**
     * Test data row of Product set author and created time, skip empty line.
     */
    @Test
    void testGetDataList_N_3() {
        List data = new ArrayList<>();
        String[] row1 = {"A", "1", "2", "3", "10"};
        String[] row2 = {"B", "1", "2", "3", ""};
        String[] row3 = {"", "", "", "", ""};
        
        List<String> row = new ArrayList<>();
        data.add(Arrays.asList(row1));
        data.add(Arrays.asList(row2));
        data.add(Arrays.asList(row3));
        
        List<Product> listProduct = (List<Product>) BeanUtil.getDataList(data, HEADERS, Product.class, true, "createdBy", "ThachLN", "createdTime");
        assertNotNull(listProduct);
        assertEquals(2, listProduct.size());
        
        // Get item 1
        Product p = listProduct.get(0);
        assertNotNull(p);
        assertEquals("A", p.getName());
        assertEquals(0.0, p.getWeight() - 10.0);
        assertEquals(p.getCreatedBy(), "ThachLN");
        assertNotNull(p.getCreatedTime());
    }
}

class Product {
    private String name;
    private String description;
    

    private Double height;

    private Double width;

    private Double length;

    private Double weight;
    
    private String createdBy;
    private Date createdTime;

    /**
    * Get value of name.
    * @return the name
    */
    public String getName() {
        return name;
    }

    /**
    * Get value of description.
    * @return the description
    */
    public String getDescription() {
        return description;
    }

    /**
    * Get value of height.
    * @return the height
    */
    public Double getHeight() {
        return height;
    }

    /**
    * Get value of width.
    * @return the width
    */
    public Double getWidth() {
        return width;
    }

    /**
    * Get value of length.
    * @return the length
    */
    public Double getLength() {
        return length;
    }

    /**
    * Get value of weight.
    * @return the weight
    */
    public Double getWeight() {
        return weight;
    }

    /**
     * Set the value for name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value for description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the value for height.
     * @param height the height to set
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Set the value for width.
     * @param width the width to set
     */
    public void setWidth(Double width) {
        this.width = width;
    }

    /**
     * Set the value for length.
     * @param length the length to set
     */
    public void setLength(Double length) {
        this.length = length;
    }

    /**
     * Set the value for weight.
     * @param weight the weight to set
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
    * Get value of createdBy.
    * @return the createdBy
    */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
    * Get value of createdTime.
    * @return the createdTime
    */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * Set the value for createdBy.
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Set the value for createdTime.
     * @param createdTime the createdTime to set
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
    
}
