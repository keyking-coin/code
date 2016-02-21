using UnityEngine;
using System.Collections;

public class NoticeEvent : CenterEvent
{
    public Font labelFont;

    class WebViewCallback : Kogarasi.WebView.IWebViewCallback
    {
        public void onLoadStart(string url)
        {
            //Debug.Log("call onLoadStart : " + url);
        }
        public void onLoadFinish(string url)
        {
           //Debug.Log("call onLoadFinish : " + url);
        }
        public void onLoadFail(string url)
        {
            //Debug.Log("call onLoadFail : " + url);
        }
    }

    class BourseEntity : MonoBehaviour
    {
        string decName;
        string url;
        public void init(string decName, string url)
        {
            this.decName = decName;
            this.url     = url;
        }

        void select(WebViewBehavior webBody , GameObject base_obj)
        {
            webBody.LoadURL(url);
        }
    }

	// Use this for initialization
	void Start () {
        readXLSX();
	}
	
	// Update is called once per frame
	void Update () {

	}

    void readXLSX()
    {
        WebViewBehavior webBody = GetComponent<WebViewBehavior>();
        webBody.setCallback(new WebViewCallback());
        UIAtlas baseAtlass = Resources.Load<UIAtlas>("Atlass/bourseIcons");
        GameObject newObj = new GameObject();
        TextAsset binAsset = Resources.Load<TextAsset>("excel/bourse");
        string[] lineArray = binAsset.text.Split(new char[]{'\r','\n'});
        float start = 300;
        float x = -start;
        float y = 450;
        for (int i = 1 ; i < lineArray.Length ; i ++)
        {
            if (MyUtilTools.stringIsNull(lineArray[i]))
            {
                continue;
            }
            string[] ss = lineArray[i].Split(","[0]);
            string spriteName = ss[0];
            string name = ss[1];
            string url  = ss[2];
            GameObject sun = NGUITools.AddChild(gameObject,newObj);
            sun.name = name;
            sun.transform.localPosition = new Vector3(x, y, 0);
            UISprite sprite = sun.AddComponent<UISprite>();
            sprite.atlas = baseAtlass;
            sprite.spriteName = spriteName;
            sprite.width = 100;
            sprite.height = 100;
            sprite.depth = 1;
            sprite.autoResizeBoxCollider = true;
            BoxCollider collider = sun.AddComponent<BoxCollider>();
            collider.size = new Vector3(100, 100, 0);
            UIButton button = sun.AddComponent<UIButton>();
            BourseEntity bourse = sun.AddComponent<BourseEntity>();
            bourse.init(name, url);
            EventDelegate event_select = new EventDelegate(bourse,"select");
            event_select.parameters[0] = new EventDelegate.Parameter();
            event_select.parameters[0].obj = webBody;
            event_select.parameters[1] = new EventDelegate.Parameter();
            event_select.parameters[1].obj = needshow[0].transform.parent.parent.parent.parent.gameObject;
            button.onClick.Add(event_select);
            GameObject font_obj = NGUITools.AddChild(sun, newObj);
            font_obj.transform.localPosition = new Vector3(0, -80, 0);
            font_obj.name = "label_" + name;
            UILabel label = font_obj.AddComponent<UILabel>();
            label.trueTypeFont = labelFont;
            label.fontSize = 40;
            label.text = name;
            label.width = 100;
            label.height = 50;
            label.color = Color.black;
            label.depth = 2;
            x += 120;
            if (x > start)
            {
                x = -start;
                y -= 180;
            }
        }
        Destroy(newObj);
    }
}
