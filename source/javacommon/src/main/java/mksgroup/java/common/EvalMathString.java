/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author thachle
 * @see http://www.rushis.com/2011/05/java-code-for-simple-math-expression
 *      The purpose of this program is to take a .
 *      String expression with basic math operators +*-/ 
 *      and parenthesis as input return the final result.
 *      EX: 1+(2*3) .
 *      result: 7
 */

public class EvalMathString {
    private static final String EXP_PATTERN = "exp([a-zA-Z0-9_.\\(\\)\\x24\\{\\}\\+\\-\\*\\/\\s]+)";
    
    public enum Operation {
        Add, Subtract, Multiply, Divide
    }

    /**
     * @param num1 .
     * @param num2 .
     * @param operation .
     * @return .
     */
    public static BigDecimal doBasicMath(BigDecimal num1, BigDecimal num2, Operation operation) {
        BigDecimal returnval;
        switch (operation) {
            case Add:
                returnval = num1.add(num2);
                break;
            case Subtract:
                returnval = num1.subtract(num2);
                break;
            case Multiply:
                returnval = num1.multiply(num2);
                break;
            case Divide:
                returnval = num1.divide(num2, 4, RoundingMode.HALF_UP);
                break;
            default:
                returnval = BigDecimal.ZERO;
        }
        return returnval;
    }

    
    /**
     * @param list .
     * @return .
     */
    public static BigDecimal doMath(List<String> list) {
        int index = 0;
        BigDecimal result = BigDecimal.ZERO;
        Object[] strArr = list.toArray();
        String temp = "";
        int length = strArr.length;
        for (index = 0; index < length; index++) {
            temp = strArr[index].toString();
            if ((temp.equals("*") || temp.equals("/"))) {
                if (index > 0) {
                    BigDecimal num1 = new BigDecimal(strArr[index - 1].toString());
                    BigDecimal num2 = new BigDecimal(strArr[index + 1].toString());
                    if (temp.equals("*")) {
                        result = doBasicMath(num1, num2, Operation.Multiply);
                    } else {
                        result = doBasicMath(num1, num2, Operation.Divide);
                    }
                    strArr[index + 1] = result.toString();
                    strArr[index - 1] = "0";
                    strArr[index] = "0";
                }
            }
        }
        result = BigDecimal.ZERO;
        for (index = 0; index < length; index++) {
            String operand = strArr[index].toString();
            if (!operand.equals("+")) {
                result = result.add(new BigDecimal(operand));
            }
        }
        return result;
    }

    /**
     * @param input.
     * @return .
     */
    public static BigDecimal simpleExpression(String input) {
        Pattern pt = Pattern.compile("(\\.\\d+)|(\\d+\\.?\\d*)");
        List<String> list = new LinkedList<String>();
        // input = "-1.1+2";
        // input = "1.2+3.5+578.4783*23.89345*1/2-.1223";
        Matcher mt = pt.matcher(input);
        int length = input.length();
        int startIndex = 0;
        int endIndex = 0;
        char ch = 'a';
        String negative = "";
        while (mt.find()) {
            startIndex = mt.start();
            endIndex = mt.end();
            if (startIndex == 1) {
                negative = "-";
            }
            if (endIndex < length) {
                ch = input.charAt(endIndex);
                list.add(negative + (mt.group().toString()));
                negative = ch == '-' ? "-" : "";
                if (ch == '-') {
                    list.add("+");
                } else {
                    list.add("" + ch);
                }
            } else {
                list.add(negative + (mt.group().toString()));
            }

        }
        /*
         * for(String s:list){ System.out.println(s); }
         */
        return doMath(list);
    }

    /**
     * @param input .
     * @return .
     */
    public static String performStringExpr(String input) {
        Stack<String> stack = new Stack<String>();
        StringBuilder temp = new StringBuilder();
        String ans = "";
        int in = 0;
        int length = input.length();
        char ch = '0';
        while (in < length) {
            ch = input.charAt(in);
            if (ch == '(') {
                stack.push(temp.toString());
                temp = new StringBuilder();
            } else if (ch == ')') {
                String inpForsimpleExpr = temp.length() == 0 ? stack.pop() : temp.toString();
                if (startsWithOperator(inpForsimpleExpr)) {
                    ans = inpForsimpleExpr;
                } else {
                    ans = performStringExpr(inpForsimpleExpr);
                }
                String str = stack.pop() + ans;
                stack.push(str);
                temp = new StringBuilder();
            } else if (ch != ' ') { // Skip space
                temp.append(ch);
            }
            in++;
        }
        String ele = "";

        if (temp.toString().equals("")) {
            ele = stack.isEmpty() ? "" : stack.pop();
            if (startsWithOperator(ele)) {
                String t1 = stack.pop();
                stack.push(t1 + ele);
            } else {
                stack.push(ele);
            }
        }
        String inputforSe = stack.isEmpty() ? temp.toString() : stack.pop();
        // Just in case the internal expressions evaluate to a -ve value and
        // the immediate preceding operator is + then change to - or if
        // the immediate preceding operator is - then change to +
        inputforSe = inputforSe.replaceAll("\\+-", "-");
        inputforSe = inputforSe.replaceAll("--", "-");
        return "" + simpleExpression(inputforSe);
    }

    private static boolean startsWithOperator(String temp1) {
        return temp1.startsWith("*") || temp1.startsWith("+") || temp1.startsWith("/") || temp1.startsWith("-")
                || temp1.endsWith("*") || temp1.endsWith("/") || temp1.endsWith("+") || temp1.endsWith("-");
    }

    /**
     * @param input .
     * @return .
     */
    public boolean validate(String input) {
        boolean isvalid = true;
        int parenCount = 0;
        String temp = input;
        temp = temp.replaceAll("[\\d\\*\\+\\(\\)-\\./]", "");
     
        if (temp.length() > 0) {
            isvalid = false;
        } else {
            temp = input.replaceAll("[\\d\\*\\+-\\./]", "");
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) == '(') {
                    parenCount++;
                } else if (temp.charAt(i) == ')') {
                    parenCount--;
                }
            }
            if (parenCount != 0) {
                isvalid = false;
            }
        }
        return isvalid;
    }

    public static String performStringExprEx(String strTemplate) {
        return performStringExprEx(strTemplate, null);
    }
    /**
     * Evaluate a part expression in a string.
     * Ex: performStringExprEx("exp(2-1)") return "exp(1)"
     * string of "exp(e)". e is a simple expression .
     * @return string after evaluated the expression e
     */
    
    public static String performStringExprEx(String strTemplate, Map<String, Object> mapValue) {
        Pattern pattern = Pattern.compile(EXP_PATTERN);
        Matcher matcher = pattern.matcher(strTemplate);
        StringBuffer sb = new StringBuffer();
        String key;
        Object objVal;

        // Find sub string "exp(xxx)"
        while (matcher.find()) {
            key = matcher.group(1);
            key = CommonUtil.formatPattern(key, mapValue);
            objVal = performStringExpr(key);
            // replace the column name pattern by question mark
            if (objVal != null) {
                matcher.appendReplacement(sb, objVal.toString());
            }
        }
        // append the tail of the query template to the String buffer
        matcher.appendTail(sb);
        matcher.reset();

        return sb.toString();
    }
}