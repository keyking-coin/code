using UnityEngine;
using System;
using System.Collections;
using System.Runtime.InteropServices;
using System.Net;
using System.IO;
using System.Text;
using System.Collections.Generic;

public class MyUtilTools  {

    public static string SELECT_FILE_NAME;

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]

    public class OpenFileName
    {
        public int structSize = 0;
        public IntPtr dlgOwner = IntPtr.Zero;
        public IntPtr instance = IntPtr.Zero;
        public String filter = null;
        public String customFilter = null;
        public int maxCustFilter = 0;
        public int filterIndex = 0;
        public String file = null;
        public int maxFile = 0;
        public String fileTitle = null;
        public int maxFileTitle = 0;
        public String initialDir = null;
        public String title = null;
        public int flags = 0;
        public short fileOffset = 0;
        public short fileExtension = 0;
        public String defExt = null;
        public IntPtr custData = IntPtr.Zero;
        public IntPtr hook = IntPtr.Zero;
        public String templateName = null;
        public IntPtr reservedPtr = IntPtr.Zero;
        public int reservedInt = 0;
        public int flagsEx = 0;
    }

    public class WindowDll
    {
        [DllImport("Comdlg32.dll", SetLastError = true, ThrowOnUnmappableChar = true, CharSet = CharSet.Auto)]
        public static extern bool GetOpenFileName([In, Out] OpenFileName ofn);
        public static bool GetOpenFileName1([In, Out] OpenFileName ofn)
        {
            return GetOpenFileName(ofn);
        }
    }

    public static void openFileSelect(string title)
    {
        OpenFileName ofn = new OpenFileName();
        ofn.structSize = Marshal.SizeOf(ofn);
        ofn.filter = "All Files\0*.*\0\0";
        ofn.file = new string(new char[256]);
        ofn.maxFile = ofn.file.Length;
        ofn.fileTitle = new string(new char[64]);
        ofn.maxFileTitle = ofn.fileTitle.Length;
        ofn.initialDir = UnityEngine.Application.dataPath;//默认路径  
        ofn.title = title;
        //ofn.defExt = "JPG";//显示文件的类型  
        //注意 一下项目不一定要全选 但是0x00000008项不要缺少  
        ofn.flags = 0x00080000 | 0x00001000 | 0x00000800 | 0x00000200 | 0x00000008;//OFN_EXPLORER|OFN_FILEMUSTEXIST|OFN_PATHMUSTEXIST| OFN_ALLOWMULTISELECT|OFN_NOCHANGEDIR
        if (WindowDll.GetOpenFileName(ofn))
        {
            SELECT_FILE_NAME = ofn.file;
            Debug.Log("Selected file with full path: {0}" + SELECT_FILE_NAME);
        }  
    }

    public static void ChangeLayer(GameObject obj, int layer)
    {
        for (int i = 0 ; i < obj.transform.childCount ; i++)
        {
            GameObject child = obj.transform.GetChild(i).gameObject;
            child.layer = layer;
            if (child.transform.childCount > 0)
            {
                ChangeLayer(child,layer);
            }
        }
        obj.layer = layer;
    }

    public static void insertStr(UILabel label , string value , int width , string tail = "")
    {
        label.text = value + "…" + tail;
        float aLen = computeLen(label);
        if (aLen < width)
        {
            label.text = value + tail;
            return;
        }
        float len = 0;
        int count = 0;
        string text = "";
        while (len < width && count < value.Length)
        {
            text += value[count];
            label.text = text + "…" + tail;
            len = computeLen(label);
            count++;
        }
    }

    public static Vector2 compute(UILabel label)
    {
        Vector2 result = Vector2.zero;
        string text = label.processedText;
        if (string.IsNullOrEmpty(text)) return result;
        label.UpdateNGUIText();
        int index = label.text.Length - 1;
        NGUIText.PrintCharacterPositions(text, UILabel.mTempVerts, UILabel.mTempIndices);
        if (UILabel.mTempVerts.size > 0)
        {
            label.ApplyOffset(UILabel.mTempVerts, 0); 
            for (int i = 0; i < UILabel.mTempIndices.size ; i++)
            {
                if (UILabel.mTempIndices[i] == index)
                {
                    result = UILabel.mTempVerts[i];
                    break;
                }
            }
            UILabel.mTempVerts.Clear();
            UILabel.mTempIndices.Clear();
        }
        NGUIText.bitmapFont = null;
        NGUIText.dynamicFont = null;
        return result;
    }

    private static float computeFloat(UILabel label)
    {
        float result = 0 ,preData = 0;
        string text = label.processedText;
        if (string.IsNullOrEmpty(text)) return result;
        label.UpdateNGUIText();
        NGUIText.PrintCharacterPositions(text, UILabel.mTempVerts, UILabel.mTempIndices);
        if (UILabel.mTempVerts.size > 0)
        {
            label.ApplyOffset(UILabel.mTempVerts, 0);
            for (int i = 0 ; i < UILabel.mTempIndices.size; i += 2)
            {
                float x = UILabel.mTempVerts[i].x;
                if (x > preData)
                {
                    result += x - preData;
                }
                else
                {
                    result += label.fontSize;
                }
                preData = x;
            }
            UILabel.mTempVerts.Clear();
            UILabel.mTempIndices.Clear();
        }
        NGUIText.bitmapFont = null;
        NGUIText.dynamicFont = null;
        return result;
    }

    public static float computeLen(UILabel label)
    {
        int th = label.height;
        string temp = label.text;
        label.height = 10000;
        string[] ss = temp.Split(new char[]{'\n'});
        float result = 0;
        if (ss.Length == 1)
        {
            label.text = temp;
            result = computeFloat(label);
        }
        else
        {
            for (int i = 0; i < ss.Length; i++)
            {
                label.text = ss[i];
                float len = computeFloat(label);
                if (i < ss.Length - 1)
                {
                    if (len < label.width){
                        len += label.width - len;
                    }
                    else
                    {
                        int num = (int) len / label.width;
                        len += label.width - (len - num * label.width);
                    }
                }
                result += len;
            }
        }
        label.height = th;
        label.text = temp;
        return result;
    }

    public static int computeRow(UILabel label)
    {
        float len = computeLen(label);
        int row = 0;
        while (len > label.width)
        {
            len -= label.width;
            row++;
        }
        if (len > 0)
        {
            row++;
        }
        return row;
    }

    public static string numToString(int num)
    {
        if (num < 10)
        {
            return "0" + num;
        }
        return num + "";
    }

    public static bool stringIsNull(string str)
    {
        if (str == null || str.Equals(""))
        {
            return true;
        }
        return false;
    }

    public static void changeAlpha(float alpha,GameObject container)
    {
        UIPanel panel = container.GetComponent<UIPanel>();
        panel.alpha = alpha;
    }

    public static ByteBuffer tryToLogic(string key)
    {
        ByteBuffer buffer = NetUtil.getInstance.find(key);
        if (buffer != null)
        {
            NetUtil.getInstance.remove(key);
            int result = buffer.ReadInt();
            if (result == 1)
            {
                string tip = buffer.ReadString();
                DialogUtil.tip(tip);
                return null;
            }
        }
        return buffer;
    }

    public static bool checkIsNull(DealBody item)
    {
        try
        {
            return item.id >= 0;
        }
        catch (System.Exception e)
        {
            Debug.LogException(e);
            return true;
        }
    }

    public static void clearChild(Transform container,string[] names = null)
    {
        for (int i = 0; i < container.childCount; i++)
        {
            GameObject child = container.GetChild(i).gameObject;
            if (names != null)
            {
                bool needContinue = false;
                foreach (string name in names)
                {
                    if (child.name.Equals(name))
                    {
                        needContinue = true;
                    }
                }
                if (needContinue)
                {
                    continue;
                }
            }
            GameObject.Destroy(child);
        }
        //container.DetachChildren();
    }

    public static bool checkEmail(string strEmail)
    {
        int i, j;
        string strTmp, strResult;
        string strWords = "abcdefghijklmnopqrstuvwxyz_-.0123456789"; //定义合法字符范围
        strTmp = strEmail.Trim();
        if (!(strTmp == "" || strTmp.Length == 0))
        {
            if ((strTmp.IndexOf("@") < 0))
            {
                return false;
            }
            string[] strChars = strTmp.Split(new char[] { '@' });
            foreach (string strChar in strChars)
            {
                i = strChar.Length;
                if (i == 0)
                {
                    return false;
                }
                for (j = 0; j < i; j++)
                {
                    strResult = strChar.Substring(j, 1).ToLower();//逐个字符取出比较
                    if (strWords.IndexOf(strResult) < 0)
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static int GetWeekDays(DayOfWeek day)
    {
        switch (day)
        {
            case DayOfWeek.Monday: return 1;
            case DayOfWeek.Tuesday: return 2;
            case DayOfWeek.Wednesday: return 3;
            case DayOfWeek.Thursday: return 4;
            case DayOfWeek.Friday: return 5;
            case DayOfWeek.Saturday: return 6;
            case DayOfWeek.Sunday: return 0;
        }
        return 0;
    }

    public static string GetWeekDaysStr(DayOfWeek day)
    {
        switch (day)
        {
            case DayOfWeek.Monday: return "周一";
            case DayOfWeek.Tuesday: return "周二";
            case DayOfWeek.Wednesday: return "周三";
            case DayOfWeek.Thursday: return "周四";
            case DayOfWeek.Friday: return "周五";
            case DayOfWeek.Saturday: return "周六";
            case DayOfWeek.Sunday: return "周日";
        }
        return null;
    }
}
