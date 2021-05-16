/**
 * Licensed to MKS Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * FA licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package mksgroup.java.common;

/**
 * This class stores status of request processing such as:<br/>
 * 1) Ajax request from client.
 * @author ThachLN
 */
public class Result {
    /** Key "status" . */
    final public static String KEY_STATUS = "status";

    /** Key "message" . */
    final public static String KEY_MESSAGE = "message";
    
    /** Success . */
    final public static String OK = "OK";
    
    /** Not Success . */
    final public static String FAIL = "FAIL";
    
    final public static Result RESULT_OK = new Result(0, OK, "Success");
    final public static Result RESULT_FAIL = new Result(1, FAIL, "Error");
    
    /** Error code. 0: No error. */
    private int cd = 0;
    
    /** Result: OK | FAIL . */
    private String status;
    
    /** Result message . */
    private String message;
    
    /**
     * Constructor with default error code = 0;
     * @param status
     * @param message
     */
    public Result(String status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    /**
     * @param cd
     * @param status
     * @param message
     */
    public Result(int cd, String status, String message) {
        this.cd = cd;
        this.status = status;
        this.message = message;
    }

    /**
    * Get value of cd.
    * @return the cd
    */
    public int getCd() {
        return cd;
    }
    /**
     * Set the value for cd.
     * @param cd the cd to set
     */
    public void setCd(int cd) {
        this.cd = cd;
    }
    /**
     * Get value of status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * Set the value for status.
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * Get value of message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * Set the value for message.
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
