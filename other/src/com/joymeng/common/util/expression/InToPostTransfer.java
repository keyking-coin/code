package com.joymeng.common.util.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class InToPostTransfer {
    private static List<String> operatorList = new ArrayList<String>();
    private static Map<String, Integer> funcParamrterCountMap = new HashMap<String, Integer>();
	
    // Use this for initialization
	void Start () {
        operatorList.add("!");
        operatorList.add("+");
        operatorList.add("-");
        operatorList.add("*");
        operatorList.add("/");
        operatorList.add("%");
        operatorList.add(">");
        operatorList.add("<");
        operatorList.add("=");
        operatorList.add("&");
        operatorList.add("|");
        operatorList.add(">=");
        operatorList.add("<=");
        operatorList.add("==");
        operatorList.add("!=");
        operatorList.add("&&");
        operatorList.add("||");
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public static String InToPost(String infixExpression)
    {
        funcParamrterCountMap.clear();
        if(operatorList.size() <= 0){
            operatorList.add("!");
            operatorList.add("+");
            operatorList.add("-");
            operatorList.add("*");
            operatorList.add("/");
            operatorList.add("%");
            operatorList.add(">");
            operatorList.add("<");
            operatorList.add("=");
            operatorList.add("&");
            operatorList.add("|");
            operatorList.add(">=");
            operatorList.add("<=");
            operatorList.add("==");
            operatorList.add("!=");
            operatorList.add("&&");
            operatorList.add("||");
        }
        boolean isPreOperator = false;
        String postfixExpression = "";
        int index = 0;
        Stack<String> stack = new Stack<String>();
        int lastIndexOfOperator = 0;
        String operatorCurrent;
        String operatorInStacktop = "";
        int lastIndexOfOperand = 0;
        String operandCurrent = "";
        int lastIndexOfCustomFunction = 0;
        String customFunction = "";
        int parameterCount = 0;
        int lastIndexOfString = 0;
        String stringInExpression = "";
        while (index < infixExpression.length() || stack.size() > 0)
        {
            if ( index >= infixExpression.length() )        //last something
            {
                postfixExpression += stack.pop() + ",";
            }//else if (infixExpression[index] == '{')      //{
            else if( infixExpression.charAt(index) == '{')
            {
                isPreOperator = false;
                stack.push("{");
                index++;
                continue;
            } //else if (infixExpression[index] == '}')
            else if( infixExpression.charAt(index) == '}')
            {
                isPreOperator = false;
                operatorInStacktop = stack.pop();
                while (operatorInStacktop != "{")
                {
                    postfixExpression += operatorInStacktop + ",";
                    operatorInStacktop = stack.pop();
                }
                index++;
                continue;
            }
            else if (infixExpression.charAt(index) == '(' || infixExpression.charAt(index) == ',')
            {
                isPreOperator = true;
                index++;
                continue;
            }
            else if (infixExpression.charAt(index) == ' ')
            {
                index++;
                continue;
            }
            else if (infixExpression.charAt(index) == ')')
            {
                isPreOperator = false;
                customFunction = stack.pop();
                parameterCount = funcParamrterCountMap.get(customFunction);
                postfixExpression += parameterCount + "," + customFunction + ",(),";
                index++;
            }
            else if (infixExpression.charAt(index) == '\'')
            {
                lastIndexOfString = GetString(infixExpression, index);
                stringInExpression = infixExpression.substring(index + 1, lastIndexOfString);
                postfixExpression += stringInExpression + ",";
                index = lastIndexOfString + 1;
            }
            else
            {
                lastIndexOfOperator = MatchOperator(infixExpression, index);
                if (lastIndexOfOperator != -1)       //operator
                {
                    operatorCurrent = infixExpression.substring(index, lastIndexOfOperator);
                    if (isPreOperator == true && operatorCurrent == "-")
                    {
                        postfixExpression += operatorCurrent;
                        index = lastIndexOfOperator;
                        isPreOperator = false;
                        continue;
                    }
                    isPreOperator = true;
                    if (stack.size() == 0)
                    {
                        stack.push(operatorCurrent);
                        index = lastIndexOfOperator;
                        continue;
                    }
                    operatorInStacktop = stack.peek();
                    if (GetOperatorPriority(operatorCurrent) > GetOperatorPriority(operatorInStacktop))
                    {
                        stack.push(operatorCurrent);
                        index = lastIndexOfOperator;
                        continue;
                    }
                    else
                    {
                        postfixExpression += stack.pop() + ",";
                        stack.push(operatorCurrent);
                        index = lastIndexOfOperator;
                        continue;
                    }
                }
                else        //operand
                {
                    lastIndexOfOperand = GetOperand(infixExpression, index);
                    if (lastIndexOfOperand != -1)
                    {
                        isPreOperator = false;
                        operandCurrent = infixExpression.substring(index, lastIndexOfOperand);
                        postfixExpression += operandCurrent + ",";
                        index = lastIndexOfOperand;
                        continue;
                    }
                    else        //custom function
                    {
                        isPreOperator = false;
                        lastIndexOfCustomFunction = GetCustomFunction(infixExpression, index);
                        customFunction = infixExpression.substring(index, lastIndexOfCustomFunction);
                        funcParamrterCountMap.put(customFunction, GetFuncParameterCount(infixExpression, lastIndexOfCustomFunction + 1));
                        stack.push(customFunction);
                        index = lastIndexOfCustomFunction;
                        continue;
                    }
                }
            }
        }
        postfixExpression = postfixExpression.substring(0, postfixExpression.length() - 1);
        return postfixExpression;
    }
    
    private static int GetString(String infixExpression, int beginIndex)
    {
        int lastIndex = beginIndex + 1;
        char ch = infixExpression.charAt(lastIndex);
        while (ch != '\'')
        {
            lastIndex++;
            ch = infixExpression.charAt(lastIndex);
        }
        return lastIndex;
    }

    private static int MatchOperator(String infixExpression, int beginIndex)
    {
        int lastIndex = beginIndex;
        String str = infixExpression.substring(beginIndex, lastIndex + 1);
 
        while ( operatorList.contains(str)  && lastIndex < infixExpression.length() )
        {
            lastIndex++;
            if (lastIndex == infixExpression.length())
            {
                continue;
            }
            str = infixExpression.substring(beginIndex, lastIndex + 1);
        }
        if (lastIndex == beginIndex)
        {
            lastIndex = -1;
        }
        return lastIndex;
    }

    private static int GetOperand(String infixExpression, int beginIndex)
    {
        int lastIndex = beginIndex;
        char ch = infixExpression.charAt(lastIndex);
        switch (ch)
        { 
            case 't':
                if (infixExpression.substring(beginIndex, beginIndex + 4).equals("true"))
                {
                    lastIndex = beginIndex + 4;
                }
                break;
            case 'f':
                if (infixExpression.substring(beginIndex, beginIndex + 5).equals("false"))
                {
                    lastIndex = beginIndex + 5;
                }
                break;
            default:
                while ( (Character.isDigit(ch) || ch == '.') && lastIndex < infixExpression.length() )
                {
                    lastIndex++;
                    if (lastIndex == infixExpression.length())
                    {
                        continue;
                    }
                    ch = infixExpression.charAt(lastIndex);
                }
                break;
        }
        if (lastIndex == beginIndex)
        {
            lastIndex = -1;
        }
        return lastIndex;
    }

    private static int GetCustomFunction(String infixExpression, int beginIndex)
    {
        int lastIndex = beginIndex;
        char ch = infixExpression.charAt(lastIndex);
        while (ch != '(' && lastIndex < infixExpression.length() )
        {
            lastIndex++;
            if (lastIndex == infixExpression.length() )
            {
                continue;
            }
            ch = infixExpression.charAt(lastIndex);
        }
        return lastIndex;
    }

    private static int GetFuncParameterCount(String infixExpression, int beginIndex)
    {
        int parametrCount = 1;
        int index = beginIndex;
        char ch = infixExpression.charAt(index);
		if (ch == ')') {
			parametrCount = 0;
		}
        while (ch != ')')
        {
            if (ch == '(')
            { 
                index++;
                ch = infixExpression.charAt(index);
                while (ch != ')')
                {
                    index++;
                    ch = infixExpression.charAt(index);
                }
            }
            if (ch == ',')
            {
                parametrCount++;
            }
            index++;
            ch = infixExpression.charAt(index);
        }
        return parametrCount;
    }

    private static int GetOperatorPriority(String ope)
    {
        switch (ope)
        { 
            case "||":
                return 1;
            case "&&":
                return 2;
            case "==":
                return 3;
            case "!=":
                return 3;
            case "<=":
                return 4;
            case ">=":
                return 4;
            case "<":
                return 4;
            case ">":
                return 4;
            case "+":
                return 5;
            case "-":
                return 5;
            case "*":
                return 6;
            case "/":
                return 6;
            case "%":
                return 6;
            case "!":
                return 7;
            case "{":
                return 0;
            default:
                return -1;
        }
    }
}
