package com.joymeng.common.util.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ProtoExpression {
    public String[] rpnStream;

    private static Map<String,BaseFunc> functionTable = new HashMap<String,BaseFunc>();

    private static Map<String, Integer> operator_switch_map = new HashMap<String,Integer>();

    static {
    	CustomExpression.ProtoExpressionInit();
    }
    
    public static void CheckArgCount(String functionName, Object[] args, String[] argNames) throws Exception
    {
        if (args.length != argNames.length)
        {
            if (argNames.length == 0)
            {
                throw new Exception(String.format("%s takes 0 arguments.", functionName));
            }
            if (argNames.length == 1)
            {
                throw new Exception(String.format("%s takes 1 argument: %s.", functionName, argNames[0]));
            }
          throw new Exception(String.format("%s takes %s arguments:%s.", functionName, argNames.length, argNames[0] ));
        }
    }


    public static void RegisterFunction(String functionName,BaseFunc func)//, String argumentsHint, Class<?extends Method> func)
    {
    	functionTable.put(functionName, func);
    	
    }
    
    private void initOperatorMap(){
        Map<String, Integer> dictionary = new HashMap<String, Integer>(0x11);
        dictionary.put("true", 0);
        dictionary.put("false", 1);
        dictionary.put("!", 2);
        dictionary.put("+", 3);
        dictionary.put("-", 4);
        dictionary.put("*", 5);
        dictionary.put("/", 6);
        dictionary.put("%", 7);
        dictionary.put(">", 8);
        dictionary.put("<", 9);
        dictionary.put(">=", 10);
        dictionary.put("<=", 11);
        dictionary.put("==", 12);
        dictionary.put("!=", 13);
        dictionary.put("&&", 14);
        dictionary.put("||", 15);
        dictionary.put("()", 0x10);
        operator_switch_map = dictionary;
    }
    
//    private Object getSumObjects(Object...objects){
//    	if( objects.length == 0 ){
//    		return null;
//    	}else if( objects.length == 1){
//    		return objects[0];
//    	}else{
//    		
//    	}
//    	return null;
//    }

    public Object RPNEvaluate() throws Exception
    {
        if ((this.rpnStream == null) || (this.rpnStream.length == 0))
        {
            return true;
        }
        Stack<Object> stack = new Stack<Object>();

        for(String str : this.rpnStream )  //foreach (String str in this.rpnStream)
        {
            Object operand1;
            Object operand2;
            boolean flagForLogic;      //for "&&" and "||"
            Object[] customOperandArray = new Object[1];
            int customOperandIndex = 0;
            String functionNameStr = "";
//            ProtoExpressionFunction function;
            String key = str;

            if (key != null)
            {
                int switchKey;
                if (operator_switch_map.size() == 0)
                {
                	initOperatorMap();
                }
                if (operator_switch_map.containsKey(key)) /*operator_switch_map.TryGetValue(key, out switchKey)*/
                {
                	switchKey = operator_switch_map.get(key);
                    switch (switchKey)
                    {
                        case 0:
                            {
                                stack.push(true);
                                continue;
                            }
                        case 1:
                            {
                                stack.push(false);
                                continue;
                            }
                        case 2:
                        	{
                        		operand1 = stack.pop();
                        		if (!(operand1 instanceof Boolean))
                        		{
                                	throw new Exception("operator ! expects boolean operand, but got " + operand1.toString());
                        		}
                        		stack.push(!((boolean)operand1));
                        		continue;
                        	}
                        case 3:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                              
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double) )
                                {
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num4 = (float)operand1;
                                        float num5 = (float)operand2;
                                        stack.push(num5 + num4);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num6 = (long)operand1;
                                        long num7 = (long)operand2;
                                        stack.push(num7 + num6);
                                    }
                                    else
                                    {
                                        int num8 = (int)operand1;
                                        int num9 = (int)operand2;
                                        stack.push(num9 + num8);
                                    }
                                    continue;
                                }
                                double num2 = (double)operand1;
                                double num3 = (double)operand2;
                                stack.push(num3 + num2);
                                continue;
                            }
                        case 4:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_034D;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num12 = (float)operand1;
                                        float num13 = (float)operand2;
                                        stack.push(num13 - num12);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num14 = (long)operand1;
                                        long num15 = (long)operand2;
                                        stack.push(num15 - num14);
                                    }
                                    else
                                    {
                                        int num16 = (int)operand1;
                                        int num17 = (int)operand2;
                                        stack.push(num17 - num16);
                                    }
                                    continue;
                                }
                                double num10 = (double)operand1;
                                double num11 = (double)operand2;
                                stack.push(num11 - num10);
                                continue;
                            }
                        case 5:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0441;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num20 = (float)operand1;
                                        float num21 = (float)operand2;
                                        stack.push(num21 * num20);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num22 = (long)operand1;
                                        long num23 = (long)operand2;
                                        stack.push(num23 * num22);
                                    }
                                    else
                                    {
                                        int num24 = (int)operand1;
                                        int num25 = (int)operand2;
                                        stack.push(num25 * num24);
                                    }
                                    continue;
                                }
                                double num18 = (double)operand1;
                                double num19 = (double)operand2;
                                stack.push(num19 * num18);
                                continue;
                            }
                        case 6:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0535;
                                	 if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                     {
                                         float num28 = (float)operand1;
                                         float num29 = (float)operand2;
                                         stack.push(num29 / num28);
                                     }
                                     else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                     {
                                         long num30 = (long)operand1;
                                         long num31 = (long)operand2;
                                         stack.push(num31 / num30);
                                     }
                                     else
                                     {
                                         int num32 = (int)operand1;
                                         int num33 = (int)operand2;
                                         stack.push(num33 / num32);
                                     }
                                     continue;
                                }
                                double num26 = (double)operand1;
                                double num27 = (double)operand2;
                                stack.push(num27 / num26);
                                continue;
                            }
                        case 7:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0629;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num36 = (float)operand1;
                                        float num37 = (float)operand2;
                                        stack.push(num37 % num36);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num38 = (long)operand1;
                                        long num39 = (long)operand2;
                                        stack.push(num39 % num38);
                                    }
                                    else
                                    {
                                        int num40 = (int)operand1;
                                        int num41 = (int)operand2;
                                        stack.push(num41 % num40);
                                    }
                                    continue;
                                }
                                double num34 = (double)operand1;
                                double num35 = (double)operand2;
                                stack.push(num35 % num34);
                                continue;
                            }
                        case 8:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_071E;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num44 = (float)operand1;
                                        float num45 = (float)operand2;
                                        stack.push(num45 > num44);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num46 = (long)operand1;
                                        long num47 = (long)operand2;
                                        stack.push(num47 > num46);
                                    }
                                    else
                                    {
                                        int num48 = (int)operand1;
                                        int num49 = (int)operand2;
                                        stack.push(num49 > num48);
                                    }
                                    continue;
                                }
                                double num42 = (double)operand1;
                                double num43 = (double)operand2;
                                stack.push(num43 > num42);
                                continue;
                            }
                        case 9:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0816;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num52 = (float)operand1;
                                        float num53 = (float)operand2;
                                        stack.push(num53 < num52);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num54 = (long)operand1;
                                        long num55 = (long)operand2;
                                        stack.push(num55 < num54);
                                    }
                                    else
                                    {
                                        int num56 = (int)operand1;
                                        int num57 = (int)operand2;
                                        stack.push(num57 < num56);
                                    }
                                    continue;
                                }
                                double num50 = (double)operand1;
                                double num51 = (double)operand2;
                                stack.push(num51 < num50);
                                continue;
                            }
                        case 10:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0911;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num60 = (float)operand1;
                                        float num61 = (float)operand2;
                                        stack.push(num61 >= num60);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num62 = (long)operand1;
                                        long num63 = (long)operand2;
                                        stack.push(num63 >= num62);
                                    }
                                    else
                                    {
                                    	int num64 = (int) operand1;
                                        int num65 = (int) operand2;
                                        stack.push(num65 >= num64);
                                    }
                                    continue;
                                }
                                double num58 = (double)operand1;
                                double num59 = (double)operand2;
                                stack.push(num59 >= num58);
                                continue;
                            }
                        case 11:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Double) && !(operand2 instanceof Double))
                                {
//                                    goto Label_0A15;
                                    if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num68 = (float)operand1;
                                        float num69 = (float)operand2;
                                        stack.push(num69 <= num68);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num70 = (long)operand1;
                                        long num71 = (long)operand2;
                                        stack.push(num71 <= num70);
                                    }
                                    else
                                    {
                                        int num72 = (int)operand1;
                                        int num73 = (int)operand2;
                                        stack.push(num73 <= num72);
                                    }
                                    continue;
                                }
                                double num66 = (double)operand1;
                                double num67 = (double)operand2;
                                stack.push(num67 <= num66);
                                continue;
                            }
                        case 12:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Boolean) && !(operand2 instanceof Boolean))
                                {
//                                    goto Label_0B16;
                                    if ((operand1 instanceof Double) || (operand2 instanceof Double))
                                    {
                                        double num74 = (double)operand1;
                                        double num75 = (double)operand2;
                                        stack.push(num75 == num74);
                                    }
                                    else if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num76 = (float)operand1;
                                        float num77 = (float)operand2;
                                        stack.push(num77 == num76);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num78 = (long)operand1;
                                        long num79 = (long)operand2;
                                        stack.push(num79 == num78);
                                    }
                                    else
                                    {
                                        int num80 = (int)operand1;
                                        int num81 = (int)operand2;
                                        stack.push(num81 == num80);
                                    }
                                    continue;
                                }
                                boolean flag1 = (boolean)operand1;
                                boolean flag2 = (boolean)operand2;
                                stack.push(flag2 == flag1);
                                continue;
                            }
                        case 13:
                            {
                                operand1 = stack.pop();
                                operand2 = stack.pop();
                                if (!(operand1 instanceof Boolean) && !(operand2 instanceof Boolean))
                                {
//                                    goto Label_0C51;
                                    if ((operand1 instanceof Double) || (operand2 instanceof Double))
                                    {
                                        double num82 = (double)operand1;//Convert.ToDouble(operand1);
                                        double num83 = (double)operand2;//Convert.ToDouble(operand2);
                                        stack.push(num83 != num82);
                                    }
                                    else if ((operand1 instanceof Float) || (operand2 instanceof Float))
                                    {
                                        float num84 = (float)operand1;//Convert.ToSingle(operand1);
                                        float num85 = (float)operand2;//Convert.ToSingle(operand2);
                                        stack.push(num85 != num84);
                                    }
                                    else if ((operand1 instanceof Long) || (operand2 instanceof Long))
                                    {
                                        long num86 = (long)operand1;//Convert.ToInt64(operand1);
                                        long num87 = (long)operand2;//Convert.ToInt64(operand2);
                                        stack.push(num87 != num86);
                                    }
                                    else
                                    {
                                        int num88 = (int)operand1;//Convert.ToInt32(operand1);
                                        int num89 = (int)operand2;//Convert.ToInt32(operand2);
                                        stack.push(num89 != num88);
                                    }
                                    continue;
                                }
                                boolean flag3 = (boolean)operand1;
                                boolean flag4 = (boolean)operand2;
                                stack.push(flag4 != flag3);
                                continue;
                            }
                        case 14:
                            operand1 = stack.pop();
                            operand2 = stack.pop();
                            if (!(operand1 instanceof Boolean) || !(operand2 instanceof Boolean))
                            {
//                                Object[] objArray1 = new Object[] { "operator && expects boolean operands, but got ", operand2.GetType(), " and ", operand1.GetType() };
                            	String objArray1 = "operator && expects boolean operands, but got " + operand2.toString() + " and " + operand1.toString();
                                throw new Exception( objArray1 );
                            }
                            flagForLogic = (boolean)operand1;
                            boolean flag5 = (boolean)operand2;
                            stack.push(!flag5 ? ((boolean)false) : ((boolean)flagForLogic));
                            continue;

                        case 15:
                            operand1 = stack.pop();
                            operand2 = stack.pop();
                            if (!(operand1 instanceof Boolean) || !(operand2 instanceof Boolean))
                            {
//                                String[] objArray2 = new String[] { "operator || expects boolean operands, but got ", operand2.toString(), " and ", operand1.toString() };
                                String objArray2 = "operator || expects boolean operands, but got " + operand2.toString() + " and " + operand1.toString();
                            	throw new Exception(objArray2);
                            }
                            flagForLogic = (boolean)operand1;
                            boolean flag6 = (boolean)operand2;
                            stack.push(flag6 ? ((boolean)true) : ((boolean)flagForLogic));
                            continue;
                        case 0x10:
                            {
                            	functionNameStr = (String)stack.pop(); //函数名
                                int customOperandCount = (int)stack.pop();//参数个数
                                customOperandArray = new Object[customOperandCount];//参数列表
                                customOperandIndex = customOperandCount - 1;
                                while (customOperandIndex >= 0)
                                {
                                	customOperandArray[customOperandIndex] = stack.pop();
                                	customOperandIndex--;
                                }
                                if ( !functionTable.containsKey(functionNameStr) )
                                {
                                	throw new Exception("Unknown function: " + functionNameStr);
                                }

                                Object ob = functionTable.get( functionNameStr).excute( customOperandArray );
                                stack.push( ob );
                                continue;
                            }
                    }
                }
            }
            
            if (str.startsWith("'"))
            {
                stack.push(str.substring(1));
            }
            else if ( str.contains(".")/*Enumerable.Contains<char>(str, '.')*/)
            {
//            	stack.push( Double.parseDouble( str ) );
            	stack.push( Float.parseFloat(str) );
                //float floatNum = Float.parseFloat( str );
                //if (float.TryParse(str, out floatNum))
                //{
                //    stack.push(floatNum);
                //}
                //else
                //{
                //   stack.push(double.Parse(str));
                //}
            }
            else if (Character.isDigit(str.charAt(0)))
            {
                stack.push( Integer.parseInt( str) );
//                if (int.TryParse(str, out intNum))
//                {
//                    stack.Push(intNum);
//                }
//                else
//                {
//                    stack.Push(long.Parse(str));
//                }
            }
            else
            {
                stack.push(str);
            }
        }
        
        return stack.pop();
    }

    public static Object ExecuteExpression(String infix) throws Exception
    {
        String postfix = InToPostTransfer.InToPost(infix);
        ProtoExpression postfixExpression = new ProtoExpression();
        postfixExpression.rpnStream = postfix.split(",");
        return postfixExpression.RPNEvaluate();
    }
    
    public static void main( String[] args) throws Exception {
    	//String infix = "{1.0+max(2.0,min(5.0,4.0))*3.0/4.0+34.0/5.0-1.0}";
    	//float result = (float) ProtoExpression.ExecuteExpression(infix);
    	//System.out.println( "result = " + result );
//    	String infix = "getLevel(1465173)>=2 && getBuild(1465173,0,CityCenter)>=2";
//    	String infix = "{min(1,2)<3&&isPrevExpression('1465173','FTUE_PHASE_01')}";
    	String condition = "false";
    	Object obj = ProtoExpression.ExecuteExpression(condition);
    	System.out.println( "result = " + obj);
    }
}
