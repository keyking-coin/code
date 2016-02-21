using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class MyInput : UIInput {

	public static char[] buffer = new char[10240];

    static GameObject baseExpression = null;

    GameObject expressions;

	protected override void DoBackspace ()
    {
		if (!string.IsNullOrEmpty(mValue))
        {
            if (mSelectionStart == mSelectionEnd)
            {
                if (mSelectionStart < 1) return;
                int i = mSelectionStart - 1;
                if (mValue[i] == '*' && i >= 4 && mValue[i - 4] == '#' && (mValue[i - 1] >= '0' && mValue[i - 1] <= '9') && (mValue[i - 2] >= '0' && mValue[i - 2] <= '9') && (mValue[i - 3] >= '0' && mValue[i - 3] <= '9'))
                {
                    mSelectionEnd -= 5;
                }
            }
			base.DoBackspace ();
		}
	}

    private static void clear(GameObject father)
    {
        for (int i = 0; i < father.transform.childCount; i++)
        {
            GameObject child = father.transform.GetChild(i).gameObject;
            Destroy(child);
        }
        father.transform.DetachChildren();
    }

    public static string getShowValue(GameObject fahter,UILabel label,string mValue)
    {
        clear(fahter);
        int count = 0 , spriteCount = 0;
		for (int i = 0 ; i < mValue.Length ; )
        {
            if (mValue[i] == '#' && i + 4 < mValue.Length && mValue[i + 4] == '*' && (mValue[i + 1] >= '0' && mValue[i + 1] <= '9')  && (mValue[i + 2] >= '0' && mValue[i + 2] <= '9') && (mValue[i + 3] >= '0' && mValue[i + 3] <= '9'))
            {
                string num = string.Format("{0}{1}{2}", mValue[i + 1], mValue[i + 2], mValue[i + 3]);
                for (int j = 0 ; j < 5; j++)
                {
                    buffer[count] = ' ';
                    count++;
                }
                char[] newchs = new char[count];
                System.Array.Copy(buffer,newchs,count);
                string newStr = new string(newchs);
                label.text = newStr;
                Vector2 v2 = MyUtilTools.compute(label);
                if (baseExpression == null)
                {
                    baseExpression = Resources.Load<GameObject>("prefabs/e1");
                }
                GameObject expression = NGUITools.AddChild(fahter,baseExpression);
                string sn = "e" + int.Parse(num);
                expression.name = "e" + spriteCount;
                UISprite sprite = expression.GetComponent<UISprite>();
                sprite.spriteName = sn;
                float x = v2.x;
                float y = v2.y - 20;
                expression.transform.localPosition = new Vector3(x,y,0);
                UIButton button = expression.GetComponent<UIButton>();
                button.enabled = false;
                button.normalSprite = sn;
                i += 5;
                spriteCount ++;
			}
            else
            {
				buffer[count] = mValue[i];
				i ++;
				count++;
			}
		}
		char[] nchs = new char[count];
		System.Array.Copy (buffer,nchs,count);
		return new string (nchs);
	}

	public override void  UpdateLabel()
    {
        if (expressions == null)
        {
            expressions = gameObject.transform.FindChild("expressions").gameObject;
        }
		string tempValue = mValue;
        mValue = getShowValue(expressions,label,mValue);
		base.UpdateLabel ();
		mValue = tempValue;
	}

    public override int GetCharUnderMouse()
    {
        int index = base.GetCharUnderMouse();
        if (index < label.text.Length && label.text[index] == ' ')
        {
            if (mValue[index] == '*')
            {
                if (index + 4 < mValue.Length && mValue[index + 4] == '#')
                {
                    index --;
                }
            }else{
                int a = Mathf.Max(index-4,0);
                int b = Mathf.Min(index + 4,mValue.Length);
                int cur1 = -1 ,cur2 = -1;
                for (int i = a ; i < index ; i++)
                {
                    if (mValue[i] == '#')
                    {
                        cur1 = i;
                        break;
                    }
                }
                for (int i = index ; i < b ; i++)
                {
                    if (mValue[i] == '*')
                    {
                        cur2 = i;
                        break;
                    }
                }
                if (cur1 != -1 && cur2 != -1 && Mathf.Abs(cur2 - cur1) == 4)
                {
                    int c = Mathf.Abs(cur1 - index);
                    int d = Mathf.Abs(cur2 - index);
                    if (c >= d)
                    {
                        return cur2 + 1;
                    }
                    else
                    {
                        return cur1;
                    }
                }
            }
        }
        return index;
    }

    public void addExpression(string str)
    {
        Insert(str);
    }
}
 
 
