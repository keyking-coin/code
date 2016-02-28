using UnityEngine;
using System;
using System.Collections.Generic;
using UnityEngine.UI;
using ZXing;
using ZXing.QrCode;
using LitJson;

public class InfoDecoder : MonoBehaviour
{
    public Font font;

    public GameObject father = null;

    public UIAtlas atlas;

    public UIWidget rect;

	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("Notice");
        if (buffer != null)
        {
            decode(buffer.ReadString());
        }
	}
	
    public void show()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("Notice");
        NetUtil.getInstance.SendMessage(buffer);
    }

    private void decode(string str)
    {
        JsonData jd = JsonMapper.ToObject(str);
        string title = jd["title"].ToString();
        float startx = 0.0f, starty = 480.0f;
        starty = addLabel(title,startx,starty,"title",true);
        starty -= 64;
        JsonData content = jd["content"];
        for (int i = 0; i < content.Count; i++ )
        {
            JsonData child = content[i];
            JsonData sun = tryToGetData(child,"p");
            if (sun != null)
            {
                starty = addLabel(sun.ToString(),startx, starty,"p_" + i,false, NGUIText.Alignment.Left);
                continue;
            }
            sun = tryToGetData(child,"t");
            if (sun != null)
            {
                starty = addTable(sun,-200,starty,"t_" + i);
                continue;
            }
            sun = tryToGetData(child, "s");
            if (sun != null)
            {
                int len = sun.ToString().Length * 20 / 2;
                starty = addLabel(sun.ToString(), 200.0f - len, starty, "s_" + i, false, NGUIText.Alignment.Right);
                continue;
            }
            sun = tryToGetData(child, "i");
            if (sun != null)
            {
                continue;
            }
        }
    }

    private JsonData tryToGetData(JsonData data ,string key)
    {
        try
        {
            JsonData item = data[key];
            return item;
        }
        catch (Exception e)
        {
            Debug.Log(e.ToString());
            return null;
        }
    }

    private float addLabel(string str, float x, float y, string name, bool isTitle = false , NGUIText.Alignment alig = NGUIText.Alignment.Center)
    {
        int fontSize = isTitle ? 32 : 24;
        int stand = rect.width - fontSize * 2;
        int total = str.Length * fontSize;
        int height = fontSize;
        float offy = 0;
        while (total > stand)
        {
            height += fontSize;
            total -= stand;
            offy += fontSize / 2;
        }
        UILabel label = NGUITools.AddChild<UILabel>(father);
        label.name = name;
        label.text = str;
        label.trueTypeFont = font;
        label.color = isTitle ? Color.red : Color.black;
        label.width = stand;
        label.height = height;
        label.alignment = alig;
        label.fontSize = fontSize;
        label.transform.localPosition = new Vector3(fontSize,y-offy,0.0f);
        return y - height;
    }

    private class OffsetBody
    {
        List<float> xs;
        List<int> cs;
        List<int> srs;
        List<int> ers;

        public OffsetBody()
        {
            xs = new List<float>();
            cs = new List<int>();
            srs = new List<int>();
            ers = new List<int>();
        }

        public void add(int col , int startRow, int endRow , float x)
        {
            cs.Add(col);
            srs.Add(startRow);
            ers.Add(endRow);
            xs.Add(x);
        }

        public float offset(int row , int col)
        {
            float result = 0;
            for (int i = 0; i < cs.Count; i++)
            {
                if (col >= cs[i] && row >= srs[i] && row <= ers[i])
                {
                    result += xs[i];
                }
            }
            return result;
        }
    };

    private float addTable(JsonData data,float x, float y,string name)
    {
        GameObject temp = NGUITools.AddChild(father);
        temp.name = name;
        int pRow = -1;
        Dictionary<int, List<JsonData>> table = new Dictionary<int,List<JsonData>>();
        for (int i = 0 ; i < data.Count ; i++)
        {
            JsonData td = data[i];
            int row = int.Parse(td["row"].ToString());
            List<JsonData> tr = null;
            if (pRow != row)
            {
                tr = new List<JsonData>();
                table.Add(row,tr);
                pRow = row;
            }
            else
            {
                tr = table[row];
            }
            tr.Add(td);
        }
        Dictionary<int,List<JsonData>>.KeyCollection.Enumerator keys = table.Keys.GetEnumerator();
        OffsetBody offset = new OffsetBody();
        while (keys.MoveNext())
        {
            int key = keys.Current;
            List<JsonData> tr = table[key];
            float tx = x;
            int height = comput_h(tr);
            int rs = 0 , cs = 0, width = 0,offcs = 0 ;
            string nr = null;
            for (int i = 0; i < tr.Count; i++)
            {
                JsonData td = tr[i];
                rs    = int.Parse(td["rs"].ToString());
                cs    = int.Parse(td["cs"].ToString());
                width = int.Parse(td["w"].ToString());
                nr    = td["nr"].ToString();
                int real_h = height;
                if (rs  > 1)
                {
                    real_h = comput_h(table,key,rs);
                }
                GameObject item = NGUITools.AddChild(temp);
                addTableItem(item,width,real_h,nr);
                item.name = name + "_(" + key + "," + i + ")";
                float offx = offset.offset(key,i);
                item.transform.localPosition = new Vector3(tx + offx,y,0);
                tx += width;
                offcs += cs - 1;
                if (rs > 1)
                {
                    offset.add(i+offcs,key+1,key+rs-1,width);
                }
            }
            y -= height;
        }
        return y - 20;
    }

    private int comput_h(Dictionary<int, List<JsonData>> table , int start , int end)
    {
        int result = 0;
        for (int i = start ; i < start + end; i++)
        {
            List<JsonData> tr = table[i];
            result += comput_h(tr);
        }
        return result;
    }

    private int comput_h(List<JsonData> tr)
    {
        int max = 0;
        for (int i = 0; i < tr.Count; i++)
        {
            JsonData td = tr[i];
            int rs = int.Parse(td["rs"].ToString());
            if (rs > 1)
            {
                continue;
            }
            int width = int.Parse(td["w"].ToString());
            string str = td["nr"].ToString();
            int result = 50;
            int fontSize = 24;
            float total = computLen(str,fontSize);
            if (total > width)
            {
                result = 30;
                while (total > width)
                {
                    result += 30;
                    total -= width;
                }
            }
            if (result > max)
            {
                max = result;
            }
        }
        return max;
    }
    private int comput_w(int col)
    {
        return 0;
    }

    private void addTableItem(GameObject item,int w,int h,string text)
    {
        UISprite sprite = NGUITools.AddSprite(item, atlas, "Highlight - Shadowed");
        sprite.name  = "grid";
        sprite.width = w + 6;
        sprite.height = h + 6;
        sprite.color = Color.black;
        sprite.centerType = UIBasicSprite.AdvancedType.Invisible;
        sprite.transform.localPosition = new Vector3(w/2,-h/2,0);
        addTableText(sprite.transform.gameObject,w,h,text);
    }

    private void addTableText(GameObject item,int w,int h,string text)
    {
        UILabel label = NGUITools.AddChild<UILabel>(item);
        label.text = text;
        label.trueTypeFont = font;
        label.transform.localPosition = new Vector3(0,0,0.0f);
        label.fontSize = 20;
        label.name = "text";
        label.width  = w - 20;
        label.height = h;
        label.alignment = NGUIText.Alignment.Left;
    }

    private float computLen(string text , int fontSize)
    {
        float result = 0;
        for (int i = 0; i < text.Length; i++)
        {
            result += text[i] < 128 ? fontSize / 2 : fontSize;
        }
        return result;
    }
}
 
 
